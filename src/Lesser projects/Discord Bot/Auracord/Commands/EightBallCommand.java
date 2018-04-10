package Commands;

import java.util.LinkedList;
import java.util.Random;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class EightBallCommand implements Command {

	public static final String COMMAND_TEXT = "8ball";
	// ALT_TEXT: eightball
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	private Random random;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		
		int value;

		if (random == null) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
		}
		
		String msg = "";
		value = random.nextInt(25)+1;
		
		switch (value) {
			// Yes - 11 hits, 11 total
			case  1: msg = "Yes, definitely.";
					 break;
			case  2: msg = "Of course.";
					 break;
			case  3: msg = "For sure.";
					 break;
			case  4: msg = "It is certain.";
					 break;
			case  5: msg = "Seems likely.";
					 break;
			case  6: msg = "You can count on it.";
					 break;
			case  7: msg = "Rely on it.";
					 break;
			case  8: msg = "Without a doubt.";
					 break;
			case  9: msg = "Signs point to yes.";
					 break;
			case 10: msg = "It is decidedly so.";
					 break;
			case 11: msg = "You may rely on it.";
					 break;
			
			// Maybe - 4, 15 total
			case 12: msg = "Absolutely. Probably.";
			 		 break;
			case 13: msg = "50% of the time, every time.";
		   			 break;
			case 14: msg = "Maybe try a /coinflip?";
			 		 break;
			case 15: msg = "I suppose it's possible.";
			 		 break;
   
   			// No: - 5, 20 total
			case 16: msg = "Absolutely not!";
	 		 		 break;
			case 17: msg = "As I see it, no.";
  			 		 break;
			case 18: msg = "Very doubtful.";
	 		 		 break;
			case 19: msg = "Don't count on it.";
	 		 		 break;
			case 20: msg = "You're sure to be disappointed.";
					 break;
	 
   			// Other: 5, 25 total
			case 21: msg = "I'd better not tell you.";
	 		 		 break;
			case 22: msg = "Are you sure you want to know?";
		 		 	 break;
			case 23: msg = "That's not an easy question to answer.";
	 		 		 break;
			case 24: msg = "Well, you see...";
	 		 		 break;
			default: msg = "I am blind in these matters..."; // aka 25
			 		 break;
		}

		event.getTextChannel().sendMessage(msg);
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
		matches.add("eightball");
		return matches;
	}
}
