package org.dice.ida.action.def;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.util.RDFUtil;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BarGraphTestAction implements Action {
	String xAxis, yAxis;
	String xleafType, transType;
	Model model = null;
	QueryExecution queryExecution = null;
	ResultSet resultSet;
	List<String> leafNodes;
	Map<String, Map<String, Map<String, String>>> instanceMap;
	Map<String, String> columnMap;

	public BarGraphTestAction() {
		model = ModelFactory.createDefaultModel();
		//Path Change Needed
		String path = "C:\\Users\\APoddar\\IdeaProjects\\RDFModel\\src\\main\\resources\\ida_viz_model_latest.ttl";
		model.read(path);
		leafNodes = new ArrayList<>();
	}

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {

		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			try {
				Map<String, Object> payload = chatMessageResponse.getPayload();
				instanceMap = new RDFUtil().getInstances("bar_chart");
				String datasetName = payload.get("activeDS").toString();
				String tableName = payload.get("activeTable").toString();
				xAxis = paramMap.get(IDAConst.PARAM_XAXIS_NAME).toString();
				yAxis = paramMap.get(IDAConst.PARAM_YAXIS_NAME).toString();
				transType = paramMap.get("trans-type").toString();
				xleafType = paramMap.get("x-type").toString();

				if (ValidatorUtil.isStringEmpty(xAxis) || ValidatorUtil.isStringEmpty(xleafType)) {
					if (ValidatorUtil.isStringEmpty(xAxis))
						SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
					else {
						if (instanceMap.size() == 1) {
							String type = instanceMap.entrySet().iterator().next().getValue().get("X-Axis").get("type");
							paramMap.put("x-type", type);
							xleafType = type;
							SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
						} else {
							String response = getXAxisTypeOptions();
							chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
							chatMessageResponse.setMessage(response);
						}

					}
				} else {
					List<String> columnNameList = new ArrayList<>();
					columnNameList.add(xAxis);
					columnMap = ValidatorUtil.areParametersValid(datasetName, tableName, columnNameList);

					if (ValidatorUtil.isStringEmpty(yAxis) || ValidatorUtil.isStringEmpty(transType)) {
						if (ValidatorUtil.isStringEmpty(yAxis)) {
							SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
						} else {
							String yType = columnMap.get(yAxis);
							instanceMap = instanceMap.entrySet().stream().filter(x -> xleafType.equalsIgnoreCase(x.getValue().
									get("X-Axis").get("type")) && (yType.equalsIgnoreCase(x.getValue().get("Y-Axis").get("type")) || x.getValue().
									get("Y-Axis").get("type").equalsIgnoreCase("Not Required")))
									.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
							if (instanceMap.size() == 0) {
								//TODO: Ask for a different y
							}
							if (instanceMap.size() == 1) {
								String ytranstype = instanceMap.entrySet().iterator().next().getValue().get("Y-Axis").get("trans-type");
								paramMap.put("trans-type", ytranstype);
								transType = ytranstype;

							}
							if (instanceMap.size() > 1) {
								String response = getTransformationOptions();
								chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
								chatMessageResponse.setMessage(response);
							}
						}
					}
				}
				if (!ValidatorUtil.isStringEmpty(xAxis) && !ValidatorUtil.isStringEmpty(xleafType) && !ValidatorUtil.isStringEmpty(yAxis) && !ValidatorUtil.isStringEmpty(transType)) {
					//TODO: render the bar graph
					chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
					chatMessageResponse.setMessage("Render the bar graph");
				}
			} catch (Exception e) {
				e.printStackTrace();
				chatMessageResponse.setMessage(IDAConst.BOT_SOMETHING_WRONG);
				chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
			}
		}
	}

	public String getTransformationOptions() {
		StringBuilder response = new StringBuilder();
		response.append("Choose a transformation type for y axis\n");
		for (String instance : instanceMap.keySet())
			response.append(instanceMap.get(instance).get("Y-Axis").get("trans_type") + "\n");

		return response.toString();
	}

	public String getXAxisTypeOptions() {
		StringBuilder response = new StringBuilder();
		HashSet<String> options = new HashSet<>();
		response.append("Select a leaf type for x-axis\n");
		for (String instanceName : instanceMap.keySet()) {
			Map<String, Map<String, String>> instance = instanceMap.get(instanceName);
			options.add(instance.get("X-Axis").get("type"));
		}
		for (String option : options)
			response.append(option + "\n");

		return response.toString();
	}
}

