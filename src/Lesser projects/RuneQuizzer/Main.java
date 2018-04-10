package RuneQuizzer;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		
		SettingsLoader sl = new SettingsLoader();
		Settings settings = sl.getSettings();
		String errors = sl.getErrors(); // float these to the gui to display to the user
		
		RuneQuizzer gui = new RuneQuizzer(settings, errors);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		gui.setResizable(false);
		//gui.pack(); // scrubs the rune out of existence.
	}
	
}
