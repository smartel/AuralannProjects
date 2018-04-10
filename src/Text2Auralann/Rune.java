package Text2Auralann;

public class Rune {
	
	private String alphaRep; // The alphanumeric representation of the rune, ie: a single, double, triple glyph, or other symbol
							 // 'T' 'TH' 'THE' '1' '.' and so on...

	Settings settings;
	RuneDictionaryEntry rde;
	
	// Auras
	AuraHori uppermostAura;
	AuraHori upperAura;
	AuraHori lowerAura;
	AuraHori lowermostAura;
	AuraVert uppermostVert;
	AuraVert upperVert;
	AuraVert lowerVert;
	AuraVert lowermostVert;
	
	private boolean isVert;
	private boolean isNumber;
	private boolean isInquisitive;
	
	int runeForm; // which form of the rune, ie: 0 for base rune (O-1), 1 for first alt (O-2), ...
		
	// Lines for text-display purposes
	String line1; // top line, aka "Uppermost" aura
	String line2; // "Upper" aura
	String line3; // Rune line
	String line4; // "Lower" aura
	String line5; // "Lowermost" aura

	
	public Rune(String alphaRep, Settings settings) {
		this.alphaRep = alphaRep.toUpperCase();
		this.settings = settings;
		determineIfNumber();
		isVert = false;
		// awaiting aura direction from the grimoire
		isInquisitive = false;
		uppermostAura = null;
		upperAura = null;
		lowerAura = null;
		lowermostAura = null;
		uppermostVert = null;
		upperVert = null;
		lowerVert = null;
		lowermostVert = null;
		try {
			rde = settings.getRuneDictionary().getRDE(alphaRep);
		} catch (Exception e) {
			// swallow for now, will get caught elsewhere like the grimoire
		}
		runeForm = 0;
	}
	
	public Rune(String alphaRep, AuraHori uppermostAura, AuraHori upperAura, AuraHori lowerAura, AuraHori lowermostAura, Settings settings) {
		this.alphaRep = alphaRep;
		this.settings = settings;
		determineIfNumber();
		isVert = false;
		isInquisitive = false;
		this.uppermostAura = uppermostAura;
		this.upperAura = upperAura;
		this.lowerAura = lowerAura;
		this.lowermostAura = lowermostAura;
		uppermostVert = null;
		upperVert = null;
		lowerVert = null;
		lowermostVert = null;
		try {
			rde = settings.getRuneDictionary().getRDE(alphaRep);
		} catch (Exception e) {
			// swallow for now, will get caught elsewhere like the grimoire
		}
		runeForm = 0;
	}
	
	// This ctor is for making a vertical aura. We had to hijack the Rune class so we could display it in the Rune line in text display.
	public Rune(int length, int side, Settings settings) {
		alphaRep = AuraVert.AURA;
		this.settings = settings;
		isNumber = false;
		isVert = true;
		isInquisitive = false;
		uppermostAura = null;
		upperAura = null;
		lowerAura = null;
		lowermostAura = null;
		uppermostVert = new AuraVert(AuraVert.LOC_UPPERMOST, side, length);
		upperVert = new AuraVert(AuraVert.LOC_UPPER, side, length);
		lowerVert = new AuraVert(AuraVert.LOC_LOWER, side, length);
		lowermostVert = new AuraVert(AuraVert.LOC_LOWERMOST, side, length);
		// giving vert auras an rde so we can handle 2 cases - one, where the alternator takes all the runes and checks for / assigns a form,
		// and two, where the grimoire needs to grab the rune line's image path
		// wait, we specifically say they are treated differently in Rune#initializeLines(). treat em special elsewhere too, then.
		//rde = settings.getRuneDictionary().getRDE(alphaRep); // vert auras do not have an rde.
		//runeForm = 0; // no form for vert auras.
	}
	
	private void determineIfNumber() {
		RuneDictionary rd = settings.getRuneDictionary();
		if (rd.isNumber(alphaRep)) {
			isNumber = true;
		} else {
			isNumber = false;
		}
	}
	
	// Returns a string representation of the rune (for printing out an individual rune, since it appends newlines)
	public String getTextDisplay() {
		initializeLines();
		String rep = line1 + "\n" + line2 + "\n" + line3 + "\n" + line4 + "\n" + line5 + "\n";
		return rep;
	}
	
	// Returns a specific line in the string representation (ie, lines 1 and 2 are upper auras, 3 is the rune, 4 and 5 are lower auras)
	// Never append a newline in a ByLine method
	public String getTextDisplayByLine(int line) {
		initializeLines();
		String rep;
		if (line == 1) {
			rep = line1;
		} else if (line == 2) {
			rep = line2;
		} else if (line == 3) {
			rep = line3;
		} else if (line == 4) {
			rep = line4;
		} else { // must be line 5
			rep = line5;
		}
		return rep;
	}
	
