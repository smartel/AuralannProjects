package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class WhatCommand implements Command {

	public static final String COMMAND_TEXT = "what";
	// ALT_TEXT: what's, whats, whatre, what're, which, ?
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	private Random random;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {

		int value;
		String token;
		String question = "";
		String currentOption = "";
		LinkedList<String> choices = new LinkedList<String>();
		String winner = "";
		String asker = event.getAuthorName();

		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		
		// Take the string that was input. We can cut early if there's no args to work with.
		if (args.size() == 0) {
			event.getTextChannel().sendMessage("I thought you had a question, " + asker + "?");
		}
		
		// determine the question and options
		boolean atChoices = false;
		for (int x = 0; x < args.size(); ++x) {
			token = args.get(x).toString();
			if (!atChoices) {
				question += token; // keep appending to the question until we hit a breaker
				if (token.endsWith("!") || token.endsWith(".") || token.endsWith("?") || token.endsWith(",")) {
					atChoices = true;
				}
			} else {
				if (token.equalsIgnoreCase("or")) {
					// save the multi-word choice and reset the holder
					choices.add(currentOption.trim());
					currentOption = ""; // reset the multi-word choice
				} else {
					currentOption += (token + " "); // extra space will get trimmed
				}
			}
		}
		if (!currentOption.isEmpty()) {
			choices.add(currentOption.trim());
		}
		
		// if collection size == 0, then ask what your options are and abort.
		if (choices.size() == 0) {
			event.getTextChannel().sendMessage("And what are my options, " + asker + "? Think about your question and try again.");
			Main.logMessage(COMMAND_TEXT + ": question determined: " + question + ", with no apparent choices. Aborting.");
			return;
		}
		
		// if collection size == 1, then there isn't really much of a choice.
		else if (choices.size() == 1) {
			event.getTextChannel().sendMessage("Not really giving me much of a choice, are you, " + asker + "?");
			winner = choices.get(0);
			winner = winner.substring(0, 1).toUpperCase() + winner.substring(1); // Capitalizing the first letter
		}
		
		else { // multiple choices to pick from, choose at random
			int numChoices = choices.size();
			value = random.nextInt(numChoices);
			winner = choices.get(value);
			winner = winner.substring(0, 1).toUpperCase() + winner.substring(1); // Capitalizing the first letter
		}

		// if the last option ends with a period, question mark, exclamation point, etc, rip it out, but only if it's not a name.
		if (choices.size() == 0 && (winner.endsWith("?") || winner.endsWith("!") || winner.endsWith("."))) {
			winner = winner.substring(0, winner.length()-2);
		}

		
		// Want a random chance for ..., !, ?, .
		// Like 75% ., 15% !, 5% ?, 5% ...
		String enthusiasm;
		int enValue = random.nextInt(100)+1;
		if (enValue <= 75) {
			enthusiasm = ".";
		} else if (enValue <= 90) {
			enthusiasm = "!";
		} else if (enValue <= 95) {
			enthusiasm = "?";
		} else { // last 5%
			enthusiasm = "...";
		}
		
		event.getTextChannel().sendMessage(winner + enthusiasm);

		String choiceStr;
		if (choices.size() > 0) {
			choiceStr = choices.toString();
		} else {
			choiceStr = event.getTextChannel().getUsers().toString();
		}
		Main.logMessage(COMMAND_TEXT + ": question determined: " + question + ", with choices: " + choiceStr);
		 
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
		matches.add("what's");
		matches.add("whats");
		matches.add("whatre");
		matches.add("what're");
		matches.add("which");
		matches.add("?");
		return matches;
	}
}
