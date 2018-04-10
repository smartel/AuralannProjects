package Text2Auralann;

import java.util.HashMap;

public class Settings {

	public static final int LOAD_FIELD_COUNT = 36; // the number of fields that are expected in a Settings file
	public static final int LISTENER_FIELD_COUNT = 52; // load_Field_count, -2 for unused fields, +many for new dict fields
	
	// finals for settings and the deepest level they are required at
	public static final String I_TEXT_LINE_LENGTH = "TEXT_LINE_LENGTH";                 	// grimoire
	public static final String I_IMAGE_LINE_LENGTH = "IMAGE_LINE_LENGTH";               	// grimoire
	public static final String I_RANDOM_DEAD_CHANCE = "RANDOM_DEAD_CHANCE";             	// lore
	public static final String I_MAJOR_STYLE = "MAJOR_STYLE";                           	// knowl
	public static final String I_DISPLAY_STYLE = "DISPLAY_STYLE";                       	// rune
	public static final String I_ORIENTATION = "ORIENTATION";                           	// lore
	public static final String I_ALT_MODE = "ALT_MODE";						        		// alternator
	public static final String B_ALLOW_DOUBLES = "ALLOW_DOUBLES";                       	// lore
	public static final String B_ALLOW_TRIPLES = "ALLOW_TRIPLES";                 			// lore
	public static final String B_ALLOW_QUADS = "ALLOW_QUADS";                     			// lore
	public static final String B_POSSESSIVE_S = "ALLOW_POSSESSIVE_S";             			// lore
	public static final String B_PLURAL_S = "ALLOW_PLURAL_S";                     			// lore
	public static final String B_ALLOW_AURAS = "ALLOW_AURAS";                     			// knowl
	public static final String B_LOWER_AURAS = "ALLOW_LOWER_AURAS";               			// knowl
	public static final String B_RUNE_OVERFLOW = "ALLOW_RUNE_OVERFLOW";           			// grimoire
	public static final String B_ALLOW_AI_DEAD = "ALLOW_AI_DEAD";                  			// lore
	public static final String I_ALLOW_APOS_DEAD = "ALLOW_APOS_DEAD";              			// lore
	public static final String B_ALLOW_COMMON_DEAD = "ALLOW_COMMON_DEAD";         			// lore
	public static final String B_ALLOW_RANDOM_DEAD = "ALLOW_RANDOM_DEAD_RUNES";   			// lore
	public static final String I_RUNE_RGB_RED = "RUNE_RGB_RED";            					// videre
	public static final String I_RUNE_RGB_GREEN = "RUNE_RGB_GREEN";        					// videre
	public static final String I_RUNE_RGB_BLUE = "RUNE_RGB_BLUE";          					// videre
	public static final String I_BACK_RGB_RED = "BACK_RGB_RED";      						// videre
	public static final String I_BACK_RGB_GREEN = "BACK_RGB_GREEN";  						// videre
	public static final String I_BACK_RGB_BLUE = "BACK_RGB_BLUE";    						// videre
	public static final String B_ALLOW_STAFF = "ALLOW_STAFF";								// videre
	public static final String I_RAY_RGB_RED = "RAY_RGB_RED";      							// videre
	public static final String I_RAY_RGB_GREEN = "RAY_RGB_GREEN";  							// videre
	public static final String I_RAY_RGB_BLUE = "RAY_RGB_BLUE";    							// videre
	public static final String B_AUTO_INQUISITIVE = "AUTO_INQUISITIVE";       				// knowl consumes, stored as far as rune/aura
	public static final String B_AUTO_WELKIN = "AUTO_WELKIN";                           	// knowl
	public static final String B_AUTO_ABYSM = "AUTO_ABYSM";                             	// knowl
	public static final String B_FILTER_ENDS = "FILTER_ENDS";                    			// knowl
	public static final String B_INSERT_SPACES = "INSERT_SPACES";							// knowl
	public static final String STR_RUNE_DICTIONARY_LOCATION = "RUNE_DICTIONARY_LOCATION"; 	// lexi
	public static final String STR_OUTPUT_DIR_LOCATION = "OUTPUT_DIR_LOCATION";				// lexi

	
	// orientation finals for I_ORIENTATION setting   (lore)
	public static final int LEFT_TO_RIGHT = 1;
	public static final int RIGHT_TO_LEFT = 2;
	
