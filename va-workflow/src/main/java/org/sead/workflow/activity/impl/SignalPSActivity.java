package org.sead.workflow.activity.impl;

import org.sead.workflow.activity.AbstractWorkflowActivity;
import org.sead.workflow.config.SeadWorkflowConfig;
import org.sead.workflow.context.SeadWorkflowContext;
import org.sead.workflow.util.Constants;

/**
 * Responsible for generating metadata standards like ORE, SIP etc.
 */
public class SignalPSActivity extends AbstractWorkflowActivity {

    @Override
    public void execute(SeadWorkflowContext context, SeadWorkflowConfig config) {
        System.out.println("Executing activity : " + activityName);

        String response;

        if(context.getProperty(Constants.VALIDATED).equals(Constants.TRUE)){
            response = "{\"response\": \"success\", \"message\" : \""+context.getProperty(Constants.RO_ID)+"\"}\n";
        } else {
            response = "{\"response\": \"error\", \"message\" : \"Validation Failed\"}\n";
        }

        System.out.println(SignalPSActivity.class.getName() + " Created response");
        context.addProperty(Constants.RESPONSE, response);
        context.updateProperty(Constants.SIGNAL_PS, Constants.TRUE); // set SIGNAL_PS flag to true
    }
}
