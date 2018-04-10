package Processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import Secret.Secrets;
import Text2Auralann.LexiconifierListener;
import Commands.AuraCommand;
import Commands.BadKharCommand;
import Commands.CoinflipCommand;
import Commands.Command;
import Commands.EightBallCommand;
import Commands.EnemyCommand;
import Commands.FriendCommand;
import Commands.GoodMorningCommand;
import Commands.GoodbyeCommand;
import Commands.GoodnightCommand;
import Commands.GreetCommand;
import Commands.HistoryCommand;
import Commands.MoshiCommand;
import Commands.PizzaCommand;
import Commands.PlsAdviseCommand;
import Commands.PraiseCommand;
import Commands.QueryCommand;
import Commands.HelpCommand;
import Commands.LexifyCommand;
import Commands.RollCommand;
import Commands.ScaleCommand;
import Commands.ScoldCommand;
import Commands.ScoreCommand;
import Commands.SettingCommand;
import Commands.ShitpostCommand;
import Commands.SigilCommand;
import Commands.SpaceCommand;
import Commands.TotinoTPBCommand;
import Commands.TotinosGetOutCommand;
import Commands.TotinosLifestyleCommand;
import Commands.TotinosPizzaTotinosBoyCommand;
import Commands.TotinosSongCommand;
import Commands.UptimeCommand;
import Commands.WarCommand;
import Commands.WhatCommand;
import Commands.WhoCommand;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

public class Main {

	// SORAZAE RELEASES
	// Version 2.5 - Totino's Pizza Rolls
	// Version 2.4 - Dragon's Crown, Eorzean dictionaries added. Moshi secret command added.
	// Version 2.3 - Changed sigil command to alternate with my new magic circle
	// Version 2.2 - War can now take in a number of rounds argument. New synonyms. Uptime now correctly returns the start time.
	// Version 2.1 - History command, better handling of "synonyms", and small improvements to commands, like <>uptime
	// Version 2.0 - Katakana dictionary in! Small tweaks.
	// Version 1.9 - More commands: ++/--, scale, 8ball, uptime, friend/enemy, praise/scold, and some secrets / tweaks. War / Scoreboards now guild based.
	// Version 1.8 - WarCommand sorts alphabetically at roll time and when displaying stats, and minor text changes
	// Version 1.7 - Added tons more fun commands, including: <>war, <>roll, <>coinflip, <>who/what and <>query.
	// Version 1.6 - Added <>spaces and <>auras after giving all non-lann'lain dictionaries some aura runes to work with.
	// Version 1.5 - more new dicts: templar, masonic, longbranch, shorttwig, medieval, dalecarlian
	// Version 1.4 - '<>set dict halsinge' and '<>set dict braille' implemented. Added alt and shortcuts commands like 'lex' for lexify
	// Version 1.3 - '<>set dict futhorc' implemented
	// Version 1.2 - Added <>set show, to show the current values for all settings.
	// Version 1.1 - Added functionality for using the English and Futhark dictionaries, and automatically changing some settings when using them.
	// Version 1.0 - Initial release
	
	public static JDA jda;
	public static CommandParser parser;
	public static HashMap<String, Command> commands;
	public static BufferedWriter bw;
	public static LexiconifierListener lexi;
	
	public static boolean sendStartupMessage = false; // Toggle this to send out a shout when the bot turns on !!!!!!!!!!
	public static String startTime;
	
