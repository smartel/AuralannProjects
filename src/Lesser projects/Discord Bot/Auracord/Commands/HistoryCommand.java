package Commands;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import Processing.GuildUsers;
import Processing.Main;
import Processing.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class HistoryCommand implements Command {

	public static final String COMMAND_TEXT = "history";
	// ALT_TEXT: hist
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT + " {username}";
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // meh, we'll still output if args aren't provided, a list of valid names
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		String target = "";
		if (args.size() == 0) {
			String msg = "I need to know whose history to tell, " + event.getAuthorName() + ". The people I am familiar with are:\n";
			LinkedList<User> userList = new LinkedList<User>();

			HashMap<String, GuildUsers> history = Main.parser.getHistory();
			GuildUsers gu = history.get(event.getGuild().getName());
			for (Map.Entry<String, User> entry : gu.getUsers().entrySet()) {
				User currUser = entry.getValue();
				userList.add(currUser);
			}
			
			Collections.sort(userList, new Comparator<User>() {
				@Override
				public int compare(User l, User r) {
					return l.getUserName().compareTo(r.getUserName());
				}
			});
			
			for (User u : userList) {
				msg += u.getUserName() + "\n";
			}
			
			event.getTextChannel().sendMessage(msg);
			return;
		} else {
			// holy crap how do you put the name together if it's multiple spaces between words? pray.
			for (String arg : args) 
				target += arg + " ";
			target = target.trim();
		}
		
		// need the hashmap to pull the user object from
		HashMap<String, GuildUsers> history = Main.parser.getHistory();
		GuildUsers gu = history.get(event.getGuild().getName());
		
		// try to get the user, if a valid name was supplied. otherwise, display valid names
		
		if (!gu.getUsers().containsKey(target)) {
			String msg = "I am not familiar with " + target + ", " + event.getAuthorName() + ". The people I am familiar with are:\n";

			LinkedList<User> userList = new LinkedList<User>();

			for (Map.Entry<String, User> entry : gu.getUsers().entrySet()) {
				User currUser = entry.getValue();
				userList.add(currUser);
			}
			
			Collections.sort(userList, new Comparator<User>() {
				@Override
				public int compare(User l, User r) {
					return l.getUserName().compareTo(r.getUserName());
				}
			});
			
			for (User u : userList) {
				msg += u.getUserName() + "\n";
			}
			
			event.getTextChannel().sendMessage(msg);
			return;
		}
		
		User user = gu.getUsers().get(target);
		

		event.getTextChannel().sendMessage(user.getOutput());
		
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
		matches.add("hist");
		return matches;
	}
	
}
