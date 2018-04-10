package RuneEditor;

import java.awt.Color;

import javax.swing.JButton;

public class PixelButton extends JButton {
	
	public static Color bgColor = new Color(255,255,255);
	public static Color runeColor = new Color(0,0,0);
	private Color color;
	private Color overrideColor;
	private boolean isBg;
	private boolean isChangeable;
	int pixelIndex; // index of the pixelbutton in the collection of all pixelbuttons
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3076487521202197531L;

	public PixelButton(int pixelIndex) {
		this.pixelIndex = pixelIndex;
		color = new Color(0,255,0); // green colors in the gui mean it was never set. we should never see it.
		overrideColor = null;
		isBg = true;
		isChangeable = true;
		setBackground(color);
	}
	
	public void swapColor() {
		if (isChangeable) {
			if (isBg) {
				color = runeColor;
				setBackground(color);
			} else {
				color = bgColor;
				setBackground(color);
			}
			isBg = !isBg;
		}
	}
	
	// imagine swapColor, but if it could only turn a square ON.
	// For use with filling multiple squares at once with ctrl / shift
	public void setToRuneColor(boolean override) {
		if (isBg && isChangeable) {
			color = runeColor;
			setBackground(color);
			isBg = !isBg;
		} else if (override && isBg) {
			color = runeColor;
			setBackground(color);
			isBg = !isBg;
		}
	}
	
	public void setToBgColor(boolean override) {
		if (!isBg && isChangeable) {
			color = bgColor;
			setBackground(color);
			isBg = !isBg;
		} else if (override && !isBg) {
			color = overrideColor;
			setBackground(color);
			isBg = !isBg;
		}
	}
	
	public void lockColor() {
		isChangeable = false;
	}
	
	public boolean getIsBg() {
		return isBg; // true if background color, false if rune color
	}
	
	public int getPixelIndex() {
		return pixelIndex;
	}
	
	// if we set this as a blue or red button (part of the side ruler), then we are using a new bgColor instead of the static one.
	// additionally, if the user imports a rune, it will destroy the ruler temporarily. This is so we can rebuild it on a nuke.
	public void overrideBgColor(Color bgColor) {
		overrideColor = bgColor;
		setBackground(overrideColor);
	}
}