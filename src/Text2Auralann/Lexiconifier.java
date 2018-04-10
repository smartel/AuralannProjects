package Text2Auralann;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;

public class Lexiconifier {

	protected static final String separator = "\n===========================================";	
	protected String defBaseDir;
	protected String outputDir;
	protected String personalPath;
	protected Settings settings;
	
	public Lexiconifier() {
		// populate with default values
		defBaseDir = System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\";
		outputDir = System.getProperty("user.dir") + "\\Output\\";
		personalPath = System.getProperty("user.dir") + Settings.PERSONAL_PATH;
		settings = new Settings();
	}
	
	public void runTextMode() {
		
		Scanner input = new Scanner(System.in);
		
		// Before anything, see if there is a settings file. If there is, use it. Otherwise, use default values.
		String settingsPath = "";
		
		boolean init = false;
		while (!init) {
			init = true;
			System.out.println("Please give the filepath to a Settings file:");
			System.out.println("(Type '~' to use the Personal Settings file, if it exists at: " + personalPath + "\nType 'DB' for default beginner values. Type 'DE' for default expert values. Type 'H' for help on making a Settings file)");

			settingsPath = input.nextLine().trim();
			System.out.println(separator);
			
			if (settingsPath.equals("~")) {
				File file = new File(personalPath);
				if (file.isFile()) {
					settings.setSettingsPath(System.getProperty("user.dir") + Settings.PERSONAL_PATH);
					loadFileSettings(settings, input);
				} else {
					System.out.println("!! Could not find Settings File to use at: " + personalPath + "!!\n");
					init = false; // so we loop again
				}
			} else if (settingsPath.equals("DB")) {
				loadDefaultSettingsBeginner(settings);
			} else if (settingsPath.equals("DE")) {
				loadDefaultSettingsExpert(settings);
			} else if (settingsPath.equalsIgnoreCase("H")) {
				System.out.println("<> Settings - Help <>\n");
				System.out.println(getSettingsHelp());
				System.out.println("\nOk? (any input to continue)");
				// take some input before displaying anything else
				input.nextLine();
				init = false; // so we loop back to the beginning
				System.out.println(separator);
			} else {
				settings.setSettingsPath(settingsPath);
				loadFileSettings(settings, input);
			}
		}
		
		// if a file was not provided, need to determine the rune directory and output directory
		if (settingsPath.equals("DB") || settingsPath.equals("DE") || settingsPath.equals("H")) {
			// Before looping, confirm the directory to load runes from.
			System.out.println(separator + "\nMost of the default values have been set.\nPlease give the path to the base Rune directory. Type '~' to use the default directory.\nDefault directory: " + defBaseDir);
			
			String baseDir = input.nextLine().trim();
			System.out.println(separator);
			if (baseDir.equalsIgnoreCase("~")) {
				settings.setBaseImageDirectory(defBaseDir);
			} else {
				// validate before using. use default if invalid
				File file = new File(baseDir + "\\SingleGlyphs\\");
				if (file.isDirectory()) {
					settings.setBaseImageDirectory(baseDir);
				} else {
					System.out.println("Could not find a Rune Dictionary to use at: " + baseDir + "!!\nUsing the default path: " + defBaseDir + "\nOk?");
					input.nextLine();
					settings.setBaseImageDirectory(defBaseDir);
				}
			}
			
			// Also confirm the directory to output text / image files to before looping
			System.out.println(separator + "\nPlease give the path to the output directory. Type '~' to use the default directory.\nDefault directory: " + outputDir);
			String outDir = input.nextLine().trim();
			if (outDir.equalsIgnoreCase("~")) {
				settings.setOutputDirectory(outputDir);
			} else {
				
				// validate before using. use default if invalid
				File file = new File(outDir);
				if (file.isDirectory()) {				
					settings.setOutputDirectory(outDir);
				} else {
					System.out.println("Could not find a directory to use at: " + outDir + "\nUsing default path: " + outputDir + "\nOk?");
					input.nextLine();
					settings.setOutputDirectory(outputDir);
				}
			}
		}
		
		boolean run = true;
		while (run) {
			System.out.println(separator);
			System.out.println("Main Menu\n1 - Lexify Text - Text Output\n2 - Lexify Text - Image Output\n3 - Basic Options\n4 - Advanced Options\n5 - View / Change Rune Dictionary\n6 - Load / Save Settings Files\n0 - Exit Program");
			String in = input.nextLine();
			
			if (in.equalsIgnoreCase("1")) { // lexify - text output
				lexifyTextToOutput(input, settings);
			} else if (in.equalsIgnoreCase("2")) { // lexify - image output
				lexifyTextToImage(input, settings);
			} else if (in.equalsIgnoreCase("3")) { // basic options
				setBasicOptions(input, settings);
			} else if (in.equalsIgnoreCase("4")) { // advanced options
				setAdvancedOptions(input, settings);
			} else if (in.equalsIgnoreCase("5")) { // view / change rune dictionary
				runeDictionaryOptions(input, settings);
			} else if (in.equalsIgnoreCase("6")) { // load / save settings file
				settingsOptions(input, settings);
			} else if (in.equalsIgnoreCase("0")) { // exit program
				System.out.println(separator + "\nClosing program.");
				run = false;
			} else {
				System.out.println(separator + "\nUnknown input.");
			}
			
			if (!in.equalsIgnoreCase("0")) {
				System.out.println(separator + "\nReturning to main menu...");
				in = input.nextLine();
			}
		}

		System.exit(0);
	}

	
	public void lexifyTextToOutput(Scanner input, Settings settings) {
		System.out.println(separator);
		System.out.println("<> Lexify Text - Text Output <>");
		System.out.println("Insert the text blob to turn into Auralann.\nUse \"^\" to insert a dead rune in its position.\nUse \"~\" to designate the end of the blob.\nA space will be inserted between chunks.");
		String blob = "";
		String chunk = input.nextLine();
		while (!chunk.equals("~")) {
			blob += " " + chunk;
			chunk = input.nextLine();
		}
		System.out.println(separator + "\nLexifying...");
		Grimoire grim = new Grimoire(settings);
		grim.createKnowlsFromBlob(blob);
		try {
			String output = grim.getTextDisplayByRuneCount(settings.getIntSetting(Settings.I_TEXT_LINE_LENGTH));
			System.out.println(output);
			// save a copy in the output directory
			String path = settings.getOutputDirectory() + "/" + generateTimestamp() + ".txt";
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
			bw.write(output);
			bw.close();
		} catch (Exception e) { // missing rune rde exception
			System.out.println("Exception caught: " + e.getMessage());
		}
	}
	
