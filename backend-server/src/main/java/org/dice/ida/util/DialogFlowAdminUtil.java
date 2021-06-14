package org.dice.ida.util;

import com.google.cloud.dialogflow.v2beta1.EntityType;
import com.google.cloud.dialogflow.v2beta1.EntityType.Entity;
import com.google.cloud.dialogflow.v2beta1.EntityTypesClient;
import com.google.cloud.dialogflow.v2beta1.ProjectAgentName;
import org.dice.ida.chatbot.IDAChatbotAdminUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class containing utility functions related to dialogflow management
 *
 * @author Nandeesh Patel
 */
@Component
public class DialogFlowAdminUtil {
	@Value("${dialogflow.project.id}")
	private String projectId;

	/**
	 * Method to add new entities to an existing entity type
	 *
	 * @param entityId - id of the existing entity type
	 * @param newEntities - list of new entities to be added ( each entry should have value and list of synonyms)
	 * @return - success or failure message
	 * @throws Exception - dialogflow client error
	 */
	public String addValuesToEntity(String entityId, Map<String, List<String>> newEntities) throws Exception {
		try (EntityTypesClient entityTypesClient = EntityTypesClient.create(IDAChatbotAdminUtil.getEntityTypeSettings())) {
			ProjectAgentName parent = ProjectAgentName.of(projectId);
			List<Entity> entityList = new ArrayList<>();
			List<String> existingColumns = new ArrayList<>();
			boolean entityFound = false;
			for (EntityType entityType : entityTypesClient.listEntityTypes(parent.toString()).iterateAll()) {
				if (entityId.equals(entityType.getDisplayName())) {
					List<String> entityValues = entityType.getEntitiesList().stream().map(Entity::getValue).collect(Collectors.toList());
					List<String> synonyms = new ArrayList<>();
					entityType.getEntitiesList().forEach(e -> synonyms.addAll(e.getSynonymsList()));
					for (String columnValue : newEntities.keySet()) {
						List<String> columnSynonyms = newEntities.get(columnValue);
						if (!columnSynonyms.contains(columnValue)) {
							columnSynonyms.add(columnValue);
						}
						if (!entityValues.contains(columnValue)) {
							columnSynonyms = columnSynonyms.stream().filter(s -> !synonyms.contains(s)).collect(Collectors.toList());
							entityList.add(Entity.newBuilder().setValue(columnValue).addAllSynonyms(columnSynonyms).build());
						} else {
							existingColumns.add(columnValue);
						}
					}
					if (!entityList.isEmpty()) {
						entityTypesClient.batchUpdateEntitiesAsync(entityType.getName(), entityList).get();
					}
					entityFound = true;
				}
			}
			entityTypesClient.close();
			if (!entityFound) {
				return "Given entity id does not exist";
			} else if (existingColumns.isEmpty()) {
				return "Success";
			} else {
				return "Following columns already exist: " + existingColumns + "\n All other columns were added Successfully";
			}
		}
	}

}
