package RuneEditor;

import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		
		RuneEditor gui = new RuneEditor();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		//gui.pack(); would rather have square pixelbuttons, no packing allowed
	}
	
}