	private void initializeLines() {
		// The size for lines that are empty (no auras)
		// it is based on the text display type.
		// will also determine which text to show.
		// rde will be checked for being null - vertical auras will behave differently.
		int emptyLength = 0;
		String displayText = "";
		if (settings.getIntSetting(Settings.I_DISPLAY_STYLE).equals(Settings.DISPLAY_RUNE_ONLY)) {
			emptyLength = alphaRep.length();
			displayText = alphaRep;
		} else if (settings.getIntSetting(Settings.I_DISPLAY_STYLE).equals(Settings.DISPLAY_RUNE_FORM)) {
			emptyLength = rde != null ? rde.getForm(runeForm).length() : alphaRep.length();
			displayText = rde != null ? rde.getForm(runeForm) : alphaRep;
		} else if (settings.getIntSetting(Settings.I_DISPLAY_STYLE).equals(Settings.DISPLAY_RUNE_FILE)) {
			emptyLength = rde != null ? rde.getFilePath(rde.getForm(runeForm)).length() : alphaRep.length();
			displayText = rde != null ? rde.getFilePath(rde.getForm(runeForm)) : alphaRep;
		} else {
			System.out.println("Error - unknown display setting: " + settings.getIntSetting(Settings.I_DISPLAY_STYLE));
		}
		if (!isVert) {
			emptyLength += 2; // for parentheses surrounding the rune
		}
		String emptiness = "";
		
		for (int x = 0; x < emptyLength; ++x) {
			emptiness += " "; // the emptiness grows.
		}
		
		// NOTE - numbers are still given horizontal auras. We have to purposely hide them in here, as well as Grimoire#getTextDisplayByRuneCount()
		
		if (uppermostAura != null && !isVert && !isNumber) {
			line1 = uppermostAura.getTextDisplay(displayText);
		} else if (uppermostVert != null) {
			line1 = uppermostVert.getTextDisplay();
		} else {
			line1 = emptiness;
		}
		if (upperAura != null && !isVert && !isNumber) {
			line2 = upperAura.getTextDisplay(displayText);
		} else if (upperVert != null) {
			line2 = upperVert.getTextDisplay();
		} else {
			line2 = emptiness;
		}
		
		if (isVert) {
			line3 = AuraVert.AURA; // No parentheses!
		} else {
			line3 = "(" + displayText + ")";
		}
		
		if (lowerAura != null && !isVert && !isNumber) {
			line4 = lowerAura.getTextDisplay(displayText);
		} else if (lowerVert != null) {
			line4 = lowerVert.getTextDisplay();
		} else {
			line4 = emptiness;
		}
		if (lowermostAura != null && !isVert && !isNumber) {
			line5 = lowermostAura.getTextDisplay(displayText);
		} else if (lowermostVert != null) {
			line5 = lowermostVert.getTextDisplay();
		} else {
			line5 = emptiness;
		}
	}
	
	public void setInquisitive(boolean inquis) {
		
		if (isInquisitive != inquis) {
			
			// toggles inquisitive on the auras, swapping them upside down if we need to change state
			
			isInquisitive = inquis;
			
			if (uppermostAura != null) {
				uppermostAura.setInquisitive(inquis);
			}
			if (upperAura != null) {
				upperAura.setInquisitive(inquis);
			}
			if (lowermostAura != null) {
				lowermostAura.setInquisitive(inquis);
			}
			if (lowerAura != null) {
				lowerAura.setInquisitive(inquis);
			}
				
			AuraHori temp = uppermostAura;
			uppermostAura = lowermostAura;
			lowermostAura = temp;
			
			temp = upperAura;
			upperAura = lowerAura;
			lowerAura = temp;
			
		}
		
	}
	
	public void addWelkinAuras(Rune rightNeighbor, int direction) {

		// The super crazy case:
		// if lowermost is meager, and uppermost is meager, then we need to make lowermost a DOUBLE MEAGER
		if (lowermostAura != null && lowermostAura.getIsMeager() && uppermostAura != null && uppermostAura.getIsMeager()) {
	
			// if already double'd, don't double again
			// we know we need to add the double-meager to the top because these are welkin auras
			if (!uppermostAura.getIsDouble()) {
				uppermostAura.setDoubleMeager();
			}
		}
		
		if (uppermostAura == null) {
			// copy the specifications of lowermost into uppermost
			if (lowermostAura != null) {
				
				if (lowermostAura.getIsMeager()) {
					uppermostAura = new AuraHori(AuraHori.LOC_UPPERMOST, false, false, true);
					
					if (direction == Lore.LAST_LORE) {
						//      we did NOT have an uppermost, the lowermost is meager, and it is the last lore.
						//      so we need to set isOpposite, to make it do lowerMeager in the position of upperMeager, so we have the right spacing / image file
						uppermostAura.setOpposite();
					}
					
				} else { // it is a minor
					uppermostAura = new AuraHori(AuraHori.LOC_UPPERMOST, false, false, false);
				}
			}
		}
		
		if (upperAura == null) {
			
			// copy the specifications of lower into upper
			if (lowerAura != null) {

				if (lowerAura.getIsLeftMajor() || lowerAura.getIsRightMajor()) {
					// If there's room for the full major, then add it.
					// if there's no neighbor to this rune's right, then we can only add a minor.
					if (rightNeighbor != null) {
						if (rightNeighbor.getUpperAura() == null) {
							// need to know who will be the major left and the major right
							upperAura = new AuraHori(AuraHori.LOC_UPPER, true, false, false);
							AuraHori otherUpper = new AuraHori(AuraHori.LOC_UPPER, false, true, false);
							rightNeighbor.setUpperAura(otherUpper);
						} else {
							// well, neighbor is taking up the spot, so we can only fit a minor!
							upperAura = new AuraHori(AuraHori.LOC_UPPER, false, false, false);
						}
					} else { // no neighbor, just do a minor
						upperAura = new AuraHori(AuraHori.LOC_UPPER, false, false, false);
					}
				} else { // it is a minor
					upperAura = new AuraHori(AuraHori.LOC_UPPER, false, false, false);
				}
			}
		}
	}
	
