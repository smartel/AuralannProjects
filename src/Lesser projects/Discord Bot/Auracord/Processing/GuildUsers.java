package Processing;

import java.util.HashMap;

public class GuildUsers {

	// A wrapper for a collection of Users

	private HashMap<String, User> users;
	
	public GuildUsers(HashMap<String, User> users) {
		this.users = users;
	}
	
	public HashMap<String, User> getUsers() {
		return users;
	}
}
