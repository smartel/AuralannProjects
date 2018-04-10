package Text2Auralann;

public class AuraHori {
	
	// consts
	// text display:
	public static final String FULL_AURA = "_";
	public static final String MEAGER_AURA = "-";
	//public static final String DOUBLE_MEAGER_AURA = "X"; // hardcoded it in getTextDisplay.
	// locations:
	public static final int LOC_UPPERMOST = 1;
	public static final int LOC_UPPER = 2;
	public static final int LOC_LOWER = 3;
	public static final int LOC_LOWERMOST = 4;
	
	// members
	private int location;
	private boolean isLeftMajor;
	private boolean isRightMajor;
	private boolean isMeager;
	private boolean isInquisitive;
	private boolean isOpposite; // for welkin / abysm evocatives
	private boolean isDoubleMeager; // for welkin / abysm evocatives, in single rune knowls ("I!" "I?!" "I..." "I...?") with no dead runes. yes, this specific.
	
	private int size;
	private int additionalLeftSpacing;
	private int additionalRightSpacing;
	
	public AuraHori(int loc, boolean isLeftMajor, boolean isRightMajor, boolean isMeager) {
		this.location = loc;
		this.isLeftMajor = isLeftMajor;
		this.isRightMajor = isRightMajor;
		this.isMeager = isMeager;
		isInquisitive = false;
	}
	
	public String getTextDisplay(String display) {
		
		// Additional initialization
		
		// For text display, the aura currently starts as the size of the display (rune, filepath, ...) + 2, due to the parentheses around the rune.
		size = display.length() + 2;
		// however, if this is meager, we need to be half the length.
		// although, only uppermost and lowermost auras are allowed to be meager. flag will be ignored otherwise.
		if (isMeager && (location == LOC_UPPERMOST || location == LOC_LOWERMOST)) {
			size = (int)(((double)size/2.0))+1;
		}
		
		// additional spacing - will be 0 if it is not meager or if it is a double meager
		if (!isDoubleMeager) {
			if (location == LOC_UPPERMOST || location == LOC_UPPER) {
				if (!isInquisitive) {
					additionalRightSpacing = (display.length() + 2) - size;
				} else {
					additionalLeftSpacing = (display.length() + 2) - size;
				}
			}
			else if (location == LOC_LOWER || location == LOC_LOWERMOST) {
				if (!isInquisitive) {
					additionalLeftSpacing = (display.length() + 2) - size;
				} {
					additionalRightSpacing = (display.length() + 2) - size;
				}
			}
		}
		
		// since we have to use lowerMeager in upper, and upperMeager in lower when using Abysm/Welkin evocatives, fix the spacing (it is off by 1 when printing)
		if (isOpposite) {
			if (location == LOC_LOWERMOST) {
				additionalRightSpacing -= 1;
			} else if (location == LOC_UPPERMOST) {
				additionalLeftSpacing -= 1;
			}
		}
		
		// String building
		
		String strLeftSpacing = "";
		for (int x = 0; x < additionalLeftSpacing; ++x) {
			strLeftSpacing += " ";
		}
		String strRightSpacing = "";
		for (int x = 0; x < additionalRightSpacing; ++x) {
			strRightSpacing += " ";
		}
		
		// get what the Aura is made of: meager or full lines
		// meager flag will only be checked IF the location is uppermost or lowermost
		String material = "";
		if (isMeager && (location == LOC_UPPERMOST || location == LOC_LOWERMOST)) {
			material = MEAGER_AURA;
		} else {
			material = FULL_AURA;
		}

		// get to the desired length.
		String strAura = "";
		for (int x = 0; x < size; ++x) {
			strAura += material;
		}
		
		// needs to have '<' and '>' at the ends (replace existing glyphs),
		// UNLESS it is a major. If it is a left major, we only have a '<' on the left end.
		// If it is a right major, we only have a '>' on the right end.
		if (isDoubleMeager) {
			strAura = "<X>"; // pretty much just hardcoding this special case, since it can only fit above (I), and we can't fit <><>, so we're doing next best thing: <X>
		} else if (isLeftMajor) {
			strAura = "<" + strAura.substring(1);
		} else if (isRightMajor) {
			strAura = strAura.substring(0, strAura.length()-1) + ">";
		} else { // not a major at all, so put closers on both ends
			strAura = "<" + strAura.substring(1, strAura.length()-1) + ">";
		}
		
		
		return strLeftSpacing + strAura + strRightSpacing;
	}
	