	public void addAbysmAuras(Rune rightNeighbor, int direction) {

		// The super crazy case:
		// if lowermost is meager, and uppermost is meager, then we need to make lowermost a DOUBLE MEAGER
		if (lowermostAura != null && lowermostAura.getIsMeager() && uppermostAura != null && uppermostAura.getIsMeager()) {
	
			// if already double'd, don't double again
			// we know we need to add the double-meager to the bottom because these are abysm auras
			if (!lowermostAura.getIsDouble()) {
				lowermostAura.setDoubleMeager();
			}
		}
		
		if (lowermostAura == null) {
			// copy the specifications of uppermost into lowermost
			if (uppermostAura != null) {
				
				if (uppermostAura.getIsMeager()) {
					lowermostAura = new AuraHori(AuraHori.LOC_LOWERMOST, false, false, true);
					
					if (direction == Lore.FIRST_LORE) {
						//      we did NOT have an lowermost, the uppermost is meager, and it is the first lore.
						//      so we need to set isOpposite, to make it do upperMeager in the position of lowerMeager, so we have the right spacing / image file
						lowermostAura.setOpposite();
					}
					
				} else { // it is a minor
					lowermostAura = new AuraHori(AuraHori.LOC_LOWERMOST, false, false, false);
				}
			}
		}
		
		if (lowerAura == null) {
			// copy the specifications of lower into upper
			if (upperAura != null) {
				
				if (upperAura.getIsLeftMajor() || upperAura.getIsRightMajor()) {
					// If there's room for the full major, then add it.
					// if there's no neighbor to this rune's right, then we can only add a minor.
					if (rightNeighbor != null) {
						if (rightNeighbor.getLowerAura() == null) {
							// need to know who will be the major left and the major right
							lowerAura = new AuraHori(AuraHori.LOC_LOWER, true, false, false);
							AuraHori otherLower = new AuraHori(AuraHori.LOC_LOWER, false, true, false);
							rightNeighbor.setLowerAura(otherLower);
						} else {
							// well, neighbor is taking up the spot, so we can only fit a minor!
							lowerAura = new AuraHori(AuraHori.LOC_LOWER, false, false, false);
						}
					} else { // no neighbor, just do a minor
						lowerAura = new AuraHori(AuraHori.LOC_LOWER, false, false, false);
					}
				} else { // it is a minor
					lowerAura = new AuraHori(AuraHori.LOC_LOWER, false, false, false);
				}
			}
		}
	}
	
	
	// Getters / Setters
	public String getAlphaRep() {
		return alphaRep;
	}
	
	public void setAlphaRep(String alphaRep) {
		this.alphaRep = alphaRep;
	}
	
	public AuraHori getUppermostAura() {
		return uppermostAura;
	}
	
	public void setUppermostAura(AuraHori aura) {
		uppermostAura = aura;
	}
	
	public AuraHori getUpperAura() {
		return upperAura;
	}
	
	public void setUpperAura(AuraHori aura) {
		upperAura = aura;
	}
	
	public AuraHori getLowerAura() {
		return lowerAura;
	}
	
	public void setLowerAura(AuraHori aura) {
		lowerAura = aura;
	}
	
	public AuraHori getLowermostAura() {
		return lowermostAura;
	}
	
	public void setLowermostAura(AuraHori aura) {
		lowermostAura = aura;
	}
	
	public int getRuneForm() {
		return runeForm;
	}
	
	public void setRuneForm(int runeForm) {
		this.runeForm = runeForm;
	}
	
	public RuneDictionaryEntry getRde() {
		return rde;
	}
	
	public boolean isVert() {
		return isVert;
	}
	
	public boolean isNumber() {
		return isNumber;
	}
	
	public AuraVert getUppermostVert() {
		return uppermostVert;
	}

	public AuraVert getUpperVert() {
		return upperVert;
	}

	public AuraVert getLowerVert() {
		return lowerVert;
	}

	public AuraVert getLowermostVert() {
		return lowermostVert;
	}
}
