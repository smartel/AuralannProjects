package Commands;

import java.util.LinkedList;

import net.dv8tion.jda.events.message.MessageReceivedEvent;
import Processing.Main;

public class SettingCommand implements Command {

	public static final String COMMAND_TEXT = "set";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT + " {lexiconifier setting name} {value}";
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		if (args.size() == 0) {
			event.getTextChannel().sendMessage("I need a setting and a value to work with, child.");
			return false;
		} else if (args.size() == 1) {
			
			// hardcoding one special case - if you want to list all settings, you only call "SHOW", no args / "value" after that.
			if (args.get(0).equalsIgnoreCase("SHOW")) {
				return true;
			}		
			event.getTextChannel().sendMessage("I need a setting and a value to work with, child.");
			return false;
		} else if (args.size() == 2) {
			return true;
		} else {
			event.getTextChannel().sendMessage("Child... what are you trying to do?");
			return false;
		}
		// TODO ability to change multiple settings at once does not exist yet.
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
		return matches;
	}
}
