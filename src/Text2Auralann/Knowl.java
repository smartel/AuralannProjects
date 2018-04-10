package Text2Auralann;

import java.util.LinkedList;
import java.util.StringTokenizer;

public class Knowl {
	
	// major styles
	public static final int NO_MAJORS = Settings.MAJORS_NONE;  // no majors, no meagers.
	public static final int MAJORS_FEW = Settings.MAJORS_FEW; // majors on first and last lores only. meagers only if needed.
	public static final int MAJORS_ALL = Settings.MAJORS_ALL; // majors on every lore. meagers only if needed.
	public static final int CUSTOM_STYLE = Settings.CUSTOM_STYLE; // custom style via ctor that allows for setting of every boolean
	
	private RuneDictionary rd;
	private Settings settings;
	private Alternator alter;
	private String alphaRep; // english representation of the knowl ie "Test." or "I am a sentence."
	private int majorStyle; // which major style should be used
	private boolean hasMajors; // whether every lore should have majors
	private boolean hasFirstLastMajorsOnly; // whether only the first / last lores should have majors
	// if both hasMeagers are false, then minors will always be used instead:
	private boolean hasMeagers; // whether uppermost/lowermost auras should always be meagers
	private boolean hasMeagersIfNeeded; // whether uppermost/lowermost auras should be meagers if first/last lore is 1 rune in length
	private LinkedList<Lore> lores; // lores in the knowl, in the order they are read. Lores only, for aura processing.
	private LinkedList<Lore> loresAndStops; // lores and stop glyphs (., ?, !, ...) in the order they are read
	
	private boolean applyAuras;
	
	// advanced features that are not usually toggled
	private boolean applyLowerAuras;
	
	
	public Knowl(String alphaRep, Settings settings) {
		this.settings = settings;
		rd = settings.getRuneDictionary();
		
		majorStyle = settings.getIntSetting(Settings.I_MAJOR_STYLE);
		initStyle(majorStyle);
		
		applyAuras = settings.getBoolSetting(Settings.B_ALLOW_AURAS);
		applyLowerAuras = settings.getBoolSetting(Settings.B_LOWER_AURAS);
		
		this.alphaRep = alphaRep.toUpperCase();
		determineLores();
		determineAuras();
		
		// now check for special auras (inquisitive, evocative)
		
		if (settings.getBoolSetting(Settings.B_AUTO_INQUISITIVE)) {
			determineInquis();
		}
		
		boolean appliedWelkin = false;
		if (settings.getBoolSetting(Settings.B_AUTO_WELKIN)) {
			appliedWelkin = determineWelkin();
		}
		
		if (settings.getBoolSetting(Settings.B_AUTO_ABYSM)) {
			determineAbysm(appliedWelkin);
		}
		
		// finally, alternate runes if applicable
		alter = new Alternator(settings);
		alter.alternate(this);		
	}
	
	private void initStyle(int style) {
		if (style == MAJORS_FEW) {
			// majors on first and last lore only. meagers only if they are needed. everything else is minor.
			hasMajors = false;
			hasFirstLastMajorsOnly = true;
			hasMeagers = false;
			hasMeagersIfNeeded = true;
		} else if (style == MAJORS_ALL) {
			// majors on every lore, meagers only if they are needed
			hasMajors = true;
			hasFirstLastMajorsOnly = false;
			hasMeagers = false;
			hasMeagersIfNeeded = true;
		} else if (style == NO_MAJORS) {
			// no majors or meagers at all
			hasMajors = false;
			hasFirstLastMajorsOnly = false;
			hasMeagers = false;
			hasMeagersIfNeeded = false;
		} else { // must be custom, so load from the Settings
			
		}
	}
	

