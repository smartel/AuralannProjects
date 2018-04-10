package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class RollCommand implements Command {

	public static final String COMMAND_TEXT = "roll";
	// ALT_TEXT: random
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	private Random random;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		int value;
		int max;
		String howGend = "";
		String name = event.getAuthorName();

		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		
		if (args.size() > 0) {
			// grab the first argument and try to use it as a max. if it fails, roll 100.
			try {
				max = Integer.parseInt(args.get(0).toString());
				howGend = "valid arg";
			} catch (Exception e) {
				//event.getTextChannel().sendMessage(name + "! That is not a valid number! We'll just go with 100...");
				// Swallow the exception, don't get mad - we're saying people should pass in their question with the call!
				max = 100;
				howGend = "bad arg, unparseable due to text";
			}
		} else {
			max = 100; // default to 100 since no args passed in
			howGend = "no arg, default";
		}
		
		if (max <= 0) {
			event.getTextChannel().sendMessage(name + "! That is not a valid number! Remember, it should be a positive integer! We'll use 100 for now.");
			howGend = "bad arg, non-positive or 0";
			max = 100;
		}
		if (max == 1) {
			event.getTextChannel().sendMessage("Well that's not very fun, " + name + ". We'll use 100 for now.");
			howGend = "bad arg, no fun detected";
			max = 100;
		}
		
		value = random.nextInt(max)+1;

		event.getTextChannel().sendMessage(name + " rolled a " + value + ".");

		Main.logMessage(COMMAND_TEXT + ": generated value: " + value + " with potential maximum of: " + max + " via: " + howGend);

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
		matches.add("random");
		return matches;
	}
}
