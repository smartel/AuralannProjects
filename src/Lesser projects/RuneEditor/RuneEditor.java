package RuneEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Text2Auralann.Videre;

public class RuneEditor extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4232148322661246490L;
	
	public static final int RUNE_DIMEN = 50;
	
	LinkedList<PixelButton> pixelButtons;
	
	public RuneEditor() {
		setTitle("Rune Editor");
		setSize(1000, 1000);
		pixelButtons = new LinkedList<PixelButton>();
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(RUNE_DIMEN,RUNE_DIMEN));
		
		int pixelIndex = 0;
		for (int r = 0; r < RUNE_DIMEN; ++r) { // row
			for (int c = 0; c < RUNE_DIMEN; ++c) { // col
				PixelButton button = new PixelButton(pixelIndex);
				button.addActionListener(new PixelListener(button, pixelButtons));
				pixelButtons.add(button);
				
				// actually, if the pixels are close to the border of the image, do not allow them to be set
				if (r <= 1 || c <= 1 || r >= RUNE_DIMEN-2 || c >= RUNE_DIMEN-2) {
					// additionally, let's use them as a ruler too: every 5, use a different color, except for corners (aka (50,50))
					if ((((r+1)%5==0) || ((c+1)%5==0)) && (r+1) != RUNE_DIMEN && (c+1) != RUNE_DIMEN) {
						button.overrideBgColor(new Color(255,0,0));
					} else if ((r+1) == RUNE_DIMEN && (c+1)%5==0) { // bottom row, only set if y cooperates
						// actually, don't do em for the very last square.
						if (((r+1)==RUNE_DIMEN) && ((c+1)==RUNE_DIMEN)) {
							button.overrideBgColor(new Color(0,0,255));
						} else {
							button.overrideBgColor(new Color(255,0,0));
						}
					}  else if ((c+1) == RUNE_DIMEN && (r+1)%5==0) { // right row, only set if x cooperates
						button.overrideBgColor(new Color(255,0,0));
					} else {
						button.overrideBgColor(new Color(0,0,255));
					}
					button.lockColor();
				} else {
					button.setBackground(new Color(255,255,255));
				}
				
				buttonPanel.add(button);
				++pixelIndex;
			}
		}
		this.setLayout(new BorderLayout());
		this.add(buttonPanel, BorderLayout.CENTER);
		
		JPanel filePanel = new JPanel();
		filePanel.setLayout(new GridLayout(12,2));
		JTextField loadField = new JTextField("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/test-1.txt");
		JButton loadButton = new JButton("Load Text File");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadTextFile(loadField.getText());
			}
		});
		filePanel.add(loadField);
		filePanel.add(loadButton);
		
		JTextField saveTextField = new JTextField("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/test-1.txt");
		JButton saveTextButton = new JButton("Save Text File");
		saveTextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveTextFile(saveTextField.getText());
			}
		});
		filePanel.add(saveTextField);
		filePanel.add(saveTextButton);
		
		JButton saveTextButtonUpside = new JButton("Save Text File Upside Down");
		saveTextButtonUpside.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveTextFileUpsideDown(saveTextField.getText());
			}
		});
		filePanel.add(saveTextButtonUpside);
		
		JButton saveTextButtonBackwards = new JButton("Save Text File Backwards");
		saveTextButtonBackwards.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveTextFileBackwards(saveTextField.getText());
			}
		});
		filePanel.add(saveTextButtonBackwards);
		
		JButton saveTextRotateLeft = new JButton("Save Text File Rotated Left");
		saveTextRotateLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveTextFileRotateLeft(saveTextField.getText());
			}
		});
		filePanel.add(saveTextRotateLeft);
		
		JButton saveTextRotateRight = new JButton("Save Text File Rotated Right");
		saveTextRotateRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveTextFileRotateRight(saveTextField.getText());
			}
		});
		filePanel.add(saveTextRotateRight);
		
		JLabel shiftLabel = new JLabel("<- Shift Amount (negatives for left and down, positives for right and up)");
		JTextField shiftField = new JTextField("1");
		filePanel.add(shiftField);
		filePanel.add(shiftLabel);
		
		JButton saveTextShiftLeft = new JButton("Save Text File Shifted Left/Right");
		saveTextShiftLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveTextFileShiftLeftRight(saveTextField.getText(), Integer.parseInt(shiftField.getText()));
			}
		});
		filePanel.add(saveTextShiftLeft);
		
		JButton saveTextShiftDown = new JButton("Save Text File Shifted Down/Up");
		saveTextShiftDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveTextFileShiftUpDown(saveTextField.getText(), Integer.parseInt(shiftField.getText()));
			}
		});
		filePanel.add(saveTextShiftDown);
		
		JTextField savePngField = new JTextField("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/test-1.png");
		JButton savePngButton = new JButton("Save Image File");
		savePngButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				savePngFile(savePngField.getText());
			}
		});
		filePanel.add(savePngField);
		filePanel.add(savePngButton);
		
		//JLabel clearLabel = new JLabel("No! Don't nuke the board!! (Clears all pixels)");
		JButton nukeAllButton = new JButton("Launch Nuke (All)");
		nukeAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearScreen();
			}
		});
		//filePanel.add(clearLabel);
		filePanel.add(nukeAllButton);
		
		JButton nukeBorderButton = new JButton("Launch Nuke (Border)");
		nukeBorderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearScreenBorder();
			}
		});
		filePanel.add(nukeBorderButton);
		
		JLabel helpLabel1 = new JLabel("Hold ctrl when clicking to hit multiple squares! Shift for more squares!"); // line of 3
		JLabel helpLabel2 = new JLabel("Hold alt to only ever fill in squares while doing ctrl or shift!"); // 3x3
		filePanel.add(helpLabel1);
		filePanel.add(helpLabel2);

		JLabel helpLabel3 = new JLabel("Auras aren't 50x50! Have a height of " + Videre.HORIZONTAL_AURA_HEIGHT + " rows!");
		JLabel helpLabel4 = new JLabel("Auras will also break the side ruler until nuked. Delete extra lines saved to text file.");
		filePanel.add(helpLabel3);
		filePanel.add(helpLabel4);
		
		JTextField saveAuraPngField = new JTextField("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/test-1.png");
		JButton saveAuraPngButton = new JButton("Save Image File with Aura Dimensions");
		saveAuraPngButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAuraPngFile(saveAuraPngField.getText());
			}
		});
		filePanel.add(saveAuraPngField);
		filePanel.add(saveAuraPngButton);
		
		JTextField convertImageField = new JTextField("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/Misc/convertMe.png");
		JButton convertImageButton = new JButton("Convert 50x50 pixel image to Rune info");
		convertImageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				convertImage(convertImageField.getText());
			}
		});
		filePanel.add(convertImageField);
		filePanel.add(convertImageButton);
		
		this.add(filePanel, BorderLayout.SOUTH);
	}
	
	@SuppressWarnings("resource")
	public void loadTextFile(String filename) {
		try {
			boolean isBg;
			int pixelIndex = 0;
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			while (br.ready()) {
				String lineContents = br.readLine();		
				StringTokenizer st = new StringTokenizer(lineContents);
				
				// we know there will be tokens on a single line until we've gotten to a count of 50
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
			
				// if it starts with a 0b or 0r, read the next token and swap polarity
				if (firstAmount == 0) {
					String nextToken = st.nextToken();
					if (nextToken.contains("B") || nextToken.contains("b")) { // it is a bg color first
						if (!isBg) {
							isBg = !isBg;
						} // otherwise, color didn't actually change
					} else if (nextToken.contains("R") || nextToken.contains("r")) { // it is a rune color first
						if (isBg) {
							isBg = !isBg;
						} // otherwise, color didn't actually change
					} else {
						System.out.println("ERROR READING RUNE FILE - INVALID RUNE MARKER IN FIRST TOKEN: " + nextToken + "   FILE: " + filename);
						return;
					}
				}
				
				int totalPixels = 0;
				int totalRead = firstAmount;
			
				for (int i = 0; i < RUNE_DIMEN; ++i) {
					if (isBg) {
						pixelButtons.get(pixelIndex).setToBgColor(true);
					} else {
						pixelButtons.get(pixelIndex).setToRuneColor(true);
					}
					
					++totalPixels;
					if (totalPixels == totalRead) {
						if (totalPixels < RUNE_DIMEN) {
							// now to grab the next token and swap color if it changes from r->b or b->r (dont assume it always alternates)
							String nextToken = st.nextToken();
							if (nextToken.contains("B") || nextToken.contains("b")) { // it is a bg color first
								if (!isBg) {
									isBg = !isBg;
								} // otherwise, color didn't actually change
							} else if (nextToken.contains("R") || nextToken.contains("r")) { // it is a rune color first
								if (isBg) {
									isBg = !isBg;
								} // otherwise, color didn't actually change
							} else {
								System.out.println("ERROR READING RUNE FILE - INVALID RUNE MARKER IN FIRST TOKEN: " + nextToken + "   FILE: " + filename);
								return;
							}
							
							int tokenAmt = Integer.parseInt(nextToken.substring(0, nextToken.length()-1));
							totalRead += tokenAmt;
						}
					}
					++pixelIndex;
				}
			}
			br.close();
		
		} catch (Exception e) {
			System.out.println("Error trying to read file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void saveTextFile(String filename) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			int pixelIndex = 0;
			for (int r = 0; r < RUNE_DIMEN; ++r) {
				
				// this is a single row. keep track of when it switches from bg color to rune color
				
				int consecutiveCount = 0;
				String currentLine = "";
				boolean isBg = true;
				
				// Note - lines will ALWAYS begin with a background, so we don't have to worry about writing 0b to the file.
				
				for (int c = 0; c < RUNE_DIMEN; ++c) {
					boolean isPixelBg = pixelButtons.get(pixelIndex).getIsBg();
					if (isBg && isPixelBg) {
						++consecutiveCount;
					} else if (isBg && !isPixelBg) {
						// was background, but now it's rune. append the consecutive count and "b" to the current line. don't append a newline. reset consecutive to 1 for rune.
						currentLine += consecutiveCount + "b ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else if (!isBg && isPixelBg) {
						// was rune, and now it is background. append the consecutive count and "r" to the current line. don't append a newline. reset consecutive to 1 for bg.
						currentLine += consecutiveCount + "r ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else {
						// wasn't bg, and still isn't bg. consecutive goes up.
						++consecutiveCount;
					}
					++pixelIndex;
				}
				if (consecutiveCount > 0) { // write out the remaining amount
					currentLine += consecutiveCount;
					if (isBg) {
						currentLine += "b";
					} else {
						currentLine += "r";
					}
				}
				
				
				bw.write(currentLine.trim());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Error trying to write text file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void saveTextFileUpsideDown(String filename) {
		try {
			LinkedList<String> allLines = new LinkedList<String>();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			int pixelIndex = 0;
			for (int r = 0; r < RUNE_DIMEN; ++r) {
				
				// this is a single row. keep track of when it switches from bg color to rune color
				
				int consecutiveCount = 0;
				String currentLine = "";
				boolean isBg = true;
				
				// Note - lines will ALWAYS begin with a background, so we don't have to worry about writing 0b to the file.
				
				for (int c = 0; c < RUNE_DIMEN; ++c) {
					boolean isPixelBg = pixelButtons.get(pixelIndex).getIsBg();
					if (isBg && isPixelBg) {
						++consecutiveCount;
					} else if (isBg && !isPixelBg) {
						// was background, but now it's rune. append the consecutive count and "b" to the current line. don't append a newline. reset consecutive to 1 for rune.
						currentLine += consecutiveCount + "b ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else if (!isBg && isPixelBg) {
						// was rune, and now it is background. append the consecutive count and "r" to the current line. don't append a newline. reset consecutive to 1 for bg.
						currentLine += consecutiveCount + "r ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else {
						// wasn't bg, and still isn't bg. consecutive goes up.
						++consecutiveCount;
					}
					++pixelIndex;
				}
				if (consecutiveCount > 0) { // write out the remaining amount
					currentLine += consecutiveCount;
					if (isBg) {
						currentLine += "b";
					} else {
						currentLine += "r";
					}
				}
				
				allLines.addFirst(currentLine);
			}
			for (int x = 0; x < allLines.size(); ++x) {
				bw.write(allLines.get(x).trim());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Error trying to write text file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void saveTextFileBackwards(String filename) {
		try {
			LinkedList<String> allLines = new LinkedList<String>();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			int pixelIndex = 0;
			for (int r = 0; r < RUNE_DIMEN; ++r) {
				
				// this is a single row. keep track of when it switches from bg color to rune color
				
				int consecutiveCount = 0;
				String currentLine = "";
				boolean isBg = true;
				
				// Note - lines will ALWAYS begin with a background, so we don't have to worry about writing 0b to the file.
				
				for (int c = 0; c < RUNE_DIMEN; ++c) {
					boolean isPixelBg = pixelButtons.get(pixelIndex).getIsBg();
					if (isBg && isPixelBg) {
						++consecutiveCount;
					} else if (isBg && !isPixelBg) {
						// was background, but now it's rune. append the consecutive count and "b" to the current line. don't append a newline. reset consecutive to 1 for rune.
						currentLine += consecutiveCount + "b ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else if (!isBg && isPixelBg) {
						// was rune, and now it is background. append the consecutive count and "r" to the current line. don't append a newline. reset consecutive to 1 for bg.
						currentLine += consecutiveCount + "r ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else {
						// wasn't bg, and still isn't bg. consecutive goes up.
						++consecutiveCount;
					}
					++pixelIndex;
				}
				if (consecutiveCount > 0) { // write out the remaining amount
					currentLine += consecutiveCount;
					if (isBg) {
						currentLine += "b";
					} else {
						currentLine += "r";
					}
				}
				
				allLines.add(currentLine);
			}
			for (int x = 0; x < allLines.size(); ++x) {
				
				// get a string tokenizer, and reverse the contents of the line before printing it
				String currentContents = allLines.get(x);
				String newContents = "";
				StringTokenizer st = new StringTokenizer(currentContents);
				while (st.hasMoreTokens()) {
					newContents = " " + st.nextToken() + " " + newContents;
				}
				
				bw.write(newContents.trim());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Error trying to write text file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void saveTextFileRotateLeft(String filename) {
		try {
			LinkedList<String> allLines = new LinkedList<String>();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			int pixelIndex = 0;
			for (int col = 1; col <= RUNE_DIMEN; ++col) {
				
				// we have to think about rotating a bit differently than standard reading of the array
				// say we have: 
				//	12345
				//	22000
				// 	30300
				//	40040
				//	50005
				// To rotate it, we want the top row to be 50005, which is the rightmost column. Then 40040 is the 2nd row, and so on.
				// Therefore, we read from the top-down of a column, and create that as a row.
				// Since we're accessing this using pixelIndex, what we'll do get the contents of a column is:
				// (MAX ROW LENGTH * CURRENT ROW) - CURRENT COLUMN,
				// 	with the minimum value for current row and current column being 1, so the first value would be 49. To get the next value, we increment current row.
				//	The 2nd would be 99, 3rd would be 149, ...
				// When we are done with the column, we reset the current row, and we increment current column, so now we get 48, 98, 148, ...
				
				int consecutiveCount = 0;
				String currentLine = "";
				boolean isBg = true;
				
				for (int row = 1; row <= RUNE_DIMEN; ++row) {
					
					pixelIndex = (RUNE_DIMEN * row) - col;
					
					boolean isPixelBg = pixelButtons.get(pixelIndex).getIsBg();
					if (isBg && isPixelBg) {
						++consecutiveCount;
					} else if (isBg && !isPixelBg) {
						// was background, but now it's rune. append the consecutive count and "b" to the current line. don't append a newline. reset consecutive to 1 for rune.
						currentLine += consecutiveCount + "b ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else if (!isBg && isPixelBg) {
						// was rune, and now it is background. append the consecutive count and "r" to the current line. don't append a newline. reset consecutive to 1 for bg.
						currentLine += consecutiveCount + "r ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else {
						// wasn't bg, and still isn't bg. consecutive goes up.
						++consecutiveCount;
					}
					++pixelIndex;
				}
				if (consecutiveCount > 0) { // write out the remaining amount
					currentLine += consecutiveCount;
					if (isBg) {
						currentLine += "b";
					} else {
						currentLine += "r";
					}
				}
				
				allLines.add(currentLine);
			}
			for (int x = 0; x < allLines.size(); ++x) {
				bw.write(allLines.get(x).trim());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Error trying to write text file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void saveTextFileRotateRight(String filename) {
		try {
			LinkedList<String> allLines = new LinkedList<String>();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			int pixelIndex = 0;
			for (int col = 0; col < RUNE_DIMEN; ++col) {
				
				// we have to think about rotating a bit differently than standard reading of the array
				// say we have: 
				//	12345
				//	22000
				// 	30300
				//	40040
				//	50005
				// To rotate it, we want the top row to be 54321, which is the leftmost column. Then 00022 is the 2nd row, and so on.
				// Therefore, we read from the bottom to the top of a column, and create that as a row.
				// Since we're accessing this using pixelIndex, what we'll do get the contents of a column is:
				// (MAX ROW LENGTH * CURRENT ROW) + CURRENT COLUMN,
				// 	with the minimum value for current row and current column being 0 (not 1, like in rotate-left),
				//  and starting from the bottom of the column (so current row of 49, decrementing until <= 0)
				//  so the first value would be 2450. To get the next value, we increment current row.
				//	The 2nd would be 2400, 3rd would be 2350, 2300, 2250, ... , 150, 100, 50, 0, DONE
				// When we are done with the column, we reset the current row, and we increment current column, so now we get 48, 98, 148, ...
				
				int consecutiveCount = 0;
				String currentLine = "";
				boolean isBg = true;
				
				for (int row = RUNE_DIMEN-1; row >= 0; --row) {
					
					pixelIndex = (RUNE_DIMEN * row) + col;
					
					boolean isPixelBg = pixelButtons.get(pixelIndex).getIsBg();
					if (isBg && isPixelBg) {
						++consecutiveCount;
					} else if (isBg && !isPixelBg) {
						// was background, but now it's rune. append the consecutive count and "b" to the current line. don't append a newline. reset consecutive to 1 for rune.
						currentLine += consecutiveCount + "b ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else if (!isBg && isPixelBg) {
						// was rune, and now it is background. append the consecutive count and "r" to the current line. don't append a newline. reset consecutive to 1 for bg.
						currentLine += consecutiveCount + "r ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else {
						// wasn't bg, and still isn't bg. consecutive goes up.
						++consecutiveCount;
					}
					++pixelIndex;
				}
				if (consecutiveCount > 0) { // write out the remaining amount
					currentLine += consecutiveCount;
					if (isBg) {
						currentLine += "b";
					} else {
						currentLine += "r";
					}
				}
				
				allLines.add(currentLine);
			}
			for (int x = 0; x < allLines.size(); ++x) {
				bw.write(allLines.get(x).trim());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Error trying to write text file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void saveTextFileShiftLeftRight(String filename, int amount) {
		try {
			LinkedList<String> allLines = new LinkedList<String>();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			int pixelIndex = 0;
			for (int r = 0; r < RUNE_DIMEN; ++r) {
				
				// this is a single row. keep track of when it switches from bg color to rune color
				
				int consecutiveCount = 0;
				String currentLine = "";
				boolean isBg = true;
				
				// Note - lines will ALWAYS begin with a background, so we don't have to worry about writing 0b to the file.
				
				for (int c = 0; c < RUNE_DIMEN; ++c) {
					boolean isPixelBg = pixelButtons.get(pixelIndex).getIsBg();
					if (isBg && isPixelBg) {
						++consecutiveCount;
					} else if (isBg && !isPixelBg) {
						// was background, but now it's rune. append the consecutive count and "b" to the current line. don't append a newline. reset consecutive to 1 for rune.
						currentLine += consecutiveCount + "b ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else if (!isBg && isPixelBg) {
						// was rune, and now it is background. append the consecutive count and "r" to the current line. don't append a newline. reset consecutive to 1 for bg.
						currentLine += consecutiveCount + "r ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else {
						// wasn't bg, and still isn't bg. consecutive goes up.
						++consecutiveCount;
					}
					++pixelIndex;
				}
				if (consecutiveCount > 0) { // write out the remaining amount
					currentLine += consecutiveCount;
					if (isBg) {
						currentLine += "b";
					} else {
						currentLine += "r";
					}
				}
				
				allLines.add(currentLine);
			}
			
			for (int x = 0; x < allLines.size(); ++x) {
				// loop over every line, adjust left or right if necessary, and write to file
			
				String outputLine = allLines.get(x);
				
				if (amount > 0) { // shift right
					StringTokenizer st = new StringTokenizer(outputLine);
					String firstToken = st.nextToken();
					if (firstToken.endsWith("r")) { // if it starts with runes, then we'll just add a bg element to the front of the string
						outputLine = "" + amount + "b" + outputLine;
					} else { // already started with bg, so add to it
						int oldAmount = Integer.parseInt(firstToken.substring(0, firstToken.length()-1));
						outputLine = outputLine.substring(firstToken.length());
						outputLine = "" + (oldAmount+amount) + "b" + outputLine;
					}
					// the way we read in right now, we don't actually need to delete anything from the tail end, so the line can go over 50 pixels
				} else if (amount < 0) { // shift left
					StringTokenizer st = new StringTokenizer(outputLine);
					String lastToken = "";
					while (st.hasMoreTokens()) {
						lastToken = st.nextToken();
					}
					if (lastToken.endsWith("r")) { // if it ends with runes, then we'll just add a bg element to the back of the string, and delete from the front
						outputLine = outputLine + amount + "b";
					} else { // already ended with bg, so add to it
						int oldAmount = Integer.parseInt(lastToken.substring(0, lastToken.length()-1));
						//outputLine = outputLine.substring(lastToken.length());
						outputLine = outputLine.substring(0, outputLine.length() - lastToken.length());
						outputLine = outputLine + (oldAmount-amount) + "b";
					}
					// now delete!
					st = new StringTokenizer(outputLine);
					int amountToDelete = Math.abs(amount);
					while (amountToDelete > 0) {
						String currentToken = st.nextToken();
						boolean isBg = !(currentToken.contains("r"));
						int tokenAmount = Integer.parseInt(currentToken.substring(0, currentToken.length()-1));
						if (tokenAmount > amountToDelete) {
							// subtract from the token amount and update it with the new value in the line
							tokenAmount -= amountToDelete;
							amountToDelete = 0;
							String symbol = "";
							if (isBg) {
								symbol = "b";
							} else {
								symbol = "r";
							}
							outputLine = outputLine.substring(currentToken.length());
							outputLine = "" + tokenAmount + symbol + outputLine;
						} else if (tokenAmount <= amountToDelete) {
							// just delete this token from the line, and subtract the tokenAmount from amountToDelete. if amountToDelete == 0, we will stop looping.
							outputLine = outputLine.substring(currentToken.length()+1); // +1 to get rid of the space as well
							amountToDelete -= tokenAmount;
						}
						
					}
				} // do nothing if amount == 0, it is bad input
				
				bw.write(outputLine.trim());
				bw.newLine();
			}
				
			bw.close();
			
		} catch (Exception e) {
			System.out.println("Error trying to write text file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}	
	
	public void saveTextFileShiftUpDown(String filename, int amount) {
		try {
			LinkedList<String> allLines = new LinkedList<String>();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
			int pixelIndex = 0;
			for (int r = 0; r < RUNE_DIMEN; ++r) {
				
				// this is a single row. keep track of when it switches from bg color to rune color
				
				int consecutiveCount = 0;
				String currentLine = "";
				boolean isBg = true;
				
				// Note - lines will ALWAYS begin with a background, so we don't have to worry about writing 0b to the file.
				
				for (int c = 0; c < RUNE_DIMEN; ++c) {
					boolean isPixelBg = pixelButtons.get(pixelIndex).getIsBg();
					if (isBg && isPixelBg) {
						++consecutiveCount;
					} else if (isBg && !isPixelBg) {
						// was background, but now it's rune. append the consecutive count and "b" to the current line. don't append a newline. reset consecutive to 1 for rune.
						currentLine += consecutiveCount + "b ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else if (!isBg && isPixelBg) {
						// was rune, and now it is background. append the consecutive count and "r" to the current line. don't append a newline. reset consecutive to 1 for bg.
						currentLine += consecutiveCount + "r ";
						consecutiveCount = 1;
						isBg = !isBg;
					} else {
						// wasn't bg, and still isn't bg. consecutive goes up.
						++consecutiveCount;
					}
					++pixelIndex;
				}
				if (consecutiveCount > 0) { // write out the remaining amount
					currentLine += consecutiveCount;
					if (isBg) {
						currentLine += "b";
					} else {
						currentLine += "r";
					}
				}
				
				allLines.add(currentLine);
			}
			
			if (amount > 0) { // shift down
				for (int x = 0; x < amount; ++x) {
					allLines.add("50b");
					allLines.removeFirst();
				}
			} else if (amount < 0) { // shift up
				for (int x = 0; x > amount; --x) {
					allLines.addFirst("50b");
					allLines.removeLast();
				}
			} // do nothing if amount == 0, it is bad input
			
			for (int x = 0; x < allLines.size(); ++x) {
				bw.write(allLines.get(x).trim());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("Error trying to write text file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void savePngFile(String filename) {

		try {
			
			BufferedImage img = new BufferedImage(RUNE_DIMEN, RUNE_DIMEN, BufferedImage.TYPE_INT_RGB);
			
			int pixelIndex = 0;
			for (int r = 0; r < RUNE_DIMEN; ++r) {
				for (int c = 0; c < RUNE_DIMEN; ++c) {
					img.setRGB(c, r, pixelButtons.get(pixelIndex).getIsBg() ? PixelButton.bgColor.getRGB() : PixelButton.runeColor.getRGB());
					++pixelIndex;
				}
			}
			
			ImageIO.write(img, "png", new File(filename));
			
		} catch (Exception e) {
			System.out.println("Error trying to write image file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	// use this if we need to output aura images.
	public void saveAuraPngFile(String filename) {

		try {
			BufferedImage img = new BufferedImage(RUNE_DIMEN, Videre.HORIZONTAL_AURA_HEIGHT, BufferedImage.TYPE_INT_RGB);

			int pixelIndex = 0;
			for (int r = 0; r < Videre.HORIZONTAL_AURA_HEIGHT; ++r) { // only does 16 rows
				for (int c = 0; c < RUNE_DIMEN; ++c) {
					img.setRGB(c, r, pixelButtons.get(pixelIndex).getIsBg() ? PixelButton.bgColor.getRGB() : PixelButton.runeColor.getRGB());
					++pixelIndex;
				}
			}
			
			ImageIO.write(img, "png", new File(filename));
			
		} catch (Exception e) {
			System.out.println("Error trying to write image file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	// converts the image at the given filepath to rune data, aka background and foreground values,
	// and loads that result into the rune editor for manual manipulation.
	public void convertImage(String filepath) {
		
		try {
			
			File file = new File(filepath);
			BufferedImage bi = ImageIO.read(file);
			
			if (bi.getHeight() != 50 || bi.getWidth() != 50) {
				throw new Exception("Provided image did not have dimensions of 50 pixels by 50 pixels");
			}
			
			// We'll need to know what value white is returned from getRGB as. depending on how it is generated,
			// perhaps anything below a certain threshold. Transparent will also need to be checked that it is not equal to black (fine if equal to white).
			int white = -1; // alpha / r / g / b = 255
			int transparent = 16777215;
						
			// This counter will keep track of when it changes from foreground to background or vice versa.
			// When it changes, the streak's amount needs to be written to the file (ie, 10b or 10r, for example),
			// then the streak needs to be reset back to 0, incremented, and the isBg flag needs to be flipped.
			// if the starting amount was 0 (ie, 0b), then don't keep it.
			boolean isBg = true;
			int streak = 0;

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("temp.txt")));
			
			
			for (int col = 0; col < bi.getHeight(); ++col) {
				for (int row = 0; row < bi.getWidth(); ++row) {
					
					int color = bi.getRGB(row,col);
					int alpha = (color >> 24) & 0xff;
					int r = (color >> 16) & 0xff;
					int g = (color >> 8) & 0xff;
					int b = color & 0xff;
					
					if (color == white || color == transparent
							|| (alpha == 255 && r > 190 && g > 190 && b > 190) // threshold, so light greys dont become dark dots
							) {
						if (isBg) {
							++streak;
						} else {
							String data = streak + "r ";
							//System.out.print(data); // temporary measure
							bw.write(data);
							streak = 1;
							isBg = !isBg;
						}
					} else {						
						if (!isBg) {
							++streak;
						} else {							
							// there is an assumption that the first pixel in a line is always background (isBg = true)
							// explicitly check if that's not the case before saving an amount for b
							if (streak != 0) {
								String data = streak + "b ";
								//System.out.print(streak + "b "); // temporary measure
								bw.write(data);
							}
							streak = 1;
							isBg = !isBg;
						}
					}
				}
				String lastData = streak + (isBg ? "b" : "r");
				//System.out.println(lastData); // temporary measure
				bw.write(lastData);
				bw.newLine();
				streak = 0; // resetting streak and isBackground to default values / assumptions
				isBg = true;
			}
			
			bw.close();
			
			// now load the file into the program
			loadTextFile("temp.txt");
			//Files.delete(new File("temp.txt").toPath()); ya know, just on the paranoid off-chance I delete something I shouldn't, the file can stay.
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void clearScreen() {
		for (int x = 0; x < pixelButtons.size(); ++x) {
			pixelButtons.get(x).setToBgColor(true);
		}
	}
	
	// only do the "picture frame" border
	public void clearScreenBorder() {
		
		// so it would be these pixels...
		// 0-99 (top 2 lines)
		// 2300-2499 (bottom 2 lines)
		
		// and then we need the left + right sides...
		// Left:
		// 100, 101
		// 150, 151
		// 200, 201
		// 250, 251
		// 300, 301
		// ...
		
		// Right:
		// 48, 49
		// 98, 99
		// 148, 149
		// 198, 199
		// 248, 249
		// 298, 299
		// ...
		
		// top rows
		for (int x = 0; x < 100; ++x) {
			pixelButtons.get(x).setToBgColor(true);
		}
		// bottom rows
		for (int x = 2400; x < 2500; ++x) {
			pixelButtons.get(x).setToBgColor(true);
		}
		// left side
		for (int x = 100; x < 2500; x += 50) {
			pixelButtons.get(x).setToBgColor(true);
			pixelButtons.get(x+1).setToBgColor(true);
		}
		// right side
		for (int x = 148; x < 2500; x += 50) {
			pixelButtons.get(x).setToBgColor(true);
			pixelButtons.get(x+1).setToBgColor(true);
		}
		
	}
	
}
