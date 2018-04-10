package Commands;

import java.io.File;
import java.util.LinkedList;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ShitpostCommand implements Command {

	public static final String COMMAND_TEXT = "shitpost";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		event.getTextChannel().sendFile(new File("C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Discord Bot\\Auracord\\shitpost.gif"), null);
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