	public void determineLores() {
		lores = new LinkedList<Lore>();
		loresAndStops = new LinkedList<Lore>();
		
		StringTokenizer st = new StringTokenizer(alphaRep);
		while (st.hasMoreTokens()) {
			String strLore = st.nextToken();
			
			// Loop over every glyph in the string. If you get to an end glyph, create a lore of everything before it, and then one of it.
			int lastStopAt = -1;
			for (int x = 0; x < strLore.length(); ++x) {
				String currentGlyph = strLore.substring(x, x+1);
				if (rd.isEndGlyph(currentGlyph) || rd.isSymbol(currentGlyph)) {	
					String currentLore = strLore.substring(lastStopAt+1, x);
					
					// hate to do it, but hardcoded an exception for "('s)" at the moment.
					// need an exception for numbers too, ie: "123.45"
					// fyi, one more hardcode, in the lore itself - last S is turned into the 'plural S' rune
					String nextGlyph = "";
					String prevGlyph = "";
					if (x+1 < strLore.length()) {
						nextGlyph = strLore.substring(x+1, x+2);
					}
					if (x-1 > 0) {
						prevGlyph = strLore.substring(x-1, x);
					}
					if (currentGlyph.equalsIgnoreCase("'") && nextGlyph.equalsIgnoreCase("S")) {
						// this is an exception " 's " so don't put an End.
					} else if (currentGlyph.equalsIgnoreCase("'") && !nextGlyph.isEmpty() && !(nextGlyph.equalsIgnoreCase(".") || nextGlyph.equalsIgnoreCase("!") || nextGlyph.equalsIgnoreCase("?"))) {
						// this is an exception - for contractions, we can put an aura under the apostrophe, but that's the only exception. (like "Don't")
					} else if (rd.isNumber(prevGlyph) && rd.isNumber(nextGlyph)) {
						// yet another exception. This is a decimal in a number, not an End
					} else {
						// If a knowl starts with a period, then we don't have a Lore yet, just a Stop.
						if (currentLore.length() > 0) {
							Lore lore = new Lore(currentLore, settings);
							lores.add(lore);
							loresAndStops.add(lore);
						}
						// now add the end glyph as its own lore
						Lore endLore = new Lore(strLore.substring(x, x+1), true, settings);
						loresAndStops.add(endLore);
						lastStopAt = x;
					}
				}
			}
			// add the lore at the very end, if it hasn't been already.
			if (lastStopAt != strLore.length()-1) {
				Lore lore = new Lore(strLore.substring(lastStopAt+1), settings);
				lores.add(lore);
				loresAndStops.add(lore);
			}
			
			// Add a spacing rune after the word, if another one follows it, if desired
			if (settings.getBoolSetting(Settings.B_INSERT_SPACES)) {
				if (st.hasMoreTokens()) {
					// it is an 'end' symbol technically, but won't be filtered if END SYMBOLS are turned off
					Lore space = new Lore(" ", true, settings);
					lores.add(space);
					loresAndStops.add(space);
				}
			}
		}
	}
	
	
	public void determineAuras() {
		if (lores.size() > 0 && applyAuras) {
			// apply 2 upper auras to the first lore in the knowl, as well as setting meagers, majors, ...
			boolean isMeager = false;
			
			int length = lores.get(0).getRunes().size();
			if (hasMeagers || (hasMeagersIfNeeded && length==1)) {
				isMeager = true;
			}
			boolean isMajor = false;
			if (hasMajors || hasFirstLastMajorsOnly) {
				isMajor = true;
			}
			lores.get(0).applyUpperAuras(2, isMeager, isMajor);
			
			// every lore after that only gets one upper aura
			isMajor = false;
			if (hasMajors) {
				isMajor = true;
			}
			for (int x = 1; x < lores.size(); ++x) {
				lores.get(x).applyUpperAuras(1, false, isMajor); // meager does nothing here since not uppermost
			}
			
			if (applyLowerAuras) {
				// apply 2 lower auras to the last lore in the knowl, as well as setting meagers, majors, ...
				isMeager = false;
				
				length = lores.get(lores.size()-1).getRunes().size();
				if (hasMeagers || (hasMeagersIfNeeded && length==1)) {
					isMeager = true;
				}
				isMajor = false;
				if (hasMajors || hasFirstLastMajorsOnly) {
					isMajor = true;
				}
				lores.get(lores.size()-1).applyLowerAuras(2, isMeager, isMajor);
				
				// every lore before the last one only gets one lower aura
				isMajor = false;
				if (hasMajors) {
					isMajor = true;
				}
				for (int x = 0; x < lores.size()-1; ++x) {
					lores.get(x).applyLowerAuras(1, false, isMajor); // meager does nothing here since not lowermost
				}
			}
		}
	}
	
	public void determineInquis() {
		
		// loop over all the ends at the end of the knowl (so we catch "?", "..?", "?..", "!?", "?!", "??" and what have you)
		// if we see there's a question mark, then we'll swap to inquisitive auras. the instant we get a non-end, we stop looking for a question mark.
		
		boolean isInquis = false;
		int numLores = loresAndStops.size();
		int lastNonEndLoreIndex = -1; // the lore at the end of the knowl that is NOT an End symbol, but an actual word.
		for (int x = numLores-1; x >= 0; --x) {
			Lore lore = loresAndStops.get(x);
			if (lore.getIsEnd()) {
				String display = lore.getAlphaRep();
				if (display.equalsIgnoreCase("?")) {
					isInquis = true;
				}
			} else {
				lastNonEndLoreIndex = x;
				x = -1;
			}
		}
		
		if (isInquis && applyAuras && (lastNonEndLoreIndex != -1)) {
			
			// just flip the currently existing ones upside-down, rather than swapping the ones from the first lore to the last lore and having to calculate majors / meagers etc.
			
			// Find the first lore, we already stored the index of the last one above
			Lore firstLore = null;
			for (int x = 0; x < numLores; ++x) {
				if (!loresAndStops.get(x).getIsEnd()) {
					firstLore = loresAndStops.get(x);
					x = numLores;
				}
			}
			Lore lastLore = loresAndStops.get(lastNonEndLoreIndex);
			
			firstLore.setInquisitive(true);
			lastLore.setInquisitive(true);
			
		}
	}
	
