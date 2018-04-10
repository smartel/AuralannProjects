package Test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import Text2Auralann.AuraHori;
import Text2Auralann.AuraVert;
import Text2Auralann.Rune;
import Text2Auralann.Settings;

public class RuneTests {

	@Test
	public void testPrinting() {

		Settings settings = new Settings();
		settings.setBaseImageDirectory(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
		settings.addIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		
		// Aura (location, rune, left major, right major, meager)
		AuraHori uppermostMeager = new AuraHori(1, false, false, true);
		AuraHori uppermost = new AuraHori(1, false, false, false);
		AuraHori upper = new AuraHori(2, false, false, false);
		AuraHori lower = new AuraHori(3, false, false, false);
		AuraHori lowermostMeager = new AuraHori(4, false, false, true);
		AuraHori lowermost = new AuraHori(4, false, false, false);
		
		System.out.println("Everything:");
		Rune rune = new Rune("ETH", uppermostMeager, upper, lower, lowermostMeager, settings);
		System.out.println(rune.getTextDisplay());
		
		System.out.println("No uppers, no meagers");
		rune = new Rune("ETH", null, null, lower, lowermost, settings);
		System.out.println(rune.getTextDisplay());
		
		System.out.println("No lowers, no meagers");
		rune = new Rune("ETH", uppermost, upper, null, null, settings);
		System.out.println(rune.getTextDisplay());
		
		System.out.println("Double Glyph");
		rune = new Rune("ER", new AuraHori(1, false, false, false), new AuraHori(2, false, false, false),
			            	  new AuraHori(3, false, false, false), new AuraHori(4, false, false, false), settings);
		System.out.println(rune.getTextDisplay());
		
		System.out.println("Single Glyph");
		rune = new Rune("A", new AuraHori(1, false, false, false), new AuraHori(2, false, false, false),
			            	 new AuraHori(3, false, false, false), new AuraHori(4, false, false, false), settings);
		System.out.println(rune.getTextDisplay());
		
		System.out.println("Major lefts");
		rune = new Rune("ER", new AuraHori(1, false, false, false), new AuraHori(2, true, false, false),
			            	  new AuraHori(3, true, false, false), new AuraHori(4, false, false, false), settings);
		System.out.println(rune.getTextDisplay());
		
		System.out.println("Major Rights");
		rune = new Rune("ER", new AuraHori(1, false, false, false), new AuraHori(2, false, true, false),
			            	  new AuraHori(3, false, true, false), new AuraHori(4, false, false, false), settings);
		System.out.println(rune.getTextDisplay());

		System.out.println("Top Left Major, Bottom Right Major");
		rune = new Rune("ER", new AuraHori(1, false, false, false), new AuraHori(2, true, false, false),
			            	  new AuraHori(3, false, true, false), new AuraHori(4, false, false, false), settings);
		System.out.println(rune.getTextDisplay());
		
		System.out.println("Top Right Major, Bottom Left Major");
		rune = new Rune("ER", new AuraHori(1, false, false, false), new AuraHori(2, false, true, false),
			            	  new AuraHori(3, true, false, false), new AuraHori(4, false, false, false), settings);
		System.out.println(rune.getTextDisplay());
		
		
		// Specific Line tests
		rune = new Rune("ETH", uppermostMeager, upper, lower, lowermostMeager, settings);
		System.out.println("ETH with everything specific lines:");
		System.out.println("Line 1:" + rune.getTextDisplayByLine(1));
		System.out.println("Line 2:" + rune.getTextDisplayByLine(2));
		System.out.println("Line 3:" + rune.getTextDisplayByLine(3));
		System.out.println("Line 4:" + rune.getTextDisplayByLine(4));
		System.out.println("Line 5:" + rune.getTextDisplayByLine(5));
		
		

		System.out.println("\nNUMBERS! (Cheating here - using a rune as the aura itself)");
		rune = new Rune(AuraVert.LENGTH_FULL, AuraVert.LOC_LEFT, settings);
		System.out.println("Full vert start:\n" + rune.getTextDisplay() + "Full vert end.");
		rune = new Rune(AuraVert.LENGTH_HALF, AuraVert.LOC_LEFT, settings);
		System.out.println("Half vert start:\n" + rune.getTextDisplay() + "Half vert end.");

		rune = new Rune(AuraVert.LENGTH_FULL, AuraVert.LOC_RIGHT, settings);
		System.out.println("Full vert start:\n" + rune.getTextDisplay() + "Full vert end.");
		rune = new Rune(AuraVert.LENGTH_HALF, AuraVert.LOC_RIGHT, settings);
		System.out.println("Half vert start:\n" + rune.getTextDisplay() + "Half vert end.");
		
		// Can't do this test here - since we are giving it directly what the alpha rep is. Needs to go through a DetermineRunes method.
		//System.out.println("\nUnknown Character Test: ž");
		//rune = new Rune("ž", uppermostMeager, upper, lower, lowermostMeager);
		//System.out.println(rune.getTextDisplay());
		
		
		// Testing display styles
		rune = new Rune("ETH", uppermostMeager, upper, lower, lowermostMeager, settings);
		settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FORM); // the default
		System.out.println("Testing display style as Rune and Form:\n" + rune.getTextDisplay()); // the default
		settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		System.out.println("Changing display style to Rune only:\n" + rune.getTextDisplay());
		settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FILE);
		System.out.println("Changing display style to File:\n" + rune.getTextDisplay());
		
		// Now getting the styles to display for vert auras...
		rune = new Rune(AuraVert.LENGTH_HALF, AuraVert.LOC_RIGHT, settings);
		settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FORM); // the default
		System.out.println("Testing display style as Rune and Form for vert aura:\n" + rune.getTextDisplay()); // the default
		settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
		System.out.println("Changing display style to Rune only for vert aura:\n" + rune.getTextDisplay());
		settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FILE);
		System.out.println("Changing display style to File for vert aura:\n" + rune.getTextDisplay());
		
		
		// Don't have a real assert yet, just testing by eye
		assertTrue(true);
	}
	
}
