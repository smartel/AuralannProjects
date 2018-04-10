package Text2Auralann;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Videre extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 5277583206940272619L;
	
	private Settings settings;
	private int finalWidth;
	private int finalHeight;
	
	public static final int HORIZONTAL_AURA_HEIGHT = 16;
	public static final int RUNE_HEIGHT = 50; // height of an individual (non-horizontal) rune 
	public static final int STAFF_HEIGHT = (HORIZONTAL_AURA_HEIGHT*4) + RUNE_HEIGHT; // height of a staff - x4 for the 4 aura lines, x1 for the rune line
	public static final int RUNE_WIDTH = 50; // width of an individual rune
	
	// Not sure what is up with these.. is it the border and toolbar interfering with the window size? had to add these to the setSize call, just once...
	public static final int WIDTH_OFFSET = 16;
	public static final int HEIGHT_OFFSET = 38;
	
	// LinkedList of LinkedList of String, because the outer linkedlist is for each "ray" (ie, 5 lines, 4 of auras and 1 of runes),
	// and the inner one is the filenames
	public Videre(Settings settings, LinkedList<LinkedList<String>> filepaths, int numRunes, int numRows) {
		this.settings = settings;
		setTitle("Videre Rune Viewer");
		
		finalWidth = numRunes*RUNE_WIDTH + WIDTH_OFFSET;		
		
		if (numRunes <= 2) {
			finalWidth = 2*RUNE_WIDTH + WIDTH_OFFSET;
		}
		
		int numRays = ((filepaths.size() + 4) / 5); // the number of 5 lines of images (top 2 auras, then runes, then bot 2 auras) there are. +4, so we round up.
		finalHeight = (STAFF_HEIGHT*numRays) + HEIGHT_OFFSET;		
		setSize(finalWidth, finalHeight);
		
		VidereComponent comp = new VidereComponent(filepaths);
		add(comp);
	}
	
	public void writeImage(String path) throws IOException, InterruptedException {
		path = path.replaceAll(".txt", ".png");
		Container content = this.getContentPane();
		BufferedImage bi = new BufferedImage(content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi.createGraphics();
		content.printAll(g2d);
		g2d.dispose();
		ImageIO.write(bi, "png", new File(path));
	}
	
	
	class VidereComponent extends JComponent {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -6430290571390297752L;
		
		private LinkedList<LinkedList<String>> filepaths;
		private BufferedImage img;
		
		public VidereComponent(LinkedList<LinkedList<String>> filepaths) {
			img = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB);
			this.filepaths = filepaths;
		}
		
		public void paintComponent(Graphics g) {
			if (filepaths == null || filepaths.isEmpty()) return;
			
			Graphics2D g2 = (Graphics2D) g;
			Color bgColor = new Color(settings.getIntSetting(Settings.I_BACK_RGB_RED),
					settings.getIntSetting(Settings.I_BACK_RGB_GREEN),
					settings.getIntSetting(Settings.I_BACK_RGB_BLUE));
			g2.setPaint(bgColor);
			g2.fillRect(0, 0, finalWidth, finalHeight);
			g2.drawImage(img, null, null);
			
			int maxRunesInLine = 0;
			
			String emptyAuraPath = "";
			String emptyRunePath = "";
			try {
				emptyAuraPath = settings.getRuneDictionary().getRDE("EMPTY").getFilePath();
				emptyRunePath = settings.getRuneDictionary().getRDE("EMPTY-RUNE").getFilePath();
			} catch (Exception e) {
				// swallow for now, will get caught elsewhere like the grimoire
			}
			
			for (int x = 0; x < filepaths.size(); ++x) {
				int numRunesInLine = filepaths.get(x).size();
				if (maxRunesInLine < numRunesInLine) {
					maxRunesInLine = numRunesInLine;
				}
				for (int y = 0; y < maxRunesInLine; ++y) {
					int runeWidthStart = RUNE_WIDTH*y;
					int runeHeightStart = HORIZONTAL_AURA_HEIGHT*x;
					int runeHeightEnd;

					if ((x+1)%5 == 3) {
						runeHeightEnd = RUNE_HEIGHT;
					} else {
						runeHeightEnd = HORIZONTAL_AURA_HEIGHT;
					}
					
					// adjust the rune height for lines that come after the rune line (since it has a different length than aura lines)
					if (((x+1) % 5 == 4) || ((x+1) % 5 == 0)) {
						runeHeightStart += 34; // the rest of the 50 pixels for a rune height, that was missed when we multiplied rune height by number of runes
					}
					
					// finally, we need to account for previous rune lines (in rays above this one)
					// for each ray, we need to add 34 more pixels (since we only counted 16 of them, the length of an aura, when originally calculating the start height)
					int numRays = (int)((double)(x)/5.0);
					for (int z = 0; z < numRays; ++z) {
						runeHeightStart += 34;
					}
					
					// paint the rune if it exists. else, it is the emptiness between numRunesInLine and maxRunesInLine, so paint bg color
					if (y < numRunesInLine) {
						paintRune(img, runeWidthStart, runeHeightStart, runeWidthStart+RUNE_WIDTH, runeHeightStart+runeHeightEnd, filepaths.get(x).get(y));
					} else {
						if (runeHeightEnd == RUNE_HEIGHT) {
							paintRune(img, runeWidthStart, runeHeightStart, runeWidthStart+RUNE_WIDTH, runeHeightStart+runeHeightEnd, emptyRunePath);
						} else {
							paintRune(img, runeWidthStart, runeHeightStart, runeWidthStart+RUNE_WIDTH, runeHeightStart+runeHeightEnd, emptyAuraPath);
						}
					}
				}
			}
		}
	}
	
	public void paintRune(BufferedImage img, int startWidth, int startHeight, int endWidth, int endHeight, String filename) {
		try {
			
			int bgColor = new Color(settings.getIntSetting(Settings.I_BACK_RGB_RED),
					  				settings.getIntSetting(Settings.I_BACK_RGB_GREEN),
					  				settings.getIntSetting(Settings.I_BACK_RGB_BLUE)).getRGB();
			
			int runeColor = new Color(settings.getIntSetting(Settings.I_RUNE_RGB_RED),
									  settings.getIntSetting(Settings.I_RUNE_RGB_GREEN),
									  settings.getIntSetting(Settings.I_RUNE_RGB_BLUE)).getRGB();
			
			int rayColor = new Color(settings.getIntSetting(Settings.I_RAY_RGB_RED),
									 settings.getIntSetting(Settings.I_RAY_RGB_GREEN),
									 settings.getIntSetting(Settings.I_RAY_RGB_BLUE)).getRGB();
			
			int currentRow = startHeight;
			boolean isBg; // if this is true, we paint with the background color. else, its false and we use the rune color.
			
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			while (br.ready()) {
				String lineContents = br.readLine();		
				StringTokenizer st = new StringTokenizer(lineContents);
			
				// we know there will be tokens until we've gotten to a count of 50
				String firstToken = st.nextToken();
				if (firstToken.contains("B") || firstToken.contains("b")) { // it is a bg color first
					isBg = true;
				} else if (firstToken.contains("R") || firstToken.contains("r")) { // it is a rune color first
					isBg = false;
				} else {
					System.out.println("ERROR READING RUNE FILE - INVALID RUNE MARKER IN FIRST TOKEN: " + firstToken + "   FILE: " + filename);
					return;
				}
			
				int firstAmount = Integer.parseInt(firstToken.substring(0, firstToken.length()-1));
				int totalPixels = 0;
				int totalRead = firstAmount;
			
				// paint in the columns for the line we read in
				for (int i = startWidth; i < endWidth; ++i) {
					if (isBg) {
						
						// Do we want rays? If we do, determine if it is a row that gets rays. Otherwise, just paint the bg.
						if (settings.getBoolSetting(Settings.B_ALLOW_STAFF)) {
							// Yes, these are magic numbers. Each ray appears halfway through its current rune, so we take half of the current height, and add the full heights of previous runes.
							// First = half of hora rune height
							// Second = half of hora rune height + 1 full hora rune height
							// Third = half of regular rune height + 2 full hora rune heights
							// Fourth = half of hora rune height + 2 full hora rune heights + 1 regular rune height
							// Fifth = half of hora rune height + 3 full hora rune heights + 1 regular rune height
							if (currentRow % STAFF_HEIGHT == 8 || currentRow % STAFF_HEIGHT == 24 || currentRow % STAFF_HEIGHT == 56
								/*|| currentRow % STAFF_HEIGHT == 57*/ || currentRow % STAFF_HEIGHT == 89 || currentRow % STAFF_HEIGHT == 105) {
								img.setRGB(i, currentRow, rayColor);
							} else {						
								img.setRGB(i, currentRow, bgColor);
							}
						} else {
							img.setRGB(i, currentRow, bgColor);
						}
						
					} else {
						img.setRGB(i, currentRow, runeColor);
					}
						
					++totalPixels;
					if (totalPixels == totalRead) {
						if (totalPixels < RUNE_WIDTH) {
							// now to grab the next token and swap color. note, dont actually check for an 'r' or 'b' char
							isBg = !isBg;
							String nextToken = st.nextToken();
							int tokenAmt = Integer.parseInt(nextToken.substring(0, nextToken.length()-1));
							totalRead += tokenAmt;
						}
					}
				}
				
				++currentRow;
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error trying to read file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}