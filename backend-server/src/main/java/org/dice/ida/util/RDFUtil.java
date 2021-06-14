package org.dice.ida.util;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.update.UpdateExecutionFactory;
import org.dice.ida.constant.IDAConst;
import org.dice.ida.model.suggestion.VisualizationInfo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Utility Class containing RDF query functions.
 *
 * @author Nandeesh Patel, Sourabh Poddar
 */
@Component
public class RDFUtil {
	private static final String dbHost = System.getenv("FUSEKI_URL");
	private Model model;
	private RDFConnectionFuseki conn = null;

	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	public ResultSet getResultFromQuery(String queryString, String dsName) {
		QueryExecution queryExecution = null;
		ResultSet resultSet = null;
		Query query = QueryFactory.create(queryString);

		/*
		 * No need to create a model from file or make database connection if the query is being run on already existing model. ( multiple queries are run on same model from getData function.)
		 */
		if (model == null) {
			/*
			 *	Create a fuseki model from the file and run the query on that model for test cases or if docker is not set up.
			 */
			if (dbHost == null || dbHost.isEmpty() || dbHost.isBlank()) {
				try {
					String resourceName = dsName.equals("ida_viz") ? "visualization_model/ida_viz_model.ttl" : "visualization_model/ida_ds_model.ttl";

					model = ModelFactory.createDefaultModel();
					String path = Objects.requireNonNull(getClass().getClassLoader().getResource(resourceName)).getFile();
					model.read(path);
					queryExecution = QueryExecutionFactory.create(query, model);
				} catch (NullPointerException ex) {
					resultSet = null;
				}
			} else {
				try {
					RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbHost + dsName);
					conn = (RDFConnectionFuseki) builder.build();
					queryExecution = conn.query(query);
				} catch (Exception ex) {
					resultSet = null;
				} finally {
					conn.close();
				}
			}
		} else {
			queryExecution = QueryExecutionFactory.create(query, model);
		}

