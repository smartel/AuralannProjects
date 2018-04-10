package Processing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

import Commands.Command;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class CommandParser {
	
	private int numProcessed = 0;
	private HashMap<String, GuildUsers> guildHistories = new HashMap<String, GuildUsers>();
	
	public CommandBean parse(String raw, MessageReceivedEvent event) {

		// Intercept and possibly edit the command string
		String mod = scoreIntercept(raw.trim());
		
		
		// TODO - if we ever decide to implement it:
		// check the global ignore chance - if non-zero, determine if we want to execute the command or not.
		// if we ignore the command, we'll replace the cmd with an IgnoreCommand, using the original text as args for logging purposes.
		
		
		// Regular operations:
		
		String trimmedMod = mod.replaceFirst(Command.SORA_START, "");
		
		LinkedList<String> tokenizedTrim = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(trimmedMod);
		while (st.hasMoreTokens()) {
			tokenizedTrim.add(st.nextToken());
		}
		String cmdText = tokenizedTrim.get(0);
		tokenizedTrim.removeFirst();
		
		// Keep a history of command requests:
		// Save whatever text they gave us to work with here.
		// We'll also increment the processed counter here too, although it's a bit premature.
		incrementProcCount();
		String guildName = event.getGuild().getName();
		String userName = event.getAuthorName();
		if (guildHistories.containsKey(guildName)) {
			GuildUsers gu = guildHistories.get(guildName);
			
			// find the user, so we can add to their history
			User user;
			if (!gu.getUsers().containsKey(userName)) {
				// if they aren't in the gu, then maybe they recently joined the channel - need to create and add them to the collection
				user = new User(userName, guildName);
				gu.getUsers().put(userName, user);
			}
			user = gu.getUsers().get(userName);
			user.addToHistory(raw);
		} else {
			HashMap<String, User> users = new HashMap<String, User>();
			
			// create an entry for everyone in the channel
			for (net.dv8tion.jda.entities.User user : event.getGuild().getUsers()) {
				String newUserName = user.getUsername();
				users.put(newUserName, new User(newUserName, guildName));
			}
			GuildUsers gu = new GuildUsers(users);
			guildHistories.put(guildName, gu);
			User user;
			if (!gu.getUsers().containsKey(userName)) {
				// if they aren't in the gu, then maybe they recently joined the channel - need to create and add them to the collection
				user = new User(userName, guildName);
				gu.getUsers().put(userName, user);
			}
			user = gu.getUsers().get(userName);
			user.addToHistory(raw);
		}
		
		return new CommandBean(cmdText, tokenizedTrim, event);
	}
	
	private String scoreIntercept(String raw) {
		// SCORE INTERCEPT:
		// Before we process the command, see if there is a trailing ++ or -- at the end.
		// If there is, we will REWRITE THE INPUT TEXT in the format that our bot can process it in.
		// ie: given "Sorazae, you're the best!  Sorazae++", it will be rewritten to:
		// "<>list ++ Sorazae"
		// So, we put the SORA_START token, call list command, a ++ (or --) argument, then the text to award/revoke a point from.

		String modified = raw;
		
		if (raw.endsWith("++") || raw.endsWith("--")) {
			
			// get the token attached to that score command
			int lastSpace = raw.lastIndexOf(" ");
			// perhaps the trimmed line was simply "Sorazae++", so finding the index of " " would return -1.
			if (lastSpace < 0) {
				lastSpace = 0;
			}
						
			String target = raw.substring(lastSpace);
			
			// determine how many pluses / minuses there were, as in, was it +++ (+2), ---- (-3), ...
			int numSigns = 0;
			for (int x = target.length(); x > 0; --x) {
				if (target.endsWith("++") && target.substring(x-1, x).equalsIgnoreCase("+")) {
					numSigns++;
				} else if (target.endsWith("--") && target.substring(x-1, x).equalsIgnoreCase("-")) {
					numSigns++;
				} else {
					x = -1; // breaking hard
				}				
			}
			// the value we want is the total count of signs, minus 1 (since ++ is 2 signs, but only increments by 1)
			int changeValue = numSigns - 1;
			// if it was ++, leave it as it is (positive value). if it was --, then negate the value
			if (target.endsWith("--")) {
				changeValue = 0 - changeValue;
			}
			
			target = target.replace("+", ""); // replace ALL +'s, no matter how many
			target = target.replace("-", ""); // "" -'s
			
			// generate the new command
			modified = Command.SORA_START + "list ";
			modified += raw.endsWith("++") ? "++" : "--";
			modified += " " + changeValue + " " + target;
		}
		
		return modified;
	}
	
	public void incrementProcCount() {
		numProcessed++;
	}
	
	public int getProcCount() {
		return numProcessed;
	}
	
	public HashMap<String, GuildUsers> getHistory() {
		return guildHistories;
	}

}