	public void toggleInquisitive() {
		// this flips the auras upside-down. So, if location is 4, make it 1, and so on.
		
		isInquisitive = !isInquisitive;
		
		if (location == LOC_UPPERMOST) {
			location = LOC_LOWERMOST;
		} else if (location == LOC_LOWERMOST) {
			location = LOC_UPPERMOST;
		} else if (location == LOC_UPPER) {
			location = LOC_LOWER;
		} else if (location == LOC_LOWER) {
			location = LOC_UPPER;
		}
		
		// Don't swap major-left with major-right and major-right with major-left! We're only flipping upside-down!
	}
	
	// passes in whether or not this aura should be inquisitive
	public void setInquisitive(boolean inquis) {
		if (isInquisitive != inquis) {
			toggleInquisitive();
		}
	}
	
	
	// Welkin and Abysm cause meagers to need to be the opposite type: ie, if we are adding a meager to uppermost, it needs the spacing and image file of the lowermost one
	public void setOpposite() {
		if (isMeager) {
			if (location == LOC_LOWERMOST) {
				location = LOC_UPPERMOST;
			} else if (location == LOC_UPPERMOST) {
				location = LOC_LOWERMOST;
			}
		}
		isOpposite = true;
	}
	
	public void setDoubleMeager() {
		isDoubleMeager = true;
	}
	
	
	// Getters / Setters
	
	public boolean getIsRightMajor() {
		return isRightMajor;
	}
	
	public void setIsRightMajor(boolean isRightMajor) {
		this.isRightMajor = isRightMajor;
	}
	
	public boolean getIsLeftMajor() {
		return isLeftMajor;
	}
	
	public void setIsLeftMajor(boolean isLeftMajor) {
		this.isLeftMajor = isLeftMajor;
	}
	
	public boolean getIsMeager() {
		return isMeager;
	}
	
	public boolean getIsInquisitive() {
		return isInquisitive;
	}
	
	public boolean getIsDouble() {
		return isDoubleMeager;
	}
	
	// for knowing which aura image to display.
	public String getForm() {
		String form = "";
		
		if (isDoubleMeager) {
			if (location == LOC_UPPERMOST) {
				form  = "DoubleUpperMeager";
			} else if (location == LOC_LOWERMOST) {
				form = "DoubleLowerMeager";
			}
		} else if (getIsLeftMajor()) {
			// determine if LowerMajorLeft or UpperMajorLeft
			if (location == LOC_UPPER || location == LOC_UPPERMOST) { // overkill, should be limited to loc_upper / loc_lower, not -mosts
				form = "UpperMajorLeft";
			} else {
				form = "LowerMajorLeft";
			}
		} else if (getIsRightMajor()) {
			// determine if LowerMajorRight or UpperMajorRight
			if (location == LOC_UPPER || location == LOC_UPPERMOST) { // overkill, should be limited to loc_upper / loc_lower, not -mosts
				form = "UpperMajorRight";
			} else {
				form = "LowerMajorRight";
			}
		} else {
			
			// determine if meager, then if UpperMeager, LowerMeager
			if (isMeager && location == LOC_UPPERMOST) {
				if (isInquisitive) {
					form = "InquisUpperMeager";
				} else {				
					form = "UpperMeager";
				}
			} else if (isMeager && location == LOC_LOWERMOST) {
				if (isInquisitive) {
					form = "InquisLowerMeager";
				} else {
					form = "LowerMeager";
				}
			}
			
			// else, determine if minor, then if UpperMinor, LowerMinor. if nothing else, it must be empty.
			else {
				if (location == LOC_UPPER || location == LOC_UPPERMOST) {
					form = "UpperMinor";
				} else if (location == LOC_LOWER || location == LOC_LOWERMOST) {
					form = "LowerMinor";
				} else {
					// Shouldn't ever hit this, but if we do, make sure you designate it as a non-aura.
					form = "Empty";
				}
			}
		}
		
		return form;
	}
}