	public static final int TEXT_LINE_LENGTH_DEFAULT = 40;
	public static final int IMAGE_LINE_LENGTH_DEFAULT = 36;
	public static final int RANDOM_DEAD_CHANCE_DEFAULT = 4; // percent chance that a random dead rune will appear
	public static final int RUNE_RGB_RED_DEFAULT = 190;
	public static final int RUNE_RGB_GREEN_DEFAULT = 65;
	public static final int RUNE_RGB_BLUE_DEFAULT = 255;
	public static final int BACK_RGB_RED_DEFAULT = 0;
	public static final int BACK_RGB_GREEN_DEFAULT = 0;
	public static final int BACK_RGB_BLUE_DEFAULT = 0;
	public static final int RAY_RGB_RED_DEFAULT = 50;
	public static final int RAY_RGB_GREEN_DEFAULT = 50;
	public static final int RAY_RGB_BLUE_DEFAULT = 50;
	
	// major style finals for I_MAJOR_STYLE setting   (knowl)
	public static final int MAJORS_NONE = 0;  // no majors, no meagers.
	public static final int MAJORS_FEW = 1; // majors on first and last lores only. meagers only if needed.
	public static final int MAJORS_ALL = 2; // majors on every lore. meagers only if needed.
	public static final int CUSTOM_STYLE = -1; // custom style via ctor that allows for setting of every boolean
	// values for how to treat majors / meagers if the I_MAJOR_STYLE is custom
	public static final String CUST_HAS_MAJORS = "Custom Style Has Majors";
	public static final String CUST_HAS_FL_MAJORS = "Custom Style Has First Last Majors";
	public static final String CUST_HAS_MEAGERS = "Custom Style Has Meagers";
	public static final String CUST_HAS_MEAGERS_NEEDED = "Custom Style Has Meagers If Needed";
	
	// text display styles
	public static final int DISPLAY_RUNE_ONLY = 0; // just the rune, ie "O"
	public static final int DISPLAY_RUNE_FORM = 1; // default for text output, ie so we know if it's "O-1" vs "O-2"
	public static final int DISPLAY_RUNE_FILE = 2; // to see the full path, ie ".../O-2.png", largely for testing
	
	public static final int IMAGE_LINE_LENGTH_MAX = 36;
	
	// alt modes
	public static final int ALTS_NO_ALTS = 0;
	public static final int ALTS_EQUAL_CHANCE = 1;
	public static final int ALTS_ALT_ALTS = 2;
	public static final int ALTS_ALTS_ONLY = 3;
	
	// values for how to treat APOS-Dead setting
	public static final int APOS_NONE = 0;
	public static final int APOS_NEEDED = 1;
	public static final int APOS_LEFT = 2;
	public static final int APOS_RIGHT = 3;
	public static final int APOS_BOTH = 4;
	
	// base directory for loading in rune images
	public String baseImageDirectory;
	
	// settings filepath
	public String settingsPath;
	public static final String PERSONAL_PATH = "\\Settings-Personal.txt";
	
	// base directory for outputting lexified text / image files
	public String outputDirectory;
	
	// listener-specific - filepaths to each dictionary
	public static final String STR_BASIC_DICT_LOCATION = "BASIC_DICT_LOCATION";
	public static final String STR_BASIC_DICT_HL_LOCATION = "BASIC_DICT_HL_LOCATION";
	public static final String STR_EXPAND_DICT_LOCATION = "EXPAND_DICT_LOCATION";
	public static final String STR_EXPAND_DICT_HL_LOCATION = "EXPAND_DICT_HL_LOCATION";
	public static final String STR_ENGLISH_DICT_LOCATION = "ENGLISH_DICT_LOCATION";
	public static final String STR_FUTHARK_DICT_LOCATION = "FUTHARK_DICT_LOCATION";
	public static final String STR_FUTHORC_DICT_LOCATION = "FUTHORC_DICT_LOCATION";
	public static final String STR_BRAILLE_DICT_LOCATION = "BRAILLE_DICT_LOCATION";
	public static final String STR_HALSINGE_DICT_LOCATION = "HALSINGE_DICT_LOCATION";
	public static final String STR_TEMPLAR_DICT_LOCATION = "TEMPLAR_DICT_LOCATION";
	public static final String STR_MASONIC_DICT_LOCATION = "MASONIC_DICT_LOCATION";
	public static final String STR_LONGBRANCH_DICT_LOCATION = "LONGBRANCH_DICT_LOCATION";
	public static final String STR_SHORTTWIG_DICT_LOCATION = "SHORTTWIG_DICT_LOCATION";
	public static final String STR_MEDIEVAL_DICT_LOCATION = "MEDIEVAL_DICT_LOCATION";
	public static final String STR_DALECARLIAN_DICT_LOCATION = "DALECARLIAN_DICT_LOCATION";
	public static final String STR_KATAKANA_DICT_LOCATION = "KATAKANA_DICT_LOCATION";
	public static final String STR_DCROWN_DICT_LOCATION = "DCROWN_DICT_LOCATION";
	public static final String STR_EORZEAN_DICT_LOCATION = "EORZEAN_DICT_LOCATION";
	public String basicDictPath;
	public String basicDictHlPath;
	public String expandDictPath;
	public String expandDictHlPath;
	public String englishDictPath;
	public String futharkDictPath;
	public String futhorcDictPath;
	public String halsingeDictPath;
	public String brailleDictPath;
	public String templarDictPath;
	public String masonicDictPath;
	public String longbranchDictPath;
	public String shorttwigDictPath;
	public String medievalDictPath;
	public String dalecarlianDictPath;
	public String katakanaDictPath;
	public String dcrownDictPath;
	public String eorzeanDictPath;
	
