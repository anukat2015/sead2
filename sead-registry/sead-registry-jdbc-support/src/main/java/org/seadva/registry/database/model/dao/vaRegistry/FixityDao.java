package org.seadva.registry.database.model.dao.vaRegistry;

import org.seadva.registry.database.model.obj.vaRegistry.Fixity;

import java.util.List;

/**
 * DAO interface for table: Fixity.
 * @author autogenerated
 */
public interface FixityDao {
	// constructor only
    List<Fixity> getFixities(String fileId);
    boolean putFixities(List<Fixity> fixities);
}
