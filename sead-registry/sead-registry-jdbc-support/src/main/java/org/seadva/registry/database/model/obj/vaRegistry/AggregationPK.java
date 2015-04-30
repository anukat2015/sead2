package org.seadva.registry.database.model.obj.vaRegistry;

import com.google.gson.annotations.Expose;
import org.seadva.registry.database.model.obj.vaRegistry.iface.IAggregationPK;


/** 
 * Object mapping for hibernate-handled table: aggregation.
 * @author autogenerated
 */

public class AggregationPK implements IAggregationPK {

	/** Serial Version UID. */
	private static final long serialVersionUID = -559002651L;

	

	/** Field mapping. */
    @Expose

	private BaseEntity child;

	/** Field mapping. */
    @Expose
	private BaseEntity parent;

 


 
	/** Return the type of this class. Useful for when dealing with proxies.
	* @return Defining class.
	*/

	public Class<?> getClassType() {
		return AggregationPK.class;
	}
 

    /**
     * Return the value associated with the column: child.
	 * @return A BaseEntity object (this.child)
	 */
	public BaseEntity getChild() {
		return this.child;
		
	}
	

  
    /**  
     * Set the value related to the column: child.
	 * @param child the child value you wish to set
	 */
	public void setChild(final BaseEntity child) {
		this.child = child;
	}

    /**
     * Return the value associated with the column: parent.
	 * @return A BaseEntity object (this.parent)
	 */

	public BaseEntity getParent() {
		return this.parent;
		
	}
	

  
    /**  
     * Set the value related to the column: parent.
	 * @param parent the parent value you wish to set
	 */
	public void setParent(final BaseEntity parent) {
		this.parent = parent;
	}


   /**
    * Deep copy.
	* @return cloned object
	* @throws CloneNotSupportedException on error
    */
    @Override
    public AggregationPK clone() throws CloneNotSupportedException {
		
        final AggregationPK copy = (AggregationPK)super.clone();

		return copy;
	}
	


	/** Provides toString implementation.
	 * @see Object#toString()
	 * @return String representation of this class.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		return sb.toString();		
	}


	/** Equals implementation. 
	 * @see Object#equals(Object)
	 * @param aThat Object to compare with
	 * @return true/false
	 */
	@Override
	public boolean equals(final Object aThat) {
		Object proxyThat = aThat;
		
		if ( this == aThat ) {
			 return true;
		}

		if (aThat == null)  {
			 return false;
		}
		
		final AggregationPK that; 
		try {
			that = (AggregationPK) proxyThat;
			if ( !(that.getClassType().equals(this.getClassType()))){
				return false;
			}
		} catch (org.hibernate.ObjectNotFoundException e) {
				return false;
		} catch (ClassCastException e) {
				return false;
		}
		
		
		boolean result = true;
		result = result && (((getChild() == null) && (that.getChild() == null)) || (getChild() != null && getChild().getId().equals(that.getChild().getId())));	
		result = result && (((getParent() == null) && (that.getParent() == null)) || (getParent() != null && getParent().getId().equals(that.getParent().getId())));	
		return result;
	}
	
	/** Calculate the hashcode.
	 * @see Object#hashCode()
	 * @return a calculated number
	 */
	@Override
	public int hashCode() {
	int hash = 0;
		hash = hash + getChild().hashCode();
		hash = hash + getParent().hashCode();
	return hash;
	}
	

	
}
