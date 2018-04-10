package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class FriendCommand implements Command {

	public static final String COMMAND_TEXT = "friend";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	private Random random;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		int value;
		String winner = "";
		String asker = event.getAuthorName();

		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		
		winner = "Sorazae";
		while (winner.equalsIgnoreCase("Sorazae")) {
			int numUsers = event.getTextChannel().getUsers().size();
			value = random.nextInt(numUsers);
			winner = event.getTextChannel().getUsers().get(value).getUsername();
		}
		// will never ++ herself
		
		
		if (winner.equals(asker)) {
			event.getTextChannel().sendMessage("You have earned my favor today, " + winner + ".  " + winner + "++");
		} else { 
			event.getTextChannel().sendMessage("Sorry, " + asker + ", but " + winner + " has earned my favor today.  " + winner + "++");
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
		return matches;
	}

}
