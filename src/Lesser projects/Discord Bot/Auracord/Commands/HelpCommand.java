package Commands;

import java.util.LinkedList;

import Processing.Main;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class HelpCommand implements Command {
	
	public static final String COMMAND_TEXT = "help";
	private final String HELP = "Usage: " + SORA_START + COMMAND_TEXT;
	
	@Override
	public boolean called(LinkedList<String> args, MessageReceivedEvent event) {
		return true; // always true, command doesn't require args
	}

	@Override
	public void executeCommand(LinkedList<String> args, MessageReceivedEvent event) {
		String name = event.getAuthorName();
		event.getTextChannel().sendMessage("[Help] Take heed, " + name + ". My abilities are as follows:\n\n" +
										   
										   // Talking
										   "Talk:\n" + 
										   "<>morning, <>hello - say goodmorning / greet the Frostwarden\n" +
										   "<>night, <>bye - say goodnight / bye to the Frostwarden\n\n" +
										   
										   // Fun
										   "Fun:\n" +
										   "<>coinflip - flips a coin\n" +
										   "<>query - answers a yes/no question\n" +
										   "<>8ball - a verbose version of <>query\n" +
										   "<>roll {number} - given a number, rolls a number-sided die (default: 100)\n" +
										   "<>scale {min} {max} - generates a number in the given range (default: 1 to 10)\n" +
										   "<>pvp, <>pvp {num} - plays War with everyone rolling a d20 for num rounds\n" +
										   "<>pvp stats - displays the stats for the current war game\n" +
										   "<>pvp reset - resets the score for war\n" +
										   "<>who {text} - Answers the question by picking a room participant\n" +
										   "      example: who is the best?! \n" +
										   "<>who {text} {?|,} {X} or {Y} or ... N - Answers the 'who' question with one of the given options\n" +
										   "      example: <>who would win in a fight? Sarnys or Aranor?\n" + // deep ones or the lost bel'lains!
										   "<>what {text} {?|,} {X} or {Y} or ... N - Answers the 'what' question with one of the given options\n" +
										   "      example: <>what is better, staying up all night or sleeping all day?\n" +
										   "Trailing {text}++ or {text}-- : grants or removes a point from text's score\n" +
										   "<>score - lists all ++/-- point scores\n" +
										   "<>score reset - resets all ++/-- point scores\n" +
										   "<>score add {text} {number} - adds the target to the scoreboard with the given score, or replaces if already exists\n" +
										   "<>score remove {text} - removes the target from the scoreboard\n" +
										   "<>praise , <>scold - praises or scolds Sorazae, changing her score\n" +
										   "<>friend , <>roulette - Sorazae picks a random user and changes their score\n" +
										   "<>uptime - shows how long Sorazae has been up\n" +
										   "<>history {user} - shows user's history\n\n" +
										   // <>plsadvise is a secret command!
										   // <>pizza is a secret command!
										   // <>shitpost is a secret command!
										   // <>badkhar is a secret command!
										   
										   // Runes
										   "Runes:\n" +
										   "<>show_sigil - shows Porv's rune\n" +
										   "<>lexify {text} , <>lex {text} - converts text using the currently loaded dictionary\n" +
										   "<>set {lexiconifier setting name} {value}\n" +
										   "<>spaces {ON | OFF} is a shortcut to quickly toggle spaces between words\n" +
										   "<>auras {ON | OFF} is a shortcut to quickly toggle auras\n\n" +
										   "For help with any commands, see: http://www.lannlaic.com/sorazaeguide.html"); // if you add more, don't forget the \n !!
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