		if (queryExecution != null) {
			try {
				resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
			} catch (Exception e) {
				resultSet = null;
			} finally {
				if (conn != null) {
					conn.close();
				}
				queryExecution.close();
			}
		}
		return resultSet;
	}

	/**
	 * Method to get the list of instances, their parameters, types and transformations of a given visualization
	 *
	 * @param vizName - name of the visualization
	 * @return Map of instance labels and its details
	 */
	public Map<String, Map<String, Map<String, String>>> getInstances(String vizName) {
		Map<String, String> instanceParam;
		Map<String, Map<String, String>> instance;
		Map<String, Map<String, Map<String, String>>> instanceMap = new HashMap<>();
		String instanceLabel;
		String paramType;
		String dependentParam;
		String paramLabel;
		QuerySolution resource;
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?label ?paramLabel ?paramType ?transformationLabel ?transformationTargetType ?dependentCol " +
				"WHERE {" +
				"  ?s a ivoc:Instance;" +
				"     ?p ?o ;" +
				"     rdfs:label ?label ;" +
				"     ivoop:hasInstanceParam ?IParam ." +
				"  {" +
				"    SELECT ?s" +
				"    WHERE {" +
				"      visualization:" + vizName + " ivoop:hasInstance ?s" +
				"    }" +
				"  }" +
				"  ?IParam ivoop:representedParam ?Param ;" +
				"          ivoop:hasRepType ?repType ." +
				"  ?Param rdfs:label ?paramLabel ;" +
				"		  ivodp:hasPriority ?priority ." +
				"  ?repType rdfs:label ?paramType ." +
				"  OPTIONAL {" +
				"    ?IParam ivoop:hasTransformation ?transformation ." +
				"    ?transformation ivoop:hasTargetRepType ?targetType ;" +
				"                    ivoop:hasTransformationType ?transformationType ." +
				"    ?targetType rdfs:label ?transformationTargetType ." +
				"    ?transformationType rdfs:label ?transformationLabel" +
				"  }" +
				"  OPTIONAL {" +
				"    ?IParam ivoop:isDependentOn ?dependentParam ." +
				"    ?dependentParam rdfs:label ?dependentCol" +
				"  }" +
				"} ORDER BY ASC(?priority)";
		ResultSet instancesResultSet = getResultFromQuery(queryString, "ida_viz");
		if (instancesResultSet == null) {
			return instanceMap;
		}
		while (instancesResultSet.hasNext()) {
			resource = instancesResultSet.next();
			instanceParam = new TreeMap<>();
			instanceLabel = resource.get("label").asLiteral().getString();
			paramType = resource.get("paramType").asLiteral().getString();
			dependentParam = resource.contains("dependentCol") ? resource.get("dependentCol").asLiteral().getString() : "";
			if (IDAConst.TRANSFORMATION_LABEL.equals(paramType)) {
				instanceParam.put(IDAConst.INSTANCE_PARAM_TYPE_KEY, resource.get("transformationTargetType").asLiteral().getString());
				instanceParam.put(IDAConst.INSTANCE_PARAM_TRANS_TYPE_KEY, resource.get("transformationLabel").asLiteral().getString());
			} else {
				instanceParam.put(IDAConst.INSTANCE_PARAM_TYPE_KEY, paramType);
				instanceParam.put(IDAConst.INSTANCE_PARAM_TRANS_TYPE_KEY, paramType);
			}
			instanceParam.put(IDAConst.INSTANCE_PARAM_DEPENDENT_KEY, dependentParam);
			instance = instanceMap.getOrDefault(instanceLabel, new TreeMap<>());
			paramLabel = resource.get("paramLabel").asLiteral().getString();
			if (instance.containsKey(paramLabel)) {
				instance.get(paramLabel).put(IDAConst.INSTANCE_PARAM_DEPENDENT_KEY, instance.get(paramLabel).get(IDAConst.INSTANCE_PARAM_DEPENDENT_KEY) + "," + dependentParam);
			} else {
				instance.put(resource.get("paramLabel").asLiteral().getString(), instanceParam);
			}
			instanceMap.put(instanceLabel, instance);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return instanceMap;
	}

	/**
	 * Method to fetch the list of parameters for a given visualization from RDF model
	 *
	 * @param vizName - name of the visualization
	 * @return - Map of attributes/parameters for the given visualization
	 */
	public Map<Integer, String> getAttributeList(String vizName) {
		Map<Integer, String> attributeMap = new TreeMap<>();
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?paramLabel  ?priority " +
				"WHERE { " +
				"  visualization:" + vizName + " ?p ?o ;" +
				"                               ivoop:hasParam ?param . " +
				"  ?param rdfs:label ?paramLabel ." +
				"  ?param ivodp:hasPriority ?priority . " +
				"} " +
				"ORDER BY ASC(?priority)";
		ResultSet attributeResultSet = getResultFromQuery(queryString, "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			String param = querySolution.get("paramLabel").asLiteral().getString();
			int priority = (int) querySolution.get("priority").asNode().getLiteralValue();
			attributeMap.put(priority, param);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return attributeMap;
	}

	public Map<Integer, String> getSuggestionAttributeList(String vizName) {
		Map<Integer, String> attributeMap = new TreeMap<>();
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?paramLabel  ?priority ?isoptional " +
				"WHERE { " +
				"  visualization:" + vizName + " ?p ?o ;" +
				"                               ivoop:hasParam ?param . " +
				"  ?param rdfs:label ?paramLabel ." +
				"  ?param ivodp:hasPriority ?priority . " +
				"  ?param ivodp:isOptional ?isoptional . " +
				"}";
		ResultSet attributeResultSet = getResultFromQuery(queryString, "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			boolean optional = (boolean) querySolution.get("isoptional").asNode().getLiteralValue();
			if (!optional) {
				String param = querySolution.get("paramLabel").asLiteral().getString();
				int priority = (int) querySolution.get("priority").asNode().getLiteralValue();
				attributeMap.put(priority, param);
			}
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return attributeMap;
	}

	public String getVizIntent(String viz) {
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?s  " +
				"WHERE { " +
				"?s rdf:type ivoc:Visualization ;" +
				"rdfs:label '" + viz + "'@en ;" +
				"}";
		ResultSet attributeResultSet = getResultFromQuery(queryString, "ida_viz");
		QuerySolution querySolution = attributeResultSet.next();
		String vizIntent = querySolution.get("s").asNode().toString();

		return vizIntent.substring(vizIntent.lastIndexOf("/") + 1);
	}

	public Map<String, Map<String, List<String>>> getSuggestionParamters() {
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?viz ?paramLabel ?propLabel ?condLabel " +
				"WHERE { " +
				"  ?s rdf:type ivoc:Visualization ;" +
				"     ?p ?o ;" +
				"     rdfs:label ?viz ;" +
				"     ivoop:hasSuggestionParamValue ?suggest ." +
				"  ?suggest ivoop:hasVizParam  ?Param ;" +
				"           ivoop:hasStatisticalProperty ?statProp ;" +
				"           ivoop:hasStatPropertyCondition ?cond ." +
				"  ?Param rdfs:label ?paramLabel ." +
				"  ?statProp rdfs:label ?propLabel ." +
				"  ?cond rdfs:label ?condLabel" +
				"}";
		ResultSet attributeResultSet = getResultFromQuery(queryString, "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		Map<String, Map<String, List<String>>> suggestionProp = new HashMap<>();
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			String Viz = querySolution.get("viz").asLiteral().getString();
			Map<String, List<String>> vizData = new HashMap<>();
			if (suggestionProp.containsKey(Viz))
				vizData = suggestionProp.get(Viz);
			String paramLabel = querySolution.get("paramLabel").asLiteral().getString();
			String propLabel = querySolution.get("propLabel").asLiteral().getString();
			String condLabel = querySolution.get("condLabel").asLiteral().getString();

			vizData.put(paramLabel, new ArrayList<>() {{
				add(propLabel);
				add(condLabel);
			}});
			suggestionProp.put(Viz, vizData);
		}
		return suggestionProp;
	}

	public Map<String, VisualizationInfo> getVisualizationInfo() {
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?viz ?desc ?link ?label " +
				"WHERE {" +
				"  ?s rdf:type ivoc:Visualization ;" +
				"     ?p ?o ;" +
				"     rdfs:label ?viz ;" +
				"     ivoop:hasInformation ?info ." +
				"  ?info  dc:description  ?desc ;" +
				"         ivoop:hasReference ?ref ." +
				"  ?ref   ivodp:link ?link ;" +
				"         rdfs:label ?label ." +
				"}";
		ResultSet attributeResultSet = getResultFromQuery(queryString, "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		Map<String, VisualizationInfo> vizInfoMap = new HashMap<>();
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			VisualizationInfo visualizationInfo = new VisualizationInfo();
			visualizationInfo.setDescription(querySolution.get("desc").asLiteral().getString());
			visualizationInfo.setLink(querySolution.get("link").asLiteral().getString());
			visualizationInfo.setLinkLabel(querySolution.get("label").asLiteral().getString());
			vizInfoMap.put(querySolution.get("viz").asLiteral().getString(), visualizationInfo);
		}
		return vizInfoMap;
	}

	public Map<String, Boolean> getAttributeOptionalMap(String vizName) {
		Map<String, Boolean> attributeOptionalMap = new TreeMap<>();
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?paramLabel ?isoptional " +
				"WHERE { " +
				"  visualization:" + vizName + " ?p ?o ;" +
				"                               ivoop:hasParam ?param . " +
				"  ?param rdfs:label ?paramLabel ." +
				"  ?param ivodp:isOptional ?isoptional  " +
				"}";
		ResultSet attributeResultSet = getResultFromQuery(queryString, "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			String param = querySolution.get("paramLabel").asLiteral().getString();
			boolean optional = (boolean) querySolution.get("isoptional").asNode().getLiteralValue();
			attributeOptionalMap.put(param, optional);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return attributeOptionalMap;
	}

	public String addDatasetName(String dsName) {
		String queryString1 = "PREFIX ab: <https://www.upb.de/ida/datasets/>" +
				" INSERT DATA {" +
				" ?dsNameUri ab:names ?dsName ;" +
				" ab:isTest false" +
				" }";
		ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString(queryString1);
		parameterizedSparqlString.setLiteral("dsName", dsName);
		parameterizedSparqlString.setIri("dsNameUri", "https://www.upb.de/ida/datasets/" + dsName);

		if (! (dbHost == null || dbHost.isEmpty() || dbHost.isBlank())) {
			try {
				UpdateExecutionFactory.createRemote(parameterizedSparqlString.asUpdate(), dbHost + "ida_ds").execute();
				return "true";
			} catch (Exception ex) {
				return "false";
			}
		} else {
			return "false";
		}
	}
}
