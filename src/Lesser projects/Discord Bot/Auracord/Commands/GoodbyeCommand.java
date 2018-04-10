package Commands;

import java.util.LinkedList;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class GoodbyeCommand implements Command {

	public static final String COMMAND_TEXT = "goodbye";
	//ALT_TEXT: bye
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	private static boolean isOdd = true; // alternates between general and specific goodbye message
	
	// TODO perhaps we should just have it pick from a pool at random?
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		if (isOdd) {
			String name = event.getAuthorName();
			event.getTextChannel().sendMessage("Goodbye, " + name + ".");
		} else {
			event.getTextChannel().sendMessage("Stay safe, young one.");
		}
		isOdd = !isOdd;
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
		matches.add("bye");
		return matches;
	}
}
