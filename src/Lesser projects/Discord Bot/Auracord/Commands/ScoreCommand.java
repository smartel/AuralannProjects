package Commands;

import java.util.LinkedList;
import java.util.TreeMap;

import Processing.Main;
import Processing.Scoreable;
import Processing.Scoreboard;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ScoreCommand implements Command {

	public static final String COMMAND_TEXT = "score";
	// ALT_TEXT: list, stats
	private final String HELP = "Usage: {any text line ending with ++ or --}\n" + SORA_START +"list\n" + SORA_START + "list reset";

	private static TreeMap<String, Scoreboard> guildScores;
	
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // calling <>list doesn't require arguments - there's too many use cases here, not just ++ / --, so always return true
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {

		// if the collection hasn't been initialized yet, we gotta create it before trying any commands
		if (guildScores == null) {
			guildScores = new TreeMap<String, Scoreboard>();
		}
		
		if (args.size() == 0) {
			// If there were no args, we mustve only got the <>list command

			// get the war session for this guild name and reset it
			Scoreboard scores = guildScores.get(event.getGuild().getName());
			
			if (scores == null || scores.getScoreboard().isEmpty()) {
				event.getTextChannel().sendMessage("There is no active Scoreboard for " + event.getGuild().getName() + "!");
				return;
			}
			
			event.getTextChannel().sendMessage(scores.getScores()); // one message to send, so it isn't rate limited
			return;
		} else if (args.size() > 0) {
			// determine if RESET, ++ or -- and act accordingly
			
			if (args.get(0).equalsIgnoreCase("RESET")) {
				
				// get the scoreboard for this guild name and reset it
				Scoreboard scores = guildScores.get(event.getGuild().getName());
				
				if (scores != null) {
					scores.reset();
					event.getTextChannel().sendMessage("Scoreboard reset for " + event.getGuild().getName() + "!");
				}
				return;
			}
			else if (args.get(0).equalsIgnoreCase("++") || args.get(0).equalsIgnoreCase("--")) {
				
				if (args.size() == 2) { // since the args will be, {++|--} {value} {name}, and we know the ++/-- and value are guaranteed
					event.getTextChannel().sendMessage("No valid target found for ++ or -- operation.");
				} else {
					Scoreboard scores = guildScores.get(event.getGuild().getName());
					
					if (scores == null) {
						scores = new Scoreboard(new TreeMap<String, Scoreable>(), event.getGuild().getName());
						guildScores.put(event.getGuild().getName(), scores);
					}
					
					// determine how much to increment or decrement by
					
					int amount = Integer.parseInt(args.get(1));
					
					// check if the target being incremented / decremented already exists

					String target = args.get(2);
					
					int newValue = 0;
					if (scores.getScoreboard().containsKey(target.toLowerCase())) {
						newValue = scores.getScoreboard().get(target.toLowerCase()).getScore();
					} else {
						// will need to insert the scoreable before we can call increment / decrement on it
						Scoreable newScore = new Scoreable(target, 0);
						scores.getScoreboard().put(target.toLowerCase(), newScore);
					}
					
					// whether it is ++ or --, add the value, because if it is -- then the value will be negative
					newValue += amount;
					scores.getScoreboard().get(target.toLowerCase()).incrementBy(amount);
					
					if (args.get(0).equalsIgnoreCase("++")) {
						event.getTextChannel().sendMessage(target + "'s score has increased by " + amount + " and is now at " + newValue + ".");
					} else {
						event.getTextChannel().sendMessage(target + "'s score has decreased by " + (amount-amount-amount) + " and is now at " + newValue + ".");
					}
				}
			}
			else if (args.get(0).equalsIgnoreCase("remove")) {
				
				if (args.size() == 1) {
					event.getTextChannel().sendMessage("No valid target found for list remove operation.");
				} else {
					Scoreboard scores = guildScores.get(event.getGuild().getName());
					
					if (scores == null) {
						event.getTextChannel().sendMessage("No scoreboard for " + event.getGuild().getName() + " exists to remove entries from yet.");
						return;
					}
					

					String target = args.get(1);
					// check if the target being removed exists
					
					if (scores.getScoreboard().containsKey(target.toLowerCase())) {
						scores.getScoreboard().remove(target.toLowerCase());
						event.getTextChannel().sendMessage(target + " successfully removed from the scoreboard.");
					} else {
						event.getTextChannel().sendMessage(target + " does not exist on the scoreboard.");
					}
				}
			}
			else if (args.get(0).equalsIgnoreCase("add")) {
				
				if (args.size() != 3) {
					event.getTextChannel().sendMessage("A valid name and integer value need to be specified to be added to the scoreboard.");
				} else {
					Scoreboard scores = guildScores.get(event.getGuild().getName());
					
					if (scores == null) {
						scores = new Scoreboard(new TreeMap<String, Scoreable>(), event.getGuild().getName());
						guildScores.put(event.getGuild().getName(), scores);
					}

					String target = args.get(1);
					int newValue;
					try {
						newValue = Integer.parseInt(args.get(2));
					} catch (Exception e) {
						event.getTextChannel().sendMessage("A valid integer value needs to be specified to be added to the scoreboard.");
						return;
					}
					
					// check if the target being added already exists - if it does, just modify their score
					if (scores.getScoreboard().containsKey(target.toLowerCase())) {
						event.getTextChannel().sendMessage(target + "'s score has been changed to " + newValue + ".");
						scores.getScoreboard().get(target.toLowerCase()).setScore(newValue);
					} else {
						Scoreable newScore = new Scoreable(target, newValue);
						scores.getScoreboard().put(target.toLowerCase(), newScore);
						event.getTextChannel().sendMessage(target + " has been added to the scoreboard with a score of " + newValue + ".");
					}
				}
				
			}
		}
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
		matches.add("list");
		matches.add("stats");
		return matches;
	}
}
