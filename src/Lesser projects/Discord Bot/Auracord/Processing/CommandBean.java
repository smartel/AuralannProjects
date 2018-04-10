package Processing;

import java.util.LinkedList;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class CommandBean {
	private String cmdText; // the command, ie: "hail" or "lexify"
	private LinkedList<String> args; // the args for the command, like "I am text to lexify"
	private MessageReceivedEvent event;
	
	public CommandBean(String cmdText, LinkedList<String> args, MessageReceivedEvent event) {
		this.cmdText = cmdText.toLowerCase();
		this.args = args;
		this.event = event;
	}
	
	public String getArgsToString() {
		String argStr = "";
		for (int x = 0; x < args.size(); ++x) {
			argStr += args.get(x);
			if (x < (args.size()-1)) { // if there's at least 1 more value, we need a comma to separate em
				argStr += ", ";
			}
		}
		return "[" + argStr + "]";
	}
	
	public String               getCmdText() { return cmdText; }
	public LinkedList<String>   getArgs()    { return args;    }
	public MessageReceivedEvent getEvent()   { return event;   }
}
