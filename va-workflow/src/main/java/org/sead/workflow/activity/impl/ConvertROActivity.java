package org.sead.workflow.activity.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sead.workflow.activity.AbstractWorkflowActivity;
import org.sead.workflow.activity.SeadWorkflowActivity;
import org.sead.workflow.config.SeadWorkflowConfig;
import org.sead.workflow.context.SeadWorkflowContext;
import org.sead.workflow.exception.SeadWorkflowException;
import org.sead.workflow.util.Constants;
import org.sead.workflow.util.IdGenerator;
import org.seadva.services.statusTracker.SeadStatusTracker;
import org.seadva.services.statusTracker.enums.SeadStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Responsible for converting the JSONLD format of metadata received from PS to the format
 * that is accepted by RO Info system.
 * Converts the namespaces of metadata in a collection and add information about files and
 * pointers to sub-collections.
 */
public class ConvertROActivity extends AbstractWorkflowActivity {


    @Override
    public void execute(SeadWorkflowContext context, SeadWorkflowConfig config) {
        System.out.println("\n=====================================");
        System.out.println("Executing activity : " + activityName);
        System.out.println("-----------------------------------\n");

        String sead_id = context.getProperty(Constants.RO_ID);
        String roId = context.getCollectionId();

        SeadStatusTracker.addStatus(sead_id, SeadStatus.WorkflowStatus.CONVERT_RO_BEGIN.getValue());

        HashMap<String, String> activityParams = new HashMap<String, String>();
        for(SeadWorkflowActivity activity : config.getActivities()){
            AbstractWorkflowActivity abstractActivity = (AbstractWorkflowActivity)activity;
            if(abstractActivity.activityName.equals(activityName)){
                activityParams = abstractActivity.params;
                break;
            }
        }

        // generate JSONLD for the collection identified by roId
        //String sead_id = IdGenerator.generateRandomID();
        //context.addProperty(Constants.RO_ID, sead_id);
        String roJsonString = getRO(roId, sead_id, activityParams, context);
        context.addProperty(Constants.JSON_RO, roJsonString);

        SeadStatusTracker.addStatus(sead_id, SeadStatus.WorkflowStatus.CONVERT_RO_END.getValue());

        System.out.println(ConvertROActivity.class.getName() + " : Successfully converted the RO");
        System.out.println("=====================================\n");


    }

    private String getRO(String roId, String seadId, HashMap<String, String> activityParams, SeadWorkflowContext context) {
        String psUrl = context.getPSInstance().getUrl();
        String tempPath = activityParams.get("tempPath");
        String ps_un = context.getPSInstance().getUser();
        String ps_pw = context.getPSInstance().getPassword();
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(ps_un, ps_pw));
        // get collection level metadata
        WebResource webResource = client.resource(
                psUrl + "/resteasy/"
        );
        webResource = webResource.path("collections")
                .path(URLEncoder.encode(roId))
                .path("unique");

        ClientResponse response = webResource
                .get(ClientResponse.class);

        StringWriter writer = new StringWriter();

        JSONObject jsonObject = null;
        try {
            IOUtils.copy(response.getEntityInputStream(), writer);

            // convert metatada namespaces
            String json = convertRO(writer.toString());
            jsonObject = new JSONObject(json);

            // add Collection location
            jsonObject.put(Constants.GEN_AT, psUrl + "/#collection?uri=" + roId);
            jsonObject.put(Constants.IDENTIFIER, seadId);

            // add File metadata
            addFilesMetadata(roId, jsonObject, context);

            // add pointers to sub-collections
            JSONArray subCollectionArray = addSubCollectionMetadata(roId, context, tempPath);
            ((JSONObject) jsonObject.get(Constants.REST_CONTEXT)).put(Constants.HAS_SUBCOLLECTIONS, "http://purl.org/dc/terms/hasPart");
            jsonObject.put(Constants.HAS_SUBCOLLECTIONS, subCollectionArray);

            for (int i = 0; i < subCollectionArray.length(); i++) {
                JSONObject arrayItem = (JSONObject) subCollectionArray.get(i);
                // generate JSONLD for each sub-collection. This is a recursive call
                getRO((String) arrayItem.get(Constants.PS_IDENTIFIER), (String)arrayItem.get(Constants.IDENTIFIER), activityParams, context);
                arrayItem.remove(Constants.PS_IDENTIFIER);
            }

            // Write JSONLD to a file
            FileOutputStream fileOutputStream = new FileOutputStream(new File(tempPath + "ro_" + getROFileName(roId) + ".json"));
            IOUtils.write(jsonObject.toString(), fileOutputStream);
            fileOutputStream.close();

        } catch (JSONException e) {
            throw new SeadWorkflowException("Error occurred while converting collection " + roId + " , Caused by: " + e.getMessage() , e);
        } catch (IOException e) {
            throw new SeadWorkflowException("Error occurred while converting collection " + roId + " , Caused by: " + e.getMessage(), e);
        }

