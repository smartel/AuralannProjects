package RuneQuizzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SettingsLoader {
	
	Settings settings;
	String settingsPath;
	String errorMsgs; // want to save any errors until the gui opens, so we can present them to the user rather than have a jar executable fail silently.
	
	public SettingsLoader() {
		settings = new Settings();
		settings.addIntSetting(Settings.I_RUNE_SIZE, Settings.RUNE_SIZE_DEFAULT); // this used to be changeable, but I've removed that functionality.
		settingsPath = System.getProperty("user.dir") + "\\RuneQuizzerSettings.txt"; // hardcoded location
		readSettingsFromFile();
	}
	
	
	// populate the settings object
	public void readSettingsFromFile() {
		errorMsgs = "";
		String setting = "";
		String value = "";
		int fieldCount = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(settingsPath));
			while (br.ready()) {
				String lineContents = br.readLine();
				setting = lineContents.substring(0, lineContents.indexOf(" "));
				value = lineContents.substring(lineContents.indexOf("[")+1, lineContents.indexOf("]"));
				
				// bools
				
				if (setting.equalsIgnoreCase(Settings.B_HAS_SINGLES)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_SINGLES, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_SINGLES, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_SINGLES + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_HAS_DOUBLES)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_DOUBLES, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_DOUBLES, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_DOUBLES + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_HAS_TRIPLES)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_TRIPLES, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_TRIPLES, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_TRIPLES + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_HAS_QUADS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_QUADS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_QUADS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_QUADS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_HAS_NUMERICS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_NUMERICS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_NUMERICS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_NUMERICS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_HAS_DEADS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_DEADS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_DEADS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_DEADS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_HAS_SYMBOLS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_SYMBOLS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_SYMBOLS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_SYMBOLS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_HAS_SPECIALS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_HAS_SPECIALS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_HAS_SPECIALS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_HAS_SPECIALS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_AROUND_WORLD)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_AROUND_WORLD, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_AROUND_WORLD, true);
					} else {
						errorMsgs += "Error with " + Settings.B_AROUND_WORLD + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				}
				
				// ints
				
				else if (setting.equalsIgnoreCase(Settings.I_RUNE_RGB_RED)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_RUNE_RGB_RED + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_RUNE_RGB_RED, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_RUNE_RGB_GREEN)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_RUNE_RGB_GREEN + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_RUNE_RGB_GREEN, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_RUNE_RGB_BLUE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_RUNE_RGB_BLUE + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_RUNE_RGB_BLUE, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_BACK_RGB_RED)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_BACK_RGB_RED + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_BACK_RGB_RED, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_BACK_RGB_GREEN)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_BACK_RGB_GREEN + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_BACK_RGB_GREEN, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_BACK_RGB_BLUE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_BACK_RGB_BLUE + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_BACK_RGB_BLUE, intVal);
					}
				}
				
				// strings
				
				else if (setting.equalsIgnoreCase(Settings.STR_OUT_PATH)) {
					fieldCount++;
					settings.setOutputPath(value);
				} else if (setting.equalsIgnoreCase(Settings.STR_RD_PATH)) {
					fieldCount++;
					errorMsgs += settings.loadRd(value);
				}
				
			}

			// ensure the settings file actually contained all settings
			if (fieldCount != Settings.LOAD_FIELD_COUNT) {
				errorMsgs += "Error - The QuizzerSettings file does not have all required settings! Fields counted: " + fieldCount + "    Expected # of Fields: " + Settings.LOAD_FIELD_COUNT + "\n";
			}
			
			br.close();
			
		} catch (NumberFormatException e) {
			errorMsgs += ("A number value is needed for the following setting:\n" + setting + "\nValue was: " + value + "\n");
		} catch (FileNotFoundException e) {
			errorMsgs += ("Error reading in settings file. The Settings File could not be found at: " + settingsPath + "\n");
		} catch (Exception e) {
			errorMsgs += ("Unknown Error reading in settings file. Exception was: " + e.getMessage() + "\n");
		}
	}
	
	
	public Settings getSettings() {
		return settings;
	}
	
	// float these to the gui so we can display to the user
	public String getErrors() {
		return errorMsgs;
	}
	
}
