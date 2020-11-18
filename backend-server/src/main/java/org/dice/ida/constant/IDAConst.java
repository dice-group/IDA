package org.dice.ida.constant;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * This interface consists of all the constant values across IDA
 * @author Nikit
 *
 */
public interface IDAConst {

	/**
	 *  UI action codes
	 */
	// Load message normally
	public static final int UAC_NRMLMSG = 1001;
	// Load dataset
	public static final int UIA_LOADDS = 1004;
	// Open Upload dataset modal window
	public static final int UAC_UPLDDTMSG = 1002;
	//Load bar graph
	public static final int UIA_BARGRAPH = 1005;
	//Load bubble chart
	public static final int UIA_BUBBLECHART = 1006;
	// Draw line chart
	public static  final int UIA_LINECHART = 1007;

	/**
	 *  Pre-defined action codes
	 */
	// Know more button
	public static final int PDAC_KNWMR = 2001;
	// Upload dataset button
	public static final int PDAC_UPLDDT = 2002;
	// View existing datasets button
	public static final int PDAC_VWDTS = 2003;

	/**
	 *  Chatbot constants
	 */
	public static final String BOT_UNAVAILABLE = "IDA is currently unavailable";
	public static final String BOT_LANGUAGE = "en-US";
	public static final String BOT_SOMETHING_WRONG = "Something went wrong with that request. Please try again later.";
	public static final String BOT_LOAD_DS_BEFORE = "Please load a dataset";
	public static final String BOT_SELECT_TABLE = "Please select a data table";

	/**
	 * Param Map Keys
	 */
	public static final String PARAM_TEXT_MSG = "TEXT_MSG";
	public static final String PARAM_DATASET_NAME = "datasetname";
	public static final String PARAM_ALL_REQUIRED_PARAMS_PRESENT = "PARAM_ALL_REQUIRED_PARAMS_PRESENT";
	public static final String PARAM_INTENT_DETECTION_CONFIDENCE = "intent_detection_confidence";
	public static final String NO_VISUALIZATION_MSG = "No optimal visualization can be used for the selected table";
	public static final String DS_DOES_NOT_EXIST_MSG = "dataset does not exist";
	public static final String INTENT_NAME = "intentname";

	// Metadata File name Pattern
	public static final String DSMD_FILE_PATTERN = "dsmd\\.[jJ][sS][oO][nN]$";
	// File path for dataset map properties
	public static final String DSMAP_PROP_FILEPATH = "datasetmap.properties";
	// CSV File name Pattern
	public static final String CSV_FILE_PATTERN = ".*[cC][sS][vV]$";

	// Bar graph
	public static final String PARAM_FILTER_STRING = "records-selection";
	public static final String PARAM_XAXIS_NAME = "x-axis";
	public static final String PARAM_YAXIS_NAME = "y-axis";
	public static final String BAR_GRAPH_LOADED = "The requested bar graph has been loaded.";
	public static final String INVALID_X_AXIS_NAME = "Provided x-axis column name was incorrect! Please try again.";
	public static final String INVALID_Y_AXIS_NAME = "Provided Y-axis column name was incorrect! Please try again.";
	public static final String INVALID_RANGE = "Provided range for filter was incorrect! Please try again.";
	public static final String INVALID_FILTER = "Please select a valid filter (for example: all | first 20 | last 30  | from 55 to 100 records).";
	public static final String BG_FILTER_ALL = "all";
	public static final String BG_FILTER_FIRST = "first";
	public static final String BG_FILTER_LAST = "last";
	public static final String BG_FILTER_FROM = "from";

	// Bubble chart
	public static final String BC_ONE = "one";
	public static final String BC_COL_NAME = "col_name";
	public static final String BC_TWO = "two";
	public static final String BC_FIRST_COL = "first_col";
	public static final String BC_SECOND_COL = "second_col";
 	public static final String BC_LOADED = "Bubble chart has been loaded";
 	public static final String BC_INVALID_COL = "Provided column name was incorrect, it does not exist on the loaded table!";
 	public static final String BC_INVALID_FIRST_COL = "Provided first column name was incorrect, it does not exist on the loaded table!";
 	public static final String BC_INVALID_SECOND_COL = "Provided second column name was incorrect, it does not exist on the loaded table!";
 	public static final String BC_NOT_NUM_SECOND_COL = "Provided second column was not numerical! try again please";
 	public static final String BC_INCORRECT_COL = "Provided column name was incorrect! please tell the column name again?";

	/**
	 * Bar graph parameters thresholds
	 */
	public static final int X_PARAM_MAX_COUNT_OF_VALUES = 50;
	public static final double  X_PARAM_UNIQUENESS_PROBABILITY = 100.0;
	public static final double Y_PARAM_UNIQUENESS_MIN_PROBABILITY = 90.0;
	public static final double X_PARAM_MIN_DUPLICATE_RATIO = 30.0;

	/**
	 * Miscellaneous
	 */
	public static final String DF_SESSION_ID = "df-sid-key";
	public static final int DF_SID_LEN = 6;

	/**
	 * Dialogflow credential object keys and util strings
	 */
	public static final String CRED_PRIVATE_KEY_ID = "private_key_id";
	public static final String CRED_PRIVATE_KEY = "private_key";
	public static final String CRED_CLIENT_EMAIL = "client_email";
	public static final String CRED_CLIENT_ID = "client_id";
	public static final String CRED_TOKEN_URI = "token_uri";
	public static final String CRED_PATH_KEY = "dialogflow.project.id";
	public static final String CRED_PRIVATE_KEY_BEGIN = "-----BEGIN PRIVATE KEY-----";
	public static final String CRED_PRIVATE_KEY_END = "-----END PRIVATE KEY-----";
	public static final String CRED_PRIVATE_KEY_TYPE = "RSA";

