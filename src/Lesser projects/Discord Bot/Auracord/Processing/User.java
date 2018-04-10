package Processing;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

public class User {

	// User objects allow for storing various information,
	// such as number of commands run,
	// a history of commands run,
	// what their "global ignore percent" chance is, if ever implemented.
	// Users are per GUILD. So if a user shares 2 guilds with Sorazae, and runs history in one, they will only see their history for that guild.
	
	String name;
	String guild;
	LinkedList<String> commandHistory;
	int numCommandsProcessed;
	
	private static int MAX_CMDS = 20; // max number of commands to keep in history
	private static int MAX_MESSAGE_SIZE = 2000; // max message size we can send to discord
	
	public User(String name, String guild) {
		this.name = name;
		this.guild = guild;
		numCommandsProcessed = 0;
		commandHistory = new LinkedList<String>();
	}
	
	public void addToHistory(String command) {
		numCommandsProcessed++;
		commandHistory.addFirst("Cmd #:" + numCommandsProcessed + " @" + getTime() + " - " + command + " |");
		
		if (commandHistory.size() > MAX_CMDS) {
			commandHistory.removeLast();
		}
	}
	
	public String getTime() {
		String time = "";
		
		Calendar cal = Calendar.getInstance(); // get a timestamp so we can have a unique file name
		String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		time += cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND); 
		if (cal.get(Calendar.AM_PM) == Calendar.AM) {
			time += "am";
		} else { // must be PM
			time += "pm";
		}
		time += " " + month + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.YEAR);
		
		return time;
	}
	
	
	public String getOutput() {
		String output = "";
		
		// put the header
		String header = name + " of " + guild + "'s history:\n# commands run: " + numCommandsProcessed + "\nLast 20 commands:\n";
		
		// we need to build the list in reverse, as in, we want to show the latest command at the very bottom (so, index 0 in the linkedlist),
		// but we need to be able to stop if / when we hit 2k characters
		String commands = "";
		int currCmd = 0;
		while (true) {
			
			// break if we either loop through all 20 commands / all entered commands so far and don't go over 2000 chars, or if we hit >2000 chars
			
			if (currCmd == MAX_CMDS || (currCmd == commandHistory.size())) {
				break;
			}
			if (commands.length() + commandHistory.get(currCmd).length() > MAX_MESSAGE_SIZE) {
				break;
			}
			
			if (commands.isEmpty()) {
				commands = commandHistory.get(currCmd);
			} else {
				commands = commandHistory.get(currCmd) + "\n" +  commands;
			}
			++currCmd;
			
		}
		
		if (commands.isEmpty()) { // user hasn't run any commands yet
			commands = "No commands run yet";
		}
		
		output = header + commands;
		
		return output;
	}
	
	public String getUserName() {
		return name;
	}
	
}
