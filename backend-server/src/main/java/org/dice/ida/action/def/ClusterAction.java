package org.dice.ida.action.def;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Value;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.clustering.KmeansAttribute;
import org.dice.ida.util.DialogFlowUtil;
import org.dice.ida.util.FileUtil;
import org.dice.ida.util.SessionUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class to handle the Clustering implementation
 *
 * @author Sourabh Poddar
 */
@Component
public class ClusterAction implements Action {

	Map<String, Object> sessionMap;
	@Autowired
	private DialogFlowUtil dialogFlowUtil;
	private StringBuilder textMsg;
	private Map<String, Object> paramMap;
	private Instances data;
	private String datasetName;
	private String tableName;
	private String clusterMethod;
	private String paramtertoChange;
	private String paramValue = "";
	private Map<String, String> multiParmaValue;
	@Autowired
	private SessionUtil sessionUtil;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {
		try {
			textMsg = new StringBuilder(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
			if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
				Map<String, Object> payload = chatMessageResponse.getPayload();
				datasetName = payload.get("activeDS").toString();
				tableName = payload.get("activeTable").toString();
				multiParmaValue = null;
				sessionMap = sessionUtil.getSessionMap();
				this.paramMap = paramMap;
				String fullIntentName = paramMap.get(IDAConst.FULL_INTENT_NAME).toString();
				clusterMethod = getClusterMethod(fullIntentName);
				String parameterChangeChoice = getParameterChangeChoice(fullIntentName);
				paramtertoChange = getParameterToChange(fullIntentName);
				if (!clusterMethod.isEmpty() && parameterChangeChoice.isEmpty()) {
					//clusterer = getClusterer(clusterMethod);
					if (clusterMethod.equals("getColumnList")) {

						if (verifynApplyFilter(paramMap.get("column_List"))) {
							textMsg.append("Okay! Here is the list clustering algorithm currently offered by IDA .\n" +
									"- Kmean\n" +
									"Which algorithm would you like to use for clustering?");
						}

					} else {
						textMsg = new StringBuilder("Okay!! Here is the list of default parameter and our suggested parameters\n\n");
						showParamList();

						textMsg.append("\nWould you like to change any parameter?");
					}
				} else if (!paramtertoChange.isEmpty()) {

					getnsetNewParamValue();
				}
				if (parameterChangeChoice.equals("no")) {
					textMsg = new StringBuilder("Here is your clustered data\n");
				}

				chatMessageResponse.setMessage(textMsg.toString());
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean verifynApplyFilter(Object column_list) throws IOException {
		ArrayList<String> columnList = new ArrayList<>();
		boolean columnExist = true;
		Value paramVal = (Value) column_list;
		paramVal.getListValue().getValuesList().forEach(str -> columnList.add(str.getStringValue()));

		String path = new FileUtil().fetchSysFilePath("datasets/" + datasetName + "/" + tableName);
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(path));
		data = loader.getDataSet();
		if (!columnList.get(0).equalsIgnoreCase("All")) {
			ObjectNode metaData = new FileUtil().getDatasetMetaData(datasetName);
			JsonNode fileDetails = metaData.get(IDAConst.FILE_DETAILS_ATTR);
			for (int i = 0; i < fileDetails.size(); i++) {
				if (tableName.equals(fileDetails.get(i).get(IDAConst.FILE_NAME_ATTR).asText())) {
					JsonNode columnDetails = fileDetails.get(i).get(IDAConst.COLUMN_DETAILS_ATTR);
					ArrayList<String> columns = new ArrayList<>();
					for (int j = 0; j < columnDetails.size(); j++)
						columns.add(columnDetails.get(j).get(IDAConst.COLUMN_NAME_ATTR).asText().toLowerCase());
					for (String column : columnList) {
						if (!columns.contains(column.toLowerCase())) {
							textMsg = new StringBuilder("Sorry, But column \"" + column + "\" doesn't exist in table \"" + tableName + "\"." +
									"Please provide the columns names carefully!! ");
							columnExist = false;
						}
					}

				}
			}
			if (columnExist) {

				int numAttribute = data.numAttributes();
				for (int i = 0; i < numAttribute; i++) {
					String name = data.attribute(i).name();
					if (!(columnList.contains(name) || columnList.contains(name.substring(1)))) {
						data.deleteAttributeAt(i);
						i--;
						numAttribute--;
					}
				}
			}
		}

		return columnExist;
	}

	private void showParamList() throws Exception {
		EM em = new EM();
		//em.setNumFolds(10);
		em.buildClusterer(data);
		int numCluster = em.numberOfClusters();
		switch (clusterMethod) {
			case IDAConst.K_MEAN_CLUSTERING:
				sessionMap.put(IDAConst.K_MEAN_CLUSTERING, new KmeansAttribute(new SimpleKMeans(), numCluster));
				sessionUtil.setSessionMap(sessionMap);
				showKmeansParamList();
		}
	}

	private void getnsetNewParamValue() {
		switch (clusterMethod) {
			case IDAConst.K_MEAN_CLUSTERING:
				getnSetKmeanParam();
				break;


		}
	}

	private void getnSetKmeanParam() {
		KmeansAttribute kmeansAttribute = (KmeansAttribute) sessionMap.get(IDAConst.K_MEAN_CLUSTERING);
		switch (paramtertoChange) {
			case IDAConst.GET_NUM_CLUSTER:
				paramValue = paramMap.get(IDAConst.NUMBER_OF_CLUSTER).toString();
				if (!paramValue.isEmpty()) {
					kmeansAttribute.setNumberOfCluster(Integer.parseInt(paramValue.split(":")[1].substring(1, 2)));
				}
				break;
			case IDAConst.GET_INIT_METHOD:
				paramValue = paramMap.get(IDAConst.INIT_METHOD).toString();
				if (!paramValue.isEmpty()) {
					kmeansAttribute.setIntitializeMethod(Integer.parseInt(paramValue.split(":")[1].substring(1, 2)));
				}
				break;
			case IDAConst.GET_MAX_ITERATION:
				paramValue = paramMap.get(IDAConst.MAX_ITERATION).toString();
				if (!paramValue.isEmpty()) {
					kmeansAttribute.setMaxIterations(Integer.parseInt(paramValue.split(":")[1].substring(1, 2)));
				}
				break;
			case IDAConst.GET_REPLACE_MISSING_VALUES:
				paramValue = paramMap.get(IDAConst.IS_REPLACE_MISSING_VALUE).toString();
				if (!paramValue.isEmpty()) {
					kmeansAttribute.setReplaceMissingValues(Boolean.parseBoolean(paramValue));
				}
				break;
			case IDAConst.GET_NUM_EXECUTION_SLOT:
				paramValue = paramMap.get(IDAConst.NUM_OF_SLOT).toString();
				if (!paramValue.isEmpty()) {
					kmeansAttribute.setNumOfExecutionSlots(Integer.parseInt(paramValue.split(":")[1].substring(1, 2)));
				}
				break;
			case IDAConst.GET_RANDOM_SEED:
				paramValue = paramMap.get(IDAConst.RANDOM_SEED).toString();
				if (!paramValue.isEmpty()) {
					kmeansAttribute.setRandomNumberSeed(Integer.parseInt(paramValue.split(":")[1].substring(1, 2)));
				}
				break;
			case IDAConst.GET_MULTI_PARAM:
				multiParmaValue = getKmeanMultiParam();
				if (!multiParmaValue.get(IDAConst.NUMBER_OF_CLUSTER).isEmpty())
					kmeansAttribute.setNumberOfCluster(getNumericValue(multiParmaValue.get(IDAConst.NUMBER_OF_CLUSTER)));
				if (!multiParmaValue.get(IDAConst.MAX_ITERATION).isEmpty())
					kmeansAttribute.setMaxIterations(getNumericValue(multiParmaValue.get(IDAConst.MAX_ITERATION)));
				if (!multiParmaValue.get(IDAConst.INIT_METHOD).isEmpty())
					kmeansAttribute.setIntitializeMethod(getNumericValue(multiParmaValue.get(IDAConst.INIT_METHOD)));
				if (!multiParmaValue.get(IDAConst.NUM_OF_SLOT).isEmpty())
					kmeansAttribute.setNumOfExecutionSlots(getNumericValue(multiParmaValue.get(IDAConst.NUM_OF_SLOT)));
				if (!multiParmaValue.get(IDAConst.RANDOM_SEED).isEmpty())
					kmeansAttribute.setRandomNumberSeed(getNumericValue(multiParmaValue.get(IDAConst.RANDOM_SEED)));
				if (!multiParmaValue.get(IDAConst.IS_REPLACE_MISSING_VALUE).isEmpty())
					kmeansAttribute.setReplaceMissingValues(Boolean.parseBoolean(multiParmaValue.get(IDAConst.IS_REPLACE_MISSING_VALUE)));
				break;


		}
		if (!paramValue.isEmpty() || multiParmaValue != null) {
			sessionMap.put(IDAConst.K_MEAN_CLUSTERING, kmeansAttribute);
			sessionUtil.setSessionMap(sessionMap);
			textMsg = new StringBuilder("Value Changed!! Would you like to change another parameter");
			dialogFlowUtil.setContext("clustering-Kmeans-followup");
		}
	}

	private Map<String, String> getKmeanMultiParam() {
		Map<String, String> kmeanMultiParam = new HashMap<>();
		kmeanMultiParam.put(IDAConst.NUMBER_OF_CLUSTER, paramMap.get(IDAConst.NUMBER_OF_CLUSTER).toString());
		kmeanMultiParam.put(IDAConst.MAX_ITERATION, paramMap.get(IDAConst.MAX_ITERATION).toString());
		kmeanMultiParam.put(IDAConst.INIT_METHOD, paramMap.get(IDAConst.INIT_METHOD).toString());
		kmeanMultiParam.put(IDAConst.NUM_OF_SLOT, paramMap.get(IDAConst.NUM_OF_SLOT).toString());
		kmeanMultiParam.put(IDAConst.RANDOM_SEED, paramMap.get(IDAConst.RANDOM_SEED).toString());
		kmeanMultiParam.put(IDAConst.IS_REPLACE_MISSING_VALUE, paramMap.get(IDAConst.IS_REPLACE_MISSING_VALUE).toString());

		return kmeanMultiParam;

	}

	private String getParameterToChange(String fullIntentName) {
		return fullIntentName.contains(" - ") && fullIntentName.split(" - ").length > 3 ? fullIntentName.split(" - ")[3] : "";
	}

	private String getParameterChangeChoice(String fullIntentName) {
		return fullIntentName.contains(" - ") && fullIntentName.split(" - ").length > 2 ? fullIntentName.split(" - ")[2] : "";
	}

	private String getClusterMethod(String fullIntentName) {
		return fullIntentName.contains(" - ") ? fullIntentName.split(" - ")[1] : "";
	}

	private void showKmeansParamList() {
		KmeansAttribute kmeansAttribute = (KmeansAttribute) sessionMap.get(IDAConst.K_MEAN_CLUSTERING);
		textMsg.append("Number of Clusters(N) = ").append(kmeansAttribute.getNumberOfCluster()).append("\n");
		textMsg.append("Intitialize Method(P) = ").append(kmeansAttribute.getIntitializeMethod()).append("\n");
		textMsg.append("Max No. of iterations(I) = ").append(kmeansAttribute.getMaxIterations()).append("\n");
		textMsg.append("Replace missing values(M) = ").append(kmeansAttribute.getReplaceMissingValues()).append("\n");
		textMsg.append("No. of execution slots(E) = ").append(kmeansAttribute.getNumOfExecutionSlots()).append("\n");
		textMsg.append("Random number seed(S) = ").append(kmeansAttribute.getRandomNumberSeed()).append("\n");
	}

	private int getNumericValue(String paramValue) {

		return Integer.parseInt(paramValue.split(":")[1].split("\\.")[0].trim());

	}

}
