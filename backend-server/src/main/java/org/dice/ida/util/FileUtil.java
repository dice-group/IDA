package org.dice.ida.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.dice.ida.constant.IDAConst;

/**
 * Class to expose util methods for File based operations in IDA
 *
 * @author Nikit
 */
@Component
@Scope("singleton")
public class FileUtil {
	private final ArrayList<String> datasetsList = new ArrayList<>();

	/**
	 * A simple method which reads available datasets and return them
	 *
	 * @return - An ArrayList object containing names of available datasets
	 */
	public ArrayList<String> getListOfDatasets() {
		try ( RDFConnection conn = RDFConnectionFactory.connect("http://localhost:3030/ds") ) {
			QueryExecution qExec = conn.query("SELECT ?object WHERE { ?subject <https://www.upb.de/ida/datasets/names> ?object }") ;
			ResultSet results = qExec.execSelect();
			while (results.hasNext()) {
				datasetsList.add(results.next().get("?object").toString());
			}
			conn.close();
			qExec.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return datasetsList;
	}

	/**
	 * Method to generate a collection of rows from a csv file in List<Map<String,
	 * String>> format
	 *
	 * @param input - csv file
	 * @return - collection of rows in List<Map<String, String>> format
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> convertToMap(File input) throws IOException {

		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();
		List<Map<String, String>> resMapList = new ArrayList<>();
		for (Object entry : readAll) {
			resMapList.add((Map<String, String>) entry);
		}
		return resMapList;
	}

	/**
	 * Method to generate a dataset map for a given dataset
	 *
	 * @param keyword - name of dataset
	 * @return - dataset map
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public ArrayList<Map> getDatasetContent(String keyword) throws IOException {
		ArrayList<Map> resMap = new ArrayList<>();
		Map<String, Object> datasetMap;

		File dir = new File(fetchSysFilePath(keyword));
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				datasetMap = new HashMap<>();
				// Do something with child
				if (child.getName().matches(IDAConst.CSV_FILE_PATTERN)) {
					datasetMap.put("name", child.getName());
					datasetMap.put("data", new DataUtil().getDataSet(keyword, child.getName()));
					resMap.add(datasetMap);
				}
			}
		}
		return resMap;
	}

	/**
	 * Method to fetch the metadata json for the given dataset
	 *
	 * @param keyword - name of dataset
	 * @return - metadata json object
	 * @throws JsonProcessingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public ObjectNode getDatasetMetaData(String dsName) throws JsonProcessingException, FileNotFoundException, IOException {
		ObjectNode resObj = null;
		String path = fetchSysFilePath(dsName.toLowerCase());
		//TODO: Change the logic to use .exists() method instead of this
		if (path != null) {
			File dir = new File(path);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					// Do something with child
					if (child.getName().matches(IDAConst.DSMD_FILE_PATTERN)) {
						ObjectMapper mapper = new ObjectMapper();
						ObjectReader reader = mapper.reader();
						resObj = (ObjectNode) reader.readTree(new FileInputStream(child));
						break;
					}
				}
			}
		}
		return resObj;
	}

	/**
	 * Method to check if given dataset exists
	 *
	 * @param dsName - name of dataset
	 * @return - if dataset exists
	 */
	public boolean datasetExists(String dsName) {
		try ( RDFConnection conn = RDFConnectionFactory.connect("http://localhost:3030/ds") ) {
			QueryExecution qExec = conn.query("SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate '"+dsName+"' }") ;
			ResultSet rs = qExec.execSelect();
			boolean flag = rs.hasNext();
			conn.close();
			qExec.close();
			return flag;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	/**
	 * Method to fetch the filepath for files stored in src/main/resources
	 *
	 * @param dsName - relative path to the file
	 * @return File System path of the file
	 */
	public String fetchSysFilePath(String dsName) {
		return "/Users/maqbool/Documents/ida-ds/" + dsName;
	}
}
