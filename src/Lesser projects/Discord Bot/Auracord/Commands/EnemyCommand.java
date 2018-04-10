package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class EnemyCommand implements Command {

	public static final String COMMAND_TEXT = "enemy";
	//ALT_TEXT: roulette, rr
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
		// will never -- herself
		
		//if (winner.equals(asker)) {
		//	String msg = "Don't ask questions you don't want to hear the answer to, " + asker;
		//} else {
			// I know you can be better than this? Try harder? Lost favor? Disappointed me?
			event.getTextChannel().sendMessage("I'm sorry, " + winner + ".  " + winner + "--");
		//}

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
		matches.add("rr");
		matches.add("roulette");
		return matches;
	}

}
