package org.dice.ida.action.def;

import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.model.clustering.KmeansAttribute;
import org.dice.ida.util.DialogFlowUtil;
import org.dice.ida.util.SessionUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import weka.clusterers.SimpleKMeans;
import java.util.HashMap;
import java.util.Map;


/**
 * Class to handle the Clustering implementation
 *
 * @author Sourabh Poddar
 */
@Component
public class ClusterAction implements Action {

	@Autowired
	private DialogFlowUtil dialogFlowUtil;
	private StringBuilder textMsg;
	Map<String, Object> sessionMap;
	private Map<String, Object> paramMap;
	private String clusterMethod;
	private String fullIntentName;
	private String paramtertoChange;
	private String paramValue = "";
	private Map<String, String> multiParmaValue;
	private String parameterChangeChoice;
	@Autowired
	private SessionUtil sessionUtil;

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {
		try {
			textMsg = new StringBuilder(paramMap.get(IDAConst.PARAM_TEXT_MSG).toString());
			if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
				multiParmaValue = null;
				sessionMap = sessionUtil.getSessionMap();
				this.paramMap = paramMap;
				fullIntentName = paramMap.get(IDAConst.FULL_INTENT_NAME).toString();
				clusterMethod = getClusterMethod(fullIntentName);
				parameterChangeChoice = getParameterChangeChoice(fullIntentName);
				paramtertoChange = getParameterToChange(fullIntentName);
				if (!clusterMethod.isEmpty() && parameterChangeChoice.isEmpty()) {
					//clusterer = getClusterer(clusterMethod);
					textMsg = new StringBuilder("Okay!! Here is the list of default parameter and our suggested parameters\n\n");
					showParamList();

					textMsg.append("\nWould you like to change any parameter?");
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

	private void showParamList() {
		switch (clusterMethod) {
			case IDAConst.K_MEAN_CLUSTERING:
				sessionMap.put(IDAConst.K_MEAN_CLUSTERING, new KmeansAttribute(new SimpleKMeans()));
				sessionUtil.setSessionMap(sessionMap);
				showKmeansParamList();
		}
	}

	private void getnsetNewParamValue() throws Exception {
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
				if (multiParmaValue != null) {
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
				}
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
		textMsg.append("Number of Clusters(N) = " + kmeansAttribute.getNumberOfCluster() + "\n");
		textMsg.append("Intitialize Method(P) = " + kmeansAttribute.getIntitializeMethod() + "\n");
		textMsg.append("Max No. of iterations(I) = " + kmeansAttribute.getMaxIterations() + "\n");
		textMsg.append("Replace missing values(M) = " + kmeansAttribute.getReplaceMissingValues() + "\n");
		textMsg.append("No. of execution slots(E) = " + kmeansAttribute.getNumOfExecutionSlots() + "\n");
		textMsg.append("Random number seed(S) = " + kmeansAttribute.getRandomNumberSeed() + "\n");
	}

	private int getNumericValue(String paramValue) {
		return Integer.parseInt(paramValue.split(":")[1].split("\\.")[0].trim());

	}
}
