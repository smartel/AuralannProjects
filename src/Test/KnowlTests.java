package Test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import Text2Auralann.Knowl;
import Text2Auralann.Settings;


public class KnowlTests {

	
	@Test
	public void testDetermineRunes() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		
		Knowl knowl = new Knowl("Porvelm", settings);
		System.out.println(knowl.getTextDisplay());

		knowl = new Knowl("Porvelm.", settings);
		System.out.println(knowl.getTextDisplay(true));

		knowl = new Knowl("Porvelm Auranor", settings);
		System.out.println(knowl.getTextDisplay());		
		
		knowl = new Knowl(".", settings);
		System.out.println(knowl.getTextDisplay());

		knowl = new Knowl(".What?", settings);
		System.out.println(knowl.getTextDisplay());
		
		knowl = new Knowl("...Really?", settings);
		System.out.println(knowl.getTextDisplay());
		
		knowl = new Knowl("Hello me, it's me again...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		knowl = new Knowl("Test sentence. No, really.", settings); // although I guess it'd be a brand new grimoire. lexi tests elsewhere.
		System.out.println(knowl.getTextDisplay());
		
		knowl = new Knowl("I am me.", settings); // just looking at the meager, that's all
		System.out.println(knowl.getTextDisplay());
		
		knowl = new Knowl("What is a žebra?", settings); // what is that wacky unknown character??
		System.out.println(knowl.getTextDisplay());

		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_NONE);
		knowl = new Knowl("I am one of many style tests.", settings);
		System.out.println(knowl.getTextDisplay());

		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		knowl = new Knowl("I am one of many style tests.", settings);
		System.out.println(knowl.getTextDisplay());

		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_ALL);
		knowl = new Knowl("I am one of many style tests.", settings);
		System.out.println(knowl.getTextDisplay());
		
		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		knowl = new Knowl("I have 2 cats.", settings);
		System.out.println(knowl.getTextDisplay());
		
		knowl = new Knowl("I have 1234.5678 cats.", settings);
		System.out.println(knowl.getTextDisplay());
		
		// turning majors and meagers off for simpler debugging. The one letter lore 'A' threw things off in this display.
		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.CUSTOM_STYLE);
		settings.addBoolSetting(Settings.CUST_HAS_MAJORS, false);
		settings.addBoolSetting(Settings.CUST_HAS_FL_MAJORS, false);
		settings.addBoolSetting(Settings.CUST_HAS_MEAGERS, false);
		settings.addBoolSetting(Settings.CUST_HAS_MEAGERS_NEEDED, false);
		knowl = new Knowl("A More Complex Sentence.", settings);
		System.out.println(knowl.getTextDisplay());
		
		// testing with and without spaces between words
		knowl = new Knowl("Incredibly Long Words For Testing Purposes", settings);
		System.out.println(knowl.getTextDisplay(false));

		settings.changeBoolSetting(Settings.CUST_HAS_MAJORS, true);
		knowl = new Knowl("Incredibly Long Words For Testing Purposes", settings);
		System.out.println(knowl.getTextDisplay(false));
		

		settings.changeBoolSetting(Settings.CUST_HAS_FL_MAJORS, true);
		settings.changeBoolSetting(Settings.CUST_HAS_MEAGERS, true);
		settings.changeBoolSetting(Settings.CUST_HAS_MEAGERS_NEEDED, true);
		knowl = new Knowl("Only I can call her that.", settings);
		System.out.println(knowl.getTextDisplay(false));

		// All styles
		System.out.println("============================================\n");

		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_NONE);
		knowl = new Knowl("I can call her that.", settings);
		System.out.println(knowl.getTextDisplay(false));

		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		knowl = new Knowl("I can call her that.", settings);
		System.out.println(knowl.getTextDisplay(false));

		settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_ALL);
		knowl = new Knowl("I can call her that.", settings);
		System.out.println(knowl.getTextDisplay(false));
		

		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	

	@Test
	public void testSarnysSnacks() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		
		
		// All S runes test:  (S)  ('S)  (final S)
		System.out.println("Hello Plural and Possessive S's");
		
		Knowl knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		// Now right_to_left
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		// Now turning off possessive S
		System.out.println("Goodbye Possessive S");

		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
		knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		// Now right_to_left
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		
		// Now turning off plural S only
		System.out.println("Goodbye Plural S");
		settings.changeBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.changeBoolSetting(Settings.B_PLURAL_S, false);
		
		knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		// Now right_to_left
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		
		// Now turning off plural and possessive S
		System.out.println("Goodbye Plural and Possessive S's");
		settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
		
		knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		// Now right_to_left
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		knowl = new Knowl("Sarnys's Snacks", settings);
		System.out.println(knowl.getTextDisplay(true));
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		
		// Now turning off optional auras
		settings.changeBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.changeBoolSetting(Settings.B_PLURAL_S, true);
		settings.changeBoolSetting(Settings.B_LOWER_AURAS, false);
		System.out.println("Goodbye purely optional lower auras...");
		knowl = new Knowl("Sarnys's Snacks.", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	

	@Test
	public void testDeadGlyphs() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		
		// DEAD GLYPH OFF
		System.out.println("Dead Glyph: OFF\n");
		
		Knowl knowl = new Knowl("I like cats.", settings);
		System.out.println(knowl.getTextDisplay(true));
		knowl = new Knowl("I.", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		// now right-to-left (incase appending a space blows it sky high)
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		knowl = new Knowl("I like cats.", settings);
		System.out.println(knowl.getTextDisplay(true));
		knowl = new Knowl("I.", settings);
		System.out.println(knowl.getTextDisplay(true));
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		// DEAD GLYPH ON
		System.out.println("Dead Glyph: ON\n");

		settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
		knowl = new Knowl("I like cats.", settings);
		System.out.println(knowl.getTextDisplay(true));
		knowl = new Knowl("I.", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		// now right-to-left
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		knowl = new Knowl("I like cats.", settings);
		System.out.println(knowl.getTextDisplay(true));
		knowl = new Knowl("I.", settings);
		System.out.println(knowl.getTextDisplay(true));
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		
	}
	
	@Test
	public void testRandomDeadGlyphs() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
			
		// now for a random dead one at the end of a knowl.
	
		settings.toggleBoolSetting(Settings.B_ALLOW_RANDOM_DEAD);
		
		settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 50);
		Knowl knowl = new Knowl("I am so dead.", settings);
		
		//settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 100);
		//knowl = new Knowl("D.", settings);
		
		System.out.println(knowl.getTextDisplay(true));
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testImpliedAuras() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		
		// DEAD GLYPH OFF
		System.out.println("Auras: OFF\n");
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, false);
		
		Knowl knowl = new Knowl("I like cats.", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		// DEAD GLYPH ON
		System.out.println("Auras: ON\n");
		settings.toggleBoolSetting(Settings.B_ALLOW_AURAS);
		
		knowl = new Knowl("I like dogs.", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}

	@Test
	public void testInquisitiveAuras() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, true);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		
		Knowl knowl = new Knowl("This is only a test?", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		knowl = new Knowl("I?", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		knowl = new Knowl("II?", settings);
		System.out.println(knowl.getTextDisplay(true));

//		knowl = new Knowl("...how about this?", settings); // text like this is not allowed! normally caught by grimoire or lexi somewhere.
//		System.out.println(knowl.getTextDisplay(true));
		
		knowl = new Knowl("or this..?", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		knowl = new Knowl("eh?..", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		knowl = new Knowl("And this??", settings);
		System.out.println(knowl.getTextDisplay(true));

		knowl = new Knowl("This one!?", settings);
		System.out.println(knowl.getTextDisplay(true));

		knowl = new Knowl("Now what?!", settings);
		System.out.println(knowl.getTextDisplay(true));

		knowl = new Knowl("123 Numbers?!", settings);
		System.out.println(knowl.getTextDisplay(true));

		knowl = new Knowl("Numbers numbers numbers 321?!", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testWelkinAuras() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		

		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, true);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, true);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		
		Knowl knowl;
		System.out.println("\n\n\n\n\n");
		
		
		System.out.println("'This is only a test!', adds a major and minor welkin on top right");
		knowl = new Knowl("This is only a test!", settings);
		System.out.println(knowl.getTextDisplay(true));

		System.out.println("'This is only a test?!', inquisitive, adds a major (well, minor, since only 2 runes) and minor welkin on top left");
		knowl = new Knowl("This is only a test?!", settings);
		System.out.println(knowl.getTextDisplay(true));

		System.out.println("'Thiiis is only a test?!', alternation of above test to add a major and minor to top left");
		knowl = new Knowl("Thiiis is only a test?!", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		System.out.println("Only room to add minors on the last rune");
		knowl = new Knowl("Great!", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		System.out.println("Only room to add minors on the first rune");
		knowl = new Knowl("Great?!", settings);
		System.out.println(knowl.getTextDisplay(true));

		
		System.out.println("Overcrowding, should cover 3/4 uppermost and lowermost (lower left empty since it is a statement)");
		knowl = new Knowl("This!", settings);
		System.out.println(knowl.getTextDisplay(true));

		System.out.println("Overcrowding, should cover 3/4 uppermost and lowermost (lower right empty since it is inquisitive)");
		knowl = new Knowl("This?!", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		System.out.println("Single rune in the first lore at the start to add meager to");
		knowl = new Knowl("It is only a test?!", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		System.out.println("Single rune in the last lore at the end to add meager to");
		knowl = new Knowl("This is it!", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		System.out.println("Double meager test");
		knowl = new Knowl("I?!", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	
	@Test
	public void testAbysmAuras() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		

		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, true);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, true);
		
		Knowl knowl;
		System.out.println("\n\n\n\n\n");
		
		
		System.out.println("'This is only a test...', adds two minor abysms on bottom left");
		knowl = new Knowl("This is only a test...", settings);
		System.out.println(knowl.getTextDisplay(true));

		System.out.println("'Thiiis is only a test...', alternation of above test to add a major and minor to bottom left");
		knowl = new Knowl("Thiiis is only a test...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		System.out.println("'This is only a test?...', inquisitive, adds a major and minor abysm on bottom right");
		knowl = new Knowl("This is only a test?...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		System.out.println("Only room to add minors on the first rune");
		knowl = new Knowl("Great...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		System.out.println("Only room to add minors on the last rune");
		knowl = new Knowl("Great?...", settings);
		System.out.println(knowl.getTextDisplay(true));

		
		System.out.println("Overcrowding, should cover 3/4 uppermost and lowermost (upper right empty since it is a statement)");
		knowl = new Knowl("This...", settings);
		System.out.println(knowl.getTextDisplay(true));

		System.out.println("Overcrowding, should cover 3/4 uppermost and lowermost (upper left empty since it is inquisitive)");
		knowl = new Knowl("This?...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		System.out.println("Single rune in the first lore at the start to add meager to");
		knowl = new Knowl("It is only a test...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		System.out.println("Single rune in the last lore at the end to add meager to");
		knowl = new Knowl("This is it...?", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		System.out.println("Double meager test");
		knowl = new Knowl("I?...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		
		// NOTE - ENABLING WELKIN!
		settings.toggleBoolSetting(Settings.B_AUTO_WELKIN);
		System.out.println("A test to prove that Welkin Auras take priority over Abysm Auras");
		knowl = new Knowl("This is the end...!?", settings);
		System.out.println(knowl.getTextDisplay(true));
		// NOTE - TURNING WELKINS BACK OFF!
		settings.toggleBoolSetting(Settings.B_AUTO_WELKIN);
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testAddingSpacesViaEmptyRune() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, true);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		
		Knowl knowl;
		System.out.println("\n\n\n\n\n");
		
		System.out.println("'This is only a test...'");
		knowl = new Knowl("This is only a test...", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	// dont.
	@Test // dont
	public void dontTest() { // public dont
		// dontTest, as opposed to testDont :>

		// DONT.
		// ok?
		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		
		// DONT!
		Knowl knowl;
		System.out.println("\n\n\n\n\n");
		
		// do
		// not
		System.out.println("Don't!\n");
		knowl = new Knowl("Don't!", settings);
		System.out.println(knowl.getTextDisplay(true));

		// do
		System.out.println("Don't'.\n");
		knowl = new Knowl("Don't'.", settings);
		System.out.println(knowl.getTextDisplay(true));

		System.out.println("Don't't.\n");
		knowl = new Knowl("Don't't.", settings);
		System.out.println(knowl.getTextDisplay(true));

		System.out.println("Don't'\n");
		knowl = new Knowl("Don't'", settings);
		System.out.println(knowl.getTextDisplay(true));
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true); // dont.
	}
	
}
