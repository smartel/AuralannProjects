package Test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import Text2Auralann.Grimoire;
import Text2Auralann.Settings;

public class GrimoireTests {

	@Test
	public void testCreateKnowl() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
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
		
		Grimoire grim = new Grimoire(settings);
		// This isn't actually Lann'lain lore, this is like.. proto-lore. Any text in these tests would be.
		grim.createKnowl("The cult of Aran.");
		grim.createKnowl("Necromancers, led by Aranor, the only one allowed a name.");
		grim.createKnowl("He holds the phylactery of a long dead god.");
		grim.createKnowl("They search for a suitable corpse.");
		grim.createKnowl("Lesser bodies are \"fed\" to the entity to retain its connection to this realm, lest its soul slip away.");
		grim.createKnowl("Darkness twists the bodies beyond recognition, as the flesh sloughs to the ground and turns to ash and aether.");
		grim.createKnowl("Throughout the ritual, smell of sulfur surrounds the altar.");
		grim.createKnowl("A suitable body would be an amalgamation of several monstrous corpses.");
		grim.createKnowl("Should an elder dragon be slain and used, the god would surely be resurrected.");
		grim.createKnowl("Little does Aranor know, each \"feeding\" increases its rancor.");
		grim.createKnowl("While men seek to bring it back to life, they are also prolonging its suffering.");
		grim.createKnowl("And should it truly ever wake, the world will crack.");
		grim.createKnowl("Ferialzaz, Sorazae, Kerazaz.");
		grim.createKnowl("The last stewards against Ulthoth.");
		grim.createKnowl("The deathwind howls.");
		
		System.out.println("Just one line...\n" + grim.getTextDisplay(0));
		
		System.out.println("All lines...");
		System.out.println(grim.getAllTextDisplay());
		
		System.out.println("Just curious how a grimoire containing only \"\" would print... No explosions!");
		grim = new Grimoire(settings);
		grim.createKnowl("");
		System.out.println(grim.getAllTextDisplay() + "And that's that.");

		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	@Test
	public void testKnowlTokenizer() {
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
		
		Grimoire grim = new Grimoire(settings);
		grim.createKnowlsFromBlob(" This is a test sentence... do you understand?   that's why we got " +
			     "all of these weird uh, End glyphs, ok? Good. Glad that's settled. ...Spacing too.   Yeesh!   Haha  ");	
		//System.out.println(grim.getAllTextDisplay());
		for (int x = 0; x < grim.getKnowlCount(); ++x) {
			System.out.println("Knowl " + x + " start:\n" + grim.getKnowl(x).getTextDisplay() + "Knowl " + x + " end.");
		}
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
	
	@Test
	public void testRuneCount() {
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
		settings.addBoolSetting(Settings.B_RUNE_OVERFLOW, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		
		try {
			Grimoire grim = new Grimoire(settings);
			grim.createKnowlsFromBlob("Time to test this runecount once and for all...");
			System.out.println(grim.getTextDisplayByRuneCount(10));
			
			
			
	
			// 2 groups of 3
			grim = new Grimoire(settings);
			grim.createKnowl("RUNECOUNT");
			System.out.println(grim.getTextDisplayByRuneCount(3));
			// 3 groups of 2
			grim = new Grimoire(settings);
			grim.createKnowl("RUNECOUNT");
			System.out.println(grim.getTextDisplayByRuneCount(2));
			// uneven grouping
			grim = new Grimoire(settings);
			grim.createKnowl("RUNECOUNT");
			System.out.println(grim.getTextDisplayByRuneCount(5));
			// overwhelming grouping
			grim = new Grimoire(settings);
			grim.createKnowl("RUNECOUNT");
			System.out.println(grim.getTextDisplayByRuneCount(20));
			
	
			// and now, have a major be appended to the end of a line (overflow)
			System.out.println("\nTest with overflow on, grouping size of 1...\n");
			grim = new Grimoire(settings);
			grim.createKnowl("RUNECOUNT");
			System.out.println(grim.getTextDisplayByRuneCount(1));
			
			System.out.println("\nTest with overflow off, grouping size of 1...\n");
			settings.changeBoolSetting(Settings.B_RUNE_OVERFLOW, false);
			grim = new Grimoire(settings);
			grim.createKnowl("RUNECOUNT");
			System.out.println(grim.getTextDisplayByRuneCount(1));
		} catch (Exception e) {
			assertTrue(false);
		}
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}

	@Test
	public void testSpecialRunes() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
		settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
		settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
		settings.addBoolSetting(Settings.B_PLURAL_S, true);
		settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
		settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
		settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, true); // for testing A-DEAD I-DEAD specials
		settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, 0);
		settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true); // for testing COMMON-DEAD special
		settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
		settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
		settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
		settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
		settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
		settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
		

		//settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FILE);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		
		System.out.println("Testing special runes...\n");

		// More proto-lore? :Y
		
		Grimoire grim = new Grimoire(settings);
		// I-DEAD: (I)(I-DEAD)
		// A-DEAD: (A)(A-DEAD)
		// UNKNOWN: (GHOST)(#)=(UNKNOWN)
		// COMMON-DEAD: (THE)(COMMON-DEAD)
		// PLURAL-S: (SIN)(PLURAL-S)
		// also a few non-special other things, like numbers ("1"), end symbols ("!"), other symbols (";"), ...
		grim.createKnowl("\"I am a ghost# ; the sins of Aran, his wrath made manifest! The instrument of your namesake's ambition!");
		grim.createKnowl("You are a fool to bind me to this ring, but I am patient. I am far older than your cult, and I will continue well after it.");
		grim.createKnowl("One must not suffer weakness - it must be culled. I can taste his fear, his regret, his sorrow. He is fragile. I would take his soul for myself.");
		grim.createKnowl("The vessels of humanity are too frail to hold your phylactery's power - and why save sheep, when they'll only be slaughtered by the impending doom?");
		grim.createKnowl("Whether a spectre or jewelry, I will haunt Porvelm, and 1 day when your trinket fails you, when ancient magic is broken or the band is split,");
		grim.createKnowl("when flesh falls off or carelessness leads to tragedy, the ring will escape its prison and I will be free.");
		grim.createKnowl("When that day comes, all will know his screams!\"");
		grim.createKnowl("- Rev'nan's curse, at his imprisonment by Aranor to the Gaol Ring, to forever be worn to hold his darkness at bay.");
		System.out.println(grim.getAllTextDisplay());
		
		System.out.println("\nNow for dead runes...\n");
		
		// now with random deads for testing's sake:
		settings.toggleBoolSetting(Settings.B_ALLOW_RANDOM_DEAD);
		settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 100);
		grim = new Grimoire(settings);
		grim.createKnowl("I am a ghost# ; the sins of Aran, his wrath made manifest!");
		System.out.println(grim.getAllTextDisplay());
		
		// Don't have a real assert yet, just testing by eye
		assert(true);
	}
}
