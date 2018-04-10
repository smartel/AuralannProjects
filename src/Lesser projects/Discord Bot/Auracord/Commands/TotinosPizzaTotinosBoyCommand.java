package Commands;

import java.io.File;
import java.util.LinkedList;


import Processing.Main;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.managers.AudioManager;

public class TotinosPizzaTotinosBoyCommand implements Command { // I am... Pizza Totino's Boy

	public static final String COMMAND_TEXT = "ptb";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	boolean isOdd = true;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		VoiceChannel vc = null;
		AudioManager am = null;
		File af = null;
		FilePlayer fp = null;
		
		try {
			vc = findChannel(event);
			if (vc != null) {
				
				am = event.getJDA().getAudioManager(event.getGuild());
				if (am.isConnected()) {
					//if (am.isAttemptingToConnect()) {
						am.closeAudioConnection();
					//}
				}
				am.openAudioConnection(vc);
				af = new File("C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Discord Bot\\Auracord\\ptb.mp3");
				fp = new FilePlayer(af);
				am.setSendingHandler(fp);
				fp.stop();
				//am.setConnectTimeout(100L);
				fp.play();
				
				//while (fp.isPlaying()) {
				//}
				am.closeAudioConnection();
			} else {
				Main.logMessage("No voice channel found for author: " + event.getAuthorName());
			}
				
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
