package RuneEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.LinkedList;

public class PixelListener implements ActionListener {

	private LinkedList<PixelButton> pixelButtons;
	private PixelButton button;
	
	public PixelListener(PixelButton button, LinkedList<PixelButton> pixelButtons) {	
		this.button = button;
		this.pixelButtons = pixelButtons;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean isAlt = (e.getModifiers() & InputEvent.ALT_MASK) != 0;
		if (isAlt) { // only enable!
			button.setToRuneColor(false);
		} else {
			button.swapColor();
		}
		
		int pixelIndex = button.getPixelIndex();
		if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) { // do a line of 3
			if (isAlt) { // only enable!
				pixelButtons.get(pixelIndex-1).setToRuneColor(false);
				pixelButtons.get(pixelIndex+1).setToRuneColor(false);
			} else { // go ahead and swap.
				pixelButtons.get(pixelIndex-1).swapColor();
				pixelButtons.get(pixelIndex+1).swapColor();
			}
		}
		if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) { // do the 3x3
			if (isAlt) { // only enable!
				pixelButtons.get(pixelIndex-1).setToRuneColor(false);
				pixelButtons.get(pixelIndex+1).setToRuneColor(false);
				
				pixelButtons.get(pixelIndex-1-RuneEditor.RUNE_DIMEN).setToRuneColor(false);
				pixelButtons.get(pixelIndex-RuneEditor.RUNE_DIMEN).setToRuneColor(false);
				pixelButtons.get(pixelIndex+1-RuneEditor.RUNE_DIMEN).setToRuneColor(false);
				
				pixelButtons.get(pixelIndex-1+RuneEditor.RUNE_DIMEN).setToRuneColor(false);
				pixelButtons.get(pixelIndex+RuneEditor.RUNE_DIMEN).setToRuneColor(false);
				pixelButtons.get(pixelIndex+1+RuneEditor.RUNE_DIMEN).setToRuneColor(false);
			} else { // go ahead and swap.
				pixelButtons.get(pixelIndex-1).swapColor();
				pixelButtons.get(pixelIndex+1).swapColor();
				
				pixelButtons.get(pixelIndex-1-RuneEditor.RUNE_DIMEN).swapColor();
				pixelButtons.get(pixelIndex-RuneEditor.RUNE_DIMEN).swapColor();
				pixelButtons.get(pixelIndex+1-RuneEditor.RUNE_DIMEN).swapColor();
				
				pixelButtons.get(pixelIndex-1+RuneEditor.RUNE_DIMEN).swapColor();
				pixelButtons.get(pixelIndex+RuneEditor.RUNE_DIMEN).swapColor();
				pixelButtons.get(pixelIndex+1+RuneEditor.RUNE_DIMEN).swapColor();
			}
		}
	}
}