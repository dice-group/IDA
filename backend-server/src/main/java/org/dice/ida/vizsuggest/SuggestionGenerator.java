package org.dice.ida.vizsuggest;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.dice.ida.model.AttributeSummary;
import org.dice.ida.model.DataSummary;
import org.dice.ida.util.MetaFileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SuggestionGenerator {

	static String prefix = "prefix dc: <http://purl.org/dc/elements/1.1/>\n" +
			"prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
			"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"prefix xml: <http://www.w3.org/XML/1998/namespace>\n" +
			"prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
			"prefix ivoc: <https://www.upb.de/ida/viz/ontology/class/> \n" +
			"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
			"prefix ivodp: <https://www.upb.de/ida/viz/ontology/data-property/> \n" +
			"prefix ivoop: <https://www.upb.de/ida/viz/ontology/object-property/> \n" +
			"prefix instance: <https://www.upb.de/ida/viz/data/instance/> \n" +
			"prefix parameter: <https://www.upb.de/ida/viz/data/parameter/> \n" +
			"prefix reference: <https://www.upb.de/ida/viz/data/reference/> \n" +
			"prefix information: <https://www.upb.de/ida/viz/data/information/> \n" +
			"prefix visualization: <https://www.upb.de/ida/viz/data/visualization/> \n" +
			"prefix instance_param: <https://www.upb.de/ida/viz/data/instance_param/> \n" +
			"prefix representation_tree: <https://www.upb.de/ida/viz/data/representation_tree/> \n" +
			"prefix representational_type: <https://www.upb.de/ida/viz/data/representational_type/> \n" +
			"prefix representation_tree_node: <https://www.upb.de/ida/viz/data/representation_tree_node/>\n";
	Model model;
	QueryExecution queryExecution = null;
	ResultSet resultSet, resultSet1;
	List<String> leafNodes;
	Map<String, List<String>> typeMap = new HashMap<>();
	Map<String, List<Map<String, List<String>>>> suggestionMap = new HashMap<>();
	DataSummary dataSummary;

	public SuggestionGenerator() {
		model = ModelFactory.createDefaultModel();
		//Path Change Needed
		String path = "C:\\Users\\APoddar\\IdeaProjects\\RDFModel\\src\\main\\resources\\ida_rdf_model.ttl";
		model.read(path);
		leafNodes = new ArrayList<>();
		try {
			//Change Needed
			dataSummary = new MetaFileReader().createDataSummary("covid19", "Case_Time_Series.tfd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getSuggestion(String paramName) {
		//Change Needed
		AttributeSummary paramSummary = dataSummary.getAttributeSummaryList().stream().filter(x -> x.getName().substring(1).equals(paramName)).collect(toList()).get(0);
		String paramType = paramSummary.getType();
		List<String> LeafList, Instances, TypesList = new ArrayList<>();
		LeafList = getLeaf(paramType.toLowerCase());
		for (String leaf : LeafList) {
			TypesList.add(gettype(leaf));
		}
		Instances = getInstance(TypesList);
		for (String instance : Instances) {
			createTypeMap(instance);
		}
		createSuggestionMap(paramSummary);
	}

	public void createSuggestionMap(AttributeSummary paramSummary) {
		for (String key : typeMap.keySet()) {
			if (key.equals("Unique")) {
				if (isUnique(paramSummary))
					addToMap(key, typeMap.get(key));
			} else
				addToMap(key, typeMap.get(key));

		}

	}

	public boolean isUnique(AttributeSummary paramSummary) {
		return paramSummary.getUniqueValuesProbability() == 100;
	}

	public void addToMap(String param1, List<String> param2) {
		List<Map<String, List<String>>> templist = new ArrayList<>();
		Map<String, List<String>> temp;
		List<String> attrList;
		if (param1.equals("Unique")) {
			temp = new HashMap<>();
			attrList = getlistofparam(param2.get(0));
			temp.put("Value of", attrList);
			templist.add(temp);
		} else {

			for (String trans : param2) {
				temp = new HashMap<>();
				String[] translist = trans.split("/");
				attrList = getlistofparam(translist[2]);
				temp.put(translist[1], attrList);
				templist.add(temp);
			}

		}
		suggestionMap.put(param1, templist);
	}

	public List<String> getlistofparam(String attrtype) {
		List<String> temp = new ArrayList<>();
		if (attrtype.equals("NotRequired"))
			temp.add("Self");
		else {
			List<AttributeSummary> attributeSummary;
			attributeSummary = dataSummary.getAttributeSummaryList().stream().filter(x -> x.getType().equals(attrtype.substring(0, 3))).collect(toList());
			for (AttributeSummary attr : attributeSummary) {
				temp.add(attr.getName());
			}
		}

		return temp;
	}

	public void createTypeMap(String Instance) {
		StringBuilder query1 = new StringBuilder(), query2 = new StringBuilder();
		query1.append(prefix);
		query2.append(prefix);
		String xaxis = null, yaxis = null;
		String priorParam = getPriorParam();
		query1.append("SELECT DISTINCT ?s ?Param ?IParam ?type\n" +
				"WHERE {\n" +
				"<" + Instance + "> ?p ?o; \n" +
				"ivoop:hasInstanceParam ?IParam.  \n" +
				"?IParam ivoop:representedParam ?Param.  \n" +
				"?IParam ivoop:hasRepType ?type  \n" +
				"}\n");
		Query quer1 = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer1, model);
		resultSet1 = ResultSetFactory.copyResults(queryExecution.execSelect());
		while (resultSet1.hasNext()) {
			QuerySolution querySolution = resultSet1.next();
			if (querySolution.get("Param").asNode().toString().equals(priorParam))
				xaxis = querySolution.get("type").asNode().toString();
			else
				yaxis = querySolution.get("type").asNode().toString();
		}
		if (yaxis.equals("https://www.upb.de/ida/viz/data/representational_type/Transformation")) {

			query2.append("SELECT DISTINCT ?s ?trans ?transtype ?transtarget ?transby \n" +
					"WHERE {\n" +
					"<" + Instance + "> ?p ?o; \n" +
					"ivoop:hasInstanceParam ?IParam.  \n" +
					"?IParam ivoop:hasTransformation ?trans.  \n" +
					"?trans ivoop:hasTransformationType ?transtype.  \n" +
					"?trans ivoop:hasTargetRepType ?transtarget.  \n" +
					"?trans ivoop:hasTransformationTarget ?transby.  \n" +
					"}\n");
			Query quer3 = QueryFactory.create(query2.toString());
			queryExecution = QueryExecutionFactory.create(quer3, model);

			resultSet1 = ResultSetFactory.copyResults(queryExecution.execSelect());
			QuerySolution temp = resultSet1.next();
			String transtype = temp.get("transtype").asNode().toString().substring(temp.get("transtype").asNode().toString().lastIndexOf("/"));
			String transtarget = temp.get("transtarget").asNode().toString().substring(temp.get("transtarget").asNode().toString().lastIndexOf("/"));
			String transby = temp.get("transby").asNode().toString().substring(temp.get("transby").asNode().toString().lastIndexOf("/"));
			putPossibleGraph(xaxis.substring(xaxis.lastIndexOf("/") + 1), "transformation" + transtype + transtarget + transby);
		} else {
			putPossibleGraph(xaxis.substring(xaxis.lastIndexOf("/") + 1), yaxis.substring(yaxis.lastIndexOf("/") + 1));
		}
	}

	public void putPossibleGraph(String key, String value) {
		List<String> temp = new ArrayList<>();
		if (typeMap.containsKey(key)) {
			temp = typeMap.get(key);
		}
		temp.add(value);
		typeMap.put(key, temp);
	}

	public List<String> getInstance(List<String> type) {
		List<String> tempInstance = new ArrayList<>();
		StringBuilder query1 = new StringBuilder();
		String priorParam = getPriorParam();
		query1.append(prefix);
		query1.append("SELECT DISTINCT ?s ?IParam ?type\n" +
				"WHERE {\n" +
				"?s a ivoc:Instance; ?p ?o; \n" +
				"ivoop:hasInstanceParam ?IParam.  \n" +
				"?IParam ivoop:representedParam ?Param.  \n" +
				"?IParam ivoop:hasRepType ?type  \n" +
				" FILTER(?Param = <" + priorParam + ">)\n" +
				"}\n");
		Query quer = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);

		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		while (resultSet.hasNext()) {
			QuerySolution next = resultSet.next();
			if (type.contains(next.get("type").asNode().toString())) {
				tempInstance.add(next.get("s").asNode().toString());
			}

		}
		return tempInstance;
	}

	public String getPriorParam() {
		StringBuilder query = new StringBuilder();
		query.append(prefix);
		query.append("SELECT DISTINCT ?s ?o \n" +
				"WHERE {\n" +
				"?s a ivoc:Parameter; ivodp:hasPriority ?o; \n" +
				" FILTER(?o = 1)\n" +
				"}\n");
		Query quer = QueryFactory.create(query.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);
		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		return resultSet.next().get("s").asNode().toString();
	}

	public List<String> getLeaf(String type) {

		String firstChild = null;
		StringBuilder query1 = new StringBuilder();
		query1.append(prefix);
		query1.append("SELECT DISTINCT ?param ?tree ?root ?firstChild\n" +
				"WHERE {\n" +
				"  ?s a ivoc:Visualization; ?p ?o;\n" +
				"  ivoop:hasParam ?param .\n" +
				"  ?param ivoop:hasParamTree ?tree .\n" +
				"  ?tree ivoop:hasRootNode ?root .\n" +
				"  ?root ivoop:hasChildNode ?firstChild\n" +
				" FILTER(?firstChild = <https://www.upb.de/ida/viz/data/representation_tree_node/" + type + ">)\n" +
				"}\n");
		Query quer = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);

		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		while (resultSet.hasNext()) {
			firstChild = resultSet.next().get("firstChild").asNode().toString();
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
		return !resultSet.hasNext();
	}

	public ResultSet getChild(String node) {
		// <https://www.upb.de/ida/viz/data/instance_param/x_axis_bins>
		StringBuilder query = new StringBuilder();
		query.append(prefix);
		query.append("SELECT ?p ?o\n" +
				"WHERE { \n" +
				"<" + node + ">" + " ivoop:hasChildNode ?o;\n" +
				"}\n");
		Query quer = QueryFactory.create(query.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);
		return ResultSetFactory.copyResults(queryExecution.execSelect());
	}

	public String gettype(String node) {
		StringBuilder query1 = new StringBuilder();
		query1.append(prefix);
		query1.append("SELECT DISTINCT ?s ?p ?o\n" +
				"WHERE {\n" +
				"?s a ivoc:RepresentationTreeNode; ivoop:hasRepType ?o; \n" +
				" FILTER(?s = <" + node + ">)\n" +
				"}\n");
		Query quer = QueryFactory.create(query1.toString());
		queryExecution = QueryExecutionFactory.create(quer, model);
		resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
		return resultSet.next().get("o").asNode().toString();
	}

}