	// Db utils
	public  static final String NULL_VALUE_IDENTIFIER = "UNKNOWN";
	public static final String QUESTION_MARK_SYMBOL = "?";

	/**
	 * parameter validation constants
	 */
	public static final String FILE_DETAILS_ATTR = "filesMd";
	public static final String FILE_NAME_ATTR = "fileName";
	public static final String COLUMN_DETAILS_ATTR = "fileColMd";
	public static final String COLUMN_NAME_ATTR = "colAttr";
	public static final String COLUMN_TYPE_ATTR = "colType";
	public static final String COLUMN_UNIQUE_ATTR = "isUnique";
	public static final String TABLE_DOES_NOT_EXIST_MSG = "Selected table does not exist";

	/**
	 * Line chart constants
	 */
	public static final String LINE_CHART_PARAM_DATE_COL = "date_column";
	public static final String LINE_CHART_PARAM_LABEL_COL = "line_label_column";
	public static final String LINE_CHART_PARAM_VALUE_COL = "line_value_column";
	public static final String INVALID_DATE_COLUMN_MSG = " is not a date column";
	public static final String INVALID_NUMERIC_COLUMN_MSG = " is not a numeric column";

	/**
	 * Column data types
	 */
	public static final String COLUMN_TYPE_NOMINAL = "string";
	public static final String COLUMN_TYPE_NUMERIC = "numeric";
	public static final String COLUMN_TYPE_DATE = "date";
	public static final String[] DATE_PATTERNS = {"dd/MM/yyyy", "dd MMM", "MMM YYYY", "dd/MM/yyyy HH:mm:ss", "dd-MMM-yyyy", "MMMM-yyyy", "YYYY"};
	public static final String LABEL_TYPE_DATE = "day";
	public static final String LABEL_TYPE_MONTH = "month";
	public static final String LABEL_TYPE_YEAR = "year";
	public static final String COUNT_OF_PREFIX = "Count of ";
	public static final String LINE_CHART_DESC_PREFIX = "Line chart for ";
	public static final String LINE_CHART_PROPERTY_NAME = "lineChartData";

	/**
	 * Prefixes for SPARQL queries
	 */
	public static final String IDA_SPARQL_PREFIX = "prefix dc: <http://purl.org/dc/elements/1.1/>\n" +
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
	public static final String TRANSFORMATION_LABEL = "Transformation";
	public static final String INSTANCE_PARAM_TYPE_KEY = "type";
	public static final String INSTANCE_PARAM_TRANS_TYPE_KEY = "trans_type";
	public static final String INSTANCE_PARAM_TYPE_UNIQUE = "unique";
	public static final String INSTANCE_PARAM_TYPE_NON_UNIQUE = "non unique";
	public static final String INSTANCE_PARAM_TYPE_NOT_REQUIRED = "not required";
	public static final String INSTANCE_PARAM_TYPE_BINS = "bins";
	public static final String TRANSFORMATION_TYPE_AVG = "average";
	public static final String TRANSFORMATION_TYPE_SUM = "sum of";
	public static final String TRANSFORMATION_TYPE_COUNT = "count of";

	public static final Map<String, List<String>> PARAM_TYPE_TREE = new HashMap<>() {{
		put("numeric", new ArrayList<>() {
			{
				add("numeric");
				add("bins");
				add("unique");
				add("non unique");
				add("sum of");
				add("average");
			}
		});
		put("date", new ArrayList<>() {
			{
				add("date");
				add("bins");
				add("unique");
				add("non unique");
			}
		});
		put("string", new ArrayList<>() {
			{
				add("string");
				add("unique");
				add("non unique");
			}
		});
	}};
	public static final List<String> MONTHS_LIST = Arrays.asList(new DateFormatSymbols().getMonths());
	public static final String COMPARATOR_TYPE_DOUBLE = "doubleString";
	public static final String COMPARATOR_TYPE_DATE = "dateString";
	public static final String COMPARATOR_TYPE_MONTH = "monthYearString";
	public static final String COMPARATOR_TYPE_YEAR = "yearString";
	public static final String COMPARATOR_TYPE_DOUBLE_BIN = "doubleBin";
	public static final String COMPARATOR_TYPE_DATE_BIN = "dateBin";
	public static final String COMPARATOR_TYPE_UNKNOWN = "unknown";

	String VIZ_TYPE_BAR_CHART = "bar_chart";
	String VIZ_TYPE_BUBBLE_CHART = "bubble_chart";
	String BUBBLE_LABEL_PARAM = "Bubble_Label";
	String BUBBLE_SIZE_PARAM = "Bubble_Size";
	String X_AXIS_PARAM = "X-Axis";
	String Y_AXIS_PARAM = "Y-Axis";

	public static final String PARAMETER_TYPE_BIN_SIZE = "bin_size";
	public static final String PARAMETER_TYPE_DURATION_UNIT = "unit";
	public static final String PARAMETER_TYPE_DURATION_SIZE = "amount";
	public static final String CONTEXT_GET_BIN_SIZE = "get_bin_size";
	public static final String CONTEXT_GET_BIN_DURATION = "get_bin_duration";
	public static final String DURATION_TYPE_WEEK = "wk";
	public static final String DURATION_TYPE_MONTH = "mo";
	public static final String DURATION_TYPE_YEAR = "yr";
	public static final String LABEL_PATTERN_DATE = "dd-MMM-yyyy";
	public static final String LABEL_PATTERN_MONTH = "MMMM-yyyy";
	public static final String LABEL_PATTERN_YEAR = "yyyy";
	public static final String ATTRIBUTE_TYPE_SUFFIX = "_type";

}
