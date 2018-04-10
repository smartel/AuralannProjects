package Processing;

import Commands.Command;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if ( event.getMessage().getContent().startsWith(Command.SORA_START) &&
			 event.getMessage().getAuthor().getId() != event.getJDA().getSelfInfo().getId() ) {

			// So, if some text starts with <> and isn't from the bot itself . . .
			// We gotta handle it!
			
			String text = event.getMessage().getContent();
			Main.logMessage("Potential command request: \"" + text + "\"");
			Main.handleCommand(Main.parser.parse(text, event));
			
		} else if ( event.getMessage().getContent().endsWith("++") || event.getMessage().getContent().endsWith("--") ) {

			// although we will let the bot send ++ and -- commands.
			
			String text = event.getMessage().getContent();
			Main.logMessage("Potential command request: \"" + text + "\"");
			Main.handleCommand(Main.parser.parse(text, event));
			
		}
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		// start message sent to all rooms she has permissions in
	
		int numChannels = event.getJDA().getTextChannels().size();
		for (int x = 0; x < numChannels; ++x) {
			
			
			String channelName = event.getJDA().getTextChannels().get(x).getName();
			boolean canWrite = event.getJDA().getTextChannels().get(x).checkPermission(event.getJDA().getUserById(event.getJDA().getSelfInfo().getId()),
																					   Permission.MESSAGE_WRITE);
			Main.logMessage("Channel: " + channelName + " - Write permissions?: " + canWrite);
			Main.logMessage("Guild: " + event.getJDA().getTextChannels().get(x).getGuild().getName() +
							"  Users: " + event.getJDA().getTextChannels().get(x).getUsers().toString());

			if (Main.sendStartupMessage && canWrite) {
				event.getJDA().getTextChannels().get(x).sendMessage("I am awakened! <>greet me, and see what I am capable of with <>help.");
			}
		}
		
		Main.logMessage("Logged in as: " + event.getJDA().getSelfInfo().getUsername());
	}
	
}
