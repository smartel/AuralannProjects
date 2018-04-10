package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class CoinflipCommand implements Command {

	public static final String COMMAND_TEXT = "coinflip";
	// ALT_TEXT: flip
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

		// if value <= 48, then heads. <= 96, then tails. that leaves 4 for landing on its side. :^)
		if (value <= 48) {
			event.getTextChannel().sendMessage("Heads.");
			result = "Heads";
		} else if (value <= 96) {
			event.getTextChannel().sendMessage("Tails.");
			result = "Tails";
		} else { // 97-100, 4%
			event.getTextChannel().sendMessage("It... landed on its side?");
			result = "Side";
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
		matches.add("flip");
		return matches;
	}

}
