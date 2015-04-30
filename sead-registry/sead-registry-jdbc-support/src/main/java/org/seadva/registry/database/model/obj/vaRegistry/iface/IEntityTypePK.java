package org.seadva.registry.database.model.obj.vaRegistry.iface;
import org.seadva.registry.database.model.obj.vaRegistry.BaseEntity;


/** 
 * Object interface mapping for hibernate-handled table: entity_type.
 * @author autogenerated
 */

public interface IEntityTypePK {



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

    /**
     * Return the value associated with the column: entityTypeId.
	 * @return A String object (this.entityTypeId)
	 */
	String getEntityTypeId();
	

  
    /**  
     * Set the value related to the column: entityTypeId.
	 * @param entityTypeId the entityTypeId value you wish to set
	 */
	void setEntityTypeId(final String entityTypeId);

	// end of interface
}