package Text2Auralann;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class RuneDictionaryEntry implements Comparable<RuneDictionaryEntry> {

	// This class stores a single entry in the Rune Dictionary.
	// An entry is defined as the character(s) we want to represent, ie: singles like "O", doubles like "ER", ...
	// It will have all the various forms of O, ie: "O-1", "O-2" (the base O rune, and its alt form), "RE-1", "RE-2", ...
	// It will also map all the various forms of the rune to a file location, so we can display the runes as images.
	
	private String key; // the character(s) we want to represent
	private LinkedList<String> keyForms; // forms of this character(s), ie "O-1", "O-2", ...
	private HashMap<String, String> filePaths; // maps the keyforms to image files for displaying
	Random rnd;

	private String runeCategory; // "Single Rune", "Double Rune", "Symbol Rune", ... used by the rune quizzer
	
	/**
	 * ctor
	 * @param key the character(s) we want to represent
	 */
	public RuneDictionaryEntry(String key) {
		this.key = key;
		keyForms = new LinkedList<String>();
		filePaths = new HashMap<String,String>();
		rnd = new Random();
	}
	
	/**
	 * Adds a new form for this entry, ex: adding "O-2" to "O"
	 * @param form the new form for this entry
	 * @param path the file path to find the image file of the new form
	 */
	public void addForm(String form, String path) {
		keyForms.add(form);
		filePaths.put(form, path);
	}
	
	/**
	 * @return true if there are any alts, false otherwise
	 */
	public boolean hasAlt() {
		boolean hasAlt = false;
		if (keyForms.size() > 1) {
			hasAlt = true;
		}
		return hasAlt;
	}
	
	/**
	 * @return the count of different alts (so, not included the base) this entry has
	 */
	public int getAltCount() {
		int altCount = 0;
		if (hasAlt()) {
			return keyForms.size()-1;
		}
		// else, no alts, so its 0
		return altCount;
	}
	
	/**
	 * @return the count of all different forms (including the base) this entry has
	 */
	public int getFormCount() {
		return keyForms.size();
	}
	
	/**
	 * @return the key (letter(s)) of the rune for this entry, ie: "O" or "OR"
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * @return the base form
	 */
	public String getBaseForm() {
		return keyForms.get(0);
	}
	
	/**
	 * Returns the desired form if it exists
	 * @param form which form to get, ie 0 (base), 1 (first alt), ...
	 * @return the desired form, or empty string if it doesn't exist
	 */
	public String getForm(int form) {
		String val = "";
		if (!(form < 0) && !(form >= keyForms.size())) {
			val = keyForms.get(form);
		}
		return val;
	}
	
	/**
	 * Returns the index of this form, if it exists
	 * @param form form to get the index of
	 * @return index of the form, or -1 if it doesn't exist
	 */
	public int getIndexOf(String form) {
		return keyForms.indexOf(form);
	}
	
	/**
	 * @return A random alt form if it exists - otherwise, the base
	 */
	public String getRandomAltIfExists() {
		String alt;
		if (keyForms.size() > 1) {
			// can probably shortcircuit rolling for a random one, most things only have one alt
			if (keyForms.size() == 2) {
				return keyForms.get(1);
			} else {
				// roll for a random one
				int form = rnd.nextInt(keyForms.size()-1) + 1; // -1 from the roll and +1 to the result so we skip the base form entirely
				return keyForms.get(form);
			}
		} else {
			alt = getBaseForm();
		}
		return alt;
	}
	
	/**
	 * @return A random form
	 */
	public String getRandomForm() {
		// roll for a random form
		int form = rnd.nextInt(keyForms.size());
		return keyForms.get(form);
	}
	
	/**
	 * Gets the file path for a given form
	 * @param form form to get the file path for
	 * @return
	 */
	public String getFilePath(String form) {
		return filePaths.get(form);
	}
	
	/**
	 * Just returns the first path. For use when there's only one form, like auras.
	 */
	public String getFilePath() {
		return filePaths.get(key);
	}
	
	/**
	 * Returns a form that isn't the form that was passed in, if possible
	 * @param form the form to not match
	 * @return a new form if possible, otherwise the same form
	 */
	public String getDifferentIfExists(String form) {
		String newForm = form;
		if (validateForm(form)) {
			if (keyForms.size() > 1) {
				// just keep rolling til you get a different one, i guess
				while (newForm.equals(form)) {
					int roll = rnd.nextInt(keyForms.size());
					newForm = keyForms.get(roll);
				}
			}
			// else, only one form, so deal with it.
		} else {
			// wasn't provided with a valid form to start with, so just use a random one
			newForm = getRandomForm();
		}
		
		return newForm;
	}
	
	/**
	 * better version of getDifferentIfExists for a single string: get any form that isn't the forms provided, unless impossible
	 * @param forms the list of forms to not match. returns a random form if there aren't any unused forms to return.
	 * @return a different form if it existed - otherwise, a random form
	 */
	public String getDifferentIfExists(LinkedList<String> forms) {
		String newForm = getRandomForm(); // just pick a form at random if we can't get a different one
		
		if (forms.size() < keyForms.size()) {
			// just keep rolling til you get a different one, i guess
			while (forms.contains(newForm)) {
				int roll = rnd.nextInt(keyForms.size());
				newForm = keyForms.get(roll);
			}
		}
		
		return newForm;
	}
	
	/**
	 * checks if the passed in form is valid or not (ie, "O-2" is valid, "O-w" is not)
	 * @param form the form to check for validity
	 * @return true if valid, false otherwise
	 */
	private boolean validateForm(String form) {
		boolean isValid = false;
		if (keyForms.contains(form)) {
			isValid = true;
		}
		return isValid;
	}
	
	// for rune quizzer
	public void setRuneCategory(String runeCategory) {
		this.runeCategory = runeCategory;
	}
	
	// for rune quizzer
	public String getRuneCategory() {
		return runeCategory;
	}
	
	// for rune quizzer. deletes a guessed form so it can't show up in the quizzer again.
	public void annihilateForm(String form) {
		keyForms.remove(form);
		filePaths.remove(form);
	}

	@Override
	public int compareTo(RuneDictionaryEntry other) {
		return key.compareToIgnoreCase(other.getKey());
	}
	
	@Override
    public int hashCode(){
        return Objects.hashCode(key);
    }

    @Override
    public boolean equals(Object obj){
        if (! (obj instanceof RuneDictionaryEntry) ) {
            return false;
        }
        RuneDictionaryEntry other = (RuneDictionaryEntry)obj;
        if (other.getKey().equalsIgnoreCase(key)) {
        	return true;
        }
        return false;
    }

}