        return jsonObject.toString();
    }

    private String convertRO(String roString) throws JSONException {
        JSONObject jsonObject = null;

        jsonObject = new JSONObject(roString);
        Iterator rootIterator = jsonObject.keys();

        while (rootIterator.hasNext()) {
            String rootKey = (String) rootIterator.next();
            if (rootKey.equals(Constants.REST_CONTEXT)) {
                JSONObject object = (JSONObject) jsonObject.get(rootKey);

                // Adding source and Flocat namespaces
                if (!object.has(Constants.FLOCAT))
                    object.put(Constants.FLOCAT, Constants.FLOCAT_URL);
                if(!object.has(Constants.GEN_AT))
                    object.put(Constants.GEN_AT, Constants.GEN_AT_URL);

                // Do the ACR to ORE mapping for the namespaces in @context
                Iterator iterator = object.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    Object value = object.get(key);
                    if (value instanceof String && Constants.metadataPredicateMap.get(value) != null) {
                        object.put(key, Constants.metadataPredicateMap.get(value));
                    } else if (value instanceof JSONObject &&
                            Constants.metadataPredicateMap.get((String) ((JSONObject) value).get(Constants.REST_ID)) != null) {
                        object.put(key, Constants.metadataPredicateMap.get(
                                Constants.metadataPredicateMap.get((String) ((JSONObject) value).get(Constants.REST_ID))));

                    } else {
                        //TODO : check what namespaces do not have a mapping URL
                    }
                }
            } else {
                // Flatten the Objects
                Object object = jsonObject.get(rootKey);
                if(object instanceof JSONObject){
                    jsonObject.put(rootKey, ((JSONObject)object).toString());
                } else if(object instanceof JSONArray){
                    JSONArray arrayObject = (JSONArray)object;
                    JSONArray newArray = new JSONArray();
                    for(int i = 0 ; i < arrayObject.length() ; i++) {
                        Object arrayElement = arrayObject.get(i);
                        if (arrayElement instanceof JSONObject) {
                            newArray.put(((JSONObject)arrayElement).toString());
                        } else if (arrayElement instanceof JSONArray) {
                            newArray.put(((JSONArray)arrayElement).toString());
                        } else {
                            newArray.put(arrayElement);
                        }
                    }
                    jsonObject.put(rootKey, newArray);
                }
            }
        }

        return jsonObject.toString();
    }

    private void addFilesMetadata(String roId, JSONObject jsonObject, SeadWorkflowContext context) throws JSONException, IOException {

        String psUrl = context.getPSInstance().getUrl();
        String ps_un = context.getPSInstance().getUser();
        String ps_pw = context.getPSInstance().getPassword();

        JSONArray hasFilesArray = new JSONArray();
        ((JSONObject)jsonObject.get(Constants.REST_CONTEXT)).put(Constants.HAS_FILES, "http://purl.org/dc/terms/hasPart");
        jsonObject.put(Constants.HAS_FILES, hasFilesArray);

        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(ps_un, ps_pw));
        WebResource webResource = client.resource(
                psUrl + "/resteasy/"
        );
        webResource = webResource.path("collections")
                .path(URLEncoder.encode(roId))
                .path("datasets");

        ClientResponse response = webResource
                .get(ClientResponse.class);

        StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntityInputStream(), writer);
        String fileArray = writer.toString();
        JSONObject fileArrayObject = new JSONObject(fileArray);
        fileArrayObject.remove(Constants.REST_CONTEXT);

        Iterator iterator = fileArrayObject.keys();
        while (iterator.hasNext()){
            String key = (String)iterator.next();

            Client fileClient = Client.create();
            fileClient.addFilter(new HTTPBasicAuthFilter(ps_un, ps_pw));
            WebResource fileWebResource = fileClient.resource(
                    psUrl + "/resteasy/"
            );
            fileWebResource = fileWebResource.path("datasets")
                    .path(URLEncoder.encode(key))
                    .path("unique");

            ClientResponse fileResponse = fileWebResource
                    .get(ClientResponse.class);

            StringWriter fileWriter = new StringWriter();
            IOUtils.copy(fileResponse.getEntityInputStream(), fileWriter);

            JSONObject fileObject = new JSONObject(convertRO(fileWriter.toString()));
            fileObject.remove(Constants.REST_CONTEXT);
            fileObject.put(Constants.GEN_AT, psUrl + "/#dataset?id=" + key);
            fileObject.put(Constants.FLOCAT,  psUrl + "/resteasy/datasets/" + key + "/file");
            fileObject.put(Constants.IDENTIFIER, IdGenerator.generateRandomID());
            hasFilesArray.put(fileObject);
        }
    }

    private JSONArray addSubCollectionMetadata(String roId, SeadWorkflowContext context, String tempPath) throws IOException, JSONException {

        String psUrl = context.getPSInstance().getUrl();
        String ps_un = context.getPSInstance().getUser();
        String ps_pw = context.getPSInstance().getPassword();

        JSONArray hasFilesArray = new JSONArray();

        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(ps_un, ps_pw));
        WebResource webResource = client.resource(
                psUrl + "/resteasy/"
        );
        webResource = webResource.path("collections")
                .path(URLEncoder.encode(roId))
                .path("collections");

        ClientResponse response = webResource
                .get(ClientResponse.class);

        StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntityInputStream(), writer);
        String subCollectionArray = writer.toString();
        JSONObject subCollectionArrayObject = new JSONObject(subCollectionArray);
        subCollectionArrayObject.remove(Constants.REST_CONTEXT);

        Iterator iterator = subCollectionArrayObject.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            JSONObject fileObject = new JSONObject();
            fileObject.put(Constants.PS_IDENTIFIER, key);
            fileObject.put(Constants.IDENTIFIER, IdGenerator.generateRandomID());
            fileObject.put(Constants.FLOCAT, tempPath + "ro_" + getROFileName(key) + ".json");
            hasFilesArray.put(fileObject);
        }

        return hasFilesArray;
    }

    private String getROFileName(String roId){
        if(roId.contains("/"))
            return roId.split("/")[roId.split("/").length-1];
        else if(roId.contains(":"))
            return roId.split(":")[roId.split(":").length-1];
        else
            return roId;
    }
}
