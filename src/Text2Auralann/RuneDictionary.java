package Text2Auralann;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class RuneDictionary {

	
	// Contains all known glyphs
	// So when we have a lore, like 'theme', we can check if there are any triples in there
	// (like 'the').

	// collection finals:
	public static final int SINGLE_RDES = 1;
	public static final int DOUBLE_RDES = 2;
	public static final int TRIPLE_RDES = 3;
	public static final int QUAD_RDES = 4;
	public static final int NUMBER_RDES = 5;
	public static final int SYMBOL_RDES = 6;
	public static final int SPECIAL_RDES = 7;
	public static final int DEAD_RDES = 8;
	
	
	private Random rnd = new Random();
	private LinkedList<RuneDictionaryEntry> singleRdes;
	private LinkedList<RuneDictionaryEntry> doubleRdes;
	private LinkedList<RuneDictionaryEntry> tripleRdes;
	private LinkedList<RuneDictionaryEntry> quadRdes;
	private LinkedList<RuneDictionaryEntry> numberRdes; // numeric runes
	private LinkedList<RuneDictionaryEntry> deadRdes; // dead runes
	private LinkedList<RuneDictionaryEntry> symbolRdes; // symbols (no ends)
	private LinkedList<RuneDictionaryEntry> endRdes; // end symbols
	private LinkedList<RuneDictionaryEntry> specialRdes; // super special runes (like a-dead, i-dead, ...)
	private LinkedList<RuneDictionaryEntry> auraRdes; // entries for auras (for storing image paths)
	
	// directories
	private static String SINGLE_GLYPH_PATH  = "/SingleRunes/";
	private static String DOUBLE_GLYPH_PATH  = "/DoubleRunes/";
	private static String TRIPLE_GLYPH_PATH  = "/TripleRunes/";
	private static String QUAD_GLYPH_PATH    = "/QuadRunes/";
	private static String NUMERIC_GLYPH_PATH = "/NumericRunes/";
	private static String SYMBOL_GLYPH_PATH  = "/SymbolRunes/";
	private static String SPECIAL_GLYPH_PATH = "/SpecialRunes/";
	private static String DEAD_SINGLE_PATH = "/DeadRunes/SingleDeadRunes/";
	private static String DEAD_DOUBLE_PATH = "/DeadRunes/DoubleDeadRunes/";
	private static String DEAD_TRIPLE_PATH = "/DeadRunes/TripleDeadRunes/";
	private static String DEAD_QUAD_PATH = "/DeadRunes/QuadDeadRunes/";
	private static String AURA_GLYPH_PATH    = "/AuraRunes/";
	
	// Rune categories
	private static String SINGLE_RUNE  = "Single Rune"; // we'll also put... deads in here, for the sake of the quizzer
	private static String DOUBLE_RUNE  = "Double Rune"; // we'll also put... deads in here, for the sake of the quizzer
	private static String TRIPLE_RUNE  = "Triple Rune"; // we'll also put... deads in here, for the sake of the quizzer
	private static String QUAD_RUNE    = "Quad Rune";   // we'll also put... deads in here, for the sake of the quizzer
	private static String NUMERIC_RUNE = "Numeric Rune";
	private static String SYMBOL_RUNE  = "Symbol Rune";
	private static String SPECIAL_RUNE = "Special Rune";
	// and auras can't be quizzed.
	
	private String baseDir;
	
	public RuneDictionary() {
		// this ctor is used for the lexiconifier gui only, so we can manually call readInRunes and catch the messages it returns.
	}
	
	public RuneDictionary(String dir) throws Exception {
		baseDir = dir;
		System.out.println(readInRunes(false, false));
	}
	
	// for the runequizzer, added onlyErrorMessages - only returns error messages in the string, if any. ignores useAbridgedStats if onlyErrorMEssages is on
	public String readInRunes(boolean useAbridgedStats, boolean onlyErrorMessages) throws Exception {
		String msgs = "";
		singleRdes = new LinkedList<RuneDictionaryEntry>();
		doubleRdes = new LinkedList<RuneDictionaryEntry>();
		tripleRdes = new LinkedList<RuneDictionaryEntry>();
		quadRdes = new LinkedList<RuneDictionaryEntry>();
		numberRdes = new LinkedList<RuneDictionaryEntry>();
		deadRdes = new LinkedList<RuneDictionaryEntry>();
		symbolRdes = new LinkedList<RuneDictionaryEntry>();
		endRdes = new LinkedList<RuneDictionaryEntry>();
		specialRdes = new LinkedList<RuneDictionaryEntry>();
		auraRdes = new LinkedList<RuneDictionaryEntry>();
		
		if (!useAbridgedStats) {
			msgs += ("Rune Dictionary: Rune loading started.\n");
		}
		
		// Directory hierarchy:
		// Passed in dir (/Runes/)
		// /SingleGlyphs/
		//   how to read in: first char is the rune, then a dash, then the base/alt number
		msgs += readInNormalRunes(1);
		
		// /DoubleGlyphs/
		//   how to read in: first two chars are the rune, then a dash, then the base/alt number
		msgs += readInNormalRunes(2);
		
		// /TripleGlyphs/
		//   how to read in: first three chars are the rune, then a dash, then the base/alt number
		msgs += readInNormalRunes(3);
		
		// /QuadGlyphs/
		//   how to read in: first four chars are the rune, then a dash, then the base/alt number
		msgs += readInNormalRunes(4);
		
		// /NumericGlyphs/
		//   how to read in: first char is the number. that's it. no alts.
		msgs += readInNumberRunes();
		
		// /SymbolGlyphs/ and /SpecialGlyphs/
		//   how to read in: the file names are things like SEMICOLON. may need to just hardcode.
		//   will read in Symbols, Ends, and Special glyphs
		msgs += readInSymbolGlyphs();
		
		// /AuraGlyphs/
		//   how to read in: hardcoded, they've got unique names relating to position:
		//      LowerMajorLeft, LowerMajorRight, LowerMeager, LowerMinor,
		//      NumberGrouper, NumberSepBottom, NumberSepMiddle, NumberSepMiddleLarge, NumberSepTop,
		//      UpperMajorLeft, UpperMajorRight, UpperMeager, UpperMinor, InquisUpperMeager, InquisLowerMeager,
		//      DoubleUpperMeager, DoubleLowerMeager
		msgs += readInAuraGlyphs();
		
		// /DeadGlyphs/
		//   how to read in: name is irrelevant, whether single double or triple, just read and store in a single collection.
		//   4 sub folders exist:
		//     /SingleDeadGlyphs/
		//     /DoubleDeadGlyphs/
		//     /TripleDeadGlyphs/
		//     /QuadDeadGlyphs/
		msgs += readInDeadRunes();
		
		if (!onlyErrorMessages) {
			msgs += (getRdStats(useAbridgedStats) + "\n");
			if (!useAbridgedStats) {
				msgs += ("Rune loading complete.");
			}
		}
		return msgs;
	}
	
	public String readInNormalRunes(int length) throws Exception {
		String msgs = "";
		
		LinkedList<String> filenames = null;
		int rdeSize = 0;
		
		if (length == 1) {
			filenames = getFileNames(baseDir + SINGLE_GLYPH_PATH);
			rdeSize = singleRdes.size();
		} else if (length == 2) {
			filenames = getFileNames(baseDir + DOUBLE_GLYPH_PATH);
			rdeSize = doubleRdes.size();
		} else if (length == 3) {
			filenames = getFileNames(baseDir + TRIPLE_GLYPH_PATH);
			rdeSize = tripleRdes.size();
		} else if (length == 4) {
			filenames = getFileNames(baseDir + QUAD_GLYPH_PATH);
			rdeSize = quadRdes.size();
		} else {
			msgs += ("ERROR - Unknown rune length, RuneDictionary#readInNormalGlyphs, length of: " + length);
		}
		
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				String rune = interpretRuneName(filenames.get(x), false);
				int form = interpretRuneNum(filenames.get(x), false);
				RuneDictionaryEntry rde = new RuneDictionaryEntry(rune);
				boolean entryFound = false;
				for (int y = 0; y < rdeSize; ++y) {
					if (!entryFound) {
						if (length == 1 && singleRdes.get(y).compareTo(rde) == 0) {
							singleRdes.get(y).addForm(rune + "-" + form, filenames.get(x));
							entryFound = true;
						} else if (length == 2 && doubleRdes.get(y).compareTo(rde) == 0) {
							doubleRdes.get(y).addForm(rune + "-" + form, filenames.get(x));
							entryFound = true;
						} else if (length == 3 && tripleRdes.get(y).compareTo(rde) == 0) {
							tripleRdes.get(y).addForm(rune + "-" + form, filenames.get(x));
							entryFound = true;
						} else if (length == 4 && quadRdes.get(y).compareTo(rde) == 0) {
							quadRdes.get(y).addForm(rune + "-" + form, filenames.get(x));
							entryFound = true;
						}
					}
				}
				if (!entryFound) { // we didnt have an entry for this one yet, so add it
					rde.addForm(rune + "-" + form, filenames.get(x));
					if (length == 1) { singleRdes.add(rde); rde.setRuneCategory(SINGLE_RUNE); }
					else if (length == 2) { doubleRdes.add(rde); rde.setRuneCategory(DOUBLE_RUNE); }
					else if (length == 3) { tripleRdes.add(rde); rde.setRuneCategory(TRIPLE_RUNE); }
					else if (length == 4) { quadRdes.add(rde); rde.setRuneCategory(QUAD_RUNE); }
					++rdeSize;
				}
			}
		}
		return msgs;
	}
	
	public String readInNumberRunes() throws Exception {
		String msgs = "";
		
		// Could be merged with readInNormalRunes and keep the FORM in the name,
		// but then we'd need a length of 0 or 5 or something weird as a magic number.
		
		LinkedList<String> filenames = getFileNames(baseDir + NUMERIC_GLYPH_PATH);
		
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				String rune = interpretRuneName(filenames.get(x), true);
				int form = interpretRuneNum(filenames.get(x), true); // we don't have alts for numbers, so ignoring dashes.
				RuneDictionaryEntry rde = new RuneDictionaryEntry(rune);
				boolean entryFound = false;
				for (int y = 0; y < numberRdes.size(); ++y) {
					if (!entryFound && numberRdes.get(y).compareTo(rde) == 0) {
						entryFound = true;
						numberRdes.get(y).addForm(rune + "-" + form, filenames.get(x));
					}
				}
				if (!entryFound) { // we didnt have an entry for this one yet, so add it
					rde.addForm(rune, filenames.get(x)); // dropped the "-FORM" cause there are no alts for numbers...
					rde.setRuneCategory(NUMERIC_RUNE);
					numberRdes.add(rde);
				}
			}
		}
		return msgs;
	}
	
	public String readInDeadRunes() throws Exception {
		String msgs = "";
		int deadCount = 0;
		
		LinkedList<String> filenames = getFileNames(baseDir + DEAD_SINGLE_PATH);
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				++deadCount;
				RuneDictionaryEntry rde = new RuneDictionaryEntry("DEAD-" + deadCount);
				rde.addForm("DEAD-" + deadCount, filenames.get(x));
				rde.setRuneCategory(SINGLE_RUNE); // set dead to these so it isn't immediately known that it's a dead in the rune quizzer
				deadRdes.add(rde);
			}
		}
		filenames = getFileNames(baseDir + DEAD_DOUBLE_PATH);
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				++deadCount;
				RuneDictionaryEntry rde = new RuneDictionaryEntry("DEAD-" + deadCount);
				rde.addForm("DEAD-" + deadCount, filenames.get(x));
				rde.setRuneCategory(DOUBLE_RUNE); // set dead to these so it isn't immediately known that it's a dead in the rune quizzer
				deadRdes.add(rde);
			}
		}
		filenames = getFileNames(baseDir + DEAD_TRIPLE_PATH);
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				++deadCount;
				RuneDictionaryEntry rde = new RuneDictionaryEntry("DEAD-" + deadCount);
				rde.addForm("DEAD-" + deadCount, filenames.get(x));
				rde.setRuneCategory(TRIPLE_RUNE); // set dead to these so it isn't immediately known that it's a dead in the rune quizzer
				deadRdes.add(rde);
			}
		}
		filenames = getFileNames(baseDir + DEAD_QUAD_PATH);
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				++deadCount;
				RuneDictionaryEntry rde = new RuneDictionaryEntry("DEAD-" + deadCount);
				rde.addForm("DEAD-" + deadCount, filenames.get(x));
				rde.setRuneCategory(QUAD_RUNE); // set dead to these so it isn't immediately known that it's a dead in the rune quizzer
				deadRdes.add(rde);
			}
		}
		return msgs;
	}
	
	public String readInSymbolGlyphs() throws Exception {
		String msgs = "";
		
		// It's icky, but we're hardcoding this, because we cant have filenames like ":", ";" "\", ".", ...
		// Will also grab some of the other weird ones that need to be handled special: A-dead, Currency character, ...
		
		LinkedList<String> filenames = getFileNames(baseDir + SYMBOL_GLYPH_PATH);
		filenames.addAll(getFileNames(baseDir + SPECIAL_GLYPH_PATH));
		
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				String rune = interpretRuneName(filenames.get(x), true);
				//int form = interpretRuneNum(filenames.get(x)); // unnecessary, since symbols don't have forms. Might also blow up on dash/hyphen anyway?
				
				// create the rune based on the filename
				
				// symbol glyphs / end glyphs
				
				if (rune.equalsIgnoreCase("Apostrophe")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("'");
					rde.addForm("'", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Backslash")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("\\");
					rde.addForm("\\", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Colon")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry(":");
					rde.addForm(":", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Comma")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry(",");
					rde.addForm(",", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Dash")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("-");
					rde.addForm("-", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("End-Exclamation")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("!");
					rde.addForm("!", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					endRdes.add(rde); // NOTE - endRdes! not symbol!
				} else if (rune.equalsIgnoreCase("End-Period")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry(".");
					rde.addForm(".", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					endRdes.add(rde); // NOTE - endRdes! not symbol!
				} else if (rune.equalsIgnoreCase("End-QuestionMark")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("?");
					rde.addForm("?", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					endRdes.add(rde); // NOTE - endRdes! not symbol!
				} else if (rune.equalsIgnoreCase("Forward-Slash")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("/");
					rde.addForm("/", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Hyphen")) {
					// not needed, because it will be picked up by the dash block.
					// leaving this block in though, so we don't hit the last else block.
				} else if (rune.equalsIgnoreCase("Negative-Sign")) {
					// not needed, because it will be picked up by the dash block.
					// leaving this block in though, so we don't hit the last else block.
				} else if (rune.equalsIgnoreCase("Division-Sign")) {
					// not needed, because it will be picked up by the slash block.
					// leaving this block in though, so we don't hit the last else block.
				} else if (rune.equalsIgnoreCase("Multiplication-Sign")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("*");
					rde.addForm("*", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Equals-Sign")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("=");
					rde.addForm("=", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Positive-Sign")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("+");
					rde.addForm("+", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Left-Bracket")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("[");
					rde.addForm("[", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Left-Parentheses")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("(");
					rde.addForm("(", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Single-Quote")) {
					// not needed, because it will be picked up by the apostrophe block.
					// leaving this block in though, so we don't hit the last else block.
				} else if (rune.equalsIgnoreCase("Double-Quote")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("\"");
					rde.addForm("\"", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Right-Bracket")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("]");
					rde.addForm("]", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Right-Parentheses")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry(")");
					rde.addForm(")", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Semicolon")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry(";");
					rde.addForm(";", filenames.get(x));
					rde.setRuneCategory(SYMBOL_RUNE);
					symbolRdes.add(rde);
				}
				
				// special glyphs
				
				else if (rune.equalsIgnoreCase("'S")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("'S");
					rde.addForm("'S", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("Plural-S")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("PLURAL-S");
					rde.addForm("PLURAL-S", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("A-Dead")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("A-DEAD");
					rde.addForm("A-DEAD", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("Apos-Dead")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("APOS-DEAD");
					rde.addForm("APOS-DEAD", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("I-Dead")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("I-DEAD");
					rde.addForm("I-DEAD", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("OneRune-CommonDead")) { // common dead rune appended to words that are one rune in length, like "THE"
					RuneDictionaryEntry rde = new RuneDictionaryEntry("COMMON-DEAD");
					rde.addForm("COMMON-DEAD", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("Currency")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("$");
					rde.addForm("$", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("UnrecognizedCharacter")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("UNKNOWN");
					rde.addForm("UNKNOWN", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("DESTINY")) { // Futhark... can't actually be used. Just for printing out with the other specials.
					RuneDictionaryEntry rde = new RuneDictionaryEntry("DESTINY");
					rde.addForm("DESTINY", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("CAPITAL")) { // Braille
					RuneDictionaryEntry rde = new RuneDictionaryEntry("CAPITAL");
					rde.addForm("CAPITAL", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				else if (rune.equalsIgnoreCase("SmallTsu")) { // katakana
					RuneDictionaryEntry rde = new RuneDictionaryEntry("SMALLTSU");
					rde.addForm("SMALLTSU", filenames.get(x));
					rde.setRuneCategory(SPECIAL_RUNE);
					specialRdes.add(rde);
				}
				
				else {
					msgs += ("Error! Unknown Symbol Rune: " + rune);
				}
			}
		}
		return msgs;
	}
	
	public String readInAuraGlyphs() throws Exception {
		String msgs = "";
		LinkedList<String> filenames = getFileNames(baseDir + AURA_GLYPH_PATH);

		// icky, but we're hardcoding the aura image files:
		//     LowerMajorLeft, LowerMajorRight, LowerMeager, LowerMinor,
		//     NumberGrouper, NumberSepBottom, NumberSepMiddle, NumberSepMiddleLarge, NumberSepTop,
		//     UpperMajorLeft, UpperMajorRight, UpperMeager, UpperMinor
		//     InquisUpperMeager, InquisLowerMeager, DoubleUpperMeager, DoubleLowerMeager
		
		
		for (int x = 0; x < filenames.size(); ++x) {
			if (filenames.get(x).endsWith(".txt")) {
				String rune = interpretRuneName(filenames.get(x), true);
				//int form = interpretRuneNum(filenames.get(x)); // no forms for auras
				
				// create the rune based on the filename
				
				if (rune.equalsIgnoreCase("LowerMajorLeft")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("LowerMajorLeft");
					rde.addForm("LowerMajorLeft", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("LowerMajorRight")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("LowerMajorRight");
					rde.addForm("LowerMajorRight", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("LowerMeager")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("LowerMeager");
					rde.addForm("LowerMeager", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("DoubleLowerMeager")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("DoubleLowerMeager");
					rde.addForm("DoubleLowerMeager", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("InquisLowerMeager")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("InquisLowerMeager");
					rde.addForm("InquisLowerMeager", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("LowerMinor")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("LowerMinor");
					rde.addForm("LowerMinor", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("NumberGrouper")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("NumberGrouper");
					rde.addForm("NumberGrouper", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("NumberSepBottom")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("NumberSepBottom");
					rde.addForm("NumberSepBottom", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("NumberSepMiddle")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("NumberSepMiddle");
					rde.addForm("NumberSepMiddle", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("NumberSepMiddleLarge")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("NumberSepMiddleLarge");
					rde.addForm("NumberSepMiddleLarge", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("NumberSepTop")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("NumberSepTop");
					rde.addForm("NumberSepTop", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("UpperMajorLeft")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("UpperMajorLeft");
					rde.addForm("UpperMajorLeft", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("UpperMajorRight")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("UpperMajorRight");
					rde.addForm("UpperMajorRight", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("UpperMeager")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("UpperMeager");
					rde.addForm("UpperMeager", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("InquisUpperMeager")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("InquisUpperMeager");
					rde.addForm("InquisUpperMeager", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("DoubleUpperMeager")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("DoubleUpperMeager");
					rde.addForm("DoubleUpperMeager", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("UpperMinor")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("UpperMinor");
					rde.addForm("UpperMinor", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("Empty")) {
					RuneDictionaryEntry rde = new RuneDictionaryEntry("Empty");
					rde.addForm("Empty", filenames.get(x));
					auraRdes.add(rde);
				} else if (rune.equalsIgnoreCase("EMPTY-RUNE")) { // all caps, since this one can be displayed in the text output file
					RuneDictionaryEntry rde = new RuneDictionaryEntry("EMPTY-RUNE");
					rde.addForm("EMPTY-RUNE", filenames.get(x));
					auraRdes.add(rde);
				}
				
				else {
					msgs += ("Error! Unknown Symbol Rune: " + rune);
				}
			}
		}
		return msgs;
	}
	
	
	// returns the name of the rune, ie: "O" from "O-2", "RE" from "RE-3", "A" from "A", ...
	// sometimes we want to ignore the dash though, in things like "A-DEAD" or "LEFT-PARENTHESES", so set ignoreDash to true in those cases
	public String interpretRuneName(String filename, boolean ignoreDash) {
		String runeName = "";

		// gotta make sure we handle both kinds of slashes
		filename = filename.replace("\\", "/");
		
		if (filename.contains("-") && !ignoreDash) {
			runeName = filename.substring(filename.lastIndexOf("/")+1, filename.lastIndexOf("-"));
		} else {
			runeName = filename.substring(filename.lastIndexOf("/")+1, filename.lastIndexOf("."));
		}
		
		return runeName;
	}
	
	// returns the form of the rune from the filename, ie: "2" from "O-2", "3" from "RE-3", "1" from "A", ...
	public int interpretRuneNum(String filename, boolean ignoreDash) {
		int runeNum = 0;

		if (filename.contains("-") && !ignoreDash) {
			int indexOfDash = filename.lastIndexOf("-")+1;
			int indexOfDot = filename.lastIndexOf(".");
			runeNum = Integer.parseInt(filename.substring(indexOfDash, indexOfDot));
		} else {
			runeNum = 1; // no explicit version set, so assume it is the base (1)
		}
		
		return runeNum;
	}
	
	public LinkedList<String> getFileNames(String path) throws Exception {
		LinkedList<String> filepaths = new LinkedList<String>();
		
		try {
			Files.walk(Paths.get(path)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					try {
						if (!Files.isHidden(filePath)) {
							filepaths.add(filePath.toString());
						}
					} catch (Exception e) {
						System.out.println("Error with Rune filepath: " + filePath);
					}
				}
			});
		} catch (NoSuchFileException e) {
			throw new Exception("Error - RuneDictionary could not find file or directory: " + e.getMessage());
		} catch (IOException e) {
			throw new Exception("Unknown Error reading image file directory\nException caught: " + e.getMessage());
		}
		
		// now sort it in alpha order
		Collections.sort(filepaths);
		
		return filepaths;
	}
	
	// Don't think we need a distinction between single / double / triple ... dead runes
	public RuneDictionaryEntry getDeadRune() {
		int x = rnd.nextInt(deadRdes.size());
		return deadRdes.get(x);//.getBaseForm();
	}
	
	
	public boolean isNumber(String rune) {
		boolean isNum = false;
		RuneDictionaryEntry rde = new RuneDictionaryEntry(rune);
		if (numberRdes.contains(rde)) {
			isNum = true;
		}
		return isNum;
	}
	
	public boolean isRune(String rune, boolean allowDoubles, boolean allowTriples, boolean allowQuads) {
		
		boolean isARune = false;
		RuneDictionaryEntry rde = new RuneDictionaryEntry(rune);
		
		if (allowQuads && quadRdes.contains(rde)) {
			isARune = true;
		}
		else if (allowTriples && tripleRdes.contains(rde)) {
			isARune = true;
		}
		else if (allowDoubles && doubleRdes.contains(rde)) {
			isARune = true;
		}
		else if (singleRdes.contains(rde) || numberRdes.contains(rde) || symbolRdes.contains(rde) || endRdes.contains(rde) || rune.equalsIgnoreCase("$")) { // no specials except $ (since technically it's a single)
			isARune = true;
		}

		return isARune;
	}
	
	// Is the rune an end rune? (like '.', '?', '!')
	public boolean isEndGlyph(String rune) {
		boolean isEnd = false;
		RuneDictionaryEntry rde = new RuneDictionaryEntry(rune);
		if (endRdes.contains(rde)) {
			isEnd = true;
		}
		return isEnd;
	}
	
	// previously isNonAura, because symbols don't have auras, but these also aren't Ends.
	public boolean isSymbol(String rune) {
		boolean isNonAura = false;
		RuneDictionaryEntry rde = new RuneDictionaryEntry(rune);
		if (symbolRdes.contains(rde) || rune.equalsIgnoreCase("$")) { // currency symbol is a weird case, we don't give it auras due to the numeric ones that follow it
			isNonAura = true;
		}
		return isNonAura;
	}
	
	public RuneDictionaryEntry getRDE(String rune) throws Exception {
		RuneDictionaryEntry rde = new RuneDictionaryEntry(rune);
		
		if (quadRdes.contains(rde)) {
			rde = quadRdes.get(quadRdes.indexOf(rde));
		}
		else if (tripleRdes.contains(rde)) {
			rde = tripleRdes.get(tripleRdes.indexOf(rde));
		}
		else if (doubleRdes.contains(rde)) {
			rde = doubleRdes.get(doubleRdes.indexOf(rde));
		}
		else if (singleRdes.contains(rde)) { 
			rde = singleRdes.get(singleRdes.indexOf(rde));
		}
		else if (numberRdes.contains(rde)) { 
			rde = numberRdes.get(numberRdes.indexOf(rde));
		}
		else if (symbolRdes.contains(rde)) { 
			rde = symbolRdes.get(symbolRdes.indexOf(rde));
		}
		else if (endRdes.contains(rde)) { 
			rde = endRdes.get(endRdes.indexOf(rde));
		}
		else if (specialRdes.contains(rde)) { 
			rde = specialRdes.get(specialRdes.indexOf(rde));
		}
		else if (auraRdes.contains(rde)) {
			rde = auraRdes.get(auraRdes.indexOf(rde));
		}
		else if (deadRdes.contains(rde)) {
			rde = deadRdes.get(deadRdes.indexOf(rde));
		}
		else {
			throw new Exception("Error: Could not find Dictionary Entry for rune: " + rune);
		}

		return rde;
	}
	
	// Reloads the dictionary - clears collections, uses the new path, reads in the runes
	public void reloadDictionary(String newPath) throws Exception {
		baseDir = newPath;
		
		clearRdes();
		
		System.out.println(readInRunes(false, false));
	}
	
	// clears the rdes
	public void clearRdes() {
		singleRdes.clear();
		doubleRdes.clear();
		tripleRdes.clear();
		quadRdes.clear();
		numberRdes.clear();
		deadRdes.clear();
		symbolRdes.clear();
		endRdes.clear();
		specialRdes.clear();
		auraRdes.clear();
	}
	
	// gets a string detailing how many runes there are of each category (count include base AND alts)
	public String getRdStats(boolean useAbridgedStats) {
		String stats = "";
		
		// we also need to count all forms as runes, not just go by base forms.
		int numSingle = getRdeRuneCount(singleRdes);
		int numDouble = getRdeRuneCount(doubleRdes);
		int numTriple = getRdeRuneCount(tripleRdes);
		int numQuad = getRdeRuneCount(quadRdes);
		int numNum = getRdeRuneCount(numberRdes);
		int numSymbol = getRdeRuneCount(symbolRdes) + getRdeRuneCount(endRdes); // symbolRdes + endRdes
		int numSpecial = getRdeRuneCount(specialRdes);
		int numDead = getRdeRuneCount(deadRdes);
		int numAura = getRdeRuneCount(auraRdes);
		
		if (!useAbridgedStats) {
			stats += ("Number of Single   Runes loaded: " + numSingle) + "\n";
			stats += ("Number of Double   Runes loaded: " + numDouble) + "\n";
			stats += ("Number of Triple   Runes loaded: " + numTriple) + "\n";
			stats += ("Number of Quad     Runes loaded: " + numQuad) + "\n";
			stats += ("Number of Numeric  Runes loaded: " + numNum) + "\n";
			stats += ("Number of Symbol   Runes loaded: " + numSymbol) + "\n";
			stats += ("Number of Special  Runes loaded: " + numSpecial) + "\n";
			stats += ("Number of Dead     Runes loaded: " + numDead) + "\n";
			stats += ("Number of Aura     Lines loaded: " + numAura);
		} else {
			stats += (" - Number of Single Runes loaded: " + numSingle) + " - Number of Double Runes loaded: " + numDouble + " - Number of Triple Runes loaded: " + numTriple + "\n";
			stats += (" - Number of Quad Runes loaded: " + numQuad) + " - Number of Numeric Runes loaded: " + numNum + " - Number of Symbol Runes loaded: " + numSymbol + "\n";
			stats += (" - Number of Special Runes loaded: " + numSpecial) + " - Number of Dead Runes loaded: " + numDead + " - Number of Aura Lines loaded: " + numAura;
		}
		
		return stats;
	}
	
	// used to get the count of runes (base AND alts) in a given rde collection
	private int getRdeRuneCount(LinkedList<RuneDictionaryEntry> rdes) {
		int count = 0;
		
		for (int x = 0; x < rdes.size(); ++x) {
			count += rdes.get(x).getFormCount();
		}
		
		return count;
	}
	
	public String getContents(int collection, int perLine) {
		
		String contents = "";
		LinkedList<RuneDictionaryEntry> rdes = null;
		
		if (collection == SINGLE_RDES) {
			rdes = singleRdes;
		} else if (collection == DOUBLE_RDES) {
			rdes = doubleRdes;
		} else if (collection == TRIPLE_RDES) {
			rdes = tripleRdes;
		} else if (collection == QUAD_RDES) { 
			rdes = quadRdes;
		} else if (collection == NUMBER_RDES) {
			rdes = numberRdes;
		} else if (collection == SYMBOL_RDES) {
			rdes = symbolRdes;
		} else if (collection == SPECIAL_RDES) {
			rdes = specialRdes;
		} else {
			System.out.println("Error - unknown collection value: " + collection);
		}
		
		contents += "Rune Count: " + getRdeRuneCount(rdes) + "\n";
		
		int currPerLine = 0;
		for (int x = 0; x < rdes.size(); ++x) {
			
			// don't just print out the base form / key - need to display all forms, ie O-1, O-2, ...
			RuneDictionaryEntry rde = rdes.get(x);
			
			for (int y = 0; y < rde.getFormCount(); ++y) {
				
				contents += rde.getForm(y) + "   ";
				
				++currPerLine;
				if (currPerLine == perLine) {
					contents += "\n";
					currPerLine = 0;
				}
			}
		}
		
		return contents;
	}
	
	
	/**
	 * Returns a random rune from the given collection (ie, singles, doubles, triples, ...)
	 * @param category
	 * @return
	 */
	public RuneDictionaryEntry getRandomRune(int collectionId) {
		LinkedList<RuneDictionaryEntry> rdes = null;
		RuneDictionaryEntry rde = null;
		
		if (collectionId == SINGLE_RDES) {
			rdes = singleRdes;
		} else if (collectionId == DOUBLE_RDES) {
			rdes = doubleRdes;
		} else if (collectionId == TRIPLE_RDES) {
			rdes = tripleRdes;
		} else if (collectionId == QUAD_RDES) { 
			rdes = quadRdes;
		} else if (collectionId == NUMBER_RDES) {
			rdes = numberRdes;
		} else if (collectionId == SYMBOL_RDES) {
			rdes = symbolRdes;
		} else if (collectionId == SPECIAL_RDES) { // plural-s, 's, and $ should rightfully all be in here now, don't need to hardcode special cases
			rdes = specialRdes;
		} else {
			System.out.println("Error - unknown collection value: " + collectionId);
		}

		int index = rnd.nextInt(rdes.size());
		rde = rdes.get(index);		
		return rde;
	}
	
	public LinkedList<RuneDictionaryEntry> getRuneCollection(int collectionId) {
		LinkedList<RuneDictionaryEntry> rdes = null;
		if (collectionId == SINGLE_RDES) {
			rdes = singleRdes;
		} else if (collectionId == DOUBLE_RDES) {
			rdes = doubleRdes;
		} else if (collectionId == TRIPLE_RDES) {
			rdes = tripleRdes;
		} else if (collectionId == QUAD_RDES) { 
			rdes = quadRdes;
		} else if (collectionId == NUMBER_RDES) {
			rdes = numberRdes;
		} else if (collectionId == SYMBOL_RDES) {
			// note - we need to include end rdes here too
			LinkedList<RuneDictionaryEntry> allSymbolRdes = new LinkedList<RuneDictionaryEntry>();
			allSymbolRdes.addAll(symbolRdes);
			allSymbolRdes.addAll(endRdes);
			rdes = allSymbolRdes;
		} else if (collectionId == SPECIAL_RDES) {
			rdes = specialRdes;
		} else if (collectionId == DEAD_RDES) {
			rdes = deadRdes;
		} else {
			System.out.println("Error - unknown collection value: " + collectionId);
		}
		return rdes;
	}
	
	public void setBaseDir(String dir) {
		baseDir = dir;
	}
	
	public String getBaseDir() {
		return baseDir;
	}
}