	// rune dictionary, so we only need to initialize / do the file io once, and not for individual Knowl, Lore, ... objects
	RuneDictionary rd;
	
	private HashMap<String, Integer> intSettings;
	private HashMap<String, Boolean> boolSettings;
	
	public Settings() {
		intSettings = new HashMap<String, Integer>();
		boolSettings = new HashMap<String, Boolean>();
		baseImageDirectory = "";
	}
	
	// adds
	
	public void addBoolSetting(String settingName, boolean value) {
		boolSettings.put(settingName, value);
	}
	
	public void addIntSetting(String settingName, int value) {
		intSettings.put(settingName, value);
	}
	
	// gets
	
	public Boolean getBoolSetting(String settingName) {
		return boolSettings.get(settingName);
	}
	
	public int getBoolSettingAsInt(String settingName) { // For writing out to the Settings file
		return boolSettings.get(settingName) ? 1 : 0;
	}
	
	public Integer getIntSetting(String settingName) {
		return intSettings.get(settingName);
	}
	
	// modify
	
	public void changeBoolSetting(String settingName, boolean value) {
		boolSettings.remove(settingName);
		addBoolSetting(settingName, value);
	}
	
	public void changeIntSetting(String settingName, int value) {
		intSettings.remove(settingName);
		addIntSetting(settingName, value);
	}
	
	// sets the bool to the opposite value
	public void toggleBoolSetting(String settingName) {
		boolean newVal = !getBoolSetting(settingName);
		boolSettings.remove(settingName);
		addBoolSetting(settingName, newVal);
	}
	
	public void setBaseImageDirectory(String newDir) {
		try {
			baseImageDirectory = newDir.replace("\\", "/");
			if (rd == null) {
				initializeRuneDictionary();
			} else {
				rd.reloadDictionary(newDir);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Program will now exit.");
			System.exit(0);
		}
	}
	
	// for the lexiconifier gui - a silent mode where we can catch issues and log messages more easily
	public String setImageDirForGui(String newDir) {
		String msgs = "";
		try {
			baseImageDirectory = newDir.replace("\\", "/");
			if (rd == null) {
				rd = new RuneDictionary();
				rd.setBaseDir(newDir);
				msgs = rd.readInRunes(true, false);
			} else {
				rd.clearRdes();
				rd.setBaseDir(newDir);
				msgs = rd.readInRunes(true, false);
			}
		} catch (Exception e) {
			msgs += e.getMessage();
		}
		return msgs;
	}
	
	public void reloadBaseImageDirectory(String newDir) {
		try {
			baseImageDirectory = newDir.replace("\\", "/");
			rd.reloadDictionary(newDir);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Program will now exit.");
			System.exit(0);
		}
	}
	
	public String getBaseImageDirectory() {
		return baseImageDirectory;
	}
	
	private void initializeRuneDictionary() {
		try {
			rd = new RuneDictionary(baseImageDirectory);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Program will now exit.");
			System.exit(0);
		}
	}

	public RuneDictionary getRuneDictionary() {
		return rd;
	}
	
	public String getOutputDirectory() {
		return outputDirectory;
	}
	
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory.replace("\\", "/");
	}
	
	public void setSettingsPath(String settingsPath) {
		this.settingsPath = settingsPath;
	}
	
	public String getSettingsPath() {
		return settingsPath;
	}
}
