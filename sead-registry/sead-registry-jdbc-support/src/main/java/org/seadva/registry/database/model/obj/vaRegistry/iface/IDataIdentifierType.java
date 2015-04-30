package org.seadva.registry.database.model.obj.vaRegistry.iface;
import java.util.Set;
import javax.persistence.Basic;
import org.seadva.registry.database.model.obj.vaRegistry.DataIdentifier;


/** 
 * Object interface mapping for hibernate-handled table: data_identifier_type.
 * @author autogenerated
 */

public interface IDataIdentifierType {



    /**
     * Return the value associated with the column: dataIdentifier.
	 * @return A Set&lt;DataIdentifier&gt; object (this.dataIdentifier)
	 */
	Set<DataIdentifier> getDataIdentifiers();
	
	/**
	 * Adds a bi-directional link of type DataIdentifier to the dataIdentifiers set.
	 * @param dataIdentifier item to add
	 */
	void addDataIdentifier(DataIdentifier dataIdentifier);

  
    /**  
     * Set the value related to the column: dataIdentifier.
	 * @param dataIdentifier the dataIdentifier value you wish to set
	 */
	void setDataIdentifiers(final Set<DataIdentifier> dataIdentifier);

    /**
     * Return the value associated with the column: dataIdentifierTypeName.
	 * @return A String object (this.dataIdentifierTypeName)
	 */
	String getDataIdentifierTypeName();
	

  
    /**  
     * Set the value related to the column: dataIdentifierTypeName.
	 * @param dataIdentifierTypeName the dataIdentifierTypeName value you wish to set
	 */
	void setDataIdentifierTypeName(final String dataIdentifierTypeName);

    /**
     * Return the value associated with the column: id.
	 * @return A String object (this.id)
	 */
	String getId();
	

  
    /**  
     * Set the value related to the column: id.
	 * @param id the id value you wish to set
	 */
	void setId(final String id);

    /**
     * Return the value associated with the column: schemaUri.
	 * @return A String object (this.schemaUri)
	 */
	String getSchemaUri();
	

  
    /**  
     * Set the value related to the column: schemaUri.
	 * @param schemaUri the schemaUri value you wish to set
	 */
	void setSchemaUri(final String schemaUri);

	// end of interface
}