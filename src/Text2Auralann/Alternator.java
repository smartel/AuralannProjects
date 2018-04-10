package Text2Auralann;

import java.util.LinkedList;

public class Alternator {

	private Settings settings;
	
	public Alternator(Settings settings) {
		this.settings = settings;
	}
	
	public void alternate(Knowl knowl) {
		// only alternate if we are in a mode that allows for alternate runes
		int altMode = settings.getIntSetting(Settings.I_ALT_MODE);
		if (!(altMode == Settings.ALTS_NO_ALTS)) {
			if (altMode == Settings.ALTS_EQUAL_CHANCE) {
				alternateEqualChance(knowl);
			} else if (altMode == Settings.ALTS_ALTS_ONLY) {
				alternateAll(knowl);
			} else if (altMode == Settings.ALTS_ALT_ALTS) {
				alternateAlts(knowl);
			}
		}
	}
	
	
	// Alt Chance (if 2 forms total, then 50% of either. if 3 forms, 33% of a form, ...)
	public void alternateEqualChance(Knowl knowl) {
		LinkedList<Lore> lores = knowl.getLoresAndStops();
		for (int x = 0; x < lores.size(); ++x) {
			Lore lore = lores.get(x);
			LinkedList<Rune> runes = lore.getRunes();
			for (int y = 0; y < runes.size(); ++y) {
				Rune rune = runes.get(y);
				RuneDictionaryEntry rde = rune.getRde();
				if (rde != null && rde.getFormCount() > 1) {
					rune.setRuneForm(rde.getIndexOf(rde.getRandomForm()));
				}
			}
		}
	}
	
	// Always Alts
	public void alternateAll(Knowl knowl) {
		LinkedList<Lore> lores = knowl.getLoresAndStops();
		for (int x = 0; x < lores.size(); ++x) {
			Lore lore = lores.get(x);
			LinkedList<Rune> runes = lore.getRunes();
			for (int y = 0; y < runes.size(); ++y) {
				Rune rune = runes.get(y);
				RuneDictionaryEntry rde = rune.getRde();
				if (rde != null && rde.getFormCount() > 1) {
					rune.setRuneForm(rde.getIndexOf(rde.getRandomAltIfExists()));
				}
			}
		}
	}
	
	
	// Alternate Alts
	public void alternateAlts(Knowl knowl) {
		LinkedList<Lore> lores = knowl.getLoresAndStops();
		for (int x = 0; x < lores.size(); ++x) {
			Lore lore = lores.get(x);
			LinkedList<Rune> runes = lore.getRunes();
			for (int y = 0; y < runes.size(); ++y) {
				Rune rune = runes.get(y);
				RuneDictionaryEntry rde = rune.getRde();
				if (rde != null) {
					String currentKey = rde.getKey();
					if (rde.getFormCount() > 1) {
						// look for the last instance of this Key in the knowl, and use a different form
						// if this is the first instance of the key, then use a random form.
						boolean found = false;
						for (int z = y-1; z > -1; --z) {
							if (runes.get(z).getRde() != null) {
								if (runes.get(z).getRde().getKey().equalsIgnoreCase(currentKey)) {
									// get the form we do not want to repeat
									int badForm = runes.get(z).getRuneForm();
									// set this rune to use a different form
									rune.setRuneForm(rde.getIndexOf(rde.getDifferentIfExists(rde.getForm(badForm))));
									// break the loop
									z = -1;
									found = true;
								}
							}
						}
						if (!found) {
							rune.setRuneForm(rde.getIndexOf(rde.getRandomForm()));
						}
					}
				}
			}
		}
	}
	
}
