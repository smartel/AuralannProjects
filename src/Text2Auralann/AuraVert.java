package Text2Auralann;

public class AuraVert {
	
	// note: the tilts are only for debugging purposes, auras are only one line long.
	
	// consts
	// text display:
	public static final String AURA = "|";
	public static final String L_SLANT = "/";
	public static final String R_SLANT = "\\";
	// locations:
	public static final int LOC_UPPERMOST = 1; // which row if this were a horizontal aura
	public static final int LOC_UPPER = 2;
	public static final int LOC_LOWER = 3;
	public static final int LOC_LOWERMOST = 4;
	// sides:
	public static final int LOC_LEFT = 1; // left vertical aura
	public static final int LOC_RIGHT = 2; // right vertical aura
	public static final int LOC_IRREL = 3; // irrelevant direction, since it is a half aura (a segment grouper, not the number opener/closer)
	// lengths:
	public static final int LENGTH_FULL = 5; // because it covers 5 rows (if slants. 4 otherwise)
	public static final int LENGTH_HALF = 3; // because it covers 3 rows (just a number grouper, not the start or end of the number)
	
	// members
	private int location;
	private int length;
	private int side;
	
	public AuraVert(int location, int side, int length) {
		this.location = location;
		this.side = side;
		this.length = length;
	}
	
	
	// This one for slants, with lengths of 5. For easy text display / debugging purposes.
	public String getTextDisplaySlants() {
		String strAura = "";
		
		if (length == LENGTH_FULL && (location != LOC_UPPERMOST) && (location != LOC_LOWERMOST)) {
			strAura = AURA;
		} else if (length == LENGTH_FULL && (location == LOC_UPPERMOST)) {
			if (side == LOC_LEFT) {
				strAura = L_SLANT;
			} else {
				strAura = R_SLANT;
			}
		} else if (length == LENGTH_FULL && (location == LOC_LOWERMOST)) {
			if (side == LOC_LEFT) {
				strAura = R_SLANT;
			} else {
				strAura = L_SLANT;
			}
		} else if ((length == LENGTH_HALF) && ((location == LOC_UPPER) || (location == LOC_LOWER))) {
			strAura = AURA;
		} else {
			strAura = " ";
		}
		
		return strAura;
	}
	
	// This one for no slants, but differing lengths (top 4 on open, bottom 4 on close)
	public String getTextDisplay() {
		String strAura = "";
		
		if (length == LENGTH_FULL && (location != LOC_UPPERMOST) && (location != LOC_LOWERMOST)) {
			strAura = AURA;
		} else if (length == LENGTH_FULL && (location == LOC_UPPERMOST) && (side == LOC_LEFT)) {
			strAura = AURA;
		} else if (length == LENGTH_FULL && (location == LOC_LOWERMOST) && (side == LOC_RIGHT)) {
			strAura = AURA;
		} else if ((length == LENGTH_HALF) && ((location == LOC_UPPER) || (location == LOC_LOWER))) {
			strAura = AURA;
		} else {
			strAura = " ";
		}
		
		return strAura;
	}
	
	// for determining which image file to use.
	public String getForm() {
		String form = "Empty";

		if (location == LOC_UPPERMOST && side == LOC_LEFT) {
			form = "NumberSepTop";
		} else if (location == LOC_UPPER && side == LOC_LEFT) {
			form = "NumberSepMiddle";
		} else if (location == LOC_UPPER && side == LOC_RIGHT) {
			form = "NumberSepTop";
		} else if (location == LOC_LOWER && side == LOC_LEFT) {
			form = "NumberSepBottom";
		} else if (location == LOC_LOWER && side == LOC_RIGHT) {
			form = "NumberSepMiddle";
		} else if (location == LOC_LOWERMOST && side == LOC_RIGHT) {
			form = "NumberSepBottom";
		}
		
		// now for the number grouper
		else if (length == LENGTH_HALF && location == LOC_UPPER) {
			form = "NumberSepTop";
		} else if (length == LENGTH_HALF && location == LOC_LOWER) {
			form = "NumberSepBottom";
		}
		
		// else, it stays empty.
		
		return form;
	}
	
}
