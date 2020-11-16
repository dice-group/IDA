package org.dice.ida.util;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.dice.ida.constant.IDAConst;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Utility Class containing RDF query functions.
 *
 * @author Nandeesh
 */
@Component
public class RDFUtil {
	private Model model;
	private RDFConnectionFuseki conn = null;
	private static final String dbHost = System.getenv("FUSEKI_URL");

	/**
	 * @param queryString the SPARQL query to be executed on the RDF dataset
	 * @return It takes query string as its parameter and returns the result set after executing the query.
	 */
	private ResultSet getResultFromQuery(String queryString) {
		QueryExecution queryExecution;
		ResultSet resultSet;
		Query query = QueryFactory.create(queryString);

		/*
		 * No need to create a model from file or make database connection if the query is being run on already existing model. ( multiple queries are run on same model from getData function.)
		 */
		if (model == null) {
			/*
			 *	Create a fuseki model from the file and run the query on that model for test cases or if docker is not set up.
			 */
			// TODO: Uncomment the below logic once fuseki server works on docker
//			if (dbHost == null || dbHost.isEmpty() || dbHost.isBlank()) {
				try {
					model = ModelFactory.createDefaultModel();
					String path = Objects.requireNonNull(getClass().getClassLoader().getResource("visualization_model/ida_viz_model.ttl")).getFile();
					model.read(path);
					queryExecution = QueryExecutionFactory.create(query, model);
				} catch (NullPointerException ex) {
					return null;
				}
			/*} else {
				try {
					RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create().destination(dbHost + "ida_viz");
					conn = (RDFConnectionFuseki) builder.build();
					queryExecution = conn.query(query);
				} catch (Exception ex) {
					return null;
				} finally {
					conn.close();
				}
			}*/
		} else {
			queryExecution = QueryExecutionFactory.create(query, model);
		}
		if (queryExecution != null) {
			try {
				resultSet = ResultSetFactory.copyResults(queryExecution.execSelect());
				queryExecution.close();
				return resultSet;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
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
		QuerySolution resource;
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?label ?paramLabel ?paramType ?transformationLabel ?transformationTargetType " +
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
				"  ?Param rdfs:label ?paramLabel ." +
				"  ?repType rdfs:label ?paramType ." +
				"  OPTIONAL {" +
				"    ?IParam ivoop:hasTransformation ?transformation ." +
				"    ?transformation ivoop:hasTargetRepType ?targetType ;" +
				"                    ivoop:hasTransformationType ?transformationType ." +
				"    ?targetType rdfs:label ?transformationTargetType ." +
				"    ?transformationType rdfs:label ?transformationLabel" +
				"  }" +
				"}";
		ResultSet instancesResultSet = getResultFromQuery(queryString);
		if (instancesResultSet == null) {
			return instanceMap;
		}
		while (instancesResultSet.hasNext()) {
			resource = instancesResultSet.next();
			instanceParam = new HashMap<>();
			instanceLabel = resource.get("label").asLiteral().getString();
			paramType = resource.get("paramType").asLiteral().getString();
			if (IDAConst.TRANSFORMATION_LABEL.equals(paramType)) {
				instanceParam.put(IDAConst.INSTANCE_PARAM_TYPE_KEY, resource.get("transformationTargetType").asLiteral().getString());
				instanceParam.put(IDAConst.INSTANCE_PARAM_TRANS_TYPE_KEY, resource.get("transformationLabel").asLiteral().getString());
			} else {
				instanceParam.put(IDAConst.INSTANCE_PARAM_TYPE_KEY, paramType);
				instanceParam.put(IDAConst.INSTANCE_PARAM_TRANS_TYPE_KEY, paramType);
			}
			instance = instanceMap.getOrDefault(instanceLabel, new HashMap<>());
			instance.put(resource.get("paramLabel").asLiteral().getString(), instanceParam);
			instanceMap.put(instanceLabel, instance);
		}
		if (conn != null) {
			conn.close();
		}
		model = null;
		return instanceMap;
	}

	public Map<Integer, String> getAttributeList(String intent) {
		Map<Integer, String> attributeMap = new TreeMap<>();
		String queryString = IDAConst.IDA_SPARQL_PREFIX +
				"SELECT DISTINCT ?paramLabel  ?priority " +
				"WHERE { " +
				"  visualization:" + intent + " ?p ?o ;" +
				"                               ivoop:hasParam ?param . " +
				"  ?param rdfs:label ?paramLabel ." +
				"  ?param ivodp:hasPriority ?priority . " +
				"}";
		ResultSet attributeResultSet = getResultFromQuery(queryString);
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
}
