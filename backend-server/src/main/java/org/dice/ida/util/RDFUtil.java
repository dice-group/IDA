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
	private Map<String, String> paramDisplayNameMap = null;
	private Map<String, String> paramDisplayMessageMap = null;
	private Map<String, String> paramOptionalMessageMap = null;
	private Map<String, String> userHelpMessageMap = null;

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
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT DISTINCT ?label ?paramLabel ?paramType ?transformationLabel ?transformationTargetType ?dependentCol ")
				.append("WHERE {" )
				.append("  ?s a ivoc:Instance;")
				.append("     ?p ?o ;")
				.append("     rdfs:label ?label ;")
				.append("     ivoop:hasInstanceParam ?IParam .")
				.append("  {")
				.append("    SELECT ?s")
				.append("    WHERE {")
				.append("      visualization:").append(vizName).append(" ivoop:hasInstance ?s")
				.append("    }")
				.append("  }")
				.append("  ?IParam ivoop:representedParam ?Param ;")
				.append("          ivoop:hasRepType ?repType .")
				.append("  ?Param rdfs:label ?paramLabel ;")
				.append("		  ivodp:hasPriority ?priority .")
				.append("  ?repType rdfs:label ?paramType .")
				.append("  OPTIONAL {")
				.append("    ?IParam ivoop:hasTransformation ?transformation .")
				.append("    ?transformation ivoop:hasTargetRepType ?targetType ;")
				.append("                    ivoop:hasTransformationType ?transformationType .")
				.append("    ?targetType rdfs:label ?transformationTargetType .")
				.append("    ?transformationType rdfs:label ?transformationLabel")
				.append("  }")
				.append("  OPTIONAL {")
				.append("    ?IParam ivoop:isDependentOn ?dependentParam .")
				.append("    ?dependentParam rdfs:label ?dependentCol")
				.append("  }")
				.append("} ORDER BY ASC(?priority)");
		ResultSet instancesResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
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
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT DISTINCT ?paramLabel  ?priority ")
				.append("WHERE { ")
				.append("  visualization:").append(vizName).append(" ?p ?o ;")
				.append("                               ivoop:hasParam ?param . ")
				.append("  ?param rdfs:label ?paramLabel .")
				.append("  ?param ivodp:hasPriority ?priority . ")
				.append("} ")
				.append("ORDER BY ASC(?priority)");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
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
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT DISTINCT ?paramLabel  ?priority ?isoptional ")
				.append("WHERE { ")
				.append("  visualization:").append(vizName).append(" ?p ?o ;")
				.append("                               ivoop:hasParam ?param . ")
				.append("  ?param rdfs:label ?paramLabel .")
				.append("  ?param ivodp:hasPriority ?priority . ")
				.append("  ?param ivodp:isOptional ?isoptional . ")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
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
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT DISTINCT ?s  ")
				.append("WHERE { ")
				.append("?s rdf:type ivoc:Visualization ;")
				.append("rdfs:label '").append(viz).append("'@en ;")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
		QuerySolution querySolution = attributeResultSet.next();
		String vizIntent = querySolution.get("s").asNode().toString();

		return vizIntent.substring(vizIntent.lastIndexOf("/") + 1);
	}

	public Map<String, Map<String, List<String>>> getSuggestionParamters() {
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT DISTINCT ?viz ?paramLabel ?propLabel ?condLabel ")
				.append("WHERE { ")
				.append("  ?s rdf:type ivoc:Visualization ;")
				.append("     ?p ?o ;")
				.append("     rdfs:label ?viz ;")
				.append("     ivoop:hasSuggestionParamValue ?suggest .")
				.append("  ?suggest ivoop:hasVizParam  ?Param ;")
				.append("           ivoop:hasStatisticalProperty ?statProp ;")
				.append("           ivoop:hasStatPropertyCondition ?cond .")
				.append("  ?Param rdfs:label ?paramLabel .")
				.append("  ?statProp rdfs:label ?propLabel .")
				.append("  ?cond rdfs:label ?condLabel")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
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
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT DISTINCT ?viz ?desc ?link ?label ")
				.append("WHERE {")
				.append("  ?s rdf:type ivoc:Visualization ;")
				.append("     ?p ?o ;")
				.append("     rdfs:label ?viz ;")
				.append("     ivoop:hasInformation ?info .")
				.append("  ?info  dc:description  ?desc ;")
				.append("         ivoop:hasReference ?ref .")
				.append("  ?ref   ivodp:link ?link ;")
				.append("         rdfs:label ?label .")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
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
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT DISTINCT ?paramLabel ?isoptional ")
				.append("WHERE { ")
				.append("  visualization:").append(vizName).append(" ?p ?o ;")
				.append("                               ivoop:hasParam ?param . ")
				.append("  ?param rdfs:label ?paramLabel .")
				.append("  ?param ivodp:isOptional ?isoptional  ")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
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
		StringBuilder queryString = new StringBuilder("PREFIX ab: <https://www.upb.de/ida/datasets/>")
				.append(" INSERT DATA {")
				.append(" ?dsNameUri ab:names ?dsName ;")
				.append(" ab:isTest false")
				.append(" }");
		ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString(queryString.toString());
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

	public Map<String, String> getParamDisplayNames() {
		if(paramDisplayNameMap != null) {
			return paramDisplayNameMap;
		}
		paramDisplayNameMap = new HashMap<>();
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT ?paramLabel ?displayName ")
				.append("WHERE { ")
				.append("    ?s rdf:type ivoc:Parameter ; ")
				.append("       rdfs:label ?paramLabel ; ")
				.append("       ivodp:hasDisplayName ?displayName ")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			String param = querySolution.get("paramLabel").asLiteral().getString();
			String displayName = querySolution.get("displayName").asLiteral().getString();
			paramDisplayNameMap.put(param, displayName);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return paramDisplayNameMap;
	}

	public Map<String, String> getParamDisplayMessages() {
		if(paramDisplayMessageMap != null) {
			return paramDisplayMessageMap;
		}
		paramDisplayMessageMap = new HashMap<>();
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT ?paramLabel ?displayMessage ")
				.append("WHERE { ")
				.append("    ?s rdf:type ivoc:Parameter ; ")
				.append("       rdfs:label ?paramLabel ; ")
				.append("       ivodp:hasDisplayMessage ?displayMessage ")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			String param = querySolution.get("paramLabel").asLiteral().getString();
			String displayName = querySolution.get("displayMessage").asLiteral().getString();
			paramDisplayMessageMap.put(param, displayName);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return paramDisplayMessageMap;
	}

	public Map<String, String> getParamOptionalMessages() {
		if(paramOptionalMessageMap != null) {
			return paramOptionalMessageMap;
		}
		paramOptionalMessageMap = new HashMap<>();
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT ?paramLabel ?optionalMessage ")
				.append("WHERE { ")
				.append("    ?s rdf:type ivoc:Parameter ; ")
				.append("       rdfs:label ?paramLabel ; ")
				.append("       ivodp:hasOptionalMessage ?optionalMessage ")
				.append("}");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			String param = querySolution.get("paramLabel").asLiteral().getString();
			String displayName = querySolution.get("optionalMessage").asLiteral().getString();
			paramOptionalMessageMap.put(param, displayName);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return paramOptionalMessageMap;
	}

	public Map<String, String> getUserHelpMessages() {
		if(userHelpMessageMap != null) {
			return userHelpMessageMap;
		}
		userHelpMessageMap = new HashMap<>();
		StringBuilder queryString = new StringBuilder(IDAConst.IDA_SPARQL_PREFIX)
				.append("SELECT ?label ?message ")
				.append("WHERE { ")
				.append("  ?s ivodp:hasHelpMessage ?message ; ")
				.append("       rdfs:label ?label ")
				.append("} ");
		ResultSet attributeResultSet = getResultFromQuery(queryString.toString(), "ida_viz");
		if (attributeResultSet == null) {
			return null;
		}
		while (attributeResultSet.hasNext()) {
			QuerySolution querySolution = attributeResultSet.next();
			String label = querySolution.get("label").asLiteral().getString();
			String message = querySolution.get("message").asLiteral().getString();
			userHelpMessageMap.put(label, message);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return userHelpMessageMap;
	}
}
