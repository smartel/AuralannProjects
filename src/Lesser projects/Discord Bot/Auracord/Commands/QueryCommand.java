package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class QueryCommand implements Command {

	// aka ReimgohsCommand
	public static final String COMMAND_TEXT = "query";
	//ALT_TEXT: yesno
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	private Random random;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		int value;
		String result;
		
		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		
		value = random.nextInt(100)+1; // value is between 1 and 100.

		// 50% chance yes, 50% no. But it's more than that - emphasis is everything!
		// 32% Yes. 10% Yes! 8% Yes...
		// 32% No. 10% No! 8% No...
		if (value <= 32) {
			event.getTextChannel().sendMessage("Yes.");
			result = "Yes.";
		} else if (value <= 42) {
			event.getTextChannel().sendMessage("Yes!");
			result = "Yes!";
		} else if (value <= 74) {
			event.getTextChannel().sendMessage("No.");
			result = "No.";
		} else if (value <= 84) {
			event.getTextChannel().sendMessage("No!");
			result = "No!";
		} else if (value <= 92) {
			event.getTextChannel().sendMessage("Yes...");
			result = "Yes...";
		} else {
			event.getTextChannel().sendMessage("No...");
			result = "No..."; // crippling depression
		}

		Main.logMessage(COMMAND_TEXT + ": generated value: " + value + ", resulting in: " + result);

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
		matches.add("yesno");
		return matches;
	}
}
