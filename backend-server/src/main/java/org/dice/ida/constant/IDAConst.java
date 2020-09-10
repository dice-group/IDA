package org.dice.ida.constant;
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
	public static final String NO_VISUALIZATION_MSG = "No optimal visualization can be used for the selected table";
	public static final String DS_DOES_NOT_EXIST_MSG = "dataset does not exist";

	// Metadata File name Pattern
	public static final String DSMD_FILE_PATTERN = "dsmd\\.[jJ][sS][oO][nN]$";
	// File path for dataset map properties
	public static final String DSMAP_PROP_FILEPATH = "datasetmap.properties";
	// CSV File name Pattern
	public static final String CSV_FILE_PATTERN = ".*[cC][sS][vV]$";

	// Bar graph
	public static final String PARAM_FILTER_STRING = "items-selection";
	public static final String PARAM_XAXIS_NAME = "x-axis";
	public static final String PARAM_YAXIS_NAME = "y-axis";
	public static final String BAR_GRAPH_LOADED = "The requested bar graph has been loaded.";
	public static final String INVALID_X_AXIS_NAME = "Provided x-axis column name was incorrect! Please try again.";
	public static final String INVALID_Y_AXIS_NAME = "Provided Y-axis column name was incorrect! Please try again.";
	public static final String BG_FILTER_ALL = "all";
	public static final String BG_FILTER_FIRST = "first";
	public static final String BG_FILTER_LAST = "last";
	public static final String BG_FILTER_FROM = "from";



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
}
