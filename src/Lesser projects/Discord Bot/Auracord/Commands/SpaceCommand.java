package Commands;

import java.util.LinkedList;

import net.dv8tion.jda.events.message.MessageReceivedEvent;
import Processing.Main;

public class SpaceCommand implements Command {

	public static final String COMMAND_TEXT = "spaces";
	// ALT_TEXT: space
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT + " {ON | OFF}";
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		if (args.size() == 0) {
			event.getTextChannel().sendMessage("I need to know whether to turn spaces On or Off, young one.");
			return false;
		} else if (args.size() == 1) {
			
			if (args.get(0).equalsIgnoreCase("ON") || args.get(0).equalsIgnoreCase("OFF")) {
				return true;
			} else {
				event.getTextChannel().sendMessage("\"On\" or \"Off\", child.");
				return false;
			}
			
		} else {
			event.getTextChannel().sendMessage("Young one... what are you trying to do?");
			return false;
		}
	}

	
	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		String name = event.getAuthorName();
		event.getTextChannel().sendMessage("As you wish, " + name + ".");

		String response = Main.lexi.processRequest(COMMAND_TEXT, args);
		event.getTextChannel().sendMessage(response);
		
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
		matches.add("space");
		return matches;
	}
}
