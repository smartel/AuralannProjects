package Processing;

import java.text.DecimalFormat;

public class WarPlayer {

	
	private String username;
	private int numGames;
	private int numWins;
	private int totalScore;
	private int highestRoll;
	private int lowestRoll;
	private int lastRoll;
	
	// these variables only apply to multi-round wars, as in, if <>war 5 is called, it's their total number of wins out of those 5 watches and their score total
	private int multiNumWins;
	private int multiTotalScore;
	
	public WarPlayer(String username) {
		this.username = username;
		lowestRoll = 0;
		highestRoll = 0;
		numGames = 0;
		numWins = 0;
		totalScore = 0;
		lastRoll = 0;
		
		// only apply to multi-round wars
		multiNumWins = 0;
		multiTotalScore = 0;
	}
	
	public void latestRoll(int value) {
		lastRoll = value;
		totalScore += value;

		if (lowestRoll == 0) {
			lowestRoll = value;
		} else if (value < lowestRoll) {
			lowestRoll = value;
		}
		
		if (highestRoll == 0) {
			highestRoll = value;
		} else if (value > highestRoll) {
			highestRoll = value;
		}

		// Multiround assumption:
		// just assume there's a multi-round war going on. if there isn't, it'll never be printed.
		// if there is, then great, we're counting it.
		// if there isn't right now, but there will be one later? these stats are wiped when initializing a multi-round so it's fine.
		multiTotalScore += value;
	}
	
	public void execGameEnd(boolean didWin) {
		numGames++;
		if (didWin) {
			numWins++;
			
			// see: "Multiround assumption"
			multiNumWins++;
		}
	}
	
	public void resetStats() {
		lowestRoll = 0;
		highestRoll = 0;
		numGames = 0;
		numWins = 0;
		totalScore = 0;
		lastRoll = 0;
	}
	
	public int getLastRoll() {
		return lastRoll;
	}
	
	public String getStats() {
		
		DecimalFormat df = new DecimalFormat("##.##");
		String winRate = numGames > 0 ? "" + df.format(((double)numWins/(double)numGames) * 100) + "%" : "0%";

		String stats = "User: " + username + " - Rounds won: " + numWins + "/" + numGames + " - Win rate: " + winRate + 
					   " - Total score: " + totalScore;
		//  + " - Highest roll: " + highestRoll + " - Lowest roll: " + lowestRoll;    // not really useful
		
		return stats;
	}
	
	public void resetMultiStats() {
		multiNumWins = 0;
		multiTotalScore = 0;
	}
	
	public int getMultiNumWins() {
		return multiNumWins;
	}
	
	public int getMultiTotalScore() {
		return multiTotalScore;
	}

	public String getMultiStats() {
		// returns stats relevant to a multi-round war
		String stats = username + " had " + multiNumWins + " victories with a total score of " + multiTotalScore + ".";
		return stats;
	}
	
}
