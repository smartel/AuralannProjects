package Commands;

import java.util.LinkedList;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public interface Command {
	public static final String SORA_START = "<>"; // what all of this bot's commands will start with

	public String help();
	public boolean called(LinkedList<String> args, MessageReceivedEvent event);
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event);
	public void postExecute(boolean success, MessageReceivedEvent event, String args);
	public LinkedList<String> getMatches();
}
