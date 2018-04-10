package Text2Auralann;

import java.util.LinkedList;
import java.util.Random;

public class Lore {
	
	// The lore is responsible for determing which runes will be used.
	// The grimoire will be responsible for the auras.
	
	// consts:
	// orientation to interpret double / triple runes in (ie, read the lore left-to-right scanning for doubles/triples, or right-to-left)
	public static final int LEFT_TO_RIGHT = Settings.LEFT_TO_RIGHT;
	public static final int RIGHT_TO_LEFT = Settings.RIGHT_TO_LEFT;
	
	// special runes that can be assigned in determineRunes()
	public static final String PLURAL_S = "PLURAL-S";
	public static final String UNKNOWN = "UNKNOWN";
	public static final String I_DEAD = "I-DEAD";
	public static final String A_DEAD = "A-DEAD";
	public static final String APOS_DEAD = "APOS-DEAD";
	public static final String COMMON_DEAD = "COMMON-DEAD";
	public static final String EMPTY_RUNE = "EMPTY-RUNE";
	
	public static final String CAPITAL = "CAPITAL"; // special braille character
	public static final String SMALLTSU = "SMALLTSU"; // special katakana character
	
	// direction for welkin auras
	public static final int FIRST_LORE = 1;
	public static final int LAST_LORE = 2;
	
	private RuneDictionary rd;
	private Random rnd;
	private Settings settings;
	
	private String alphaRep;
	private int orientation; // LEFT_TO_RIGHT or RIGHT_TO_LEFT
	private LinkedList<Rune> runes; // goes in order they appear in the lore, ie: [0] is the first rune, [1] is the 2nd rune, ...
	private boolean allowDoubles; // true if double glyphs are allowed as runes
	private boolean allowTriples; // true if triple glyphs are allowed as runes
	private boolean allowQuads; // true if quad glyphs are allowed as runes
	private boolean isEnd; // true if this is a stop/end glyph (!, ., ?, ...) This means it won't have auras.

	// advanced features that are not usually toggled
	private boolean allowPossessiveS;
	private boolean allowPluralS;
	private boolean allowAIDead;
	private int allowAposDead;
	private boolean allowCommonDead;
	private boolean allowRandomDead;

	
	public Lore(String alphaRep, Settings settings) {
		this.settings = settings;
		rd = settings.getRuneDictionary();
		rnd = new Random();

		this.alphaRep = alphaRep.toUpperCase();
		
		orientation = settings.getIntSetting(Settings.I_ORIENTATION);
		allowDoubles = settings.getBoolSetting(Settings.B_ALLOW_DOUBLES);
		allowTriples = settings.getBoolSetting(Settings.B_ALLOW_TRIPLES);
		allowQuads = settings.getBoolSetting(Settings.B_ALLOW_QUADS);
		allowPossessiveS = settings.getBoolSetting(Settings.B_POSSESSIVE_S);
		allowPluralS = settings.getBoolSetting(Settings.B_PLURAL_S);
		allowAIDead = settings.getBoolSetting(Settings.B_ALLOW_AI_DEAD);
		allowAposDead = settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD);
		allowCommonDead = settings.getBoolSetting(Settings.B_ALLOW_COMMON_DEAD);
		allowRandomDead = settings.getBoolSetting(Settings.B_ALLOW_RANDOM_DEAD);
		
		isEnd = false;
		
