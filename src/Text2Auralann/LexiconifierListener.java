package Text2Auralann;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.LinkedList;

public class LexiconifierListener {

	protected Settings settings;
	
	public LexiconifierListener() {
		settings = new Settings();
	}
	
	

	public boolean runListenerMode(String settingsPath) {
		boolean isSuccessfulStart; // returns true at the end if we can initialize everything nicely
		
		// Does the initialization for the listener, so it can start processing requests
		
		// load specified listener settings file
		File file = new File(settingsPath);
		if (file.isFile()) {
			settings.setSettingsPath(settingsPath);
			isSuccessfulStart = loadFileSettings(settings);
		} else {
			System.out.println("Could not find Settings File to use at: " + settingsPath + "!");
			isSuccessfulStart = false;
		}

		return isSuccessfulStart; // if true, ready to listen for requests!
	}
	
	
	// Used for the MainListener - sends a request to the listener, such as to lexify text or change a setting
	public String processRequest(String command, LinkedList<String> args) {
		String response = "";
		
		// Commands list:
		// lexify {example text} - image output only, returns an image filepath. Do we need a 1 second wait?
		// Change Dictionary - will need special commands, only allows basic dictionary +- hollowless, expanded dictionary +- hollowless
		//                     cd basic, cd basichl, cd expand, cd expandhl ?
		// Spaces on/off - setting shortcut
		// Auras on/off - setting shortcut
		// Settings:
		//   set allow_doubles 0-1
		//   set allow_triples 0-1
		//   set allow_quads 0-1
		//   set orientation 1-2
		//   set major_style 0-2
		//   set rune_rgb_red / rune_rgb_green / rune_rgb_blue 0-255
		//   set back_rgb_red / back_rgb_green / back_rgb_blue 0-255
		//   set allow_staff 0-1
		//   set ray_rgb_red / ray_rgb_green / ray_rgb_blue 0-255
		//   set allow_possessive_s 0-1
		//   set allow_plural_s 0-1
		//   set allow_auras 0-1
		//   set allow_lower_auras 0-1, will not override allow_auras if it is 0
		//   set auto_inquisitive 0-1
		//   set auto_welkin 0-1
		//   set auto_abysm 0-1
		//   set image_line_length
		//   set allow_rune_overflow
		//   set allow_ai_dead
		//   set allow_apos_dead
		//   set allow_common_dead
		//   set allow_random_dead
		//   set random_dead_chance
		//   set alt_mode
		//   set filter_ends
		//   set insert_spaces
		
		// FEATURES NOT NEEDED FOR THE LISTENER:
		// Loading default settings
		// Lexify Text - Text Output
		// Get Settings Help String
		// Settings: DISPLAY_STYLE (unused), TEXT_LINE_LENGTH (unused)
		// Load / Save settings file
		// Change log location
		// View Dictionary
		// Exit program
		
		// TODO how do we handle multiple settings on one line, ie: set x y a b c d, as in, [x->y],[a->b],[c->d] ...
		//      loop over this stuff?
		// TODO and how would you report multiple errors in one response? append em? should we *always* be appending to the response?
		
		// The bot breaks input into tokens - we're gonna recombine the args here and store the result, as many commands, like Lexify, just want 1 string.
		String catArgs = "";
		for (int x = 0; x < args.size(); ++x) {
			catArgs += args.get(x) + " ";
		}
		catArgs = catArgs.trim();
		// However, settings are in the format: "SET {SETTING_NAME} {VALUE}", so we'll go ahead and make easy variables to access those too.
		// Note, that if we are lexifying a sentence like "lexify hello there!", then these variables are effectively filled with junk ("hello" "there!")
		String settingName = "";
		String settingVal = "";
		if (args.size() > 1) {
			settingName = args.get(0);
			settingVal = args.get(1);
		} else {
			settingName = args.get(0);
			// no value supplied
		}
		
		
		
		// Lexify:
		if (command.equalsIgnoreCase("LEXIFY")) {
			if (catArgs.isEmpty()) {
				response = "No text was provided to lexify.";
			} else {
				response = lexifyTextToImage(catArgs);
				if (response.isEmpty()) {
					response = "An unknown error occurred attempting to lexify the following text: " + catArgs + ".";
				}
			}
		}
		
		// Setting Shortcuts:
		else if (command.equalsIgnoreCase("SPACES")) {
			// Yeah, using a variable named "settingName" here is crap but what are ya gonna do.
			if (settingName.equalsIgnoreCase("ON")) {
				settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
				response = "Spaces have been turned on.";
			} else { // safe to assume OFF, it is checked for validity in the SpaceCommand
				settings.changeBoolSetting(Settings.B_INSERT_SPACES, false);
				response = "Spaces have been turned off.";
			}
		}
		else if (command.equalsIgnoreCase("AURAS")) {
			// Yeah, using a variable named "settingName" here is crap but what are ya gonna do.
			if (settingName.equalsIgnoreCase("ON")) {
				settings.changeBoolSetting(Settings.B_ALLOW_AURAS, true);
				response = "Auras have been turned on.";
			} else { // safe to assume OFF, it is checked for validity in the SpaceCommand
				settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
				response = "Auras have been turned off.";
			}
		}
		
		else if (command.equalsIgnoreCase("SET")) {
			try { // catch NumberFormatExceptions, set the response to an error

				// Change Dictionary:
				if (settingName.equalsIgnoreCase("DICT")) {
					response = "Runic Dictionary swapped.";
					// Assume it will be a Lann'lain dictionary - toggle Auras on, and don't add spaces between words.
					settings.changeBoolSetting(Settings.B_ALLOW_AURAS, true);
					settings.changeBoolSetting(Settings.B_INSERT_SPACES, false);
					settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
					settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true);
					settings.changeBoolSetting(Settings.B_PLURAL_S, true);
					settings.changeBoolSetting(Settings.B_POSSESSIVE_S, true);
					settings.changeBoolSetting(Settings.B_FILTER_ENDS, true);
					settings.changeIntSetting(Settings.I_ALT_MODE, 1);
					if (settingVal.equalsIgnoreCase("BASIC")) {
						settings.reloadBaseImageDirectory(settings.basicDictPath);
					} else if (settingVal.equalsIgnoreCase("BASICHL")) {
						settings.reloadBaseImageDirectory(settings.basicDictHlPath);
					} else if (settingVal.equalsIgnoreCase("EXPAND")) {
						settings.reloadBaseImageDirectory(settings.expandDictPath);
					} else if (settingVal.equalsIgnoreCase("EXPANDHL")) {
						settings.reloadBaseImageDirectory(settings.expandDictHlPath);
					} else if (settingVal.equalsIgnoreCase("ENGLISH")) {
						settings.reloadBaseImageDirectory(settings.englishDictPath);
						// Auto turn off auras, and add spaces between words. Also turn off some misc stuff.
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}  else if (settingVal.equalsIgnoreCase("FUTHARK")) {
						settings.reloadBaseImageDirectory(settings.futharkDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}  else if (settingVal.equalsIgnoreCase("FUTHORC")) {
						settings.reloadBaseImageDirectory(settings.futhorcDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}  else if (settingVal.equalsIgnoreCase("HALSINGE")) {
						settings.reloadBaseImageDirectory(settings.halsingeDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}  else if (settingVal.equalsIgnoreCase("BRAILLE")) {
						settings.reloadBaseImageDirectory(settings.brailleDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}  else if (settingVal.equalsIgnoreCase("TEMPLAR")) {
						settings.reloadBaseImageDirectory(settings.templarDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}     else if (settingVal.equalsIgnoreCase("MASONIC")) {
						settings.reloadBaseImageDirectory(settings.masonicDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}     else if (settingVal.equalsIgnoreCase("LBRANCH")) {
						settings.reloadBaseImageDirectory(settings.longbranchDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}     else if (settingVal.equalsIgnoreCase("STWIG")) {
						settings.reloadBaseImageDirectory(settings.shorttwigDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}     else if (settingVal.equalsIgnoreCase("MEDIEVAL")) {
						settings.reloadBaseImageDirectory(settings.medievalDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}     else if (settingVal.equalsIgnoreCase("DALE")) {
						settings.reloadBaseImageDirectory(settings.dalecarlianDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}     else if (settingVal.equalsIgnoreCase("KATAKANA")) {
						settings.reloadBaseImageDirectory(settings.katakanaDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
						settings.changeBoolSetting(Settings.B_FILTER_ENDS, false);
					}     else if (settingVal.equalsIgnoreCase("DCROWN")) {
						settings.reloadBaseImageDirectory(settings.dcrownDictPath);
						// Auto turn off auras, and add spaces between words
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
					}     else if (settingVal.equalsIgnoreCase("EORZEAN")) {
						settings.reloadBaseImageDirectory(settings.eorzeanDictPath);
						// Auto turn off auras, and add spaces between words, and make sure quads are on!
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						settings.changeIntSetting(Settings.I_ALT_MODE, 0);
						settings.changeBoolSetting(Settings.B_ALLOW_QUADS, true); // although only matters if they manually change alt mode...
					}      else {
						response = "Invalid dictionary: " + catArgs + ".";
					}
				}
				
				//   set allow_doubles 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_DOUBLES)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_DOUBLES, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_DOUBLES + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_triples 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_TRIPLES)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_TRIPLES, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_TRIPLES + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_quads 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_QUADS)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_QUADS, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_QUADS + ", please use a range from 0 to 1.";
					}
				}
				
				//   set orientation 1-2
				else if (settingName.equalsIgnoreCase(Settings.I_ORIENTATION)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 1 && intVal <= 2) {
						settings.changeIntSetting(Settings.I_ORIENTATION, intVal);
					} else {
						response = "Invalid value for " + Settings.I_ORIENTATION + ", please use a range from 1 to 2.";
					}
				}
				
				//   set major_style 0-2
				else if (settingName.equalsIgnoreCase(Settings.I_MAJOR_STYLE)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 2) {
						settings.changeIntSetting(Settings.I_MAJOR_STYLE, intVal);
					} else {
						response = "Invalid value for " + Settings.I_MAJOR_STYLE + ", please use a range from 0 to 2.";
					}
				}
				
				
				
				//   set rune_rgb_red / rune_rgb_green / rune_rgb_blue 0-255
				else if (settingName.equalsIgnoreCase(Settings.I_RUNE_RGB_RED)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_RUNE_RGB_RED, intVal);
					} else {
						response = "Invalid value for " + Settings.I_RUNE_RGB_RED + ", please use a range from 0 to 255.";
					}
				}
				else if (settingName.equalsIgnoreCase(Settings.I_RUNE_RGB_GREEN)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_RUNE_RGB_GREEN, intVal);
					} else {
						response = "Invalid value for " + Settings.I_RUNE_RGB_GREEN + ", please use a range from 0 to 255.";
					}
				}
				else if (settingName.equalsIgnoreCase(Settings.I_RUNE_RGB_BLUE)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_RUNE_RGB_BLUE, intVal);
					} else {
						response = "Invalid value for " + Settings.I_RUNE_RGB_BLUE + ", please use a range from 0 to 255.";
					}
				}
				
				//   set back_rgb_red / back_rgb_green / back_rgb_blue 0-255 
				else if (settingName.equalsIgnoreCase(Settings.I_BACK_RGB_RED)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_BACK_RGB_RED, intVal);
					} else {
						response = "Invalid value for " + Settings.I_BACK_RGB_RED + ", please use a range from 0 to 255.";
					}
				}
				else if (settingName.equalsIgnoreCase(Settings.I_BACK_RGB_GREEN)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_BACK_RGB_GREEN, intVal);
					} else {
						response = "Invalid value for " + Settings.I_BACK_RGB_GREEN + ", please use a range from 0 to 255.";
					}
				}
				else if (settingName.equalsIgnoreCase(Settings.I_BACK_RGB_BLUE)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_BACK_RGB_BLUE, intVal);
					} else {
						response = "Invalid value for " + Settings.I_BACK_RGB_BLUE + ", please use a range from 0 to 255.";
					}
				}
				
				//   set allow_staff 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_STAFF)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_STAFF, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_STAFF + ", please use a range from 0 to 1.";
					}
				}
				
				//   set ray_rgb_red / ray_rgb_green / ray_rgb_blue 0-255
				else if (settingName.equalsIgnoreCase(Settings.I_RAY_RGB_RED)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_RAY_RGB_RED, intVal);
					} else {
						response = "Invalid value for " + Settings.I_RAY_RGB_RED + ", please use a range from 0 to 255.";
					}
				}
				else if (settingName.equalsIgnoreCase(Settings.I_RAY_RGB_GREEN)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_RAY_RGB_GREEN, intVal);
					} else {
						response = "Invalid value for " + Settings.I_RAY_RGB_GREEN + ", please use a range from 0 to 255.";
					}
				}
				else if (settingName.equalsIgnoreCase(Settings.I_RAY_RGB_BLUE)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 255) {
						settings.changeIntSetting(Settings.I_RAY_RGB_BLUE, intVal);
					} else {
						response = "Invalid value for " + Settings.I_RAY_RGB_BLUE + ", please use a range from 0 to 255.";
					}
				}
				
				//   set allow_possessive_s 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_POSSESSIVE_S)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_POSSESSIVE_S + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_plural_s 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_PLURAL_S)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_PLURAL_S, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_PLURAL_S + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_auras 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_AURAS)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_AURAS + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_lower_auras 0-1, will not override allow_auras if it is 0
				else if (settingName.equalsIgnoreCase(Settings.B_LOWER_AURAS)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_LOWER_AURAS, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_LOWER_AURAS + ", please use a range from 0 to 1.";
					}
				}
				
				//   set auto_inquisitive 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_AUTO_INQUISITIVE)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_AUTO_INQUISITIVE, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_AUTO_INQUISITIVE + ", please use a range from 0 to 1.";
					}
				}
				
				//   set auto_welkin 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_AUTO_WELKIN)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_AUTO_WELKIN, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_AUTO_WELKIN + ", please use a range from 0 to 1.";
					}
				}
				
				//   set auto_abysm 0-1
				else if (settingName.equalsIgnoreCase(Settings.B_AUTO_ABYSM)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_AUTO_ABYSM, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_AUTO_ABYSM + ", please use a range from 0 to 1.";
					}
				}
				
				//   set image_line_length
				else if (settingName.equalsIgnoreCase(Settings.I_IMAGE_LINE_LENGTH)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 1 && intVal <= 36) {
						settings.changeIntSetting(Settings.I_IMAGE_LINE_LENGTH, intVal);
					} else {
						response = "Invalid value for " + Settings.I_IMAGE_LINE_LENGTH + ", , please use a range from 1 to 36.";
					}
				}
				
				//   set allow_rune_overflow
				else if (settingName.equalsIgnoreCase(Settings.B_RUNE_OVERFLOW)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_RUNE_OVERFLOW, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_RUNE_OVERFLOW + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_ai_dead
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_AI_DEAD)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_AI_DEAD + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_apos_dead
				else if (settingName.equalsIgnoreCase(Settings.I_ALLOW_APOS_DEAD)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 4) {
						settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, intVal);
					} else {
						response = "Invalid value for " + Settings.I_ALLOW_APOS_DEAD + ", please use a range from 0 to 4.";
					}
				}
				
				//   set allow_common_dead
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_COMMON_DEAD)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_COMMON_DEAD + ", please use a range from 0 to 1.";
					}
				}
				
				//   set allow_random_dead
				else if (settingName.equalsIgnoreCase(Settings.B_ALLOW_RANDOM_DEAD)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_ALLOW_RANDOM_DEAD + ", please use a range from 0 to 1.";
					}
				}
				
				//   set random_dead_chance
				else if (settingName.equalsIgnoreCase(Settings.I_RANDOM_DEAD_CHANCE)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 100) {
						settings.changeIntSetting(Settings.I_RANDOM_DEAD_CHANCE, intVal);
					} else {
						response = "Invalid value for " + Settings.I_RANDOM_DEAD_CHANCE + ", please use a range from 0 to 100.";
					}
				}
				
				//   set alt_mode 
				else if (settingName.equalsIgnoreCase(Settings.I_ALT_MODE)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 3) {
						settings.changeIntSetting(Settings.I_ALT_MODE, intVal);
					} else {
						response = "Invalid value for " + Settings.I_ALT_MODE + ", please use a range from 0 to 3.";
					}
				}
				
				//   set filter_ends
				else if (settingName.equalsIgnoreCase(Settings.B_FILTER_ENDS)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_FILTER_ENDS, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_FILTER_ENDS + ", please use a range from 0 to 1.";
					}
				}
				
				//   set insert_spaces
				else if (settingName.equalsIgnoreCase(Settings.B_INSERT_SPACES)) {
					int intVal = Integer.parseInt(settingVal);
					if (intVal >= 0 && intVal <= 1) {
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, settingVal.equals("0") ? false : true);
					} else {
						response = "Invalid value for " + Settings.B_INSERT_SPACES + ", please use a range from 0 to 1.";
					}
				}
				
				// Technically not actually a setting, but we shoved it into the Setting command anyway.
				else if (settingName.equalsIgnoreCase("SHOW")) {
					// Show the current state of all the settings
					response = getAbridgedSettingsString();
					
					
				}
				
				else {
					response = "Setting does not exist: " + settingName;
				}
				
				
				// we need a response if we haven't set one yet. TODO how will this work if we're appending messages together?
				if (response.isEmpty()) {
					response = "Setting " + settingName + " successfully set to " + settingVal + ".";
				}
				
			} catch (Exception e) {
				response = "An unknown error occurred attempting to set setting " + settingName + " with value " + settingVal + ". Exception: " + e.getMessage();
			}
		}
		
		else {
			response = "Command does not exist: " + command;
		}
		
		
		return response;
	}
	
	
	public String generateTimestamp() {
		String path = "";
		Calendar cal = Calendar.getInstance(); // get a timestamp so we can have a unique file name
		path += cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "  " +
				cal.get(Calendar.HOUR) + "h" + cal.get(Calendar.MINUTE) + "m" + cal.get(Calendar.SECOND) + "s";
		if (cal.get(Calendar.AM_PM) == Calendar.AM) {
			path += "AM";
		} else { // must be PM
			path += "PM";
		}
		return path;
	}
	
	
	public String lexifyTextToImage(String text) {
		String response = "";
		Grimoire grim = new Grimoire(settings);
		grim.createKnowlsFromBlob(text);
		
		int lineLength = settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH);
		try {
			String output = grim.getTextDisplayByRuneCount(lineLength);
			
			LinkedList<LinkedList<String>> filepaths = grim.getFilePaths();
			
			if (filepaths.size() > 0) {
				int numRunes = filepaths.get(0).size();
				Videre vi = new Videre(settings, filepaths, numRunes, filepaths.size());
				vi.setVisible(true);
				
				// save a text copy in the output directory for kicks
				String path = settings.getOutputDirectory() + "/" + generateTimestamp() + ".txt";
				String responsePath = settings.getOutputDirectory() + "/" + generateTimestamp() + ".png";
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
				bw.write(output);
				bw.close();
				
				// we've been getting blank image output recently - give it a moment to think before doing the image writing.
				Thread.sleep(500); // giving it a moment to process the image.
				
				// now save an image copy in the output directory. use the same filename, but replace the extension
				vi.writeImage(path);
				response = responsePath;
				

			} else {
				//System.out.println("No valid text to lexify!"); When it sees the response is still empty, it will throw a warning/error.
			}
		} catch (Exception e) {
			//System.out.println("Exception caught: " + e.getMessage()); When it sees the response is still empty, it will throw a warning/error.
		}
		return response;
	}
	
	
	// Returns true if there were no errors
	public boolean loadFileSettings(Settings settings) {
		String errorMsgs = "";
		String setting = "";
		String value = "";
		int fieldCount = 0;
		
		// We are going to default these settings, because changing them has no noticeable effect with the Listener, but may still be checed in code:
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, 0);
		settings.addIntSetting(Settings.I_TEXT_LINE_LENGTH, 40);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(settings.getSettingsPath())));
			while (br.ready()) {
				String lineContents = br.readLine();
				setting = lineContents.substring(0, lineContents.indexOf(" "));
				value = lineContents.substring(lineContents.indexOf("[")+1, lineContents.indexOf("]"));
				
				if (setting.equalsIgnoreCase(Settings.I_MAJOR_STYLE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal != Settings.MAJORS_NONE && intVal != Settings.MAJORS_FEW && intVal != Settings.MAJORS_ALL) {
						errorMsgs += "Error with " + Settings.I_MAJOR_STYLE + ": value is not 0, 1, or 2. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_MAJOR_STYLE, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_ORIENTATION)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal != Settings.LEFT_TO_RIGHT && intVal != Settings.RIGHT_TO_LEFT) {
						errorMsgs += "Error with " + Settings.I_ORIENTATION + ": value is not 1 or 2. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_ORIENTATION, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_IMAGE_LINE_LENGTH)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 1 || intVal > Settings.IMAGE_LINE_LENGTH_MAX) {
						errorMsgs += "Error with " + Settings.I_IMAGE_LINE_LENGTH + ": value is less than 1 or greater than " + Settings.IMAGE_LINE_LENGTH_MAX + ". Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_IMAGE_LINE_LENGTH, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_RANDOM_DEAD_CHANCE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 100) {
						errorMsgs += "Error with " + Settings.I_RANDOM_DEAD_CHANCE + ": value is less than 0 or greater than 100. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_ALT_MODE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal != Settings.ALTS_ALT_ALTS && intVal != Settings.ALTS_ALTS_ONLY && intVal != Settings.ALTS_EQUAL_CHANCE && intVal != Settings.ALTS_NO_ALTS) {
						errorMsgs += "Error with " + Settings.I_ALT_MODE + ": value is not 0, 1, 2 or 3. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_ALT_MODE, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_RUNE_RGB_RED)) {
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
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_STAFF)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_STAFF, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_STAFF, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_STAFF + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.I_RAY_RGB_RED)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_RAY_RGB_RED + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_RAY_RGB_RED, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_RAY_RGB_GREEN)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_RAY_RGB_GREEN + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_RAY_RGB_GREEN, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_RAY_RGB_BLUE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 0 || intVal > 255) {
						errorMsgs += "Error with " + Settings.I_RAY_RGB_BLUE + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_RAY_RGB_BLUE, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_RANDOM_DEAD)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_RANDOM_DEAD + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_DOUBLES)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_DOUBLES + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_TRIPLES)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_TRIPLES + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_QUADS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_QUADS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_QUADS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_POSSESSIVE_S)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_POSSESSIVE_S, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
					} else {
						errorMsgs += "Error with " + Settings.B_POSSESSIVE_S + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_PLURAL_S)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_PLURAL_S, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_PLURAL_S, true);
					} else {
						errorMsgs += "Error with " + Settings.B_PLURAL_S + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_AURAS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_AURAS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_AURAS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_LOWER_AURAS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_LOWER_AURAS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_LOWER_AURAS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_AI_DEAD)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_AI_DEAD + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.I_ALLOW_APOS_DEAD)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal != Settings.APOS_NONE && intVal != Settings.APOS_NEEDED && intVal != Settings.APOS_LEFT && intVal != Settings.APOS_RIGHT && intVal != Settings.APOS_BOTH) {
						errorMsgs += "Error with " + Settings.I_ALLOW_APOS_DEAD + ": value is not 0, 1, 2, 3 or 4. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_COMMON_DEAD)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true);
					} else {
						errorMsgs += "Error with " + Settings.B_ALLOW_COMMON_DEAD + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_RUNE_OVERFLOW)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_RUNE_OVERFLOW, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_RUNE_OVERFLOW, true);
					} else {
						errorMsgs += "Error with " + Settings.B_RUNE_OVERFLOW + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_AUTO_INQUISITIVE)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, true);
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS) == null || settings.getBoolSetting(Settings.B_ALLOW_AURAS) == false) {
							errorMsgs += "Error with " + Settings.B_AUTO_INQUISITIVE + ": " + Settings.B_ALLOW_AURAS + " is set to 0 or failed to set, so Inquisitive Auras can't be used.\n";
						}
						if (settings.getBoolSetting(Settings.B_LOWER_AURAS) == null || settings.getBoolSetting(Settings.B_LOWER_AURAS) == false) {
							errorMsgs += "Error with " + Settings.B_AUTO_INQUISITIVE + ": " + Settings.B_LOWER_AURAS + " is set to 0 or failed to set, so Inquisitive Auras can't be used.\n";
						}
					} else {
						errorMsgs += "Error with " + Settings.B_AUTO_INQUISITIVE + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_AUTO_WELKIN)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_AUTO_WELKIN, true);
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS) == null || settings.getBoolSetting(Settings.B_ALLOW_AURAS) == false) {
							errorMsgs += "Error with " + Settings.B_AUTO_WELKIN + ": " + Settings.B_ALLOW_AURAS + " is set to 0 or failed to set, so Welkin Auras can't be used.\n";
						}
						if (settings.getBoolSetting(Settings.B_LOWER_AURAS) == null || settings.getBoolSetting(Settings.B_LOWER_AURAS) == false) {
							errorMsgs += "Error with " + Settings.B_AUTO_WELKIN + ": " + Settings.B_LOWER_AURAS + " is set to 0 or failed to set, so Welkin Auras can't be used.\n";
						}
					} else {
						errorMsgs += "Error with " + Settings.B_AUTO_WELKIN + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_AUTO_ABYSM)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_AUTO_ABYSM, true);
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS) == null || settings.getBoolSetting(Settings.B_ALLOW_AURAS) == false) {
							errorMsgs += "Error with " + Settings.B_AUTO_ABYSM + ": " + Settings.B_ALLOW_AURAS + " is set to 0 or failed to set, so Abysm Auras can't be used.\n";
						}
						if (settings.getBoolSetting(Settings.B_LOWER_AURAS) == null || settings.getBoolSetting(Settings.B_LOWER_AURAS) == false) {
							errorMsgs += "Error with " + Settings.B_AUTO_ABYSM + ": " + Settings.B_LOWER_AURAS + " is set to 0 or failed to set, so Abysm Auras can't be used.\n";
						}
					} else {
						errorMsgs += "Error with " + Settings.B_AUTO_ABYSM + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_FILTER_ENDS)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_FILTER_ENDS, true);
					} else {
						errorMsgs += "Error with " + Settings.B_FILTER_ENDS + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.B_INSERT_SPACES)) {
					fieldCount++;
					if (value.equalsIgnoreCase("0")) {
						settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
					} else if (value.equalsIgnoreCase("1")) {
						settings.addBoolSetting(Settings.B_INSERT_SPACES, true);
					} else {
						errorMsgs += "Error with " + Settings.B_INSERT_SPACES + ": value is not 0 or 1. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_RUNE_DICTIONARY_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.setBaseImageDirectory(value);
					} else {
						errorMsgs += "Error with " + Settings.STR_RUNE_DICTIONARY_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_OUTPUT_DIR_LOCATION)) {
					fieldCount++;
					// validate the output location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.setOutputDirectory(value);
					} else {
						errorMsgs += "Error with " + Settings.STR_OUTPUT_DIR_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_BASIC_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.basicDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_BASIC_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_BASIC_DICT_HL_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.basicDictHlPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_BASIC_DICT_HL_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_EXPAND_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.expandDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_EXPAND_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_EXPAND_DICT_HL_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.expandDictHlPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_EXPAND_DICT_HL_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_ENGLISH_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.englishDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_ENGLISH_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_FUTHARK_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.futharkDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_FUTHARK_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_FUTHORC_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.futhorcDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_FUTHORC_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_HALSINGE_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.halsingeDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_HALSINGE_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} else if (setting.equalsIgnoreCase(Settings.STR_BRAILLE_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.brailleDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_BRAILLE_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}  else if (setting.equalsIgnoreCase(Settings.STR_TEMPLAR_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.templarDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_TEMPLAR_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}  else if (setting.equalsIgnoreCase(Settings.STR_MASONIC_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.masonicDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_MASONIC_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}  else if (setting.equalsIgnoreCase(Settings.STR_LONGBRANCH_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.longbranchDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_LONGBRANCH_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}  else if (setting.equalsIgnoreCase(Settings.STR_SHORTTWIG_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.shorttwigDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_SHORTTWIG_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}  else if (setting.equalsIgnoreCase(Settings.STR_MEDIEVAL_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.medievalDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_MEDIEVAL_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}  else if (setting.equalsIgnoreCase(Settings.STR_DALECARLIAN_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.dalecarlianDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_DALECARLIAN_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}   else if (setting.equalsIgnoreCase(Settings.STR_KATAKANA_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.katakanaDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_KATAKANA_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}   else if (setting.equalsIgnoreCase(Settings.STR_DCROWN_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.dcrownDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_DCROWN_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				}   else if (setting.equalsIgnoreCase(Settings.STR_EORZEAN_DICT_LOCATION)) {
					fieldCount++;
					// validate the dictionary location
					File file = new File(value);
					if (file.isDirectory()) {
						settings.eorzeanDictPath = value;
					} else {
						errorMsgs += "Error with " + Settings.STR_EORZEAN_DICT_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
					}
				} 
			}
			
			// ensure the settings file actually contained all settings
			if (fieldCount != Settings.LISTENER_FIELD_COUNT) {
				errorMsgs += "Error - The settings file does not have all required settings! Fields counted: " + fieldCount + "    Expected # of Fields: " + Settings.LISTENER_FIELD_COUNT + "\n";
			}
			
			if (!errorMsgs.isEmpty()) {
				System.out.println("\n**********\nErrors while processing the Settings file:\n" + errorMsgs + "Program will now exit. Ok?");
				br.close();
				return false; // return false for the listener run
			}
			
			br.close();
		} catch (NumberFormatException e) {
			System.out.println("\n**********\nError reading in settings file. A number value is needed for the following setting:\n" + setting + "\nValue was: " + value);
			System.out.println("Program will now exit. Ok?");
			return false; // return false for the listener run
		} catch (FileNotFoundException e) {
			System.out.println("\n**********\nError reading in settings file. The Settings File could not be found at: " + settings.getSettingsPath());
			System.out.println("Program will now exit. Ok?");
			return false; // return false for the listener run
		} catch (Exception e) {
			System.out.println("\n**********\nUnknown Error reading in settings file. Program will now exit. Exception was: " + e.getMessage());
			return false; // return false for the listener run
		}
		return true; // successfully init all the settings for the MainListener
	}
	
	public String getAbridgedSettingsString() {
		String content = "";
		
		// Print rune dictionary name only
		String dirPath = settings.getRuneDictionary().getBaseDir();
		dirPath = dirPath.substring(0, dirPath.length()-1);
		int lastIndex = dirPath.lastIndexOf("\\");
		if (lastIndex == -1) {
			lastIndex = dirPath.lastIndexOf("/");
		}
		dirPath = dirPath.substring(lastIndex+1);
		content += "Rune Dictionary: " + dirPath + "\n\n";
		
		// These 2 are not publically visible via the Discord bot:
		//content += (Settings.STR_RUNE_DICTIONARY_LOCATION + " [" + settings.getBaseImageDirectory() + "]\n");
		//content += (Settings.STR_OUTPUT_DIR_LOCATION + " [" + settings.getOutputDirectory() + "]\n");
		
		// Rune formatting settings
		content += "Rune Formatting Settings:\n";
		content += (Settings.B_ALLOW_DOUBLES + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_DOUBLES) + "]\n"); 
		content += (Settings.B_ALLOW_TRIPLES + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_TRIPLES) + "]\n"); 
		content += (Settings.B_ALLOW_QUADS + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_QUADS) + "]\n"); 
		content += (Settings.B_POSSESSIVE_S + " [" + settings.getBoolSettingAsInt(Settings.B_POSSESSIVE_S) + "]\n"); 
		content += (Settings.B_PLURAL_S + " [" + settings.getBoolSettingAsInt(Settings.B_PLURAL_S) + "]\n"); 
		content += (Settings.B_ALLOW_AI_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_AI_DEAD) + "]\n"); 
		content += (Settings.I_ALLOW_APOS_DEAD + " [" + settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) + "]\n");
		content += (Settings.B_ALLOW_COMMON_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_COMMON_DEAD) + "]\n"); 
		content += (Settings.B_ALLOW_RANDOM_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_RANDOM_DEAD) + "]\n"); 
		content += (Settings.I_RANDOM_DEAD_CHANCE + " [" + settings.getIntSetting(Settings.I_RANDOM_DEAD_CHANCE) + "]\n"); 
		content += (Settings.I_ALT_MODE + " [" + settings.getIntSetting(Settings.I_ALT_MODE) + "]\n");
		content += (Settings.I_ORIENTATION + " [" + settings.getIntSetting(Settings.I_ORIENTATION) + "]\n"); 
		content += (Settings.B_FILTER_ENDS + " [" + settings.getBoolSettingAsInt(Settings.B_FILTER_ENDS) + "]\n"); 
		content += (Settings.B_INSERT_SPACES + " [" + settings.getBoolSettingAsInt(Settings.B_INSERT_SPACES) + "]\n\n");
		
		// Aura formatting settings
		content += "Aura Formatting Settings:\n";
		content += (Settings.I_MAJOR_STYLE + " [" + settings.getIntSetting(Settings.I_MAJOR_STYLE) + "]\n");
		content += (Settings.B_ALLOW_AURAS + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_AURAS) + "]\n"); 
		content += (Settings.B_LOWER_AURAS + " [" + settings.getBoolSettingAsInt(Settings.B_LOWER_AURAS) + "]\n"); 
		content += (Settings.B_AUTO_INQUISITIVE + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_INQUISITIVE) + "]\n"); 
		content += (Settings.B_AUTO_WELKIN + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_WELKIN) + "]\n"); 
		content += (Settings.B_AUTO_ABYSM + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_ABYSM) + "]\n\n"); 
		
		// Image formatting settings
		content += "Image Formatting Settings:\n";
		content += (Settings.I_IMAGE_LINE_LENGTH + " [" + settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH) + "]\n"); 
		content += (Settings.B_RUNE_OVERFLOW + " [" + settings.getBoolSettingAsInt(Settings.B_RUNE_OVERFLOW) + "]\n"); 
		content += (Settings.I_RUNE_RGB_RED + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_RED) + "]\n"); 
		content += (Settings.I_RUNE_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_GREEN) + "]\n"); 
		content += (Settings.I_RUNE_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_BLUE) + "]\n"); 
		content += (Settings.I_BACK_RGB_RED + " [" + settings.getIntSetting(Settings.I_BACK_RGB_RED) + "]\n"); 
		content += (Settings.I_BACK_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_BACK_RGB_GREEN) + "]\n"); 
		content += (Settings.I_BACK_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_BACK_RGB_BLUE) + "]\n"); 
		content += (Settings.B_ALLOW_STAFF + "[" + settings.getBoolSettingAsInt(Settings.B_ALLOW_STAFF) + "]\n");
		content += (Settings.I_RAY_RGB_RED + " [" + settings.getIntSetting(Settings.I_RAY_RGB_RED) + "]\n"); 
		content += (Settings.I_RAY_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_RAY_RGB_GREEN) + "]\n"); 
		content += (Settings.I_RAY_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_RAY_RGB_BLUE) + "]\n\n"); 
		
		// These 2 are unused in the Discord Bot:
		//content += (Settings.I_DISPLAY_STYLE + " [" + settings.getIntSetting(Settings.I_DISPLAY_STYLE) + "]\n");
		//content += (Settings.I_TEXT_LINE_LENGTH + " [" + settings.getIntSetting(Settings.I_TEXT_LINE_LENGTH) + "]\n"); 
		
		return content;
	}
}