	public static void main(String[] args) {
		try {
			jda = new JDABuilder().addListener(new BotListener())
									.setBotToken(Secrets.TOKEN_ID)
									.buildBlocking();
			jda.setAutoReconnect(true);
		} catch(Exception e) {
			System.out.println("Exception caught: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Set start-time for calling <>uptime

		startTime = "";
		Calendar cal = Calendar.getInstance(); // get a timestamp so we can have a unique file name
		String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
		startTime += cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND); 
		if (cal.get(Calendar.AM_PM) == Calendar.AM) {
			startTime += "am";
		} else { // must be PM
			startTime += "pm";
		}
		startTime += " " + month + " " + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.YEAR);

		
		// Create the list of commands
		parser = new CommandParser();
		commands = new HashMap<String, Command>();

		LinkedList<String> greets = new GreetCommand().getMatches();
		for (String cmd : greets)
			commands.put(cmd, new GreetCommand());
		
		LinkedList<String> byes = new GoodbyeCommand().getMatches();
		for (String cmd : byes)
			commands.put(cmd, new GoodbyeCommand());
		
		LinkedList<String> nights = new GoodnightCommand().getMatches();
		for (String cmd : nights)
			commands.put(cmd, new GoodnightCommand());
		
		LinkedList<String> mornings = new GoodMorningCommand().getMatches();
		for (String cmd : mornings)
			commands.put(cmd, new GoodMorningCommand());
		
		LinkedList<String> lexs = new LexifyCommand().getMatches();
		for (String cmd : lexs)
			commands.put(cmd, new LexifyCommand());
		
		LinkedList<String> helps = new HelpCommand().getMatches();
		for (String cmd : helps)
			commands.put(cmd, new HelpCommand());

		LinkedList<String> ups = new UptimeCommand().getMatches();
		for (String cmd : ups)
			commands.put(cmd, new UptimeCommand());
		
		LinkedList<String> sigils = new SigilCommand().getMatches();
		for (String cmd : sigils)
			commands.put(cmd, new SigilCommand());
		
		LinkedList<String> sets = new SettingCommand().getMatches();
		for (String cmd : sets)
			commands.put(cmd, new SettingCommand());
		
		LinkedList<String> spaces = new SpaceCommand().getMatches();
		for (String cmd : spaces)
			commands.put(cmd, new SpaceCommand());
		
		LinkedList<String> auras = new AuraCommand().getMatches();
		for (String cmd : auras)
			commands.put(cmd, new AuraCommand());
		
		LinkedList<String> flips = new CoinflipCommand().getMatches();
		for (String cmd : flips)
			commands.put(cmd, new CoinflipCommand());
		
		LinkedList<String> queries = new QueryCommand().getMatches();
		for (String cmd : queries)
			commands.put(cmd, new QueryCommand());
		
		LinkedList<String> rolls = new RollCommand().getMatches();
		for (String cmd : rolls)
			commands.put(cmd, new RollCommand());
		
		LinkedList<String> wars = new WarCommand().getMatches();
		for (String cmd : wars)
			commands.put(cmd, new WarCommand());

		LinkedList<String> friends = new FriendCommand().getMatches();
		for (String cmd : friends)
			commands.put(cmd, new FriendCommand());
		
		LinkedList<String> enemies = new EnemyCommand().getMatches();
		for (String cmd : enemies)
			commands.put(cmd, new EnemyCommand());
		
		LinkedList<String> praises = new PraiseCommand().getMatches();
		for (String cmd : praises)
			commands.put(cmd, new PraiseCommand());
		
		LinkedList<String> scolds = new ScoldCommand().getMatches();
		for (String cmd : scolds)
			commands.put(cmd, new ScoldCommand());

		LinkedList<String> scores = new ScoreCommand().getMatches();
		for (String cmd : scores)
			commands.put(cmd, new ScoreCommand());
		
		LinkedList<String> eights = new EightBallCommand().getMatches();
		for (String cmd : eights)
			commands.put(cmd, new EightBallCommand());
		
		LinkedList<String> scales = new ScaleCommand().getMatches();
		for (String cmd : scales)
			commands.put(cmd, new ScaleCommand());
		
		LinkedList<String> whos = new WhoCommand().getMatches();
		for (String cmd : whos)
			commands.put(cmd, new WhoCommand());
		
		LinkedList<String> whats = new WhatCommand().getMatches();
		for (String cmd : whats)
			commands.put(cmd, new WhatCommand());

		LinkedList<String> histories = new HistoryCommand().getMatches();
		for (String cmd : histories)
			commands.put(cmd, new HistoryCommand());
		
		// Secret commands
		
		LinkedList<String> plss = new PlsAdviseCommand().getMatches();
		for (String cmd : plss)
			commands.put(cmd, new PlsAdviseCommand());
		
		LinkedList<String> pizzas = new PizzaCommand().getMatches();
		for (String cmd : pizzas)
			commands.put(cmd, new PizzaCommand());

		LinkedList<String> khars = new BadKharCommand().getMatches();
		for (String cmd : khars)
			commands.put(cmd, new BadKharCommand());
		
		LinkedList<String> posts = new ShitpostCommand().getMatches();
		for (String cmd : posts)
			commands.put(cmd, new ShitpostCommand());

		LinkedList<String> moshi = new MoshiCommand().getMatches();
		for (String cmd : moshi)
			commands.put(cmd, new MoshiCommand());

		LinkedList<String> ttpb = new TotinoTPBCommand().getMatches();
		for (String cmd : ttpb)
			commands.put(cmd, new TotinoTPBCommand());

		LinkedList<String> pbt = new TotinosPizzaTotinosBoyCommand().getMatches();
		for (String cmd : pbt)
			commands.put(cmd, new TotinosPizzaTotinosBoyCommand());

		LinkedList<String> lifestyle = new TotinosLifestyleCommand().getMatches();
		for (String cmd : lifestyle)
			commands.put(cmd, new TotinosLifestyleCommand());

		LinkedList<String> song = new TotinosSongCommand().getMatches();
		for (String cmd : song)
			commands.put(cmd, new TotinosSongCommand());

		LinkedList<String> getout = new TotinosGetOutCommand().getMatches();
		for (String cmd : getout)
			commands.put(cmd, new TotinosGetOutCommand());
		
		// TODO how do we run HELP on individual commands? :Y
		
		lexi = new LexiconifierListener();
		boolean isInit = lexi.runListenerMode(Secrets.LEXI_SETTING_PATH);
		if (isInit) {
			logMessage("Lexiconifier successfully initialized.");
		} else {
			logMessage("Lexiconifier failed to initialized! Lexify command will not function!");
		}
	}
	
	public static void handleCommand(CommandBean cmdBean) {
		if (commands.containsKey(cmdBean.getCmdText())) {
			
			boolean doExecute = commands.get(cmdBean.getCmdText()).called(cmdBean.getArgs(), cmdBean.getEvent());
			
			if(doExecute) {
				commands.get(cmdBean.getCmdText()).executeCommand(cmdBean.getArgs(), cmdBean.getEvent());
				commands.get(cmdBean.getCmdText()).postExecute(doExecute, cmdBean.getEvent(), cmdBean.getArgsToString());
			} else {
				commands.get(cmdBean.getCmdText()).postExecute(doExecute, cmdBean.getEvent(), cmdBean.getArgsToString());
			}
		}
	}
	
	public static void logMessage(String message) {
		try {
			String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			message = timestamp + " | " + message;
			System.out.println(message);
			bw = new BufferedWriter(new FileWriter(new File("soralog.txt"), true));
			bw.write(message);
			bw.newLine();
			bw.close();
		} catch (Exception e) {
			// just gonna have to swallow it and let logging fail. Don't want to stop the bot for this, nor spam System.out with stacktraces.
			System.out.println("Logging has an exception: " + e.getMessage());
		}
	}
}