		runes = determineRunes();
	}
	
	// This is the ctor for end glyphs (!, ., ?, ...). They are treated as their own lores, with no auras attached, and they cannot have dead runes attached to them.
	public Lore(String alphaRep, boolean isEnd, Settings settings) {
		this.settings = settings;
		rd = settings.getRuneDictionary();
		rnd = new Random();
		
		this.alphaRep = alphaRep.toUpperCase(); // shouldn't be needed, but may as well be consistent

		orientation = settings.getIntSetting(Settings.I_ORIENTATION);
		allowDoubles = settings.getBoolSetting(Settings.B_ALLOW_DOUBLES);
		allowTriples = settings.getBoolSetting(Settings.B_ALLOW_TRIPLES);
		allowQuads = settings.getBoolSetting(Settings.B_ALLOW_QUADS);
		allowPossessiveS = settings.getBoolSetting(Settings.B_POSSESSIVE_S);
		allowPluralS = settings.getBoolSetting(Settings.B_PLURAL_S);
		allowAIDead = settings.getBoolSetting(Settings.B_ALLOW_AI_DEAD);

		//allowCommonDead = settings.getBoolSetting(Settings.B_ALLOW_COMMON_DEAD);
		// we need to turn off common deads for punctuation, otherwise it tries to append a dead to this one-rune lore.
		allowCommonDead = false;
		allowRandomDead = false;
		
		this.isEnd = isEnd;
		
		runes = determineRunes();
	}
	
	public LinkedList<Rune> determineRunes() {
		runes = new LinkedList<Rune>();
		
		// Add the runes, in order of appearance, to the list of runes.
		// So, from left to right, grab 3 chars.
		// If it is a triple, add the rune to the list. If not, try for a double (letters 1&2 only). If not, then it must be a single.
		// Move to the right 1 if single, 2 if double, 3 if triple.
		// ^^ This explanation is the default left-to-right orientation.
		
		boolean addedRune = false;
		if (orientation == LEFT_TO_RIGHT) {
			for (int x = 0; x < alphaRep.length(); ++x) {
				
				boolean foundQuad = false;
				boolean foundTriple = false;
				boolean foundDouble = false;
				
				// chance to add a dead rune, if on.
				if (!addedRune) {
					addedRune = addDeadRuneChance(runes);
				} else {
					addedRune = false;
				}
				
				// try for quad
				if (x + 3 < alphaRep.length()) {
					String potentialQuad = alphaRep.substring(x, x+4);
					if (rd.isRune(potentialQuad, allowDoubles, allowTriples, allowQuads)) {
						runes.add(new Rune(potentialQuad, settings));
						foundQuad = true;
						// skipping ahead a bit on the string parsing
						x += 3;
						
						// we know we found a quad, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 4) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
					}
				}
				
				// try for triple
				if (x + 2 < alphaRep.length() && (!foundQuad)) {
					String potentialTriple = alphaRep.substring(x, x+3);
					if (rd.isRune(potentialTriple, allowDoubles, allowTriples, allowQuads)) {
						runes.add(new Rune(potentialTriple, settings));
						foundTriple = true;
						// skipping ahead a bit on the string parsing
						x += 2;
						
						// we know we found a triple, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 3) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
					}
				}
				
				// try for double
				if (x + 1 < alphaRep.length() && (!foundQuad) && (!foundTriple)) {
					String potentialDouble = alphaRep.substring(x,  x+2);
					
					// This is an exception for " 's ". Only use this double if the flag is set.
					if (potentialDouble.equalsIgnoreCase("'S")) {
						if (allowPossessiveS) {
							runes.add(new Rune(potentialDouble, settings));
							foundDouble = true;
							x += 1;

							// we know we found a double, so if it is the only rune in the lore, add the generic common dead
							if (allowCommonDead && alphaRep.length() == 2) {
								runes.add(new Rune(COMMON_DEAD, settings));
							}
						}
					} else if (rd.isRune(potentialDouble, allowDoubles, allowTriples, allowQuads)) {
						runes.add(new Rune(potentialDouble, settings));
						foundDouble = true;
						// skipping ahead a bit on the string parsing
						x += 1;
						
						// we know we found a double, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 2) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
					}
					
				}
				
				// settle for single if nothing else
				if ((!foundTriple) && (!foundDouble) && (!foundQuad)) {
					String single = alphaRep.substring(x, x+1);

					// MASSIVE NUMBER / DECIMAL HANDLING WITH VERTICAL AURAS
					// see if this is a number, and add vertical auras if it is
					if (rd.isNumber(single)) {
						runes.add(new Rune(AuraVert.LENGTH_FULL, AuraVert.LOC_LEFT, settings));
						
						// build the entire number, putting in separators and the closing aura
						String numberString = single;
						while (x+1 <= alphaRep.length()-1) {
							String nextRune = alphaRep.substring(x+1, x+2);
							if (rd.isNumber(nextRune) || nextRune.equals(".")) {
								numberString += nextRune;
								x++;
							} else {
								break;
							}
						}
						int groupRuneIn = -1; // when this reaches 0, add a half vert aura and reset to 3
						if (numberString.length() > 3) {
							// if there's a decimal, need to do the modulus of this and that instead
							if (numberString.contains(".")) {
								String preDecimal = numberString.substring(0, numberString.indexOf("."));
								if (preDecimal.length() > 3) {
									groupRuneIn = preDecimal.length() % 3;
								}
								if (groupRuneIn == 0) {
									groupRuneIn = 3; // we can't start with a half aura, so wait 3 first.
								}
							} else {
								groupRuneIn = numberString.length() % 3;
								if (groupRuneIn == 0) {
									groupRuneIn = 3; // we can't start with a half aura, so wait 3 first.
								}
							}
						}
						int numDecimalPoints = 0;
						for (int y = 0; y < numberString.length(); ++y) {
							if (numberString.substring(y, y+1).equalsIgnoreCase(".")) {
								numDecimalPoints++;
							}
						}
						int numberParser = 0;
						while (numberParser < numberString.length()) {
							groupRuneIn--;
							String currentGlyph = numberString.substring(numberParser, numberParser+1);
							runes.add(new Rune(currentGlyph, settings));
							if (currentGlyph.equalsIgnoreCase(".")) {
								groupRuneIn = 3;
							} else if (groupRuneIn == 0 && numberParser < numberString.length()-1
									   && numDecimalPoints < 2 && !numberString.substring(numberParser+1, numberParser+2).equals(".")) {
								runes.add(new Rune(AuraVert.LENGTH_HALF, AuraVert.LOC_IRREL, settings));
								groupRuneIn = 3;
							}
							numberParser++;
						}
						
						// add the closing vert aura
						runes.add(new Rune(AuraVert.LENGTH_FULL, AuraVert.LOC_RIGHT, settings));
					}
					
					// else, it's not a number. if the final rune is an 'S', use the 'plural S' rune.
					else if (single.equals("S") && (x == alphaRep.length()-1) && allowPluralS) {
						runes.add(new Rune(PLURAL_S, settings));

						// we know we found a single, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 1) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
					}
					// else, it's not a number or 'S'. If it is just I or A (one letter word), add the common dead glyph.
					else if ((single.equals("I") || single.equals("A")) && allowAIDead && alphaRep.length() == 1) {
						runes.add(new Rune(single, settings));
						if (single.equals("I")) {
							runes.add(new Rune(I_DEAD, settings));
							// to prevent getting a meager above the I, we need alphaRep to have a length > 1
							alphaRep += " ";
							x++; // to skip over the space we added
						} else if (single.equals("A")) {
							runes.add(new Rune(A_DEAD, settings));
							alphaRep += " ";
							x++; // to skip over the space we added
						}
					}
					// determine if an apostrophe-dead needs to be added to either the left or right side
					else if ((single.equals("'")) && allowAposDead != Settings.APOS_NONE) {
						
						if (allowAposDead == Settings.APOS_LEFT) { // always add apos-dead to the left
							runes.add(new Rune(APOS_DEAD, settings));
							runes.add(new Rune(single, settings));
						} else if (allowAposDead == Settings.APOS_RIGHT) { // always add apos-dead to the right
							runes.add(new Rune(single, settings));
							runes.add(new Rune(APOS_DEAD, settings));
						} else if (allowAposDead == Settings.APOS_BOTH) { // always add apos-dead to both sides
							runes.add(new Rune(APOS_DEAD, settings));
							runes.add(new Rune(single, settings));
							runes.add(new Rune(APOS_DEAD, settings));
						} else if (allowAposDead == Settings.APOS_NEEDED) {
							// add as needed.
							// we're simplifying this for use with the english language, as in, we're looking for contractions, and only expecting 1 apostrophe in a word:
							// I'm, Can't, Don't, etc, rather than Lann'lain which *has no apostrophes that can't fit major auras*, ie rev'nan, sora'eth, lann'laic, ...
							// So, see how far you are from the left end of the lore, and how far from the right. If it's just 1 away on either side, add the dead to it.
							// (Then again, if you have multiple apostrophes in a single word, then the things in the middle DON'T NEED MAJORS anyway, so this is actually 100% fine)
							
							// if we're located at the 2nd char in the string, ie: "I[']mma", then add the dead on the left side
							if (x == 1) {
								runes.add(new Rune(APOS_DEAD, settings));
							}
							
							runes.add(new Rune(single, settings));
							
							// if we're located one away from the end of the word, ie: "Don[']t", then add the dead on the right side
							if (x + 2 == alphaRep.length()) {
								runes.add(new Rune(APOS_DEAD, settings));
							}
						}
					} else {
						// at the very least, confirm it is a legit rune
						if (rd.isRune(single, false, false, false)) {
							runes.add(new Rune(single, settings));
							
							// we know we found a single, so if it is the only rune in the lore, add the generic common dead
							if (allowCommonDead && alphaRep.length() == 1) {
								runes.add(new Rune(COMMON_DEAD, settings));
							}
							
						} else if (single.equals(" ")) {
							// this was just spacing, so add an EMPTY-RUNE
							runes.add(new Rune(EMPTY_RUNE, settings));
						} else if (single.equals("^")) {
						
							// add a random dead
							runes.add(new Rune(rd.getDeadRune().getBaseForm(), settings));
							
						} else if (single.equals("&")) {
							
							// This is a hacky implementation, but "&" is a placeholder for a unique / unconventional special character in a given dictionary.
							// Since dictionary names / paths can change based on how they're stored on the local machine,
							// and since the Dictionary name isn't an available field, just go down the line until you pick one.
							// As of this time, no dictionaries have unique characters like these that share the same name, but that could be
							// a future issue one day, in which this'll need to be tackled (presumably a switch based on the dictionary's name,
							// a field that will be set in the settings file)
							
							// braille - add a Capital special rune.
							// katakana - adds a Small Tsu special rune.
							// technically we could add "destiny" to elder futhark but that rune's existence is sketchy at best.
							
							// adds a Capital character, or unknown if it can't be determined
							try {
								RuneDictionaryEntry rde = settings.getRuneDictionary().getRDE("CAPITAL");
								if (rde != null) { // wasnt braille, so try the next dictionary's special-rune character, and unknown after that
									runes.add(new Rune(CAPITAL, settings));
								}
							} catch(Exception e) {
								try {
									RuneDictionaryEntry rde = settings.getRuneDictionary().getRDE("SMALLTSU"); // katakana
									if (rde != null) {
										runes.add(new Rune(SMALLTSU, settings));
									}
								} catch (Exception ex) {
									runes.add(new Rune(UNKNOWN, settings));
								}
							}
							
						} else {
							runes.add(new Rune(UNKNOWN, settings));
							// No common dead here.
						}
					}
				}
				
				// if we didn't add a dead glyph before the rune, try again to see if we add one after the rune
				if (!addedRune) {
					addedRune = addDeadRuneChance(runes);
				} else {
					addedRune = false;
				}
				
			}
			
		} else if (orientation == RIGHT_TO_LEFT) {
			for (int x = alphaRep.length(); x > 0; --x) {
				
				boolean foundTriple = false;
				boolean foundDouble = false;
				boolean foundQuad = false;
				
				// chance to add a dead rune, if on.
				if (!addedRune) {
					addedRune = addDeadRuneChance(runes);
				} else {
					addedRune = false;
				}
				
				// try for quad
				if (x - 4 > -1) {
					String potentialQuad = alphaRep.substring(x-4, x);
					if (rd.isRune(potentialQuad, allowDoubles, allowTriples, allowQuads)) {
						runes.addFirst(new Rune(potentialQuad, settings));
						foundQuad = true;
						// skipping ahead a bit on the string parsing
						x -= 3;
						
						// we know we found a quad, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 4) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
						
					}
				}
				
				// try for triple
				if (x - 3 > -1 && (!foundQuad)) {
					String potentialTriple = alphaRep.substring(x-3, x);
					if (rd.isRune(potentialTriple, allowDoubles, allowTriples, allowQuads)) {
						runes.addFirst(new Rune(potentialTriple, settings));
						foundTriple = true;
						// skipping ahead a bit on the string parsing
						x -= 2;

						// we know we found a triple, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 3) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
					}
				}
				
				// try for double
				if (x - 2 > -1 && (!foundQuad) && (!foundTriple)) {
					String potentialDouble = alphaRep.substring(x-2,  x);
					
					// This is an exception for " 's ". Only use this double if the flag is set.
					if (potentialDouble.equalsIgnoreCase("'S")) {
						if (allowPossessiveS) {
							runes.addFirst(new Rune(potentialDouble, settings));
							foundDouble = true;
							x -= 1;
							
							// we know we found a double, so if it is the only rune in the lore, add the generic common dead
							if (allowCommonDead && alphaRep.length() == 2) {
								runes.add(new Rune(COMMON_DEAD, settings));
							}
						}
					} else if (rd.isRune(potentialDouble, allowDoubles, allowTriples, allowQuads)) {
						runes.addFirst(new Rune(potentialDouble, settings));
						foundDouble = true;
						// skipping ahead a bit on the string parsing
						x -= 1;
						
						// we know we found a double, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 2) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
						
					}
				}
				
				// settle for single if nothing else
				if ((!foundTriple) && (!foundDouble) && (!foundQuad)) {
					String single = alphaRep.substring(x-1, x);
					
					
					
					
					// MASSIVE NUMBER / DECIMAL HANDLING WITH VERTICAL AURAS
					// see if this is a number, and add vertical auras if it is
					if (rd.isNumber(single)) {
						runes.addFirst(new Rune(AuraVert.LENGTH_FULL, AuraVert.LOC_RIGHT, settings));
						
						// build the entire number, putting in separators and the closing aura
						String numberString = single;
						while (x-1 > 0) {
							String nextRune = alphaRep.substring(x-2, x-1);
							if (rd.isNumber(nextRune) || nextRune.equals(".")) {
								numberString = nextRune + numberString;
								x--;
							} else {
								break;
							}
						}
						int groupRuneIn = -1; // when this reaches 0, add a half vert aura and reset to 3
						if (numberString.length() > 3) {
							// if there's a decimal, need to do the modulus of this and that instead
							if (numberString.contains(".")) {
								String postDecimal = numberString.substring(numberString.lastIndexOf(".")+1);
								if (postDecimal.length() > 3) {
									groupRuneIn = postDecimal.length() % 3;
								}
								if (groupRuneIn == 0) {
									groupRuneIn = 3; // we can't start with a half aura, so wait 3 first.
								}
							} else {
								groupRuneIn = numberString.length() % 3;
								if (groupRuneIn == 0) {
									groupRuneIn = 3; // we can't start with a half aura, so wait 3 first.
								}
							}
						}
						int numDecimalPoints = 0;
						for (int y = 0; y < numberString.length(); ++y) {
							if (numberString.substring(y, y+1).equalsIgnoreCase(".")) {
								numDecimalPoints++;
							}
						}
						int numberParser = numberString.length();
						while (numberParser > 0) {
							groupRuneIn--;
							String currentGlyph = numberString.substring(numberParser-1, numberParser);
							runes.addFirst(new Rune(currentGlyph, settings));
							if (currentGlyph.equalsIgnoreCase(".")) {
								groupRuneIn = 3;
							} else if (groupRuneIn == 0 && numberParser > 1
									   && numDecimalPoints < 2 && !numberString.substring(numberParser-2, numberParser-1).equals(".")) {
								runes.addFirst(new Rune(AuraVert.LENGTH_HALF, AuraVert.LOC_IRREL, settings));
								groupRuneIn = 3;
							}
							numberParser--;
						}
						
						// add the closing vert aura
						runes.addFirst(new Rune(AuraVert.LENGTH_FULL, AuraVert.LOC_LEFT, settings));
					}
					
					// else, it's not a number. if the final rune is an 'S', use the 'plural S' rune.
					else if (single.equals("S") && (x == alphaRep.length()) && allowPluralS) {
						runes.addFirst(new Rune(PLURAL_S, settings));

						// we know we found a single, so if it is the only rune in the lore, add the generic common dead
						if (allowCommonDead && alphaRep.length() == 1) {
							runes.add(new Rune(COMMON_DEAD, settings));
						}
						
					}
					
					// else, it's not a number or 'S'. If it is just I or A (one letter word), add the common dead glyph.
					else if ((single.equals("I") || single.equals("A")) && allowAIDead && alphaRep.length() == 1) {
						if (single.equals("I")) {
							runes.addFirst(new Rune(I_DEAD, settings));
							alphaRep += " ";
							x--; // to skip over the space we added
						} else if (single.equals("A")) {
							runes.addFirst(new Rune(A_DEAD, settings));
							alphaRep += " ";
							x--; // to skip over the space we added
						}
						runes.addFirst(new Rune(single, settings));
					}
					
					// determine if an apostrophe-dead needs to be added to either the left or right side
					else if ((single.equals("'")) && allowAposDead != Settings.APOS_NONE) {
						
						if (allowAposDead == Settings.APOS_LEFT) { // always add apos-dead to the left
							runes.addFirst(new Rune(single, settings));
							runes.addFirst(new Rune(APOS_DEAD, settings));
						} else if (allowAposDead == Settings.APOS_RIGHT) { // always add apos-dead to the right
							runes.addFirst(new Rune(APOS_DEAD, settings));
							runes.addFirst(new Rune(single, settings));
						} else if (allowAposDead == Settings.APOS_BOTH) { // always add apos-dead to both sides
							runes.addFirst(new Rune(APOS_DEAD, settings));
							runes.addFirst(new Rune(single, settings));
							runes.addFirst(new Rune(APOS_DEAD, settings));
						} else if (allowAposDead == Settings.APOS_NEEDED) {
							// add as needed.
							// we're simplifying this for use with the english language, as in, we're looking for contractions, and only expecting 1 apostrophe in a word:
							// I'm, Can't, Don't, etc, rather than Lann'lain which *has no apostrophes that can't fit major auras*, ie rev'nan, sora'eth, lann'laic, ...
							// So, see how far you are from the left end of the lore, and how far from the right. If it's just 1 away on either side, add the dead to it.
							// (Then again, if you have multiple apostrophes in a single word, then the things in the middle DON'T NEED MAJORS anyway, so this is actually 100% fine)
							
							// if we're located one away from the end of the word, ie: "Don[']t", then add the dead on the right side
							if (x == alphaRep.length() - 1) {
								runes.addFirst(new Rune(APOS_DEAD, settings));
							}
							
							runes.addFirst(new Rune(single, settings));
						
							// if we're located at the 2nd char in the string, ie: "I[']mma", then add the dead on the left side
							if (x == 2) {
								runes.addFirst(new Rune(APOS_DEAD, settings));
							}
							
						}
					}
					
					else {
						// at the very least, confirm it is a legit rune
						if (rd.isRune(single, false, false, false)) {
							runes.addFirst(new Rune(single, settings));
							
							// we know we found a single, so if it is the only rune in the lore, add the generic common dead
							if (allowCommonDead && alphaRep.length() == 1) {
								runes.add(new Rune(COMMON_DEAD, settings));
							}
							
						} else if (single.equals(" ")) {
							// this was just spacing, so add an EMPTY-RUNE
							runes.addFirst(new Rune(EMPTY_RUNE, settings));
						} else if (single.equals("^")) {
						
							// add a random dead
							runes.addFirst(new Rune(rd.getDeadRune().getBaseForm(), settings));
							
						} else if (single.equals("&")) { // braille - add a Capital special rune
							
							// This is a hacky implementation, but "&" is a placeholder for a unique / unconventional special character in a given dictionary.
							// Since dictionary names / paths can change based on how they're stored on the local machine,
							// and since the Dictionary name isn't an available field, just go down the line until you pick one.
							// As of this time, no dictionaries have unique characters like these that share the same name, but that could be
							// a future issue one day, in which this'll need to be tackled (presumably a switch based on the dictionary's name,
							// a field that will be set in the settings file)
							
							// braille - add a Capital special rune.
							// katakana - adds a Small Tsu special rune.
							// technically we could add "destiny" to elder futhark but that rune's existence is sketchy at best.
							
							// adds a Capital character, or unknown if this isn't a braille dictionary
							try {
								RuneDictionaryEntry rde = settings.getRuneDictionary().getRDE("CAPITAL");
								if (rde != null) {
									runes.addFirst(new Rune(CAPITAL, settings));
								}
							} catch(Exception e) {
								try {
									RuneDictionaryEntry rde = settings.getRuneDictionary().getRDE("SMALLTSU"); // katakana
									if (rde != null) {
										runes.addFirst(new Rune(SMALLTSU, settings));
									}
								} catch (Exception ex) {
									runes.addFirst(new Rune(UNKNOWN, settings));
								}
							}
							
						} else {
							runes.addFirst(new Rune(UNKNOWN, settings));
							// No common dead here.
						}
					}
				}
				
				// if we didn't add a dead glyph before the rune, try again to see if we add one after the rune
				if (!addedRune) {
					addedRune = addDeadRuneChance(runes);
				} else {
					addedRune = false;
				}
			}
		}
		
		return runes;
	}
	
	public String getTextDisplay() {
		String rep = "";
		
		rep += getTextDisplayByLine(1) + "\n";
		rep += getTextDisplayByLine(2) + "\n";
		rep += getTextDisplayByLine(3) + "\n";		
		rep += getTextDisplayByLine(4) + "\n";
		rep += getTextDisplayByLine(5) + "\n";
		
		return rep;
	}
	
	// Never append a newline in a ByLine method
	public String getTextDisplayByLine(int line) {
		String rep = "";
		
		for (int x = 0; x < runes.size(); ++x) {
			rep += runes.get(x).getTextDisplayByLine(line);
		}
		
		return rep;
	}
	
	
	// Aura application
	public void applyUpperAuras(int numAuras, boolean isMeager, boolean isMajor) {
		
		if (isEnd) {
			return; // no auras allowed.
		}
		
		Rune firstRune = runes.get(0);
		
		// will only apply these to the rune as needed
		AuraHori uppermost = new AuraHori(1, false, false, isMeager);
		AuraHori upper = new AuraHori(2, false, false, isMeager);
		// will apply the majors after, if exist
		
		if (numAuras == 2) {
			firstRune.setUppermostAura(uppermost);
			firstRune.setUpperAura(upper);
		} else if (numAuras == 1) {
			firstRune.setUpperAura(upper);
		} else {
			// No auras. I cry every time.
		}
		
		// Extend the major if true and possible
		if (isMajor && numAuras > 0) {
			
			if (runes.size() > 1 && !runes.get(1).isVert()) {

				firstRune.getUpperAura().setIsLeftMajor(true);
				
				//      so, normally, the next rune would never make its own auras, right?
				//      well, if its a 2 rune word, then yeah, it would be making its own, but they'd be lower, right?
				//      so we're safe to add an upper here, and it'll never be influenced by anything else / overwritten?
				//      best to create a new Aura rather than reuse an existing one
				//      well, a major can never be meager anyway cause its only upper, not uppermost. we can grab the alpharep.
				//      so we've got everything we need.
				
				Rune secondRune = runes.get(1);
				AuraHori major = new AuraHori(2, false, true, false);
				secondRune.setUpperAura(major);
			}
		}
	}
	
	
	
	// Apply the lower auras:
	// There are 2 potential lower auras: The 1st, from top to bottom, is the "lower" aura, which can be major (cover 1st and 2nd rune).
	// The 2nd is the "lowermost" aura, which can be meager (half length of the 1st / "lower" aura)
	// If there is only one aura, it will be an "lower" and cannot be meager.
	public void applyLowerAuras(int numAuras, boolean isMeager, boolean isMajor) {

		if (isEnd) {
			return; // no auras allowed
		}
		
		Rune lastRune = runes.get(runes.size()-1);
		
		// will only apply these to the rune as needed
		AuraHori lower = new AuraHori(3, false, false, isMeager);
		AuraHori lowermost = new AuraHori(4, false, false, isMeager);
		// will apply the majors after, if exist
		
		
		if (numAuras == 2) {
			lastRune.setLowerAura(lower);
			lastRune.setLowermostAura(lowermost);
		} else if (numAuras == 1) { 
			lastRune.setLowerAura(lower);
		} else {
			// No auras. I cry every time.
		}

		// Extend the major if true and possible
		if (isMajor && numAuras > 0) {
			if (runes.size() > 1 && !runes.get(runes.size()-2).isVert()) {

				lastRune.getLowerAura().setIsRightMajor(true);

				Rune secondLastRune = runes.get(runes.size()-2);
				AuraHori major = new AuraHori(3, true, false, false);
				secondLastRune.setLowerAura(major);
			}
		}
	}
	
	public void setInquisitive(boolean inquis) {
		
		// get the first 2 runes in the lore, and set the inquisitive state
		getRunes().get(0).setInquisitive(true);
		if (getRunes().size() > 1) {
			getRunes().get(1).setInquisitive(true);
		}

		// get the last 2 runes in the lore, and set the inquisitive state
		getRunes().get(getRunes().size()-1).setInquisitive(true);
		if (getRunes().size() > 1) {
			getRunes().get(getRunes().size()-2).setInquisitive(true);
		}
		
	}
	
	public void addWelkinAuras(int direction) {
		
		// We need to know the 'direction' - if this is the first lore, we want to mirror the first 2 runes.
		// Likewise, if it is the last lore, we want to mirror the last 2 runes.
		
		if (direction == FIRST_LORE) {
			// get the first 2 runes in the lore, and apply upper auras
			Rune neighbor = null;
			if (getRunes().size() > 1) {
				neighbor = getRunes().get(1);
			}
			getRunes().get(0).addWelkinAuras(neighbor, direction);
			if (getRunes().size() > 1) {
				getRunes().get(1).addWelkinAuras(null, direction);
			}
		}
		else if (direction == LAST_LORE) {
			// get the last 2 runes in the lore, and apply upper auras
			Rune lastRune = getRunes().get(getRunes().size()-1);
			Rune beforeLast = null;
			if (getRunes().size() > 1) {
				beforeLast = getRunes().get(getRunes().size()-2);
				beforeLast.addWelkinAuras(lastRune, direction);
			}
			lastRune.addWelkinAuras(null, direction);
		}
	}
	
	public void addAbysmAuras(int direction) {

		if (direction == FIRST_LORE) {
			// get the first 2 runes in the lore, and apply lower auras
			Rune neighbor = null;
			if (getRunes().size() > 1) {
				neighbor = getRunes().get(1);
			}
			getRunes().get(0).addAbysmAuras(neighbor, direction);
			if (getRunes().size() > 1) {
				getRunes().get(1).addAbysmAuras(null, direction);
			}
		}
		else if (direction == LAST_LORE) {
			// get the last 2 runes in the lore, and apply lower auras
			Rune lastRune = getRunes().get(getRunes().size()-1);
			Rune beforeLast = null;
			if (getRunes().size() > 1) {
				beforeLast = getRunes().get(getRunes().size()-2);
				beforeLast.addAbysmAuras(lastRune, direction);
			}
			lastRune.addAbysmAuras(null, direction);
		}
	}
	
	private boolean addDeadRuneChance(LinkedList<Rune> runes) {
		boolean addedRune = false;
		
		// Get whether or not dead runes are allowed, from the settings. Do nothing if it is turned off.
		if (allowRandomDead) {

			// Get the chance percent from the settings	
			int deadChance = settings.getIntSetting(Settings.I_RANDOM_DEAD_CHANCE);
			int roll = rnd.nextInt(100)+1; // rolls 1-100
			
			if (roll <= deadChance) {
				runes.add(new Rune(rd.getDeadRune().getBaseForm(), settings));
				addedRune = true;
			}
		}
		
		// This is capable of doing another dead rune after the commonly-used dead after a lore that's just "A" or "I".
		// Isn't a big deal, just more obfuscation.
		
		return addedRune;
	}
	
	
	// getters / setters
	
	public String getAlphaRep() {
		return alphaRep;
	}

	/*
	public void setAlphaRep(String alphaRep) {
		this.alphaRep = alphaRep;
		// will need to recalculate runes. Auras?
		runes = determineRunes();
	}
	*/
	
	public boolean getIsEnd() {
		return isEnd;
	}
	
	public LinkedList<Rune> getRunes() {
		return runes;
	}
	
}
