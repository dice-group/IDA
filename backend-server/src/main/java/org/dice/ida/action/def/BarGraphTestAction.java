package org.dice.ida.action.def;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.ChatMessageResponse;
import org.dice.ida.util.ValidatorUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BarGraphTestAction implements Action {
	String xAxis,yAxis;
	String xleafType,transType;
	Model model = null;
	QueryExecution queryExecution = null;
	ResultSet resultSet;
	List<String> leafNodes;
	public BarGraphTestAction()
	{
		model = ModelFactory.createDefaultModel();
		//Path Change Needed
		String path = "C:\\Users\\APoddar\\IdeaProjects\\RDFModel\\src\\main\\resources\\ida_viz_model_latest.ttl";
		model.read(path);
		leafNodes= new ArrayList<>();
	}

	@Override
	public void performAction(Map<String, Object> paramMap, ChatMessageResponse chatMessageResponse) {

		if (ValidatorUtil.preActionValidation(chatMessageResponse)) {
			try {
				Map<String, Object> payload = chatMessageResponse.getPayload();
				String datasetName = payload.get("activeDS").toString();
				String tableName = payload.get("activeTable").toString();
				xAxis = paramMap.get(IDAConst.PARAM_XAXIS_NAME).toString();
				yAxis = paramMap.get(IDAConst.PARAM_YAXIS_NAME).toString();
				transType = paramMap.get("trans-type").toString();
				xleafType = paramMap.get("x-type").toString();

				if (ValidatorUtil.isStringEmpty(xAxis)||ValidatorUtil.isStringEmpty(xleafType))
				{
					if(ValidatorUtil.isStringEmpty(xAxis))
						SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
					else
					{
						List<String> columnNameList = new ArrayList<>();
						columnNameList.add(xAxis);
						Map<String, String> columnMap = ValidatorUtil.areParametersValid(datasetName, tableName, columnNameList);
						//TODO: changes required
						String response = generateOptions(columnMap.get(xAxis),"bar_chart");
						chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
						chatMessageResponse.setMessage(response);
					}
				}
				else
				{
					if(ValidatorUtil.isStringEmpty(yAxis)||ValidatorUtil.isStringEmpty(transType)){
						if(ValidatorUtil.isStringEmpty(yAxis))
						{
							SimpleTextAction.setSimpleTextResponse(paramMap, chatMessageResponse);
						}
						else
						{
							List<String> Instances  = getInstance(new ArrayList<>() {{
								add(gettype(xleafType));
							}
							},"bar_chart");
							if(Instances.size()==0)
							{
								paramMap.put("trans-type","Not_Required");
								transType = "Not Required";
							}
							if(Instances.size()==1)
							{
								String transform = getInstanceTransformation(Instances.get(0));
								paramMap.put("trans-type",transform.substring(transform.lastIndexOf("/")+1));
								transType= transform.substring(transform.lastIndexOf("/")+1);
							}
							if(Instances.size()>1)
							{
								String response = getTransformationOptions(Instances);
								chatMessageResponse.setUiAction(IDAConst.UAC_NRMLMSG);
								chatMessageResponse.setMessage(response);
							}
						}
					}
				}
				if(!ValidatorUtil.isStringEmpty(xAxis)&&!ValidatorUtil.isStringEmpty(xleafType)&&!ValidatorUtil.isStringEmpty(yAxis)&&!ValidatorUtil.isStringEmpty(transType))
				{
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
	public String getTransformationOptions(List<String> Instances)
	{
		StringBuilder response = new StringBuilder();
		response.append("Choose a transformation type for y axis\n");
		for (String instance :Instances)
		{
			String transformation = getInstanceTransformation(instance);
			response.append(transformation.substring(transformation.lastIndexOf("/")+1)+"\n");
		}
		return response.toString();
	}
	public String generateOptions(String xAxisType, String graphType)
	{
		StringBuilder options = new StringBuilder();
		List<String> leaf;
		options.append("Please select usage type for the xaxis date\n");
		//TODO: change to variable when data is corrected in dsmd
		leaf = getLeaf("date","bar_chart");
		for(String category:leaf)
		{
			options.append(category.substring(category.lastIndexOf("/"))+"\n");
		}
		return options.toString();
	}
	public List<String> getLeaf(String type,String graphType) {

		String firstChild = null;
		StringBuilder query1 = new StringBuilder();
		query1.append(IDAConst.prefix);
		query1.append("SELECT DISTINCT ?param ?tree ?root ?firstChild\n" +
				"WHERE {\n" +
				"  visualization:"+graphType+" ?p ?o;\n" +
				"  ivoop:hasParam ?param .\n" +
				"  ?param ivoop:hasParamTree ?tree .\n" +
				"  ?tree ivoop:hasRootNode ?root .\n" +
				"  ?root ivoop:hasChildNode ?firstChild\n" +
				" FILTER(?firstChild = <https://www.upb.de/ida/viz/data/representation_tree_node/"+type+">)\n" +
				"}\n");
		Query quer = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);

		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			// System.out.println(querySolution.toString());
			firstChild = querySolution.get("firstChild").asNode().toString();
			if (firstChild.substring(firstChild.lastIndexOf("/") + 1).equals(type)) {
				break;
			}

		}
		explore(firstChild);
		return leafNodes;

	}
	public void explore(String node) {
		ResultSet resultSet = getChild(node);
		while (resultSet.hasNext()) {
			String temp = resultSet.next().get("o").asNode().toString();
			if (isLeaf(temp))
				leafNodes.add(temp);
			else
				explore(temp);
		}
	}
	public boolean isLeaf(String node) {
		ResultSet resultSet = getChild(node);
		if (!resultSet.hasNext()) {
			return true;
		}
		return false;
	}

	public ResultSet getChild(String node) {
		// <https://www.upb.de/ida/viz/data/instance_param/x_axis_bins>
		//Question : is the nominal, date, numeruc unique for each visualization
		StringBuilder query = new StringBuilder();
		query.append(IDAConst.prefix);
		query.append("SELECT ?p ?o\n" +
				"WHERE { \n" +
				"<" + node + ">" + " ivoop:hasChildNode ?o;\n" +
				"}\n");
		Query quer = QueryFactory.create(query.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);
		ResultSet resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		return resultSet;
	}
	public  String gettype(String node)
	{
		StringBuilder query1 = new StringBuilder();
		query1.append(IDAConst.prefix);
		query1.append("SELECT DISTINCT ?s ?p ?o\n" +
				"WHERE {\n" +
				"?s a ivoc:RepresentationTreeNode; ivoop:hasRepType ?o; \n"+
				" FILTER(?s = <https://www.upb.de/ida/viz/data/representation_tree_node/"+ node + ">)\n" +
				"}\n");
		Query quer = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);
		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());

		return resultSet.next().get("o").asNode().toString();
	}
	public List<String> getInstance(List<String> type,String graphType)
	{
		List<String> tempInstance = new ArrayList<String>();
		StringBuilder query1 = new StringBuilder(), query2 = new StringBuilder();
		String priorParam = getPriorParam(graphType);
		query1.append(IDAConst.prefix);
		query1.append("SELECT DISTINCT ?s ?IParam ?type\n" +
				"WHERE {\n" +
				"?s a ivoc:Instance; ?p ?o; \n"+
				"ivoop:hasInstanceParam ?IParam.  \n"+
				"?IParam ivoop:representedParam ?Param.  \n"+
				"?IParam ivoop:hasRepType ?type  \n"+
				" FILTER(?Param = <"+priorParam+">)\n" +
				"}\n");
		Query quer = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);

		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		while(resultSet.hasNext())
		{
			QuerySolution next = resultSet.next();
			if(type.contains(next.get("type").asNode().toString()) )
			{
				tempInstance.add(next.get("s").asNode().toString());
			}

		}
		return tempInstance;
	}
	public String getPriorParam(String graphType)
	{
		StringBuilder query = new StringBuilder();
		query.append(IDAConst.prefix);
		query.append("SELECT DISTINCT ?s ?param ?o \n" +
				"WHERE {\n" +
				"visualization:"+graphType+" ivoop:hasParam ?param.\n"+
				"?param ivodp:hasPriority ?o. \n"+

				" FILTER(?o = 1)\n" +
				"}\n");
		Query quer = QueryFactory.create(query.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);
		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		return resultSet.next().get("param").asNode().toString();
	}
	public String getInstanceTransformation(String Instance)
	{
		StringBuilder query1 = new StringBuilder();
		query1.append(IDAConst.prefix);
		query1.append("SELECT DISTINCT ?s ?Param ?IParam ?type ?Transtype\n" +
				"WHERE {\n" +
				"<"+ Instance + "> ?p ?o; \n"+
				"ivoop:hasInstanceParam ?IParam.  \n"+
				"?IParam ivoop:hasTransformation ?Transform.  \n"+
				"?Transform ivoop:hasTransformationType ?Transtype  \n"+
				"}\n");
		Query quer1 = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer1, model);
		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		return resultSet.next().get("Transtype").asNode().toString() ;

	}
}
