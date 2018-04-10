package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ScaleCommand implements Command {

	public static final String COMMAND_TEXT = "scale";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT + " (no arguments defaults to 1-10 range) \n" +
							    SORA_START + COMMAND_TEXT + " {optional: max value} \n" +
							    SORA_START + COMMAND_TEXT + " {optional: min value} {optional: max value} \n";
	
	private Random random;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args (they're optional)
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		int value;
		int firstArg = -1;
		int secondArg = -1;
		int min = 1; // default min
		int max = 10; // default max

		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		
		// Note, extra text may be passed in after the fact for fun, so if there are no args, it's fine.
		// If there are args, they may not be numbers, so don't barf if they can't be parsed.
		
		
		if (args.size() > 0) {
			// grab the first argument and see if it is a number. if it isn't, it's fine
			try {
				firstArg = Integer.parseInt(args.get(0).toString());
				// since we didn't blow up, try and get a second one too, if it exists
				if (args.size() > 1) {
					secondArg = Integer.parseInt(args.get(1).toString());
				}
			} catch (Exception e) {
				// swallow exceptions
			}
		}
		
		// so, we potentially have a passed in max, or a passed in min and max.
		if (firstArg > 0 && secondArg < 0) { 
			// only have a max passed in
			max = firstArg;
		}
		else if (firstArg > 0 && secondArg > 0) {
			min = firstArg;
			max = secondArg;
		}
		// else, no useable args were passed in, so leave as default 1-10
		
		
		value = random.nextInt(max-min+1)+min;

		event.getTextChannel().sendMessage(value + "/" + max);


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
		return matches;
	}
}
