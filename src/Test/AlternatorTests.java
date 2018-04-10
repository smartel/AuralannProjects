package Test;

import org.junit.Test;

import Text2Auralann.Grimoire;
import Text2Auralann.Settings;

public class AlternatorTests {

	@Test
	public void testAlternateModes() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
		settings.addIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FORM);
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
		
		
		System.out.println("\n\nTests with 2 possible forms");
		
		
		// Test no alts
		settings.addIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		Grimoire grim = new Grimoire(settings);
		grim.createKnowl("CCCCCCCCCCCCCCCC");
		System.out.println("\n\nNo alts (should all be C-1):\n" + grim.getAllTextDisplay());
		
		
		// Test equal chance
		settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_EQUAL_CHANCE);
		grim = new Grimoire(settings);
		grim.createKnowl("CCCCCCCCCCCCCCCC");
		System.out.println("\n\nEqual chance for all forms (50% chance of either):\n" + grim.getAllTextDisplay());
		
		
		// Test alts only
		settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALTS_ONLY);
		grim = new Grimoire(settings);
		grim.createKnowl("CCCCCCCCCCCCCCCC");
		System.out.println("\n\nAlts only if possible (they are):\n" + grim.getAllTextDisplay());
		
		
		// Test alternate alts
		settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALT_ALTS);
		grim = new Grimoire(settings);
		grim.createKnowl("CCCCCCCCCCCCCCCC");
		System.out.println("\n\nAlternate forms:\n" + grim.getAllTextDisplay());
		
		
		System.out.println("\n\nTests with 3 possible forms:");

		
		// Test no alts
		settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
		grim = new Grimoire(settings);
		grim.createKnowl("RERERERERERERERE");
		System.out.println("\n\nNo alts (should all be RE-1):\n" + grim.getAllTextDisplay());
		
		
		// Test equal chance
		settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_EQUAL_CHANCE);
		grim = new Grimoire(settings);
		grim.createKnowl("RERERERERERERERE");
		System.out.println("\n\nEqual chance for all forms (1/3 chance for each):\n" + grim.getAllTextDisplay());
		
		
		// Test alts only
		settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALTS_ONLY);
		grim = new Grimoire(settings);
		grim.createKnowl("RERERERERERERERE");
		System.out.println("\n\nAlts only if possible (they are):\n" + grim.getAllTextDisplay());
		
		
		// Test alternate alts
		settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALT_ALTS);
		grim = new Grimoire(settings);
		grim.createKnowl("RERERERERERERERE");
		System.out.println("\n\nAlternate forms:\n" + grim.getAllTextDisplay());
		
		
		// Don't have a real assert yet, just testing by eye
		assert(true);
	}
}
