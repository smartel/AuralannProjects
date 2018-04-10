package Processing;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class Scoreboard {

	// Wrapper class for a simple collection, linking "things" to scores incremented or decremented by the ++ / -- command
	
	private TreeMap<String, Scoreable> scores;
	private String guildName;
	
	public Scoreboard(TreeMap<String, Scoreable> scores, String guildName) {
		this.scores = scores;
		this.guildName = guildName;
	}
	
	public TreeMap<String, Scoreable> getScoreboard() {
		return scores;
	}
	
	public void reset() {
		scores.clear();
	}
	
	// print out scores in order
	public String getScores() {
		
		LinkedList<Scoreable> scoreList = new LinkedList<Scoreable>();

		for (Map.Entry<String, Scoreable> entry : scores.entrySet()) {
			Scoreable currScoreable = entry.getValue();
			scoreList.add(currScoreable);
		}
		
		
		Collections.sort(scoreList, new Comparator<Scoreable>() {
			@Override
			public int compare(Scoreable l, Scoreable r) {
				return l.getScore() < r.getScore() ? 1 : l.getScore() > r.getScore() ? -1 : l.getDisplayName().compareTo(r.getDisplayName());
			}
		});
		
		String msg = "Scoreboard for " + guildName + ":\n";
		for (int x = 0; x < scoreList.size(); ++x) {
			String pointOrPoints = " points";
			if (scoreList.get(x).getScore() == 1) {
				pointOrPoints = " point";
			}
			msg += scoreList.get(x).getScore() + pointOrPoints + ": " + scoreList.get(x).getDisplayName() + "\n";
		}
		
		return msg;
	}
	
}
