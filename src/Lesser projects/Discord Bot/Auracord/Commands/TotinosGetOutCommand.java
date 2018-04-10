
package Commands;

import java.util.LinkedList;


import Processing.Main;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.managers.AudioManager;

public class TotinosGetOutCommand implements Command { // Makes Sorazae leave the voice channel, since she likes to hang around a lot. {

	public static final String COMMAND_TEXT = "getout";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	boolean isOdd = true;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		AudioManager am = null;
		
		try {

			am = event.getJDA().getAudioManager(event.getGuild());
			//if (am.isConnected()) {
				am.closeAudioConnection();
			//}
				
		} catch (Exception e) {
			Main.logMessage(e.toString());
			am.closeAudioConnection();
		}
	}
	
	public VoiceChannel findChannel(MessageReceivedEvent event) {
		VoiceChannel vc = null;
		for (VoiceChannel channels : event.getGuild().getVoiceChannels()) {
			for (User user : channels.getUsers()) {
				if (user.getId().equals(event.getAuthor().getId())) {
					vc = channels;
					break;
				}
			}
		}
		
		return vc;
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
