package Text2Auralann;

import java.util.LinkedList;

public class Grimoire {

	RuneDictionary rd;
	LinkedList<Knowl> knowls;
	Settings settings;
	private LinkedList<LinkedList<String>> linesToFilePaths; // [0] = filepaths for runes for the first line, [1] = filepaths for runes for the second line, ...
	
	public Grimoire(Settings settings) {
		rd = settings.getRuneDictionary();
		knowls = new LinkedList<Knowl>();
		this.settings = settings;

		// for image displaying
		linesToFilePaths = new LinkedList<LinkedList<String>>();
	}
	
	// Will create the knowl with the supplied settings
	public void createKnowl(String alphaRep) {
		Knowl knowl = new Knowl(alphaRep, settings);
		knowls.add(knowl);
	}

	// A knowl-tokenizer. Converts a blob of text into knowls
	// Default values: mixed style, left-to-right parsing, allows doubles / triples
	public void createKnowlsFromBlob(String text) {
		// If you get to the end of the string, add what you've got as a knowl.
		// If you get an End, and there's a space after it, insert everything you've got up to and including the End as a knowl.
		// Meh, just trim the strings before inserting into knowls, incase we get things like " Hello!   Good morning!  "
		
		int current = 0;
		String currentKnowl = "";
		
		while (current < text.length()) {
			String thisGlyph = text.substring(current, current+1);
			currentKnowl += thisGlyph;
			if ((current < text.length() - 2) && rd.isEndGlyph(thisGlyph) && text.substring(current+1, current+2).equals(" ")) {
				currentKnowl = currentKnowl.trim();
				createKnowl(currentKnowl);
				currentKnowl = "";
			}
			current++;
		}
		if (!currentKnowl.isEmpty()) {
			createKnowl(currentKnowl);
		}
		
		// add spacing, if desired, between the knowls (skip over the first one)
		for (int x = 1; x < knowls.size(); ++x) {
			knowls.get(x).addSpaceToStartOfKnowl();
		}
	}
	
	
	public int getKnowlCount() {
		return knowls.size();
	}
	
	public Knowl getKnowl(int lineNum) {
		return knowls.get(lineNum);
	}
	
	// only used for testing!!
	public String getTextDisplay(int lineNum) {

		// only used for testing! use getTextDisplayByRuneCount!!
		
		return knowls.get(lineNum).getTextDisplay();
	}

	// only used for testing!!
	public String getAllTextDisplay() {
		
		// only used for testing! use getTextDisplayByRuneCount!!
		
		String display = "";
		String line1 = "", line2 = "", line3 = "", line4 = "", line5 = "";
		for (int x = 0; x < knowls.size(); ++x) {
			Knowl knowl = knowls.get(x);
			line1 += knowl.getTextDisplayByLine(1, false);
			line2 += knowl.getTextDisplayByLine(2, false);
			line3 += knowl.getTextDisplayByLine(3, false);
			line4 += knowl.getTextDisplayByLine(4, false);
			line5 += knowl.getTextDisplayByLine(5, false);
		}
		display = line1 + "\n" + line2 + "\n" + line3 + "\n" + line4 + "\n" + line5 + "\n";
		return display;
	}
	
