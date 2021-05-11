package org.dice.ida.constant;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * This interface consists of all the constant values across IDA
 *
 * @author Nikit
 */
public interface IDAConst {

	/**
	 * UI action codes
	 */
	// Load message normally
	public static final int UAC_NRMLMSG = 1001;
	// Load dataset
	public static final int UIA_LOADDS = 1004;
	// Open Upload dataset modal window
	public static final int UAC_UPLDDTMSG = 1002;
	//Load bar graph
	public static final int UIA_BARGRAPH = 1005;
	// Load scatter plot
	public static final int UIA_SCATTERPLOT = 1011;
	//Load bubble chart
	public static final int UIA_BUBBLECHART = 1006;
	// Draw line chart
	public static final int UIA_LINECHART = 1007;
	//Load clustered dara
	public static final int UIA_CLUSTER = 1008;
	// Load grouped bar chart
	public static final int UIA_GROUPED_BARGRAPH = 1009;
	// Load grouped bubble chart
	public static final int UIA_GROUPED_BUBBLECHART = 1010;
	//Load scatter plot matrix
	public static final int UIA_SCATTERPLOT_MATRIX = 1012;

	/**
	 * Pre-defined action codes
	 */
	// Know more button
	public static final int PDAC_KNWMR = 2001;
	// Upload dataset button
	public static final int PDAC_UPLDDT = 2002;
	// View existing datasets button
	public static final int PDAC_VWDTS = 2003;

	/**
	 * Chatbot constants
	 */
	public static final String BOT_LANGUAGE = "en-US";
	public static final String UNK_INTENT_COUNT = "unknownIntentCount";
	public static final String[] VISUALIZATIONS_LIST = {"Bar chart", "Bubble chart", "Line chart"};

	/**
	 * IDA response
	 */
	public static final String BOT_UNAVAILABLE = "IDA is currently unavailable";
	public static final String BOT_SOMETHING_WRONG = "Something went wrong with that request. Please try again later.";
	public static final String BOT_LOAD_DS_BEFORE = "Please load a dataset";
	public static final String BOT_SELECT_TABLE = "The currently active table is meta data table, please open a data table from side bar";
	public static final String BOT_HELP = "Hmm, I could not understand it again. May be you can try asking me \"what can you do\" to get help?";

	/**
	 * Param Map Keys
	 */
	public static final String PARAM_TEXT_MSG = "TEXT_MSG";
	public static final String PARAM_INTENT = "INTENT";
	public static final String PARAM_DATASET_NAME = "datasetname";
	public static final String PARAM_ALL_REQUIRED_PARAMS_PRESENT = "PARAM_ALL_REQUIRED_PARAMS_PRESENT";
	public static final String PARAM_INTENT_DETECTION_CONFIDENCE = "intent_detection_confidence";
	public static final String NO_VISUALIZATION_MSG = "No optimal visualization can be used for the selected table.";
	public static final String DS_DOES_NOT_EXIST_MSG = " dataset does not exist. <br/><br/> You can ask to \"list all datasets\" to confirm if your dataset is present.";
	public static final String INTENT_NAME = "intentname";
	public static final String FULL_INTENT_NAME = "fullintentname";

	// Metadata File name Pattern
	public static final String DSMD_FILE_PATTERN = "dsmd\\.[jJ][sS][oO][nN]$";
	// File path for dataset map properties
	public static final String DSMAP_PROP_FILEPATH = "datasetmap.properties";
	// CSV File name Pattern
	public static final String CSV_FILE_PATTERN = ".*[cC][sS][vV]$";

	// Scatter plot
	public static final String SCATTER_PLOT_LOADED = "The requested scatter plot has been loaded.";
	// Scatter plot Matrix
	public static final String SCATTER_PLOT_MATRIX_LOADED = "The requested scatter plot matrix has been loaded.";

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
	public static final double X_PARAM_UNIQUENESS_PROBABILITY = 100.0;
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
	public static final String NULL_VALUE_IDENTIFIER = "UNKNOWN";
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
	public static final String LINE_CHART_LOADED = "The requested line chart has been loaded.";
	public static final String LINE_CHART_TEMPORAL_PARAM = "Temporal_Column";
	public static final String LINE_CHART_LABLE_PARAM = "Line_Label";
	public static final String LINE_CHART_VALUE_PARAM = "Line_Value";

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
	 * Visualization with List Paramter
	 */