	public boolean determineWelkin() {
		
		boolean appliedWelkin = false;
		
		// don't change any existing auras, just add new ones!
		
		// loop over all the ends at the end of the knowl (so we catch '!')
		// if we see there's an exclamation point, then we'll apply welkin auras. the instant we get a non-end, we stop looking for a "!".
		
		boolean isWelkin = false;
		int numLores = loresAndStops.size();
		int lastNonEndLoreIndex = -1; // the lore at the end of the knowl that is NOT an End symbol, but an actual word.
		for (int x = numLores-1; x >= 0; --x) {
			Lore lore = loresAndStops.get(x);
			if (lore.getIsEnd()) {
				String display = lore.getAlphaRep();
				if (display.equalsIgnoreCase("!")) {
					isWelkin = true;
				}
			} else {
				lastNonEndLoreIndex = x;
				x = -1;
			}
		}
		
		if (isWelkin && applyAuras && (lastNonEndLoreIndex != -1)) {
			
			// Find the first lore, we already stored the index of the last one above
			Lore firstLore = null;
			for (int x = 0; x < numLores; ++x) {
				if (!loresAndStops.get(x).getIsEnd()) {
					firstLore = loresAndStops.get(x);
					x = numLores;
				}
			}
			Lore lastLore = loresAndStops.get(lastNonEndLoreIndex);
			
			firstLore.addWelkinAuras(Lore.FIRST_LORE);
			lastLore.addWelkinAuras(Lore.LAST_LORE);
			appliedWelkin = true;
		}
		
		return appliedWelkin;
	}
	
	public void determineAbysm(boolean appliedWelkin) {

		// if Welkin was already applied, don't apply Abysm, because we'll have all upper and lower auras on for the first and last lores, and no idea how to read it.
		
		if (!appliedWelkin) {
			
			// don't change any existing auras, just add new ones!
			
			// loop over all the ends at the end of the knowl (so we catch "...")
			// if we see there's a question mark, then we'll swap to inquisitive auras. the instant we get a non-end, we stop looking for a question mark.
			
			int dotCount = 0;
			int numLores = loresAndStops.size();
			int lastNonEndLoreIndex = -1; // the lore at the end of the knowl that is NOT an End symbol, but an actual word.
			for (int x = numLores-1; x >= 0; --x) {
				Lore lore = loresAndStops.get(x);
				if (lore.getIsEnd()) {
					String display = lore.getAlphaRep();
					if (display.equalsIgnoreCase(".")) {
						dotCount++;
					}
				} else {
					lastNonEndLoreIndex = x;
					x = -1;
				}
			}
			
			if ((dotCount>=2) && applyAuras && (lastNonEndLoreIndex != -1)) {
				
				// Find the first lore, we already stored the index of the last one above
				Lore firstLore = null;
				for (int x = 0; x < numLores; ++x) {
					if (!loresAndStops.get(x).getIsEnd()) {
						firstLore = loresAndStops.get(x);
						x = numLores;
					}
				}
				Lore lastLore = loresAndStops.get(lastNonEndLoreIndex);
				
				firstLore.addAbysmAuras(Lore.FIRST_LORE);
				lastLore.addAbysmAuras(Lore.LAST_LORE);
			}
		}
	}
	
	
	// there are no spaces in Auralann, so an option to pass in a boolean for debugging purposes should be kept secret.
	// NOTE: ONLY USED FOR TESTING, see Grimoire#getTextDisplayByRuneCount for real method
	public String getTextDisplay() {
		return getTextDisplay(false);
	}

	// NOTE: ONLY USED FOR TESTING, see Grimoire#getTextDisplayByRuneCount for real method
	public String getTextDisplay(boolean spacesBetweenWords) {
		String rep = "";
		
		rep += getTextDisplayByLine(1, spacesBetweenWords) + "\n";
		rep += getTextDisplayByLine(2, spacesBetweenWords) + "\n";
		rep += getTextDisplayByLine(3, spacesBetweenWords) + "\n";		
		rep += getTextDisplayByLine(4, spacesBetweenWords) + "\n";
		rep += getTextDisplayByLine(5, spacesBetweenWords) + "\n";
		
		return rep;
	}

	// Never append a newline in a ByLine method
	// NOTE: ONLY USED FOR TESTING, see Grimoire#getTextDisplayByRuneCount for real method
	public String getTextDisplayByLine(int line, boolean spacesBetweenWords) {
		String rep = "";

		for (int x = 0; x < loresAndStops.size(); ++x) {
			if (!spacesBetweenWords) {
				rep += loresAndStops.get(x).getTextDisplayByLine(line);
			} else {
				rep += loresAndStops.get(x).getTextDisplayByLine(line) + "   ";
			}
		}
		
		return rep;
	}
	
	public LinkedList<Lore> getLoresAndStops() {
		return loresAndStops;
	}
	
	public void addSpaceToStartOfKnowl() {
		// When there is more than one knowl, and the user desires spaces between lores, this method will add a space between KNOWLS.
		// ie, this will add the space [HERE] in the following example: "This is an example.[HERE]Now we have space!"
		// Grimoire will call this on every knowl except the first one.
		
		if (settings.getBoolSetting(Settings.B_INSERT_SPACES)) {
			// Space is an 'end' symbol technically, but won't be filtered if END SYMBOLS are turned off
			Lore space = new Lore(" ", true, settings);
			lores.addFirst(space);
			loresAndStops.addFirst(space);
		}
	}
}