	public String getTextDisplayByRuneCount(int runeCount) throws Exception {
		String rep = "";
		LinkedList<LinkedList<String>> allLines = new LinkedList<LinkedList<String>>();
		// The innermost list is lines 1-5 in chunks of runecount, and the outerlist is the collection of all these chunks
		
		RuneDictionaryEntry emptyRde = settings.getRuneDictionary().getRDE("EMPTY");
		String emptyPath = emptyRde.getFilePath("Empty");
		RuneDictionaryEntry emptyRuneRde = settings.getRuneDictionary().getRDE("EMPTY-RUNE");
		String emptyRunePath = emptyRuneRde.getFilePath("EMPTY-RUNE");
		RuneDictionaryEntry middleVertRde = settings.getRuneDictionary().getRDE("NumberSepMiddleLarge");
		String vertPath = middleVertRde.getFilePath("NumberSepMiddleLarge");
		
		// need to get all the runes here, and we can tell them to print by line
		LinkedList<Rune> allRunes = new LinkedList<Rune>();
		// for each knowl
		//   for each lore
		//     for each rune, insert the rune into the collection, so we can iterate over and print it
		for (int x = 0; x < knowls.size(); ++x) {
			Knowl currentKnowl = knowls.get(x);
			LinkedList<Lore> loresToLoop = currentKnowl.getLoresAndStops();
			for (int y = 0; y < loresToLoop.size(); ++y) {
				Lore currentLore = loresToLoop.get(y);
				
				// see if this is an end .,?,!, and filter if it is and desired
				if (currentLore.getRunes().size() == 1) {
					Rune currentRune = currentLore.getRunes().get(0);						
					if (currentRune.getAlphaRep().equals(".") || 
						currentRune.getAlphaRep().equals("?") || 
						currentRune.getAlphaRep().equals("!"))
					{
						if (settings.getBoolSetting(Settings.B_FILTER_ENDS)) {
							// do nothing, we are filtering the char out
						} else {
							// add as usual
							allRunes.add(currentRune);
						}
					} else {
						// add as usual
						allRunes.add(currentRune);
					}
				} else {
					for (int z = 0; z < currentLore.getRunes().size(); ++z) {
						Rune currentRune = currentLore.getRunes().get(z);
						allRunes.add(currentRune);
					}
				}
			}
		}
		
		
		// Rune counting and overflowing
		int current = 0;
		
		while (current < allRunes.size()) {
			LinkedList<String> filepaths1 = new LinkedList<String>(); // Holds uppermost aura line for display
			LinkedList<String> filepaths2 = new LinkedList<String>(); // Holds upper aura line for display
			LinkedList<String> filepaths3 = new LinkedList<String>(); // Holds rune line for display
			LinkedList<String> filepaths4 = new LinkedList<String>(); // Holds lower aura line for display
			LinkedList<String> filepaths5 = new LinkedList<String>(); // Holds lowermost aura line for display
			String line1 = "", line2 = "", line3 = "", line4 = "", line5 = "";
			int end = current + runeCount;
			while (current < end) {
				if (current < allRunes.size()) {
					Rune rune = allRunes.get(current);
					line1 += rune.getTextDisplayByLine(1);
					line2 += rune.getTextDisplayByLine(2);
					line3 += rune.getTextDisplayByLine(3);
					line4 += rune.getTextDisplayByLine(4);
					line5 += rune.getTextDisplayByLine(5);
					
					
					// for image display, add the file paths to the runes and auras/empty space
					if (rune.getUppermostAura() != null && !rune.isVert() && !rune.isNumber()) {
						filepaths1.add(rd.getRDE(rune.getUppermostAura().getForm()).getFilePath());
					} else if (rune.isVert() && rune.getUppermostVert() != null) {
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS)) {
							filepaths1.add(rd.getRDE(rune.getUppermostVert().getForm()).getFilePath());
						} else {
							filepaths1.add(emptyPath);
						}
					} else {
						filepaths1.add(emptyPath);
					}
					if (rune.getUpperAura() != null && !rune.isVert() && !rune.isNumber()) {
						filepaths2.add(rd.getRDE(rune.getUpperAura().getForm()).getFilePath());
					} else if (rune.isVert() && rune.getUpperVert() != null) {
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS)) {
							filepaths2.add(rd.getRDE(rune.getUpperVert().getForm()).getFilePath());
						} else {
							filepaths2.add(emptyPath);
						}
					} else {
						filepaths2.add(emptyPath);
					}
					