	public void lexifyTextToImage(Scanner input, Settings settings) {
		System.out.println(separator);
		System.out.println("<> Lexify Text - Image Output <>");
		System.out.println("Insert the text blob to turn into Auralann.\nUse \"^\" to insert a dead rune in its position.\nUse \"~\" to designate the end of the blob.\nA space will be inserted between chunks.");
		String blob = "";
		String chunk = input.nextLine();
		while (!chunk.equals("~")) {
			blob += " " + chunk;
			chunk = input.nextLine();
		}
		System.out.println(separator + "\nLexifying...");
		Grimoire grim = new Grimoire(settings);
		grim.createKnowlsFromBlob(blob);
		
		int lineLength = settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH);
		try {
			String output = grim.getTextDisplayByRuneCount(lineLength);
			
			LinkedList<LinkedList<String>> filepaths = grim.getFilePaths();
			
			if (filepaths.size() > 0) {
				int numRunes = filepaths.get(0).size();
				Videre vi = new Videre(settings, filepaths, numRunes, filepaths.size());
				//vi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				vi.setVisible(true);
				
				// save a text copy in the output directory for kicks
				String path = settings.getOutputDirectory() + "/" + generateTimestamp() + ".txt";
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
				bw.write(output);
				bw.close();
				
				// we've been getting blank image output recently - give it a second to think before doing the image writing.
				System.out.println("Give me a second to process... (hit any key once the Videre Rune Viewer appears)");
				input.nextLine();
				
				// now save an image copy in the output directory. use the same filename, but replace the extension
				vi.writeImage(path);

			} else {
				System.out.println("No valid text to lexify!");
			}
		} catch (Exception e) {
			System.out.println("Exception caught: " + e.getMessage());
		}
	}
	
	
	public void setBasicOptions(Scanner input, Settings settings) {
		
		boolean run = true;
		while (run) {
			System.out.println(separator);
			System.out.println("<> Basic Options <>");
			String allowDoubles = settings.getBoolSetting(Settings.B_ALLOW_DOUBLES) ? "ON" : "OFF";
			String allowTriples = settings.getBoolSetting(Settings.B_ALLOW_TRIPLES) ? "ON" : "OFF";
			String allowQuads = settings.getBoolSetting(Settings.B_ALLOW_QUADS) ? "ON" : "OFF";
			String orientation = settings.getIntSetting(Settings.I_ORIENTATION) == Settings.LEFT_TO_RIGHT ? "LEFT-TO-RIGHT" : "RIGHT-TO-LEFT";
			String majorStyleDesc = "Major styles:\nStyle 0 - No majors, no meagers\nStyle 1 - Majors on first and last lores, meagers if needed\nStyle 2 - Majors on every lore, meagers if needed";
			String majorDisplay = "";
			int majorInt = settings.getIntSetting(Settings.I_MAJOR_STYLE);
			if (majorInt == Settings.MAJORS_NONE) {
				majorDisplay = "No Majors";
			} else if (majorInt == Settings.MAJORS_FEW) {
				majorDisplay = "Few Majors";
			} else if (majorInt == Settings.MAJORS_ALL) {
				majorDisplay = "All Majors";
			}
			String textStyleDesc = "Display styles:\nStyle 0 - Rune only (like 'O')\nStyle 1 - Rune and Form (like 'O-1' or 'O-2')\nStyle 2 - File Path (like '.../O-1.png')";
			String textDisplay = "";
			int displayInt = settings.getIntSetting(Settings.I_DISPLAY_STYLE);
			if (displayInt == Settings.DISPLAY_RUNE_ONLY) {
				textDisplay = "Rune only";
			} else if (displayInt == Settings.DISPLAY_RUNE_FORM) {
				textDisplay = "Rune and Form";
			} else if (displayInt == Settings.DISPLAY_RUNE_FILE) {
				textDisplay = "File path";
			}
			String runeColorLine = "(" + settings.getIntSetting(Settings.I_RUNE_RGB_RED) + "," + settings.getIntSetting(Settings.I_RUNE_RGB_GREEN) + "," +
							   settings.getIntSetting(Settings.I_RUNE_RGB_BLUE) + ")";
			String backColorLine = "(" + settings.getIntSetting(Settings.I_BACK_RGB_RED) + "," + settings.getIntSetting(Settings.I_BACK_RGB_GREEN) + "," +
					   settings.getIntSetting(Settings.I_BACK_RGB_BLUE) + ")";
			String allowStaff = settings.getBoolSetting(Settings.B_ALLOW_STAFF) ? "ON" : "OFF";
			String rayColorLine = "(" + settings.getIntSetting(Settings.I_RAY_RGB_RED) + "," + settings.getIntSetting(Settings.I_RAY_RGB_GREEN) + "," +
					   settings.getIntSetting(Settings.I_RAY_RGB_BLUE) + ")";
			
			System.out.println(" 1 - Allow Double Glyphs - currently: " + allowDoubles);
			System.out.println(" 2 - Allow Triple Glyphs - currently: " + allowTriples);
			System.out.println(" 3 - Allow Quad Glyphs - currently: " + allowQuads);
			System.out.println(" 4 - Parsing for double/triple glyphs orientation - currently: " + orientation);
			System.out.println(" 5 - Change Major Style - currently: " + majorDisplay);
			System.out.println(" 6 - Change Text Display Style - currently: " + textDisplay);
			System.out.println(" 7 - Change Rune RGB values in Image Output - currently (RGB): " + runeColorLine);
			System.out.println(" 8 - Change Background RGB values in Image Output - currently (RGB): " + backColorLine);
			System.out.println(" 9 - Allow Staff in Image Output - currently: " + allowStaff);
			System.out.println("10 - Change Staff's Ray RGB values in Image Output - currently (RGB): " + rayColorLine);
			System.out.println(" 0 - Exit Basic Options");
			
			String val = input.nextLine();
			
			if (val.equalsIgnoreCase("1")) {
				
				System.out.println(separator + "\nToggling Double Glyphs...");
				settings.toggleBoolSetting(Settings.B_ALLOW_DOUBLES);
				
			} else if (val.equalsIgnoreCase("2")) {

				System.out.println(separator + "\nToggling Triple Glyphs...");
				settings.toggleBoolSetting(Settings.B_ALLOW_TRIPLES);
				
			} else if (val.equalsIgnoreCase("3")) {

				System.out.println(separator + "\nToggling Quad Glyphs...");
				settings.toggleBoolSetting(Settings.B_ALLOW_QUADS);
				
			} else if (val.equalsIgnoreCase("4")) {
			
				System.out.println(separator + "\nChanging orientation...");
				int newOrien = settings.getIntSetting(Settings.I_ORIENTATION) == Settings.LEFT_TO_RIGHT ?
						Settings.RIGHT_TO_LEFT : Settings.LEFT_TO_RIGHT;
				settings.changeIntSetting(Settings.I_ORIENTATION, newOrien);
				
			} else if (val.equalsIgnoreCase("5")) {
				
				System.out.println(separator + "\n<> Changing Major Style <>");
				System.out.println(majorStyleDesc);
				System.out.println("Change to which style? (0-2)");
				String style = input.nextLine();
				if (style.equalsIgnoreCase("0")) {
					settings.changeIntSetting(Settings.I_MAJOR_STYLE, 0);
				} else if (style.equalsIgnoreCase("1")) {
					settings.changeIntSetting(Settings.I_MAJOR_STYLE, 1);
				} else if (style.equalsIgnoreCase("2")) {
					settings.changeIntSetting(Settings.I_MAJOR_STYLE, 2);
				} else {
					System.out.println(separator + "\nUnknown style. Cancelling style change.");
				}
				
				if (style.equalsIgnoreCase("0") || style.equalsIgnoreCase("1") || style.equalsIgnoreCase("2")) {
					System.out.println(separator + "\nStyle successfully changed.");
				}
				
			} else if (val.equalsIgnoreCase("6")) {
				
				System.out.println(separator + "\n<> Changing Display Style <>");
				System.out.println(textStyleDesc);
				System.out.println("Change to which style? (0-2)");
				String style = input.nextLine();
				if (style.equalsIgnoreCase("0")) {
					settings.changeIntSetting(Settings.I_DISPLAY_STYLE, 0);
				} else if (style.equalsIgnoreCase("1")) {
					settings.changeIntSetting(Settings.I_DISPLAY_STYLE, 1);
				} else if (style.equalsIgnoreCase("2")) {
					settings.changeIntSetting(Settings.I_DISPLAY_STYLE, 2);
				} else {
					System.out.println(separator + "\nUnknown style. Cancelling style change.");
				}
				
				if (style.equalsIgnoreCase("0") || style.equalsIgnoreCase("1") || style.equalsIgnoreCase("2")) {
					System.out.println(separator + "\nStyle successfully changed.");
				}
				
			} else if (val.equalsIgnoreCase("7")) {

				System.out.println(separator + "\nChanging RGB values for the Runes in the image...");
				int amount = 0;
				
				boolean ready = false;
				while (!ready) {
					System.out.println("What is the value for red? (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_RED));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for red! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_RED));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_RED));
					}
				}
				settings.changeIntSetting(Settings.I_RUNE_RGB_RED, amount);
				
				ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for green? (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_GREEN));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for green! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_GREEN));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_GREEN));
					}
				}
				settings.changeIntSetting(Settings.I_RUNE_RGB_GREEN, amount);
				
				ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for blue? (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_BLUE));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for blue! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_BLUE));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_RUNE_RGB_BLUE));
					}
				}
				settings.changeIntSetting(Settings.I_RUNE_RGB_BLUE, amount);
					
			} else if (val.equalsIgnoreCase("8")) {
				
				System.out.println(separator + "\nChanging RGB values for the background in the image...");
				int amount = 0;
				
				boolean ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for red? (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_RED));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for red! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_RED));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_RED));
					}
				}
				settings.changeIntSetting(Settings.I_BACK_RGB_RED, amount);
				
				ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for green? (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_GREEN));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for green! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_GREEN));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_GREEN));
					}
				}
				settings.changeIntSetting(Settings.I_BACK_RGB_GREEN, amount);
				
				ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for blue? (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_BLUE));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for blue! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_BLUE));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_BACK_RGB_BLUE));
					}
				}
				settings.changeIntSetting(Settings.I_BACK_RGB_BLUE, amount);
					
			} else if (val.equalsIgnoreCase("9")) {
				
				System.out.println(separator + "\nToggling Staff...");
				settings.toggleBoolSetting(Settings.B_ALLOW_STAFF);
				
			} else if (val.equalsIgnoreCase("10")) {
				
				System.out.println(separator + "\nChanging RGB values for the Rays in the image...");
				int amount = 0;
				
				boolean ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for red? (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_RED));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for red! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_RED));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_RED));
					}
				}
				settings.changeIntSetting(Settings.I_RAY_RGB_RED, amount);
				
				ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for green? (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_GREEN));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for green! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_GREEN));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_GREEN));
					}
				}
				settings.changeIntSetting(Settings.I_RAY_RGB_GREEN, amount);
				
				ready = false;
				while (!ready) {
					System.out.println(separator + "\nWhat is the value for blue? (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_BLUE));
					try {
						amount = Integer.parseInt(input.nextLine());
						while (amount > 255 || amount < 0) {
							System.out.println(separator + "\nInvalid color value for blue! Input a proper value (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_BLUE));
							amount = Integer.parseInt(input.nextLine());
						}
						ready = true;
					} catch (NumberFormatException e) {
						System.out.println(separator + "\nThat is not a number!");// Input a proper numeric value (0-255), current is: " + settings.getIntSetting(Settings.I_RAY_RGB_BLUE));
					}
				}
				settings.changeIntSetting(Settings.I_RAY_RGB_BLUE, amount);
				
			} else if (val.equalsIgnoreCase("0")) {
				run = false;
			} else {
				System.out.println(separator + "\nUnknown command.");
			}
		}
	}
	
	
	public void setAdvancedOptions(Scanner input, Settings settings) {
		System.out.println(separator);
		System.out.println("<> Advanced Options <>");
		
		boolean run = true;
		while (run) {
			System.out.println(separator);
			System.out.println("Advanced Options");
			String allowPossess = settings.getBoolSetting(Settings.B_POSSESSIVE_S) ? "ON" : "OFF";
			String allowPlural = settings.getBoolSetting(Settings.B_PLURAL_S) ? "ON" : "OFF";
			String allowLowerAura = settings.getBoolSetting(Settings.B_LOWER_AURAS) ? "ON" : "OFF";
			String allowAuras = settings.getBoolSetting(Settings.B_ALLOW_AURAS) ? "ON" : "OFF";
			String allowInquis = settings.getBoolSetting(Settings.B_AUTO_INQUISITIVE) ? "ON" : "OFF";
			String allowWelkin = settings.getBoolSetting(Settings.B_AUTO_WELKIN) ? "ON" : "OFF";
			String allowAbysm = settings.getBoolSetting(Settings.B_AUTO_ABYSM) ? "ON" : "OFF";
			String allowRuneOverflow = settings.getBoolSetting(Settings.B_RUNE_OVERFLOW) ? "ON" : "OFF";
			String allowAIDead = settings.getBoolSetting(Settings.B_ALLOW_AI_DEAD) ? "ON" : "OFF";
			String allowCommonDead = settings.getBoolSetting(Settings.B_ALLOW_COMMON_DEAD) ? "ON" : "OFF";
			String allowRandomDead = settings.getBoolSetting(Settings.B_ALLOW_RANDOM_DEAD) ? "ON" : "OFF";
			String filterEnds = settings.getBoolSetting(Settings.B_FILTER_ENDS) ? "ON" : "OFF";
			String insertSpaces = settings.getBoolSetting(Settings.B_INSERT_SPACES) ? "ON" : "OFF";
			String altMode = "";
			int nAltMode = settings.getIntSetting(Settings.I_ALT_MODE);
			if (nAltMode == Settings.ALTS_NO_ALTS) {
				altMode = "No Alternate Runes";
			} else if (nAltMode == Settings.ALTS_ALTS_ONLY) {
				altMode = "Always use Alternates";
			} else if (nAltMode == Settings.ALTS_EQUAL_CHANCE) {
				altMode = "Equal chance to be Alternate";
			} else if (nAltMode == Settings.ALTS_ALT_ALTS) {
				altMode = "Don't repeat Alternates";
			}
			String allowApos = "";
			int nApos = settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD);
			if (nApos == Settings.APOS_NONE) {
				allowApos = "OFF";
			} else if (nApos == Settings.APOS_NEEDED) {
				allowApos = "As needed";
			} else if (nApos == Settings.APOS_LEFT) {
				allowApos = "Left only";
			} else if (nApos == Settings.APOS_RIGHT) {
				allowApos = "Right only";
			} else if (nApos == Settings.APOS_BOTH) {
				allowApos = "Both left and right";
			}
			
			System.out.println(" 1 - Allow Possessive-S ('S) Rune, will work even if Double Glyphs are not allowed - currently: " + allowPossess);
			System.out.println(" 2 - Allow Plural-S Rune at the end of Lores (all Lores ending with S will use it) - currently: " + allowPlural);
			System.out.println(" 3 - Allow Auras (vs Implied Auras. If Off, will override Lower, Inquisitive, and Evocative Auras) - currently: " + allowAuras);
			System.out.println(" 4 - Allow Lower Auras (does not override \"Allow Auras\". Turning off will Disable Inquisitive and Evocative Auras) - currently: " + allowLowerAura);
			System.out.println(" 5 - Allow Automatic Inquisitive Auras (used when a Knowl ends in \"?\". Will turn on Lower Auras) - currently: " + allowInquis);
			System.out.println(" 6 - Allow Automatic Welkin Evocative Auras (used when a Knowl ends in \"!\". Will turn on Lower Auras. Stacks with Inquis but not Abysm. Priority over Abysm.) - currently: " + allowWelkin);
			System.out.println(" 7 - Allow Automatic Abysm Evocative Auras (used when a Knowl ends in excessive \".\". Will turn on Lower Auras. Stacks with Inquis but not Welkin.) - currently: " + allowAbysm);
			System.out.println(" 8 - Change Number of Runes in a single line when lexifying for text output - currently: " + settings.getIntSetting(Settings.I_TEXT_LINE_LENGTH));
			System.out.println(" 9 - Change Number of Runes in a single line when lexifying for image output - currently: " + settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH));
			System.out.println("10 - Allow Rune Overflow (if you have x runes per line, will do x+1 instead if rune x is left part of a major aura) - currently: " + allowRuneOverflow);
			System.out.println("11 - Allow Commonly-Used Dead Runes to One-Rune Lores A & I - currently: " + allowAIDead);
			System.out.println("12 - Allow Commonly-Used Dead Rune next to Apostrophe Rune - currently: " + allowApos);
			System.out.println("13 - Allow Commonly-Used Dead Rune after generic One-Rune Lore - currently: " + allowCommonDead);
			System.out.println("14 - Allow randomly placed Dead Runes - currently: " + allowRandomDead);
			System.out.println("15 - Change chance that random Dead Runes will appear - currently: " + settings.getIntSetting(Settings.I_RANDOM_DEAD_CHANCE) + "%");
			System.out.println("16 - Change Alternate Rune usage - currently: " + altMode);
			System.out.println("17 - Filter out End Symbols from output - currently: " + filterEnds);
			System.out.println("18 - Insert Spaces between Lores - currently: " + insertSpaces);
			System.out.println("19 - Change output file directory - currently: " + settings.getOutputDirectory());
			System.out.println("0 - Exit Advanced Options");
			
			String val = input.nextLine();
			
			if (val.equalsIgnoreCase("1")) {
				
				System.out.println(separator + "\nToggling Possessive-S...");
				settings.toggleBoolSetting(Settings.B_POSSESSIVE_S);
				
			} else if (val.equalsIgnoreCase("2")) {

				System.out.println(separator + "\nToggling Plural-S...");
				settings.toggleBoolSetting(Settings.B_PLURAL_S);
				
			} else if (val.equalsIgnoreCase("3")) {

				System.out.println(separator + "\nToggling Auras...");
				settings.toggleBoolSetting(Settings.B_ALLOW_AURAS);
				
			} else if (val.equalsIgnoreCase("4")) {

				System.out.println(separator + "\nToggling Lower Auras...");
				settings.toggleBoolSetting(Settings.B_LOWER_AURAS);
				
				if ((settings.getBoolSetting(Settings.B_LOWER_AURAS) != true) && (settings.getBoolSetting(Settings.B_AUTO_INQUISITIVE))) {
					// if lower auras were just turned off, and inquisitive was is on, we need to disable inquisitive
					System.out.println("Also disabling Inquisitive Auras!");
					settings.toggleBoolSetting(Settings.B_AUTO_INQUISITIVE);
				}
				
				if ((settings.getBoolSetting(Settings.B_LOWER_AURAS) != true) && (settings.getBoolSetting(Settings.B_AUTO_WELKIN))) {
					// if lower auras were just turned off, and evocative was is on, we need to disable inquisitive
					System.out.println("Also disabling Welkin Evocative Auras!");
					settings.toggleBoolSetting(Settings.B_AUTO_WELKIN);
				}

				if ((settings.getBoolSetting(Settings.B_LOWER_AURAS) != true) && (settings.getBoolSetting(Settings.B_AUTO_ABYSM))) {
					// if lower auras were just turned off, and evocative was is on, we need to disable inquisitive
					System.out.println("Also disabling Abysm Evocative Auras!");
					settings.toggleBoolSetting(Settings.B_AUTO_ABYSM);
				}

			} else if (val.equalsIgnoreCase("5")) {
				
				System.out.println(separator + "\nToggling Automatic Inquisitive Auras...");
				settings.toggleBoolSetting(Settings.B_AUTO_INQUISITIVE);
				if ((settings.getBoolSetting(Settings.B_LOWER_AURAS) != true) && (settings.getBoolSetting(Settings.B_AUTO_INQUISITIVE))) {
					// if lower auras were off, and inquisitive was just turned on, we need to enable lower auras
					System.out.println("Also enabling Lower Auras!");
					settings.toggleBoolSetting(Settings.B_LOWER_AURAS);
				}
				
			} else if (val.equalsIgnoreCase("6")) {
				
				System.out.println(separator + "\nToggling Automatic Welkin Evocative Auras...");
				settings.toggleBoolSetting(Settings.B_AUTO_WELKIN);
				if ((settings.getBoolSetting(Settings.B_LOWER_AURAS) != true) && (settings.getBoolSetting(Settings.B_AUTO_WELKIN))) {
					// if lower auras were off, and evocation was just turned on, we need to enable lower auras
					System.out.println("Also enabling Lower Auras!");
					settings.toggleBoolSetting(Settings.B_LOWER_AURAS);
				}
				
			} else if (val.equalsIgnoreCase("7")) {
				
				System.out.println(separator + "\nToggling Automatic Abysm Evocative Auras...");
				settings.toggleBoolSetting(Settings.B_AUTO_ABYSM);
				if ((settings.getBoolSetting(Settings.B_LOWER_AURAS) != true) && (settings.getBoolSetting(Settings.B_AUTO_ABYSM))) {
					// if lower auras were off, and evocation was just turned on, we need to enable lower auras
					System.out.println("Also enabling Lower Auras!");
					settings.toggleBoolSetting(Settings.B_LOWER_AURAS);
				}
				
			} else if (val.equalsIgnoreCase("8")) {

				System.out.println(separator + "\nHow many runes should be in a line (when lexifying text only)? (minimum: 1)");
				try {
					int numRunes = Integer.parseInt(input.nextLine());
					if (numRunes > 0) {
						settings.changeIntSetting(Settings.I_TEXT_LINE_LENGTH, numRunes);	
					} else {
						System.out.println(separator + "\nInvalid rune count! Cancelling...");
					}
				} catch (Exception e) {
					System.out.println(separator + "\nInvalid rune count! Cancelling...");
				}
				
			} else if (val.equalsIgnoreCase("9")) {

				System.out.println(separator + "\nHow many runes should be in a line (when lexifying for image output)?\nNote: the paired text output will also use this value. (minimum: 1, maximum: " + Settings.IMAGE_LINE_LENGTH_MAX + ")");
				try {
					int numRunes = Integer.parseInt(input.nextLine());
					if (numRunes > 0 && numRunes <= Settings.IMAGE_LINE_LENGTH_MAX) {
						settings.changeIntSetting(Settings.I_IMAGE_LINE_LENGTH, numRunes);	
					} else {
						System.out.println(separator + "\nInvalid rune count! Cancelling...");
					}
				} catch (Exception e) {
					System.out.println(separator + "\nInvalid rune count! Cancelling...");
				}
				
			}  else if (val.equalsIgnoreCase("10")) {

				System.out.println(separator + "\nToggling Rune Overflow...");
				settings.toggleBoolSetting(Settings.B_RUNE_OVERFLOW);
				
			} else if (val.equalsIgnoreCase("11")) {

				System.out.println(separator + "\nToggling Dead Runes on One-Lore A & I...");
				settings.toggleBoolSetting(Settings.B_ALLOW_AI_DEAD);
				
			} else if (val.equalsIgnoreCase("12")) {
				System.out.println(separator + "\nChanging Apos-Dead Usage...");
				System.out.println("0 - No Apos-Dead Rune allowed\n1 - Apos-Dead Rune only used as needed\n2 - Apos-Dead always on the left of an apostrophe\n3 - Apos-Dead always on the right of an apostrophe\n4 - Apos-Dead always on both the left and right of an apostrophe");
				System.out.println("Change to which mode? (0-4)");
				String style = input.nextLine();
				if (style.equalsIgnoreCase("0")) {
					settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_NONE);
				} else if (style.equalsIgnoreCase("1")) {
					settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_NEEDED);
				} else if (style.equalsIgnoreCase("2")) {
					settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_LEFT);
				} else if (style.equalsIgnoreCase("3")) {
					settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_RIGHT);
				} else if (style.equalsIgnoreCase("4")) {
					settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_BOTH);
				} else {				
					System.out.println(separator + "\nUnknown mode. Cancelling Apos-Dead usage change.");
				}
			} else if (val.equalsIgnoreCase("13")) {
			
				System.out.println(separator + "\nToggling Common Dead Runes after generic One-Rune Lores...");
				settings.toggleBoolSetting(Settings.B_ALLOW_COMMON_DEAD);
				
			} else if (val.equalsIgnoreCase("14")) {
			
				System.out.println(separator + "\nToggling Random Dead Runes...");
				settings.toggleBoolSetting(Settings.B_ALLOW_RANDOM_DEAD);
				
			} else if (val.equalsIgnoreCase("15")) {
				
				System.out.println(separator + "\nHow often should random Dead Runes appear around regular Runes? (0%-100%, rolls up to twice per rune, once for left and right side each)");
				try {
					int percent = Integer.parseInt(input.nextLine());
					if (percent > 100) {
						System.out.println(separator + "\nPercent entered was greater than 100. Setting to 100%.");
						settings.changeIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 100);
					} else if (percent < 0) {
						// technically this is the same as having the option set to off, but if the user wants it, whatever.
						System.out.println(separator + "\nPercent entered was less than 0. Setting to 0%.");
						settings.changeIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 0);
					} else {
						settings.changeIntSetting(Settings.I_RANDOM_DEAD_CHANCE, percent);
					}
				} catch (Exception e) {
					System.out.println(separator + "\nInvalid percent! Cancelling...");
				}
				
			} else if (val.equalsIgnoreCase("16")) {
				
				System.out.println(separator + "\nChanging Alternate Mode...");
				System.out.println("0 - No Alternate Runes allowed\n1 - Equal chance to use any form of a Rune\n2 - Don't use the same form twice in a row in a sentence\n3 - Always use Alternates if they exist");
				System.out.println("Change to which mode? (0-3)");
				String style = input.nextLine();
				if (style.equalsIgnoreCase("0")) {
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
				} else if (style.equalsIgnoreCase("1")) {
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_EQUAL_CHANCE);
				} else if (style.equalsIgnoreCase("2")) {
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALT_ALTS);
				} else if (style.equalsIgnoreCase("3")) {
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALTS_ONLY);
				} else {				
					System.out.println(separator + "\nUnknown mode. Cancelling mode change.");
				}
				
			} else if (val.equalsIgnoreCase("17")) {
			
				System.out.println(separator + "\nToggling Filtering out End Symbols...");
				settings.toggleBoolSetting(Settings.B_FILTER_ENDS);
				
			} else if (val.equalsIgnoreCase("18")) {
			
				System.out.println(separator + "\nToggling Space Symbols...");
				settings.toggleBoolSetting(Settings.B_INSERT_SPACES);
				
			} else if (val.equalsIgnoreCase("19")) {
				
				System.out.println(separator + "\nSet Output Directory Path...");
				System.out.println("Use '~' to use the default path. Default: " + System.getProperty("user.dir") + "\\Output");
				System.out.println("Use '~~' to keep the current path.");
				String in = input.nextLine().trim();
				if (in.equalsIgnoreCase("~")) {
					System.out.println(separator + "\nUsing the default path...");
					settings.setOutputDirectory(System.getProperty("user.dir") + "\\Output");
				} else if (in.equalsIgnoreCase("~~")) {
					System.out.println(separator + "\nKeeping the same path...");
				} else {
					System.out.println(separator + "\nUsing the new path...");
					
					// validate, keep the current path if it fails
					File file = new File(in);
					if (file.isDirectory()) {
						settings.setOutputDirectory(in);
					} else {
						System.out.println(separator + "\nCould not find a directory at: " + in + "\nKeeping the old path instead: " + settings.getOutputDirectory() + "\nOk?");
						input.nextLine();
					}
				}
				
			} else if (val.equalsIgnoreCase("0")) {
				run = false;
			} else {
				System.out.println(separator + "\nUnknown command.");
			}
			
		}
	}
	
	public void runeDictionaryOptions(Scanner input, Settings settings) {
		boolean run = true;
		while (run) {
			System.out.println(separator);
			System.out.println("<> Rune Dictionary Options <>");
			System.out.println("1 - Set Base Rune Dictionary Path - currently: " + settings.getBaseImageDirectory());
			System.out.println("2 - Display Rune Stats");
			System.out.println("3 - Display Single Runes");
			System.out.println("4 - Display Double Runes");
			System.out.println("5 - Display Triple Runes");
			System.out.println("6 - Display Quad Runes");
			System.out.println("7 - Display Numeric Runes");
			System.out.println("8 - Display Symbol Runes");
			System.out.println("9 - Display Special Runes");
			System.out.println("0 - Exit Basic Options");
			
			String val = input.nextLine();
			
			if (val.equalsIgnoreCase("1")) {
				
				System.out.println(separator + "\nSet Base Rune Dictionary Path...");
				System.out.println("Use '~' to use the default path. Default: " + System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
				System.out.println("Use '~~' to keep the current path.");
				String in = input.nextLine().trim();
				if (in.equalsIgnoreCase("~")) {
					System.out.println("Using the default path...");
					settings.reloadBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
				} else if (in.equalsIgnoreCase("~~")) {
					System.out.println("Keeping the same path...");
				} else {
					System.out.println("Using the new path...");

					// validate before using. use default if invalid
					File file = new File(in + "\\SingleGlyphs\\");
					if (file.isDirectory()) {
						settings.reloadBaseImageDirectory(in);
					} else {
						System.out.println("Could not find a Rune Dictionary to use at: " + in + "!!\nKeeping the current path at: " + settings.getBaseImageDirectory() + "\nOk?");
					}
				}
				
			} else if (val.equalsIgnoreCase("2")) {

				System.out.println(separator + "\nDisplay Rune Stats...");
				System.out.println(settings.getRuneDictionary().getRdStats(false));
				
			} else if (val.equalsIgnoreCase("3")) {

				System.out.println(separator + "\nDisplay Single Runes...");
				System.out.println(settings.getRuneDictionary().getContents(RuneDictionary.SINGLE_RDES, 6));
				
			} else if (val.equalsIgnoreCase("4")) {

				System.out.println(separator + "\nDisplay Double Runes...");
				System.out.println(settings.getRuneDictionary().getContents(RuneDictionary.DOUBLE_RDES, 6));
				
			} else if (val.equalsIgnoreCase("5")) {

				System.out.println(separator + "\nDisplay Triple Runes...");
				System.out.println(settings.getRuneDictionary().getContents(RuneDictionary.TRIPLE_RDES, 6));
				
			}  else if (val.equalsIgnoreCase("6")) {

				System.out.println(separator + "\nDisplay Quad Runes...");
				System.out.println(settings.getRuneDictionary().getContents(RuneDictionary.QUAD_RDES, 6));
				
			}  else if (val.equalsIgnoreCase("7")) {

				System.out.println(separator + "\nDisplay Numeric Runes...");
				System.out.println(settings.getRuneDictionary().getContents(RuneDictionary.NUMBER_RDES, 6));
				
			}  else if (val.equalsIgnoreCase("8")) {

				System.out.println(separator + "\nDisplay Symbol Runes...");
				System.out.println(settings.getRuneDictionary().getContents(RuneDictionary.SYMBOL_RDES, 6));
				
			}  else if (val.equalsIgnoreCase("9")) {

				System.out.println(separator + "\nDisplay Special Runes...");
				System.out.println(settings.getRuneDictionary().getContents(RuneDictionary.SPECIAL_RDES, 6));
				
			} else if (val.equalsIgnoreCase("0")) {
				run = false;
			} else {
				System.out.println(separator + "\nUnknown command.");
			}
			
			if (!val.equalsIgnoreCase("0")) {
				System.out.println(separator + "\nReturning to Rune Dictionary Options...");
				input.nextLine();
			}
		}
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
	
	public String getSettingsHelp() {
		String c = ""; // c for contents 
		c += ("The file needs to contain the all of the following settings with values\nValues must be in brackets like [this]. Anything else in the line will be ignored.\n");
		c += ("Example line: MAJOR_STYLE [1]\n");
		c += ("All of the settings that need to be set:\n");
		c += ("RUNE_DICTIONARY_LOCATION [] with the path to the rune dictionary base directory. Example: " + System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\\n");
		c += ("OUTPUT_DIR_LOCATION [] with the path to the output directory. Example: " + System.getProperty("user.dir") + "\\Output\n");
		c += ("MAJOR_STYLE [] with a 0 for No Majors, 1 for Few Majors, 2 for All Majors\n");
		c += ("DISPLAY_STYLE [] with a 0 for Rune text display only (\"O\"), 1 for Rune and Form (\"O-2\"), and 2 for the full filepath (\"...\\SingleGlyphs\\A-1.png\")\n");
		c += ("ORIENTATION [] with 1 to parse doubles/triples/quads reading from left-to-right, and 2 for right-to-left\n");
		c += ("TEXT_LINE_LENGTH [] with the number of runes you want to show up in a line in text (minimum 1)\n");
		c += ("IMAGE_LINE_LENGTH [] with the number of runes you want to show up in a line in image display (minimum 1, maximum " + Settings.IMAGE_LINE_LENGTH_MAX + ")\n");
		c += ("RANDOM_DEAD_CHANCE [] with the % chance you want for a random dead rune to appear in the lexification, ie [4] for 4%. Maximum 100%\n");
		c += ("ALT_MODE [] with 0 for no Alt Runes, 1 for an equal chance at being any Rune form, 2 for alternating Rune forms, and 3 for always using Alts (if available)\n");
		c += ("RUNE_RGB_RED [] with the amount of red you want in the Rune color in the image display (0-255)\n");
		c += ("RUNE_RGB_GREEN [] with the amount of green you want in the Rune color in the image display (0-255)\n");
		c += ("RUNE_RGB_BLUE [] with the amount of blue you want in the Rune color in the image display (0-255)\n");
		c += ("BACK_RGB_RED [] with the amount of red you want in the background color in the image display (0-255)\n");
		c += ("BACK_RGB_GREEN [] with the amount of green you want in the background color in the image display (0-255)\n");
		c += ("BACK_RGB_BLUE [] with the amount of blue you want in the background color in the image display (0-255)\n");
		c += ("ALLOW_STAFF [] with 0 if you don't want Staff Rays in the background of the image display, 1 to make a Ray display for each Rune row\n");
		c += ("RAY_RGB_RED [] with the amount of red you want in the Ray color in the image display (0-255)\n");
		c += ("RAY_RGB_GREEN [] with the amount of green you want in the Ray color in the image display (0-255)\n");
		c += ("RAY_RGB_BLUE [] with the amount of blue you want in the Ray color in the image display (0-255)\n");
		c += ("ALLOW_RANDOM_DEAD_RUNES [] with 0 for no random dead runes allowed, 1 for allowed\n");
		c += ("ALLOW_DOUBLES [] with 0 for no double runes allowed, 1 for allowed\n");
		c += ("ALLOW_TRIPLES [] with 0 for no triple runes allowed, 1 for allowed\n");
		c += ("ALLOW_QUADS [] with 0 for no quad runes allowed, 1 for allowed\n");
		c += ("ALLOW_POSSESSIVE_S [] with 0 for no POSSESSIVE-S runes allowed ('s), 1 for allowed\n");
		c += ("ALLOW_PLURAL_S [] with 0 for no PLURAL-S runes allowed at the end of sentences, 1 for allowed\n");
		c += ("ALLOW_AURAS [] with 0 for no auras of any kind allowed, 1 for allowed\n");
		c += ("ALLOW_LOWER_AURAS [] with 0 for no lower auras allowed, 1 for allowed. Will not override ALLOW_AURAS if ALLOW_AURAS is 0.\n");
		c += ("ALLOW_AI_DEAD [] with 0 for no A-DEAD or I-DEAD runes allowed, 1 for allowed\n");
		c += ("ALLOW_APOS_DEAD [] with 0 for no APOS-DEAD, 1 to put it only when there's 1 Rune to the left and/or right of the apostrophe, 2 for always on left, 3 for always on right, 4 for always on both sides.\n");
		c += ("ALLOW_COMMON_DEAD [] with 0 for no common dead runes after 1 rune words allowed, 1 for allowed\n");
		c += ("ALLOW_RUNE_OVERFLOW [] with 0 for no rune overflow (adding an extra rune to the line length) allowed, 1 for allowed\n");
		c += ("AUTO_INQUISITIVE [] with a 0 to not apply inquisitive auras, and a 1 to apply them automatically to sentences ending in \"?\"\n");
		c += ("AUTO_WELKIN [] with a 0 to not apply welkin evocative auras, and a 1 to apply them automatically to sentences ending in \"!\"\n");
		c += ("AUTO_ABYSM [] with a 0 to not apply abysm evocative auras, and a 1 to apply them automatically to sentences ending in excessive \".\"\n");
		c += ("FILTER_ENDS [] with a 0 to leave End symbols in the output (\".\",\"!\",\"?\"), 1 to remove them\n");
		c += ("INSERT_SPACES [] with a 1 to insert spaces (with a 'space' Rune) between Lores, 0 to not have them");
		return c;
	}
	
	
	public void loadDefaultSettingsBeginner(Settings settings) {
		//System.out.println("Using default beginner values.");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FORM);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_TEXT_LINE_LENGTH, Settings.TEXT_LINE_LENGTH_DEFAULT);
		settings.addIntSetting(Settings.I_IMAGE_LINE_LENGTH, Settings.IMAGE_LINE_LENGTH_DEFAULT);
		settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 0);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_EQUAL_CHANCE);
		settings.addIntSetting(Settings.I_RUNE_RGB_RED, Settings.RUNE_RGB_RED_DEFAULT);
		settings.addIntSetting(Settings.I_RUNE_RGB_GREEN, Settings.RUNE_RGB_GREEN_DEFAULT);
		settings.addIntSetting(Settings.I_RUNE_RGB_BLUE, Settings.RUNE_RGB_BLUE_DEFAULT);
		settings.addIntSetting(Settings.I_BACK_RGB_RED, Settings.BACK_RGB_RED_DEFAULT);
		settings.addIntSetting(Settings.I_BACK_RGB_GREEN, Settings.BACK_RGB_GREEN_DEFAULT);
		settings.addIntSetting(Settings.I_BACK_RGB_BLUE, Settings.BACK_RGB_BLUE_DEFAULT);
		settings.addBoolSetting(Settings.B_ALLOW_STAFF, true);
		settings.addIntSetting(Settings.I_RAY_RGB_RED, Settings.RAY_RGB_RED_DEFAULT);
		settings.addIntSetting(Settings.I_RAY_RGB_GREEN, Settings.RAY_RGB_GREEN_DEFAULT);
		settings.addIntSetting(Settings.I_RAY_RGB_BLUE, Settings.RAY_RGB_BLUE_DEFAULT);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, true);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 1);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true);
		settings.addBoolSetting(Settings.B_RUNE_OVERFLOW, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, true);
		// RUNE_DICTIONARY_LOCATION and OUTPUT_DIR_LOCATION will be retrieved via user input.
	}
	
	
	public void loadDefaultSettingsExpert(Settings settings) {
		//System.out.println("Using default expert values.");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FORM);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_TEXT_LINE_LENGTH, Settings.TEXT_LINE_LENGTH_DEFAULT);
		settings.addIntSetting(Settings.I_IMAGE_LINE_LENGTH, Settings.IMAGE_LINE_LENGTH_DEFAULT);
		settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, Settings.RANDOM_DEAD_CHANCE_DEFAULT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_EQUAL_CHANCE);
		settings.addIntSetting(Settings.I_RUNE_RGB_RED, Settings.RUNE_RGB_RED_DEFAULT);
		settings.addIntSetting(Settings.I_RUNE_RGB_GREEN, Settings.RUNE_RGB_GREEN_DEFAULT);
		settings.addIntSetting(Settings.I_RUNE_RGB_BLUE, Settings.RUNE_RGB_BLUE_DEFAULT);
		settings.addIntSetting(Settings.I_BACK_RGB_RED, Settings.BACK_RGB_RED_DEFAULT);
		settings.addIntSetting(Settings.I_BACK_RGB_GREEN, Settings.BACK_RGB_GREEN_DEFAULT);
		settings.addIntSetting(Settings.I_BACK_RGB_BLUE, Settings.BACK_RGB_BLUE_DEFAULT);
		settings.addBoolSetting(Settings.B_ALLOW_STAFF, false);
		settings.addIntSetting(Settings.I_RAY_RGB_RED, Settings.RAY_RGB_RED_DEFAULT);
		settings.addIntSetting(Settings.I_RAY_RGB_GREEN, Settings.RAY_RGB_GREEN_DEFAULT);
		settings.addIntSetting(Settings.I_RAY_RGB_BLUE, Settings.RAY_RGB_BLUE_DEFAULT);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, true);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, true);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 1);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true);
		settings.addBoolSetting(Settings.B_RUNE_OVERFLOW, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, true);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, true);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, true);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, true);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		// RUNE_DICTIONARY_LOCATION and OUTPUT_DIR_LOCATION will be retrieved via user input.
	}
	
	public void loadFileSettings(Settings settings, Scanner input) {
		String errorMsgs = "";
		String setting = "";
		String value = "";
		int fieldCount = 0;
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
				} else if (setting.equalsIgnoreCase(Settings.I_DISPLAY_STYLE)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal != Settings.DISPLAY_RUNE_FILE && intVal != Settings.DISPLAY_RUNE_FORM && intVal != Settings.DISPLAY_RUNE_ONLY) {
						errorMsgs += "Error with " + Settings.I_DISPLAY_STYLE + ": value is not 0, 1, or 2. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_DISPLAY_STYLE, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_ORIENTATION)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal != Settings.LEFT_TO_RIGHT && intVal != Settings.RIGHT_TO_LEFT) {
						errorMsgs += "Error with " + Settings.I_ORIENTATION + ": value is not 1 or 2. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_ORIENTATION, intVal);
					}
				} else if (setting.equalsIgnoreCase(Settings.I_TEXT_LINE_LENGTH)) {
					fieldCount++;
					int intVal = Integer.parseInt(value);
					if (intVal < 1) {
						errorMsgs += "Error with " + Settings.I_TEXT_LINE_LENGTH + ": value is less than 1. Value was: " + intVal + "\n";
					} else {
						settings.addIntSetting(Settings.I_TEXT_LINE_LENGTH, intVal);
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
				}
			}
			
			// ensure the settings file actually contained all settings
			if (fieldCount != Settings.LOAD_FIELD_COUNT) {
				errorMsgs += "Error - The settings file does not have all required settings! Fields counted: " + fieldCount + "    Expected # of Fields: " + Settings.LOAD_FIELD_COUNT + "\n";
			}
			
			if (!errorMsgs.isEmpty()) {
				System.out.println("\n**********\nErrors while processing the Settings file:\n" + errorMsgs + "Program will now exit. Ok?");
				input.nextLine();
				System.exit(0);
			}
			
			br.close();
		} catch (NumberFormatException e) {
			System.out.println("\n**********\nError reading in settings file. A number value is needed for the following setting:\n" + setting + "\nValue was: " + value);
			System.out.println("Program will now exit. Ok?");
			input.nextLine();
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.out.println("\n**********\nError reading in settings file. The Settings File could not be found at: " + settings.getSettingsPath());
			System.out.println("Program will now exit. Ok?");
			input.nextLine();
			System.exit(0);
		} catch (Exception e) {
			System.out.println("\n**********\nUnknown Error reading in settings file. Program will now exit. Exception was: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void settingsOptions(Scanner input, Settings settings) {
		
		boolean run = true;
		while (run) {
			System.out.println(separator);
			System.out.println("<> Settings File Options <>");
			System.out.println("1 - Load a Settings file\n2 - Save current settings as a Settings file\n3 - Load Personal Settings File (if exists)\n4 - Load Default Beginner Settings (does not set Rune Dictionary or Output Directory)\n5 - Load Default Expert Settings (does not set Rune Dictionary or Output Directory)\n0 - Exit");
		
			String val = input.nextLine();
			
			if (val.equals("1")) {
				System.out.println(separator + "\nLoad a Settings File...");
				try {
					System.out.println("Give the full path to the settings file, including the file name and extension. (Use '~' to cancel loading)");
					System.out.println("Example path: " + System.getProperty("user.dir") + Settings.PERSONAL_PATH);
					String loadPath = input.nextLine().trim();
					if (loadPath.equals("~")) {
						System.out.println(separator + "\nCancelling load...");
					} else {
						File file = new File(loadPath);
						if (file.exists()) {
							System.out.println(separator + "\nLoading from Settings file...");
							settings.setSettingsPath(loadPath);
							loadFileSettings(settings, input);
						} else {
							System.out.println(separator + "\nSupplied file path is invalid! Path was: " + loadPath);
						}
					}
				} catch (Exception e) {
					System.out.println(separator + "\nThere was an unknown issue loading from the Settings file. The program will need to exit.");
					e.printStackTrace();
				}
			} else if (val.equals("2")) {
				System.out.println(separator + "\nSave a Settings File...");
				System.out.println("The current Settings will be saved as a Settings file.");
				System.out.println("This file will appear in the output directory.\nGive the file name only (not the full path. '.txt' extension is optional!) to use for the save file (use '~' to cancel saving)");
				String saveName = input.nextLine();
				String savePath = (settings.getOutputDirectory() + "/" + saveName).trim();
				if (!savePath.endsWith(".txt")) {
					savePath += ".txt";
				}
				if (saveName.equals("~")) {
					System.out.println(separator + "\nCancelling save...");
				} else {
					try {
						System.out.println(separator + "\nWriting Settings file to: " + savePath);
						saveSettingsFile(settings, savePath);
					} catch (IOException e) {
						System.out.println(separator + "\nThere was an issue saving the Settings file. Supplied filepath was:" + savePath + "\nException: " + e.getMessage());
						e.printStackTrace();
					}
				}
			} else if (val.equals("3")) {
				System.out.println(separator + "\nLoading Personal Settings File (if exists)...\n");
				
				String path = System.getProperty("user.dir") + Settings.PERSONAL_PATH;
				File file = new File(path);
				if (file.isFile()) {
					settings.setSettingsPath(System.getProperty("user.dir") + Settings.PERSONAL_PATH);
					loadFileSettings(settings, input);
				} else {
					System.out.println(separator + "\nCould not find Settings File to use at: " + path + "\n");
				}
			} else if (val.equals("4")) {
				System.out.println(separator + "\nLoading Default Beginner Settings (Except for Rune Directory and Output Path)...");
				loadDefaultSettingsBeginner(settings); // This doesn't set the rune dictionary or output dir. could use the finals in Settings, but may not work on others' machines.
			} else if (val.equals("5")) {
				System.out.println(separator + "\nLoading Default Expert Settings (Except for Rune Directory and Output Path)...");
				loadDefaultSettingsExpert(settings); // This doesn't set the rune dictionary or output dir. could use the finals in Settings, but may not work on others' machines.
			} else if (val.equalsIgnoreCase("0")) {
				run = false;
			} else {
				System.out.println(separator + "\nUnknown command.");
			}
			
			if (!val.equalsIgnoreCase("0")) {
				System.out.println("\nOk?");
				input.nextLine();
			}
		}
	}
	
	public void saveSettingsFile(Settings settings, String filepath) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filepath)));
		
		// write out each field to the file
		bw.write(Settings.STR_RUNE_DICTIONARY_LOCATION + " [" + settings.getBaseImageDirectory() + "]"); bw.newLine();
		bw.write(Settings.STR_OUTPUT_DIR_LOCATION + " [" + settings.getOutputDirectory() + "]"); bw.newLine();
		bw.write(Settings.I_MAJOR_STYLE + " [" + settings.getIntSetting(Settings.I_MAJOR_STYLE) + "]"); bw.newLine();
		bw.write(Settings.I_DISPLAY_STYLE + " [" + settings.getIntSetting(Settings.I_DISPLAY_STYLE) + "]"); bw.newLine();
		bw.write(Settings.I_ORIENTATION + " [" + settings.getIntSetting(Settings.I_ORIENTATION) + "]"); bw.newLine();
		bw.write(Settings.I_TEXT_LINE_LENGTH + " [" + settings.getIntSetting(Settings.I_TEXT_LINE_LENGTH) + "]"); bw.newLine();
		bw.write(Settings.I_IMAGE_LINE_LENGTH + " [" + settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH) + "]"); bw.newLine();
		bw.write(Settings.I_RANDOM_DEAD_CHANCE + " [" + settings.getIntSetting(Settings.I_RANDOM_DEAD_CHANCE) + "]"); bw.newLine();
		bw.write(Settings.I_ALT_MODE + " [" + settings.getIntSetting(Settings.I_ALT_MODE) + "]"); bw.newLine();
		bw.write(Settings.I_RUNE_RGB_RED + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_RED) + "]"); bw.newLine();
		bw.write(Settings.I_RUNE_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_GREEN) + "]"); bw.newLine();
		bw.write(Settings.I_RUNE_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_BLUE) + "]"); bw.newLine();
		bw.write(Settings.I_BACK_RGB_RED + " [" + settings.getIntSetting(Settings.I_BACK_RGB_RED) + "]"); bw.newLine();
		bw.write(Settings.I_BACK_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_BACK_RGB_GREEN) + "]"); bw.newLine();
		bw.write(Settings.I_BACK_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_BACK_RGB_BLUE) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_STAFF + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_STAFF) + "]"); bw.newLine();
		bw.write(Settings.I_RAY_RGB_RED + " [" + settings.getIntSetting(Settings.I_RAY_RGB_RED) + "]"); bw.newLine();
		bw.write(Settings.I_RAY_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_RAY_RGB_GREEN) + "]"); bw.newLine();
		bw.write(Settings.I_RAY_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_RAY_RGB_BLUE) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_RANDOM_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_RANDOM_DEAD) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_DOUBLES + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_DOUBLES) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_TRIPLES + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_TRIPLES) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_QUADS + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_QUADS) + "]"); bw.newLine();
		bw.write(Settings.B_POSSESSIVE_S + " [" + settings.getBoolSettingAsInt(Settings.B_POSSESSIVE_S) + "]"); bw.newLine();
		bw.write(Settings.B_PLURAL_S + " [" + settings.getBoolSettingAsInt(Settings.B_PLURAL_S) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_AURAS + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_AURAS) + "]"); bw.newLine();
		bw.write(Settings.B_LOWER_AURAS + " [" + settings.getBoolSettingAsInt(Settings.B_LOWER_AURAS) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_AI_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_AI_DEAD) + "]"); bw.newLine();
		bw.write(Settings.I_ALLOW_APOS_DEAD + " [" + settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) + "]"); bw.newLine();
		bw.write(Settings.B_ALLOW_COMMON_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_COMMON_DEAD) + "]"); bw.newLine();
		bw.write(Settings.B_RUNE_OVERFLOW + " [" + settings.getBoolSettingAsInt(Settings.B_RUNE_OVERFLOW) + "]"); bw.newLine();
		bw.write(Settings.B_AUTO_INQUISITIVE + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_INQUISITIVE) + "]"); bw.newLine();
		bw.write(Settings.B_AUTO_WELKIN + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_WELKIN) + "]"); bw.newLine();
		bw.write(Settings.B_AUTO_ABYSM + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_ABYSM) + "]"); bw.newLine();
		bw.write(Settings.B_FILTER_ENDS + " [" + settings.getBoolSettingAsInt(Settings.B_FILTER_ENDS) + "]"); bw.newLine();
		bw.write(Settings.B_INSERT_SPACES + " [" + settings.getBoolSettingAsInt(Settings.B_INSERT_SPACES) + "]"); bw.newLine();
		
		bw.close();
	}
	
	public String getAbridgedSettingsString(Settings settings, boolean allowNewlines) {
		String content = "";
		content += (Settings.STR_RUNE_DICTIONARY_LOCATION + " [" + settings.getBaseImageDirectory() + "] ");
		content += (Settings.STR_OUTPUT_DIR_LOCATION + " [" + settings.getOutputDirectory() + "] ");
		if (allowNewlines) {
			// newline 1
			content += "\n";
		}
		content += (Settings.I_MAJOR_STYLE + " [" + settings.getIntSetting(Settings.I_MAJOR_STYLE) + "] ");
		content += (Settings.I_DISPLAY_STYLE + " [" + settings.getIntSetting(Settings.I_DISPLAY_STYLE) + "] ");
		content += (Settings.I_ORIENTATION + " [" + settings.getIntSetting(Settings.I_ORIENTATION) + "] "); 
		content += (Settings.I_TEXT_LINE_LENGTH + " [" + settings.getIntSetting(Settings.I_TEXT_LINE_LENGTH) + "] "); 
		content += (Settings.I_IMAGE_LINE_LENGTH + " [" + settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH) + "] "); 
		content += (Settings.I_RANDOM_DEAD_CHANCE + " [" + settings.getIntSetting(Settings.I_RANDOM_DEAD_CHANCE) + "] "); 
		content += (Settings.I_ALT_MODE + " [" + settings.getIntSetting(Settings.I_ALT_MODE) + "] ");
		content += (Settings.I_RUNE_RGB_RED + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_RED) + "] "); 
		content += (Settings.I_RUNE_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_GREEN) + "] "); 
		content += (Settings.I_RUNE_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_RUNE_RGB_BLUE) + "] "); 
		if (allowNewlines) {
			// newline 2
			content += "\n";
		}
		content += (Settings.I_BACK_RGB_RED + " [" + settings.getIntSetting(Settings.I_BACK_RGB_RED) + "] "); 
		content += (Settings.I_BACK_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_BACK_RGB_GREEN) + "] "); 
		content += (Settings.I_BACK_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_BACK_RGB_BLUE) + "] "); 
		content += (Settings.B_ALLOW_STAFF + "[" + settings.getBoolSettingAsInt(Settings.B_ALLOW_STAFF) + "] ");
		content += (Settings.I_RAY_RGB_RED + " [" + settings.getIntSetting(Settings.I_RAY_RGB_RED) + "] "); 
		content += (Settings.I_RAY_RGB_GREEN + " [" + settings.getIntSetting(Settings.I_RAY_RGB_GREEN) + "] "); 
		content += (Settings.I_RAY_RGB_BLUE + " [" + settings.getIntSetting(Settings.I_RAY_RGB_BLUE) + "] "); 
		content += (Settings.B_ALLOW_RANDOM_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_RANDOM_DEAD) + "] "); 
		content += (Settings.B_ALLOW_DOUBLES + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_DOUBLES) + "] "); 
		content += (Settings.B_ALLOW_TRIPLES + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_TRIPLES) + "] "); 
		content += (Settings.B_ALLOW_QUADS + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_QUADS) + "] "); 
		content += (Settings.B_POSSESSIVE_S + " [" + settings.getBoolSettingAsInt(Settings.B_POSSESSIVE_S) + "] "); 
		content += (Settings.B_PLURAL_S + " [" + settings.getBoolSettingAsInt(Settings.B_PLURAL_S) + "] "); 
		if (allowNewlines) {
			// newline 3
			content += "\n";
		}
		content += (Settings.B_ALLOW_AURAS + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_AURAS) + "] "); 
		content += (Settings.B_LOWER_AURAS + " [" + settings.getBoolSettingAsInt(Settings.B_LOWER_AURAS) + "] "); 
		content += (Settings.B_ALLOW_AI_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_AI_DEAD) + "] "); 
		content += (Settings.I_ALLOW_APOS_DEAD + " [" + settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) + "] ");
		content += (Settings.B_ALLOW_COMMON_DEAD + " [" + settings.getBoolSettingAsInt(Settings.B_ALLOW_COMMON_DEAD) + "] "); 
		content += (Settings.B_RUNE_OVERFLOW + " [" + settings.getBoolSettingAsInt(Settings.B_RUNE_OVERFLOW) + "] "); 
		content += (Settings.B_AUTO_INQUISITIVE + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_INQUISITIVE) + "] "); 
		content += (Settings.B_AUTO_WELKIN + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_WELKIN) + "] "); 
		content += (Settings.B_AUTO_ABYSM + " [" + settings.getBoolSettingAsInt(Settings.B_AUTO_ABYSM) + "] "); 
		content += (Settings.B_FILTER_ENDS + " [" + settings.getBoolSettingAsInt(Settings.B_FILTER_ENDS) + "] "); 
		content += (Settings.B_INSERT_SPACES + " [" + settings.getBoolSettingAsInt(Settings.B_INSERT_SPACES) + "] "); 
		if (allowNewlines) {
			// newline 4
			content += "\n";
		}
		return content;
	}
	
}

