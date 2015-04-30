package org.seadva.registry.database.model.obj.vaRegistry.iface;
import javax.persistence.Basic;
import org.seadva.registry.database.model.obj.vaRegistry.BaseEntity;
import org.seadva.registry.database.model.obj.vaRegistry.DataIdentifierType;


/** 
 * Object interface mapping for hibernate-handled table: data_identifier.
 * @author autogenerated
 */

public interface IDataIdentifierPK {



    /**
     * Return the value associated with the column: dataIdentifierType.
	 * @return A DataIdentifierType object (this.dataIdentifierType)
	 */
	DataIdentifierType getDataIdentifierType();
	

  
    /**  
     * Set the value related to the column: dataIdentifierType.
	 * @param dataIdentifierType the dataIdentifierType value you wish to set
	 */
	void setDataIdentifierType(final DataIdentifierType dataIdentifierType);

    /**
     * Return the value associated with the column: entity.
	 * @return A BaseEntity object (this.entity)
	 */
	BaseEntity getEntity();
	

  
    /**  
     * Set the value related to the column: entity.
	 * @param entity the entity value you wish to set
	 */
	void setEntity(final BaseEntity entity);

	// end of interface
}