package Commands;

import java.util.LinkedList;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class UptimeCommand implements Command {
	
	public static final String COMMAND_TEXT = "uptime";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		event.getTextChannel().sendMessage("Frostwarden active since " + Main.startTime + "\n" + Main.parser.getProcCount() + " commands have been processed\n");
		
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