	public static final String HAS_LIST_COLUMN = "hasListColumn";

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
	public static final String INSTANCE_PARAM_DEPENDENT_KEY = "dependentOn";
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
	String VIZ_TYPE_SCATTER_PLOT = "scatter_plot";
	String VIZ_TYPE_LINE_CHART = "line_chart";
	String VIZ_TYPE_SCATTER_PLOT_MATRIX = "scatter_plot_matrix";
	String BUBBLE_LABEL_PARAM = "Bubble_Label";
	String BUBBLE_SIZE_PARAM = "Bubble_Size";
	String X_AXIS_PARAM = "X-Axis";
	String Y_AXIS_PARAM = "Y-Axis";
	String REFERENCE_VALUES_PARAM = "Reference_Values";
	String SCATTER_PLOT_LABEL_PARAM = "Scatterplot_Label";

	public static final String PARAMETER_TYPE_BIN_SIZE = "bin_size";
	public static final String PARAMETER_TYPE_DURATION_UNIT = "unit";
	public static final String PARAMETER_TYPE_DURATION_SIZE = "amount";
	public static final String CONTEXT_GET_BIN_SIZE = "get_bin_size";
	public static final String CONTEXT_GET_BIN_DURATION = "get_bin_duration";
	public static final String DURATION_TYPE_WEEK = "wk";
	public static final String DURATION_TYPE_MONTH = "mo";
	public static final String DURATION_TYPE_YEAR = "yr";
	public static final String LABEL_PATTERN_DATE = "dd-MM-yy";
	public static final String LABEL_PATTERN_MONTH = "MMMM-yyyy";
	public static final String LABEL_PATTERN_YEAR = "yyyy";
	public static final String ATTRIBUTE_TYPE_SUFFIX = "_type";
	public static final String ATTRIBUTE_CHOICE_SUFFIX = "_choice";


	/**
	 * Clustering Parameters
	 */
	public static final String K_MEAN_CLUSTERING = "Kmeans";
	public static final String FARTHEST_FIRST = "FarthestFirst";
	public static final String GET_NUM_CLUSTER = "getNumCluster";
	public static final String NUMBER_OF_CLUSTER = "num_cluster";
	public static final String GET_INIT_METHOD = "getInitMethod";
	public static final String INIT_METHOD = "init_method";
	public static final String GET_MAX_ITERATION = "getMaxIteration";
	public static final String MAX_ITERATION = "max_iteration";
	public static final String GET_REPLACE_MISSING_VALUES = "getReplaceMissingValues";
	public static final String IS_REPLACE_MISSING_VALUE = "isreplacemissing";
	public static final String GET_NUM_EXECUTION_SLOT = "getNumExecutionSlot";
	public static final String NUM_OF_SLOT = "num_slot";
	public static final String GET_RANDOM_SEED = "getRandomSeed";
	public static final String RANDOM_SEED = "random_seed";
	public static final String GET_MULTI_PARAM = "getMultiParam";

	public static final long TIMEOUT_LIMIT = 300000;
	public static final String TIMEOUT_MSG = "Sorry, it looks like that request has crossed the allowed time limit of " + (TIMEOUT_LIMIT / 60000) + " minutes. Please optimize your task.";
	public static final Map<String, String> PARAM_NAME_MAP = new HashMap<>() {{
		put("X-Axis", "X-Axis");
		put("Y-Axis", "Y-Axis");
		put("Bubble_Label", "label of the bubbles");
		put("Bubble_Size", "size of the bubbles");
		put("Temporal_Column", "X-Axis (Temporal data)");
		put("Line_Label", "Line Labels");
		put("Line_Value", "Line Values");
		put("Reference_Values", "Reference Values");
		put("Scatterplot_Label", "label for scatter plot");
	}};
	public static final Map<String, String> PARAM_TYPE_EG_MAP = new HashMap<>() {{
		put("numeric", "Group of N values");
		put("date", "Group of N days, weeks, months or years");
	}};
	public static final List<String> TRANSFORMATION_TYPES = new ArrayList<>() {
		{
			add("Count Of");
			add("Sum Of");
			add("Average");
		}
	};
	public static final Map<String, String> TRANSFORMATION_EG_MAP = new HashMap<>() {{
		put("Count Of", "Count of rows");
		put("Sum Of", "Sum of values");
		put("Average", "Average of values");
	}};
	public static final String PARAM_TYPE_NON_BIN = "As it is";
}
