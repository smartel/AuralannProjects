package Text2Auralann;

import java.util.LinkedList;

public class MainListener {

	// This is an example version of the lexiconifier that "waits" for a command, and then when it receives one, processes it and returns a message.
	// Example commands are things like lexifying text requests or changing / toggling settings.
	// This is currently being written for a Discord bot, and will have its own DiscordSettings file.
	// A listener's settings file is slightly different, in that it needs to include more paths, so someone in the chatroom can change between
	// any of the dictionaries at will.
	
	// You do not need to instantiate a MainListener() - in your class, just create a Lexiconifier and call runListenerMode() to initialize it,
	// and then you can send commands with processRequest(), with a string command and a list of args, which returns a string response.
	// Every request will get a response, whether it is an image filepath, a confirmation of executing a command, or an error message.

	// Example requests are:
	// lexify text here  (always returns an image filepath). Lexify is the command, "text here" are the args.
	// set setting_name value (basic and advanced)
	// set dict basic - change directory command, setting it to the basic dictionary. Other dictionaries are: expand, basichl, expandhl (hl = hollowless)
	
	public MainListener() {}
	
	public static void main(String[] args) {
		LexiconifierListener lexi = new LexiconifierListener();
		
		// Give the path to the Listener settings file.
		// Note: The Listener Settings file is different in the following ways:
		// When text is lexified, it always returns an image file path. The text form is never returned.
		// Thus, the following settings serve no purpose for the listener and are not in the Settings file.
		//       DISPLAY_STYLE
		//       TEXT_LINE_LENGTH
		// Additional settings have been added to the file:
		//       BASIC_DICT_LOCATION
		//       BASIC_DICT_HL_LOCATION
		//       EXPAND_DICT_LOCATION
		//       EXPAND_DICT_HL_LOCATION
		// Whichever dictionary the setting RUNE_DICTIONARY_LOCATION is pointing to, will be the default dictionary the program uses.
		// We're not gonna allow the english dictionary or the futhark dictionary. Clashes too much with existing settings and not appropriate for the bot's intent.
		
		String settingsPath = "C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\ListenerSettings-Discord.txt";
		
		boolean didInitSuccessfully = lexi.runListenerMode(settingsPath);
		if (didInitSuccessfully) {
			// can now send and process commands
			String command = "lexify";
			LinkedList<String> cmdArgs = new LinkedList<String>();
			cmdArgs.add("example"); cmdArgs.add("text.");
			String response = lexi.processRequest(command, cmdArgs);
			System.out.println(response);
		}
		
	}
	
	
	
}
