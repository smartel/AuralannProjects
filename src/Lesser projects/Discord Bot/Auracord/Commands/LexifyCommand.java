package Commands;

import java.io.File;
import java.util.LinkedList;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class LexifyCommand implements Command {

	public static final String COMMAND_TEXT = "lexify";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT + " {text_to_lexify}";
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		if (args.size() == 0) {
			event.getTextChannel().sendMessage("I need text to work with, child.");
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		String name = event.getAuthorName();
		event.getTextChannel().sendMessage("A moment, " + name + ".");
		
		String response = Main.lexi.processRequest(COMMAND_TEXT, args);
		event.getTextChannel().sendFile(new File(response), null);
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
		matches.add("lex");
		return matches;
	}
}
