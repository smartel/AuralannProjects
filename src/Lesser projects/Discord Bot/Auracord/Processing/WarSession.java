package Processing;

import java.util.Map;
import java.util.TreeMap;

public class WarSession {

	// A wrapper for a collection of WarPlayers

	private TreeMap<String, WarPlayer> players;
	
	public WarSession(TreeMap<String, WarPlayer> players) {
		this.players = players;
	}
	
	public TreeMap<String, WarPlayer> getPlayers() {
		return players;
	}
	
	public void resetMultiRoundStats() {
		for (Map.Entry<String, WarPlayer> entry : players.entrySet()) {
			entry.getValue().resetMultiStats();
		}
	}
}
