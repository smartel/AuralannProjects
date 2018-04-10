package Processing;

public class Scoreable {

	// Scoreables are objects that have a display name (such as "Sorazae") and a matching name (all lowercase).
	// The matching name is so text like "sorazae", "sORAZAE", "sOrAzAe" etc all increment / decrement the same object
	
	private String displayName;
	private String matchingName;
	private int currentScore;
	
	public Scoreable(String displayName, int value) {
		this.displayName = displayName; // we keep the name it is initialized with, and all future changes will match to the initial
		matchingName = displayName.toLowerCase();
		currentScore = value;
	}
	
	public void increment() {
		currentScore += 1;
	}
	
	public void decrement() {
		currentScore -= 1;
	}
	
	public void incrementBy(int amount) {
		currentScore += amount;
	}
	
	public void decrementBy(int amount) {
		currentScore -= amount;
	}
	
	public void setScore(int newScore) {
		currentScore = newScore;
	}
	
	public String getMatchingName() {
		return matchingName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public int getScore() {
		return currentScore;
	}
	
}
