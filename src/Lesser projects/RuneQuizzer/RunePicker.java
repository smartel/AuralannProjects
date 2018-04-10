package RuneQuizzer;

import java.util.LinkedList;
import java.util.Random;

import Text2Auralann.RuneDictionary;
import Text2Auralann.RuneDictionaryEntry;

public class RunePicker {
	
	private RuneDictionary rd;
	private Settings settings;
	private Random rnd;
	
	private LinkedList<RuneDictionaryEntry> apr; // "All Possible Runes"
	private int maxRuneCount; // maximum size of apr when it was full and none were removed. used to know how many runes are in a run of Around the World
	
	public RunePicker(RuneDictionary rd, Settings settings) throws Exception {
		this.rd = rd;
		this.settings = settings;
		populateAllPossibleRunes();
		rnd = new Random(System.currentTimeMillis());
	}
	
	public void populateAllPossibleRunes() throws Exception {
		apr = new LinkedList<RuneDictionaryEntry>();
		
		if (settings.getBoolSetting(Settings.B_HAS_SINGLES)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.SINGLE_RDES) );
		}
		if (settings.getBoolSetting(Settings.B_HAS_DOUBLES)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.DOUBLE_RDES) );
		}
		if (settings.getBoolSetting(Settings.B_HAS_TRIPLES)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.TRIPLE_RDES) );
		}
		if (settings.getBoolSetting(Settings.B_HAS_QUADS)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.QUAD_RDES) );
		}
		if (settings.getBoolSetting(Settings.B_HAS_NUMERICS)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.NUMBER_RDES) );
		}
		if (settings.getBoolSetting(Settings.B_HAS_DEADS)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.DEAD_RDES));
		}
		if (settings.getBoolSetting(Settings.B_HAS_SYMBOLS)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.SYMBOL_RDES));
		}
		if (settings.getBoolSetting(Settings.B_HAS_SPECIALS)) {
			apr.addAll( rd.getRuneCollection(RuneDictionary.SPECIAL_RDES));
		}
		
		maxRuneCount = 0;
		for (int x = 0; x < apr.size(); ++x) {
			maxRuneCount += apr.get(x).getFormCount();
		}
		
		if (maxRuneCount == 0) {
			throw new Exception("No Runes to quiz with. Pick categories to quiz over and ensure the path + folders aren't empty.");
		}
	}
	
	// will pick a random rune. will never return the same rune twice in a row.
	public RuneDictionaryEntry getRandomRune(String currRune) {
		RuneDictionaryEntry rde = null;

		if (apr.size() > 0) {
			int i = rnd.nextInt(apr.size());
			if (settings.getBoolSetting(Settings.B_AROUND_WORLD)) {
				// if there are more forms left, we can't remove it from the collection yet...
				// the forms get reduced in the RuneQuizzer, after a random form / file path has been picked in SetNextRune
				rde = apr.get(i);
				if (rde.getFormCount() == 1) {
					apr.remove(i);
				}
			} else {
				// loop until we get a rune that is NOT the same as the one we passed in
				rde = apr.get(i);
				while (rde.getKey().equalsIgnoreCase(currRune)) {
					i = rnd.nextInt(apr.size());
					rde = apr.get(i);
				}
			}
		}
		
		return rde;
	}
	
	/* old versions, unexpected to be used again
	public RuneDictionaryEntry getRandomSingle() {
		return rd.getRandomRune(RuneDictionary.SINGLE_RDES);
	}
	public RuneDictionaryEntry getRandomDouble() {
		return rd.getRandomRune(RuneDictionary.DOUBLE_RDES);
	}
	public RuneDictionaryEntry getRandomTriple() {
		return rd.getRandomRune(RuneDictionary.TRIPLE_RDES);
	}
	public RuneDictionaryEntry getRandomQuad() {
		return rd.getRandomRune(RuneDictionary.QUAD_RDES);
	}
	public RuneDictionaryEntry getRandomNumber() {
		return rd.getRandomRune(RuneDictionary.NUMBER_RDES);
	}
	public RuneDictionaryEntry getRandomSymbol() {
		return rd.getRandomRune(RuneDictionary.SYMBOL_RDES);
	}
	public RuneDictionaryEntry getRandomSpecial() {
		return rd.getRandomRune(RuneDictionary.SPECIAL_RDES);
	}
	*/
	
	public int getMaxRuneCount() {
		return maxRuneCount;
	}
}