					if (rune.getRde() != null) { // this is null for vertical auras
						filepaths3.add(rune.getRde().getFilePath(rune.getRde().getForm(rune.getRuneForm())));
					} else {
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS)) {
							filepaths3.add(vertPath);
						} else {
							filepaths3.add(emptyRunePath);
						}
					}
						
					if (rune.getLowerAura() != null && !rune.isVert() && !rune.isNumber()) {
						filepaths4.add(rd.getRDE(rune.getLowerAura().getForm()).getFilePath());
					} else if (rune.isVert() && rune.getLowerVert() != null) {
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS)) {
							filepaths4.add(rd.getRDE(rune.getLowerVert().getForm()).getFilePath());
						} else {
							filepaths4.add(emptyPath);
						}
					} else {
						filepaths4.add(emptyPath);
					}
					if (rune.getLowermostAura() != null && !rune.isVert() && !rune.isNumber()) {
						filepaths5.add(rd.getRDE(rune.getLowermostAura().getForm()).getFilePath());
					} else if (rune.isVert() && rune.getLowermostVert() != null) {
						if (settings.getBoolSetting(Settings.B_ALLOW_AURAS)) {
							filepaths5.add(rd.getRDE(rune.getLowermostVert().getForm()).getFilePath());
						} else {
							filepaths5.add(emptyPath);
						}
					} else {
						filepaths5.add(emptyPath);
					}
				}
				current++;
			}
		
			// if this last rune is the left part of a major (either upper or lower), append the right part to this string as well.
			// It's an "Overflow"
			try {
				if (settings.getBoolSetting(Settings.B_RUNE_OVERFLOW)) {
					if ( (allRunes.get(current-1).getUpperAura() != null && allRunes.get(current-1).getUpperAura().getIsLeftMajor()) ||
						 (allRunes.get(current-1).getLowerAura() != null && allRunes.get(current-1).getLowerAura().getIsLeftMajor())) {
						Rune rune = allRunes.get(current);
						line1 += rune.getTextDisplayByLine(1);
						line2 += rune.getTextDisplayByLine(2);
						line3 += rune.getTextDisplayByLine(3);
						line4 += rune.getTextDisplayByLine(4);
						line5 += rune.getTextDisplayByLine(5);
						
						
						// for image display, add the file paths to the runes and auras/empty space
						if (rune.getUppermostAura() != null && !rune.isVert() && !rune.isNumber()) {
							filepaths1.add(rd.getRDE(rune.getUppermostAura().getForm()).getFilePath());
						} else if (rune.isVert() && rune.getUppermostVert() != null) {
							filepaths1.add(rd.getRDE(rune.getUppermostVert().getForm()).getFilePath());
						} else {
							filepaths1.add(emptyPath);
						}
						if (rune.getUpperAura() != null && !rune.isVert() && !rune.isNumber()) {
							filepaths2.add(rd.getRDE(rune.getUpperAura().getForm()).getFilePath());
						} else if (rune.isVert() && rune.getUpperVert() != null) {
							filepaths2.add(rd.getRDE(rune.getUpperVert().getForm()).getFilePath());
						} else {
							filepaths2.add(emptyPath);
						}
						
						if (rune.getRde() != null) { // this is null for vertical auras
							filepaths3.add(rune.getRde().getFilePath(rune.getRde().getForm(rune.getRuneForm())));
						} else {
							filepaths3.add(vertPath);
						}
						
						if (rune.getLowerAura() != null && !rune.isVert() && !rune.isNumber()) {
							filepaths4.add(rd.getRDE(rune.getLowerAura().getForm()).getFilePath());
						} else if (rune.isVert() && rune.getLowerVert() != null) {
							filepaths4.add(rd.getRDE(rune.getLowerVert().getForm()).getFilePath());
						} else {
							filepaths4.add(emptyPath);
						}
						if (rune.getLowermostAura() != null && !rune.isVert() && !rune.isNumber()) {
							filepaths5.add(rd.getRDE(rune.getLowermostAura().getForm()).getFilePath());
						} else if (rune.isVert() && rune.getLowermostVert() != null) {
							filepaths5.add(rd.getRDE(rune.getLowermostVert().getForm()).getFilePath());
						} else {
							filepaths5.add(emptyPath);
						}
						
						current++;
					}
				}
			} catch (Exception e) {
				// just swallow exception, it means we had nothing to append to the end of the string, since current > end
			}
			
			LinkedList<String> currentLines = new LinkedList<String>();
			currentLines.add(line1);
			currentLines.add(line2);
			currentLines.add(line3);
			currentLines.add(line4);
			currentLines.add(line5);
			allLines.add(currentLines);
			
			// for image display...
			linesToFilePaths.add(filepaths1);
			linesToFilePaths.add(filepaths2);
			linesToFilePaths.add(filepaths3);
			linesToFilePaths.add(filepaths4);
			linesToFilePaths.add(filepaths5);
		}

		String delimiter = "";
		for (int x = 0; x < (runeCount*4)+2; ++x) {
			delimiter += "=";
		}
		delimiter += "\n";
		
		for (int x = 0; x < allLines.size(); ++x) {
			LinkedList<String> currentLines = allLines.get(x);
			rep += delimiter;
			for (int y = 0; y < currentLines.size(); ++y) {
				rep += currentLines.get(y) + "\n";
			}
		}
		rep += delimiter;
		
		return rep;
	}
	
	public LinkedList<LinkedList<String>> getFilePaths() {
		return linesToFilePaths;
	}
	
}
