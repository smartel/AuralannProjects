package Test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;

import Text2Auralann.Lore;
import Text2Auralann.Rune;
import Text2Auralann.Settings;

public class LoreTests {


	@Test
	public void testDetermineRunes() {
		
		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		
		Lore lore = new Lore("theme", settings);
		LinkedList<Rune> runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		lore = new Lore("Test's", settings); // need to treat ('s) as one rune
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		lore = new Lore("Tests", settings); // need to treat final S as plural, even if it isn't actually plural
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		// Now for right_to_left
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("Tests", settings); // need to treat final S as plural, even if it isn't actually plural
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		lore = new Lore("axe", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();

		System.out.print("WHERE with left_to_right orientation: ");
		lore = new Lore("where", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		System.out.print("WHERE with right_to_left orientation: ");
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("where", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		System.out.print("WHERE with no doubles allowed: ");
		settings.changeBoolSetting(Settings.B_ALLOW_DOUBLES, false);
		lore = new Lore("where", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		settings.changeBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		
		lore = new Lore("strata", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		System.out.print("MARE with everything: ");
		lore = new Lore("mare", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();

		System.out.print("MARE with no doubles allowed: ");
		settings.changeBoolSetting(Settings.B_ALLOW_DOUBLES, false);
		lore = new Lore("mare", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();

		System.out.print("MARE with no doubles or triples allowed: ");
		settings.changeBoolSetting(Settings.B_ALLOW_TRIPLES, false);
		lore = new Lore("mare", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		settings.changeBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.changeBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		
		lore = new Lore("are", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		lore = new Lore("LETH", settings); // that's not even a word
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		lore = new Lore("ether", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		lore = new Lore("cerune", settings); // that's not feyrune! or auralann!
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		System.out.println("\nUnknown Character Test - Lore is \"ž\"");
		lore = new Lore("ž", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		
		settings.changeBoolSetting(Settings.B_ALLOW_QUADS, true);
		System.out.println("\nQuad Tests\n");
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("QUADRUNES", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		lore = new Lore("QUADRUNES", settings);
		runes = lore.determineRunes();
		for (int x = 0; x < runes.size(); ++x) {
			System.out.print("(" + runes.get(x).getAlphaRep() + ")");
		}
		System.out.println();
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testPrinting() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		
		// final destination
		System.out.println("Everything:");
		Lore lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(2, true, true);
		lore.applyLowerAuras(2, true, true);
		System.out.println(lore.getTextDisplay());

		// turn off meagers
		System.out.println("No meagers:");
		lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		
		// 1-to-1
		System.out.println("1-to-1:");
		lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(1, true, true);
		lore.applyLowerAuras(1, true, true);
		System.out.println(lore.getTextDisplay());
		
		// no lower, meager on
		System.out.println("No lowers, top is meager:");
		lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(2, true, true);
		lore.applyLowerAuras(0, true, true);
		System.out.println(lore.getTextDisplay());

		// no lower, meager off
		System.out.println("No lowers, no meager:");
		lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(0, false, true);
		System.out.println(lore.getTextDisplay());

		// no upper
		System.out.println("No uppers:");
		lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(0, true, true);
		lore.applyLowerAuras(2, true, true);
		System.out.println(lore.getTextDisplay());
		
		// no majors, meager
		System.out.println("No majors, meager:");
		lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(2, true, false);
		lore.applyLowerAuras(2, true, false);
		System.out.println(lore.getTextDisplay());
		
		// no majors, meager
		System.out.println("No majors, no meager:");
		lore = new Lore("PORVELM", settings);
		lore.applyUpperAuras(2, false, false);
		lore.applyLowerAuras(2, false, false);
		System.out.println(lore.getTextDisplay());
		
		
		
		System.out.println("\nPlural / Possessive Testing\n");

		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("YesPlurals", settings);
		System.out.println(lore.getTextDisplay());

		settings.changeBoolSetting(Settings.B_PLURAL_S, false);
		lore = new Lore("NoPlurals", settings);
		System.out.println(lore.getTextDisplay());
		settings.changeBoolSetting(Settings.B_PLURAL_S, true);
		
		lore = new Lore("YesPossessive's", settings);
		System.out.println(lore.getTextDisplay());

		settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
		lore = new Lore("NoPossessive's", settings);
		System.out.println(lore.getTextDisplay());
		settings.changeBoolSetting(Settings.B_POSSESSIVE_S, true);

		settings.changeBoolSetting(Settings.B_PLURAL_S, false);
		settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
		lore = new Lore("NoPluralPossessive's", settings);
		System.out.println(lore.getTextDisplay());
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testNumbers() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		
		System.out.println("Number tests:\n");
		
		// only full auras
		Lore lore = new Lore("1", settings);
		System.out.println(lore.getTextDisplay());
		
		// no half auras
		lore = new Lore("123", settings);
		System.out.println(lore.getTextDisplay());
		
		// one half aura
		lore = new Lore("1234", settings);
		System.out.println(lore.getTextDisplay());

		lore = new Lore("1234.567", settings);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("1234.567", settings);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		lore = new Lore("1234567.1234567", settings);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("1234567.1234567", settings);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		// numerals all in middle regions
		lore = new Lore("abc123def456ghi", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("abc123def456ghi", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);

		// right side is numeral
		lore = new Lore("abc123def456ghi789", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("abc123def456ghi789", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);

		// left side is numeral
		lore = new Lore("123abc456def", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("123abc456def", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		// seeing how WHEREs do. (WH)(ER)E vs (WH)(E)(RE)
		// (doubles / triples allowed here)
		lore = new Lore("123WHERE456", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("123WHERE456", settings);
		lore.applyLowerAuras(2, false, true);
		lore.applyUpperAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		lore = new Lore("VOL-2.0", settings);
		System.out.println("Before determining auras:\n" + lore.getTextDisplay() + "\nAfter determining auras:\n");
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("VOL-2.0", settings);
		System.out.println("Before determining auras:\n" + lore.getTextDisplay() + "\nAfter determining auras:\n");
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		lore = new Lore("VOL-1232.0123456", settings);
		System.out.println("Now with more decimals...\n");
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("VOL-1232.0123456", settings);
		System.out.println("Now with more decimals...\n");
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		

		lore = new Lore("VOL-1232.0123456.123456.2345.23", settings);
		System.out.println("Well this isn't fair, bad decimals... AKA, show no half vert auras.\n");
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("VOL-1232.0123456.123456.2345.23", settings);
		System.out.println("Well this isn't fair, bad decimals... AKA, show no half vert auras.\n");
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		
		// prices
		System.out.println("Price time.\n");
		// note - the main program won't actually put auras on the "$", but we are forcing them here
		lore = new Lore("$10", settings);
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		// note - the main program won't actually put auras on the "$", but we are forcing them here
		lore = new Lore("$10.99", settings);
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("10", settings);
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("10.99", settings);
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		
		
		// acronyms with major auras
		lore = new Lore("m1u2m", settings);
		System.out.println("Acronym time.\n");
		lore.applyUpperAuras(2, false, true);
		lore.applyLowerAuras(2, false, true);
		System.out.println(lore.getTextDisplay());
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testDeadRunes() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, true);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, true);
		settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 100);
		
		System.out.println("Everywhere I look is dead.");
		Lore lore = new Lore("Dead", settings);
		System.out.println(lore.getTextDisplay());
		// there isn't a dead at the end, because the next lore would have one at the start.
		// and since end runes are their own lores, we don't end up with a dead rune after a period.

		settings.changeIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 0);
		System.out.println("\nDead are gone now:");
		lore = new Lore("Alive", settings);
		System.out.println(lore.getTextDisplay());
		
		
		settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
		settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true);
		System.out.println("\nNow for something different: generic dead runes after one-rune lores, although testing with A & I first.");
		lore = new Lore("A", settings);
		System.out.println(lore.getTextDisplay()); // A&I
		lore = new Lore("I", settings);
		System.out.println(lore.getTextDisplay()); // A&I
		lore = new Lore("W", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("WY", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("ER", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("ERA", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("THE", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("THEME", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("RUNE", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("RUNED", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("S", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("WWWWW", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("'S", settings);
		System.out.println(lore.getTextDisplay()); // should
		
		System.out.println("Now trying with right-to-left parsing...");
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		lore = new Lore("A", settings);
		System.out.println(lore.getTextDisplay()); // A&I
		lore = new Lore("I", settings);
		System.out.println(lore.getTextDisplay()); // A&I
		lore = new Lore("W", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("WY", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("ER", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("ERA", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt, also (RA) comes before (ER)
		lore = new Lore("THE", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("THEME", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("RUNE", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("RUNED", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt, (ED) comes before (RUNE)
		lore = new Lore("S", settings);
		System.out.println(lore.getTextDisplay()); // should
		lore = new Lore("WWWWW", settings);
		System.out.println(lore.getTextDisplay()); // shouldnt
		lore = new Lore("'S", settings);
		System.out.println(lore.getTextDisplay()); // should
		

		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testAposDeadRunes() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, true);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 100);
		
		
		
		// NONE
		System.out.println("Apostrophe Configuration: NONE");
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_NONE);
		System.out.println("I'm");
		Lore lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// AS NEEDED
		System.out.println("Apostrophe Configuration: AS NEEDED");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_NEEDED);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// ALWAYS LEFT
		System.out.println("Apostrophe Configuration: LEFT");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_LEFT);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// ALWAYS RIGHT
		System.out.println("Apostrophe Configuration: RIGHT");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_RIGHT);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// ALWAYS BOTH
		System.out.println("Apostrophe Configuration: BOTH");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_BOTH);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		// do me a favor and try some right-to-left orientation too
		System.out.println("NOW SWITCHING TO RIGHT-TO-LEFT!!!");
		settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
		
		// NONE
		System.out.println("Apostrophe Configuration: NONE");
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_NONE);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// AS NEEDED
		System.out.println("Apostrophe Configuration: AS NEEDED");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_NEEDED);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// ALWAYS LEFT
		System.out.println("Apostrophe Configuration: LEFT");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_LEFT);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// ALWAYS RIGHT
		System.out.println("Apostrophe Configuration: RIGHT");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_RIGHT);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		
		
		
		// ALWAYS BOTH
		System.out.println("Apostrophe Configuration: BOTH");
		settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, Settings.APOS_BOTH);
		System.out.println("I'm");
		lore = new Lore("I'm", settings);
		System.out.println(lore.getTextDisplay());
		
		System.out.println("Rev'nan");
		lore = new Lore("Rev'nan", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Can't");
		lore = new Lore("Can't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'ont");
		lore = new Lore("D'ont", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("D'on't");
		lore = new Lore("D'on't", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("'Test");
		lore = new Lore("'Test", settings);
		System.out.println(lore.getTextDisplay());

		System.out.println("Test'");
		lore = new Lore("Test'", settings);
		System.out.println(lore.getTextDisplay());
		

		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
}
