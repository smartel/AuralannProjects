package RuneQuizzer;
import Text2Auralann.RuneDictionary;

import java.util.HashMap;

public class Settings {

	// settings count:
	// bools - 9: 8 collection bools, 1 around the world bool
	// ints - 6: 6 rgb ints, removing the 1 rune size int
	// strings - 2: 1 rd path, 1 output path
	// so 17 expected fields in the settings file we read in.
	public static final int LOAD_FIELD_COUNT = 17;
	
	public static final String B_HAS_SINGLES    = "B_HAS_SINGLES";           
	public static final String B_HAS_DOUBLES    = "B_HAS_DOUBLES";       
	public static final String B_HAS_TRIPLES    = "B_HAS_TRIPLES";       
	public static final String B_HAS_QUADS      = "B_HAS_QUADS";       
	public static final String B_HAS_NUMERICS   = "B_HAS_NUMERICS";             
	public static final String B_HAS_DEADS      = "B_HAS_DEADS"; 
	public static final String B_HAS_SYMBOLS    = "B_HAS_SYMBOLS"; 
	public static final String B_HAS_SPECIALS   = "B_HAS_SPECIALS";
	public static final String B_AROUND_WORLD   = "B_AROUND_WORLD";
	public static final String I_RUNE_RGB_RED   = "I_RUNE_RGB_RED";
	public static final String I_RUNE_RGB_GREEN = "I_RUNE_RGB_GREEN";
	public static final String I_RUNE_RGB_BLUE  = "I_RUNE_RGB_BLUE";
	public static final String I_BACK_RGB_RED   = "I_BACK_RGB_RED";
	public static final String I_BACK_RGB_GREEN = "I_BACK_RGB_GREEN";
	public static final String I_BACK_RGB_BLUE  = "I_BACK_RGB_BLUE";
	public static final String STR_OUT_PATH     = "STR_OUT_PATH";
	public static final String STR_RD_PATH      = "STR_RD_PATH";
	
	// values for I_RUNE_SIZE
	public static final String I_RUNE_SIZE      = "I_RUNE_SIZE"; // NO LONGER CHANGEABLE
	public static int NORMAL_SIZED_RUNES = 0;
	public static int DOUBLE_SIZED_RUNES = 1;
	public static int QUAD_SIZED_RUNES = 2;
	public static int RUNE_SIZE_DEFAULT = 2;
	
	// defaults
	public static final int RUNE_RGB_RED_DEFAULT = 190;
	public static final int RUNE_RGB_GREEN_DEFAULT = 65;
	public static final int RUNE_RGB_BLUE_DEFAULT = 255;
	public static final int BACK_RGB_RED_DEFAULT = 0;
	public static final int BACK_RGB_GREEN_DEFAULT = 0;
	public static final int BACK_RGB_BLUE_DEFAULT = 0;
	
	// file paths
	private String outputPath;
	private String rdPath; // rune dictionary path
	
	RuneDictionary rd;
	private HashMap<String, Integer> intSettings;
	private HashMap<String, Boolean> boolSettings;
	
	public Settings() {
		intSettings = new HashMap<String, Integer>();
		boolSettings = new HashMap<String, Boolean>();
	}
	
	public void loadRuneDictionary(String rdPath) {
		this.rdPath = rdPath;
		loadRd(this.rdPath);
	}
	
	// adds
	
	public void addBoolSetting(String settingName, boolean value) {
		boolSettings.put(settingName, value);
	}
	
	public void addIntSetting(String settingName, int value) {
		intSettings.put(settingName, value);
	}
	
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	// gets
	
	public Boolean getBoolSetting(String settingName) {
		return boolSettings.get(settingName);
	}
	
	public Integer getIntSetting(String settingName) {
		return intSettings.get(settingName);
	}
	
	public String getOutputPath() {
		return outputPath;
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

	
	public String loadRd(String newDir) {
		String msgs = "";
		try {
			rdPath = newDir.replace("\\", "/");
			if (rd == null) {
				rd = new RuneDictionary();
				rd.setBaseDir(newDir);
				msgs = rd.readInRunes(true, true);
			} else {
				rd.clearRdes();
				rd.setBaseDir(newDir);
				msgs = rd.readInRunes(true, true);
			}
		} catch (Exception e) {
			msgs += e.getMessage();
		}
		return msgs;
	}


	public RuneDictionary getRuneDictionary() {
		return rd;
	}
}
