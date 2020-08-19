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
	// Load the provided dataset into a table
	public static final int UAC_LOADDATAMSG = 1002;
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

	/**
	 * Param Map Keys
	 */
	public static final String PARAM_TEXT_MSG = "TEXT_MSG";
	public static final String PARAM_DATASET_NAME = "DATASET_NAME";
	public static final String PARAM_ALL_REQUIRED_PARAMS_PRESENT = "PARAM_ALL_REQUIRED_PARAMS_PRESENT";

	// Metadata File name Pattern
	public static final String DSMD_FILE_PATTERN = "dsmd\\.[jJ][sS][oO][nN]$";
	// File path for dataset map properties
	public static final String DSMAP_PROP_FILEPATH = "datasetmap.properties";
	// CSV File name Pattern
	public static final String CSV_FILE_PATTERN = ".*[cC][sS][vV]$";
}
