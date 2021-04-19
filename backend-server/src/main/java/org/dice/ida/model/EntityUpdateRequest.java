package org.dice.ida.model;

import java.util.List;
import java.util.Map;

/**
 * Model for entity update request
 *
 * @author Nandeesh Patel
 */
public class EntityUpdateRequest {
	String entityId;						// ID of the entity type to be updated
	Map<String, List<String>> entityList;	// list of new entities to be added

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Map<String, List<String>> getEntityList() {
		return entityList;
	}

	public void setEntityList(Map<String, List<String>> entityList) {
		this.entityList = entityList;
	}
}
