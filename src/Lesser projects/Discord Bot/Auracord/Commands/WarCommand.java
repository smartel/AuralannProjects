package Commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import Processing.Main;
import Processing.WarPlayer;
import Processing.WarSession;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class WarCommand implements Command {

	public static final String COMMAND_TEXT = "war";
	// ALT_TEXT: pvp
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	private Random random;
	private static TreeMap<String, WarSession> guildSessions;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {

		// if the collection hasn't been initialized yet, we gotta create it before trying any commands
		if (guildSessions == null) {
			guildSessions = new TreeMap<String, WarSession>();
		}
		
		if (args.size() > 0) {
			if (args.get(0).equalsIgnoreCase("RESET")) {
				
				// get the war session for this guild name and reset it
				WarSession session = guildSessions.get(event.getGuild().getName());
				
				if (session != null) {
					for (Map.Entry<String, WarPlayer> entry : session.getPlayers().entrySet()) {
						WarPlayer currPlayer = entry.getValue();
						currPlayer.resetStats();	
					}
					event.getTextChannel().sendMessage("War stats reset for " + event.getGuild().getName() + "!");
				}
				return;
			} else if (args.get(0).equalsIgnoreCase("STATS")) {
				// technically we may want to sort this by winner or something one day

				// get the war session for this guild name and reset it
				WarSession session = guildSessions.get(event.getGuild().getName());
				
				if (session == null || session.getPlayers().isEmpty()) {
					event.getTextChannel().sendMessage("There is no active War game for " + event.getGuild().getName() + "!");
					return;
				}
				
				// need to build one string to send, so we don't get rate-limited:
				String msg = "Current d20 War stats for " + event.getGuild().getName() + ":\n";
				
				for (Map.Entry<String, WarPlayer> entry : session.getPlayers().entrySet()) {
					WarPlayer currPlayer = entry.getValue();
					String currStats = currPlayer.getStats();
					msg += currStats + "\n";
				}
				event.getTextChannel().sendMessage(msg);
				return;
			} else {
				// see if it's a number - if it is, we'll do a multi-war (multiple rounds) and then return.
				// if it isn't a number / we don't know what to do with it, then we'll just let it do 1 war like normal
				try {
					int numRounds = Integer.parseInt(args.get(0));
					
					// if numRounds is smaller than or equal to 0, then only run a single round. if it is greater than 10000, then set to 10000
					if (numRounds <= 0) { // lower bounds
						numRounds = 1;
					}
					if (numRounds > 10000) { // upper bounds
						numRounds = 10000;
					}
					
					runMultipleRounds(event, numRounds);
					
					return;
				} catch (Exception e) {
					// swallow exception, we won't return, so normal execution will perform a single war instead
				}
				
			}
		}
		
		runSingleRound(event);
		
	}
	
	public void runSingleRound(MessageReceivedEvent event) {
		int numUsers;
		int highestRoll = 0;
		int numHighestPlayers = 0;
		String winner = "";

		WarSession session = guildSessions.get(event.getGuild().getName());
		
		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		if (session == null) {
			session = new WarSession(new TreeMap<String, WarPlayer>());
			guildSessions.put(event.getGuild().getName(), session);
		}
		
		// loop over everyone in the room, roll the die, print and save the result

		// need to build one string to send, so we don't get rate-limited:
		String msg = "The d20 battle commences!\n...\n";
		
		LinkedList<User> users = new LinkedList<User>(event.getTextChannel().getUsers());
		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(User l, User r) {
				return l.getUsername().compareTo(r.getUsername());
			}
		});
		
		numUsers = users.size();
		for (int x = 0; x < numUsers; ++x) {
			String name = users.get(x).getUsername();
			int roll = random.nextInt(20)+1;
			msg += (name + " rolled a " + roll + ".\n");
			
			if (roll > highestRoll) { // potential winner
				highestRoll = roll;
				numHighestPlayers = 1;
				winner = name;
			} else if (roll == highestRoll) { // potential tie
				++numHighestPlayers;
				winner += " and " + name;
			}
			
			if (session.getPlayers().containsKey(name)) {
				WarPlayer player = session.getPlayers().get(name);
				player.latestRoll(roll);
			} else {
				WarPlayer player = new WarPlayer(name);
				player.latestRoll(roll);
				session.getPlayers().put(name, player);
			}
		}
		
		// at the end, pick the winner
		// if there was a tie, then we'll consider them both/all winners.
		
		if (numHighestPlayers == 1) {
			event.getTextChannel().sendMessage(msg + "...\nThe winner is " + winner + ", with a " + highestRoll + "!");
		} else {
			event.getTextChannel().sendMessage(msg + "...\nThe winners are " + winner + ", who rolled " + highestRoll + "!");
		}
		
		for (Map.Entry<String, WarPlayer> entry : session.getPlayers().entrySet()) {
			WarPlayer currPlayer = entry.getValue();
			currPlayer.execGameEnd(currPlayer.getLastRoll() == highestRoll ? true : false);			
		}
	}
	
	public void runMultipleRounds(MessageReceivedEvent event, int numRounds) {

		// run multiple rounds of war, keeping track of victories and total points for each of these rounds

		int numUsers;
		int mostWins = 0;
		int numMostWinningPlayers = 0;
		int highestTotalScore = 0;
		String winner = "";
		
		WarSession session = guildSessions.get(event.getGuild().getName());
		
		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		if (session == null) {
			session = new WarSession(new TreeMap<String, WarPlayer>());
			guildSessions.put(event.getGuild().getName(), session);
		}

		// need to build one string to send, so we don't get rate-limited:
		String msg = "The d20 battle commences!\n...\n";

		// initialize multi round by eliminating any previous multi-rounds
		session.resetMultiRoundStats();
		
		for (int round = 0; round < numRounds; ++round) {

			int highestRoll = 0;
		
			// loop over everyone in the room, roll the die, but DON'T PRINT THE RESULT
	
			LinkedList<User> users = new LinkedList<User>(event.getTextChannel().getUsers());
			Collections.sort(users, new Comparator<User>() {
				@Override
				public int compare(User l, User r) {
					return l.getUsername().compareTo(r.getUsername());
				}
			});
			
			numUsers = users.size();
			for (int x = 0; x < numUsers; ++x) {
				String name = users.get(x).getUsername();
				int roll = random.nextInt(20)+1;
				//msg += (name + " rolled a " + roll + ".\n");
				
				if (roll > highestRoll) { // potential winner
					highestRoll = roll;
				}
				
				if (session.getPlayers().containsKey(name)) {
					WarPlayer player = session.getPlayers().get(name);
					player.latestRoll(roll);
				} else {
					WarPlayer player = new WarPlayer(name);
					player.latestRoll(roll);
					session.getPlayers().put(name, player);
				}
			}
			
			// now, loop over everyone in the session, and if their score = this round's highest score, give them a win

			for (Map.Entry<String, WarPlayer> entry : session.getPlayers().entrySet()) {
				WarPlayer currPlayer = entry.getValue();
				currPlayer.execGameEnd(currPlayer.getLastRoll() == highestRoll ? true : false);			
			}
			
		}
		
		// now, loop over everyone in the session, and print how many wins they had
		// if there was a tie on number of victories, then we'll check the total score.
		// if there's still a tie, then they both win, I guess.

		for (Map.Entry<String, WarPlayer> entry : session.getPlayers().entrySet()) {
			WarPlayer currPlayer = entry.getValue();
			msg += currPlayer.getMultiStats() + "\n";
			
			int roundWins = currPlayer.getMultiNumWins();

			if (roundWins > mostWins) { // potential winner
				mostWins = roundWins;
				numMostWinningPlayers = 1;
				winner = entry.getKey();
				highestTotalScore = currPlayer.getMultiTotalScore();
			} else if (roundWins == mostWins) { // potential tie
				if (currPlayer.getMultiTotalScore() > highestTotalScore) {
					winner = entry.getKey(); // this is the new winner, they have a higher score
					highestTotalScore = currPlayer.getMultiTotalScore();
				} else if (currPlayer.getMultiTotalScore() == highestTotalScore) { // oh my god a tie
					++numMostWinningPlayers;
					winner += " and " + entry.getKey();
				} // else, the previous winner remains cause they had a higher total
			}
			
		}
		
		// and now announce who the winner was
		
		if (numMostWinningPlayers == 1) {
			event.getTextChannel().sendMessage(msg + "...\nThe winner is " + winner + ", with " + mostWins + " victories and a total score of " + highestTotalScore + "!");
		} else {
			event.getTextChannel().sendMessage(msg + "...\nThe winners are " + winner + ", with " + mostWins + " victories and a total score of " + highestTotalScore + "!");
		}
		
		// don't need to give them *another* win, since they already got all their wins during round processing
	}

	@Override
	public void postExecute(boolean success, MessageReceivedEvent event, String args) {
		Main.logMessage("Executed " + COMMAND_TEXT + " command with args " + args);
		return;
	}
	
	@Override
	public String help() {
		return HELP;
	}

	@Override
	public LinkedList<String> getMatches() {
		LinkedList<String> matches = new LinkedList<String>();
		matches.add(COMMAND_TEXT);
		matches.add("pvp");
		return matches;
	}
}
