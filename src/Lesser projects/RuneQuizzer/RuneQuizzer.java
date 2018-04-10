package RuneQuizzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Text2Auralann.RuneDictionary;
import Text2Auralann.RuneDictionaryEntry;

public class RuneQuizzer extends JFrame {

	private static final long serialVersionUID = -8946218999504156540L;

	public static final int RUNE_DIMEN = 50;
	private static final int WINDOW_HEIGHT = 440;
	private static final int WINDOW_WIDTH = 600;
	
	private Settings settings;
	private BufferedWriter logWriter;
	private boolean logInitialized;
	private JTextArea systemOut;
	
	private RuneDictionary rd;
	private RunePicker rp;
	
	private Stats stats;
	
	// for painting new runes
	private String currRune;
	private String currForm;
	private String currPath;
	private String currCat; // rune category from rde
	private JPanel runePanel;
	private LucentComponent currImage;
	private int numAttempts;
	private int runeCounter;
	private boolean isItOver;

	public RuneQuizzer(Settings settings, String errors) {
		setTitle("Rune Quizzer");
		setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		stats = new Stats();
		this.settings = settings;
		
		logInitialized = false;
		systemOut = new JTextArea("\n\n\n\n\n\n\n");
		systemOut.setEditable(false);
		addText("Rune Quizzer started"); // so it gets logged

		try {
			if (errors != null && !errors.isEmpty()) {
				addText(errors);
				throw new SetupException("Errors occured during program initialization.");
			}
			
			rd = settings.getRuneDictionary();
			rp = new RunePicker(rd, settings);
			runeCounter = 0;
			isItOver = false;
			this.add(getRunePanel(), BorderLayout.CENTER);
			this.add(new JScrollPane(systemOut), BorderLayout.SOUTH);
		} catch (SetupException e) {
			addText("Errors occurred during setup. Review above errors and the log.");
			this.add(new JScrollPane(systemOut), BorderLayout.SOUTH);
		} catch (Exception e) {
			addText("Error caught: " + e.getMessage());
			this.add(new JScrollPane(systemOut), BorderLayout.SOUTH);
		}
	}
	
	public void updateRune() {
		setNextRune(true);
		
		runePanel.remove(currImage);
		currImage = new LucentComponent(currPath);
		runePanel.add(currImage, BorderLayout.CENTER);
		
		// ugly, but it will do. otherwise, the rune image isn't refreshing.
		this.setVisible(false);
		this.setSize(0,0);
		this.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		this.setVisible(true);
	}
	
	public JPanel getRunePanel() {
		
		runePanel = new JPanel();
		runePanel.setLayout(new BorderLayout());

		setNextRune(true);
		currImage = new LucentComponent(currPath);
		runePanel.add(currImage, BorderLayout.CENTER);
		
		// labels (some update as buttons are clicked):
		JLabel answerLabel = new JLabel("Answer:");
		answerLabel.setHorizontalAlignment(JLabel.CENTER);
		JLabel attemptsLabel = new JLabel("Num Attempts: " + numAttempts);
		attemptsLabel.setHorizontalAlignment(JLabel.CENTER);
		JLabel countLabel = new JLabel("");
		if (settings.getBoolSetting(Settings.B_AROUND_WORLD)) {
			countLabel.setText("Around the World: " + runeCounter + "/" + rp.getMaxRuneCount());
		} else {
			countLabel.setText("On Rune #" + runeCounter);
		}
		countLabel.setHorizontalAlignment(JLabel.CENTER);
		JLabel countAndAttLabel = new JLabel();
		//countAndAttLabel.setHorizontalAlignment(JLabel.CENTER);
		countAndAttLabel.setText("<html><p>" + countLabel.getText() + "<br>" + attemptsLabel.getText() + "</p></html>");
		JLabel catLabel = new JLabel("<html><p><br>Category:<br>" + currCat + "</p></html>");
		catLabel.setHorizontalAlignment(JLabel.RIGHT);
		
		// this panel goes in the west, and will give the spacing the rune needs to be centered, as well as the attempt/rune count:
		JPanel countPanel = new JPanel();
		countPanel.setLayout(new BorderLayout());
		countPanel.add(new JLabel("                                                                  "), BorderLayout.NORTH); // for spacing
		countPanel.add(countAndAttLabel, BorderLayout.CENTER);
		runePanel.add(countPanel, BorderLayout.WEST);

		// this panel goes in the east, and has the rune's category
		JPanel catPanel = new JPanel();
		catPanel.setLayout(new BorderLayout());
		catPanel.add(catLabel, BorderLayout.CENTER);
		runePanel.add(catPanel, BorderLayout.EAST);
		
		
		
		
		JTextField answerField = new JTextField("");
		JButton answerBtn = new JButton("Answer!");
		answerBtn.setMnemonic(KeyEvent.VK_A);
		answerBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!answerField.getText().isEmpty() && !isItOver) {
					boolean isCorrect = stats.attempt(answerField.getText(), currRune, currForm, currPath);
					++numAttempts; // may get reset by updateRune in a sec...
					if (isCorrect) {
						if (numAttempts > 1) {
							addText("Correctly guessed " + currForm + " after " + numAttempts + " attempts!"); // plural
						} else {
							addText("Correctly guessed " + currForm + " after " + numAttempts + " attempt!"); // singular
						}
						countAndAttLabel.setForeground(Color.black);
						updateRune();
						if (settings.getBoolSetting(Settings.B_AROUND_WORLD)) {
							countLabel.setText("Around the World: " + runeCounter + "/" + rp.getMaxRuneCount());
						} else {
							countLabel.setText("On Rune #" + runeCounter);
						}
						catLabel.setText("<html><p><br>Category:<br>" + currCat + "</p></html>");
					} else {
						addText("Incorrect guess! \"" + answerField.getText() + "\" is wrong!");
						countAndAttLabel.setForeground(Color.red);
					}
					answerField.setText("");
					attemptsLabel.setText("Num Attempts: " + numAttempts);
					countAndAttLabel.setText("<html><p>" + countLabel.getText() + "<br>" + attemptsLabel.getText() + "</p></html>");
				} else if (answerField.getText().isEmpty()) {
					addText("Please provide an answer!");
				} else {
					addText("There are no more Runes left!");
				}
				answerField.requestFocus();
			}
		});
		JButton skipBtn = new JButton("Skip!");
		skipBtn.setMnemonic(KeyEvent.VK_S);
		skipBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isItOver) {
					stats.addSkip(currRune, currForm, currPath);
					answerField.setText("");
					addText("Skipping Rune! It was Rune: " + currForm);
					countAndAttLabel.setForeground(Color.black);
					updateRune();
					if (settings.getBoolSetting(Settings.B_AROUND_WORLD)) {
						countLabel.setText("Around the World: " + runeCounter + "/" + rp.getMaxRuneCount());
					} else {
						countLabel.setText("On Rune #" + runeCounter);
					}
					answerField.setText("");
					attemptsLabel.setText("Num Attempts: " + numAttempts);
					countAndAttLabel.setText("<html><p>" + countLabel.getText() + "<br>" + attemptsLabel.getText() + "</p></html>");
					catLabel.setText("<html><p><br>Category:<br>" + currCat + "</p></html>");
				} else {
					addText("There are no more Runes left!");
				}
				answerField.requestFocus();
			}
		});
		
		JButton awardBtn = new JButton("Give Credit (must try at least once)!");
		awardBtn.setMnemonic(KeyEvent.VK_D);
		awardBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (numAttempts > 0 && !isItOver) {
					stats.addCredit(currRune, currForm, currPath);
					answerField.setText("");
					addText("Awarding credit for Rune!");
					countAndAttLabel.setForeground(Color.black);
					updateRune();
					if (settings.getBoolSetting(Settings.B_AROUND_WORLD)) {
						countLabel.setText("Around the World: " + runeCounter + "/" + rp.getMaxRuneCount());
					} else {
						countLabel.setText("On Rune #" + runeCounter);
					}
					answerField.setText("");
					attemptsLabel.setText("Num Attempts: " + numAttempts);
					countAndAttLabel.setText("<html><p>" + countLabel.getText() + "<br>" + attemptsLabel.getText() + "</p></html>");
					catLabel.setText("<html><p><br>Category:<br>" + currCat + "</p></html>");
				} else if (isItOver) { 
					addText("There are no more Runes left!");
				}
				else {
					addText("You must attempt the Rune at least once first!");
				}
				answerField.requestFocus();
			}
		});
		/* Old button for testing.
		JButton reportOneBtn = new JButton("Report Current (FOR TESTING)!");
		reportOneBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addText("Writing report!");
				addText(stats.getSingleDetails(currRune, currForm, currPath));
			}
		});
		*/
		JButton reportAllBtn = new JButton("Write Report to log!");
		reportAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addText("Writing report to log!");
				if (isItOver) { // if there's no Runes left, go ahead and report the "current" one too, since it was done
					addText(stats.getAllDetails(false, "n/a"));
				} else {
					addText(stats.getAllDetailsIgnoreCurrent(currRune, currForm, currPath, false, "n/a"));
				}
				answerField.requestFocus();
			}
		});
		JButton reportToFileBtn = new JButton("Write Report to file!");
		reportToFileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addText("Writing report to path: " + settings.getOutputPath() +  "!"); 
				if (isItOver) { // if there's no Runes left, go ahead and report the "current" one too, since it was done
					addText(stats.getAllDetails(true, settings.getOutputPath()));
				} else {
					addText(stats.getAllDetailsIgnoreCurrent(currRune, currForm, currPath, true, settings.getOutputPath()));
				}
				String writeErrors = stats.removeErrorMsgs();
				if (!writeErrors.isEmpty()) {
					addText(writeErrors);
				}
				answerField.requestFocus();
			}
		});
				
		// New style
		JPanel answerPanel = new JPanel();
		answerPanel.setLayout(new GridLayout(1,3));
		answerPanel.add(answerLabel);
		answerPanel.add(answerField);
		answerPanel.add(answerBtn);
		JPanel miscPanel = new JPanel();
		miscPanel.setLayout(new GridLayout(2, 2));
		miscPanel.add(skipBtn);
		miscPanel.add(awardBtn);
		miscPanel.add(reportAllBtn);
		miscPanel.add(reportToFileBtn);
		
		JPanel allButtons = new JPanel();
		allButtons.setLayout(new BorderLayout());
		allButtons.add(answerPanel, BorderLayout.NORTH);
		allButtons.add(miscPanel, BorderLayout.SOUTH);
		runePanel.add(allButtons, BorderLayout.SOUTH);
		
		return runePanel;
	}
	
	public void setNextRune(boolean isQuiet) {
		RuneDictionaryEntry rde = rp.getRandomRune(currRune);
		if (rde != null) {
			numAttempts = 0; // this attempts is local to the instance of the rune currently being displayed - even if it has come up twice, we always start this at 0 and ignore previous attempts.
			++runeCounter;
			currRune = rde.getKey();
			currForm = rde.getRandomForm();
			currPath = rde.getFilePath(currForm);
			currCat = rde.getRuneCategory();
			
			// ANNIHILATE the form so it can't be shown again
			rde.annihilateForm(currForm);
			
			if (!isQuiet) {
				addText("Rune: [" + currRune + "]  Form: [" + currForm + "]  Path: [" + currPath + "]");
				// note, the user would not need to guess a form number, just the letter itself. ie, just M, not M-1 or M-2.
			}
			stats.addStats(currRune, currForm, currPath);
		} else { // we are at the end of the world. deactivate answer,skip,credit buttons
			isItOver = true;
			addText("You have gone around the world!\nThere are no Runes left!");
		}
	}
	
	
	
	class LucentComponent extends JComponent {	
		
		private static final long serialVersionUID = -5681361311399081747L;
		
		private String filepath;
		private BufferedImage img;
		
		// grab the finals from the Settings object...
		int I_RUNE_SIZE = settings.getIntSetting(Settings.I_RUNE_SIZE);
		int DOUBLE_SIZED_RUNES = Settings.DOUBLE_SIZED_RUNES;
		int QUAD_SIZED_RUNES = Settings.QUAD_SIZED_RUNES;
		
		public LucentComponent(String filepath) {
			if (I_RUNE_SIZE == QUAD_SIZED_RUNES) {
				img = new BufferedImage(RUNE_DIMEN*4, RUNE_DIMEN*4, BufferedImage.TYPE_INT_ARGB);
			}
			else if (I_RUNE_SIZE == DOUBLE_SIZED_RUNES) {
				img = new BufferedImage(RUNE_DIMEN*2, RUNE_DIMEN*2, BufferedImage.TYPE_INT_ARGB);
			} else {
				img = new BufferedImage(RUNE_DIMEN, RUNE_DIMEN, BufferedImage.TYPE_INT_ARGB);
			}
			this.filepath = filepath;
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (filepath == null || filepath.isEmpty()) return;
			
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage(img, null, null);

			paintRune(img, 0, 0, 0+RUNE_DIMEN, 0+RUNE_DIMEN, filepath);
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
			
			int currentRow = startHeight;
			boolean isBg; // if this is true, we paint with the background color. else, its false and we use the rune color.
			
			// grab the finals from the Settings object...
			int I_RUNE_SIZE = settings.getIntSetting(Settings.I_RUNE_SIZE);
			int DOUBLE_SIZED_RUNES = Settings.DOUBLE_SIZED_RUNES;
			int QUAD_SIZED_RUNES = Settings.QUAD_SIZED_RUNES;
			
			
			if (I_RUNE_SIZE == QUAD_SIZED_RUNES) {
				endWidth += (RUNE_DIMEN * 3);
				endHeight += (RUNE_DIMEN * 3);
			}
			else if (I_RUNE_SIZE == DOUBLE_SIZED_RUNES) {
				endWidth += RUNE_DIMEN;
				endHeight += RUNE_DIMEN;
			}
			
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
					addText("ERROR READING RUNE FILE - INVALID RUNE MARKER IN FIRST TOKEN: " + firstToken + "   FILE: " + filename);
					return;
				}
			
				int firstAmount = Integer.parseInt(firstToken.substring(0, firstToken.length()-1));
				int totalPixels = 0;
				int totalRead = firstAmount;
			
				// paint in the columns for the line we read in
				for (int i = startWidth; i < endWidth; ++i) {
					if (isBg) {
						if (I_RUNE_SIZE == DOUBLE_SIZED_RUNES || I_RUNE_SIZE == QUAD_SIZED_RUNES) {
							img.setRGB(i, currentRow, bgColor);
							img.setRGB(i+1, currentRow, bgColor);
							img.setRGB(i, currentRow+1, bgColor); // do these to the next row as well, so we are twice as tall
							img.setRGB(i+1, currentRow+1, bgColor); // do these to the next row as well, so we are twice as tall
							if (I_RUNE_SIZE == QUAD_SIZED_RUNES) {
								// double it lengthwise for the origin row and the doubled row
								img.setRGB(i+2, currentRow, bgColor);
								img.setRGB(i+3, currentRow, bgColor);
								img.setRGB(i+2, currentRow+1, bgColor);
								img.setRGB(i+3, currentRow+1, bgColor);
								
								// then repeat all the pixels on the next 2 rows
								img.setRGB(i, currentRow+2, bgColor);
								img.setRGB(i+1, currentRow+2, bgColor);
								img.setRGB(i+2, currentRow+2, bgColor);
								img.setRGB(i+3, currentRow+2, bgColor);
								img.setRGB(i, currentRow+3, bgColor);
								img.setRGB(i+1, currentRow+3, bgColor);
								img.setRGB(i+2, currentRow+3, bgColor);
								img.setRGB(i+3, currentRow+3, bgColor);
								i+=2;
							}
							i++;
						} else {
							img.setRGB(i, currentRow, bgColor);
						}
					} else {
						if (I_RUNE_SIZE == DOUBLE_SIZED_RUNES || I_RUNE_SIZE == QUAD_SIZED_RUNES) {
							img.setRGB(i, currentRow, runeColor);
							img.setRGB(i+1, currentRow, runeColor);
							img.setRGB(i, currentRow+1, runeColor); // do these to the next row as well, so we are twice as tall
							img.setRGB(i+1, currentRow+1, runeColor); // do these to the next row as well, so we are twice as tall
							if (I_RUNE_SIZE == QUAD_SIZED_RUNES) {
								// double it lengthwise for the origin row and the doubled row
								img.setRGB(i+2, currentRow, runeColor);
								img.setRGB(i+3, currentRow, runeColor);
								img.setRGB(i+2, currentRow+1, runeColor);
								img.setRGB(i+3, currentRow+1, runeColor);
								
								// then repeat all the pixels on the next 2 rows
								img.setRGB(i, currentRow+2, runeColor);
								img.setRGB(i+1, currentRow+2, runeColor);
								img.setRGB(i+2, currentRow+2, runeColor);
								img.setRGB(i+3, currentRow+2, runeColor);
								img.setRGB(i, currentRow+3, runeColor);
								img.setRGB(i+1, currentRow+3, runeColor);
								img.setRGB(i+2, currentRow+3, runeColor);
								img.setRGB(i+3, currentRow+3, runeColor);
								i+=2;
							}
							i++;
						} else {
							img.setRGB(i, currentRow, runeColor);
						}
					}
						
					++totalPixels;
					if (totalPixels == totalRead) {
						if (totalPixels < RUNE_DIMEN) {
							// now to grab the next token and swap color. note, dont actually check for an 'r' or 'b' char
							isBg = !isBg;
							String nextToken = st.nextToken();
							int tokenAmt = Integer.parseInt(nextToken.substring(0, nextToken.length()-1));
							totalRead += tokenAmt;
						}
					}
				}
				
				++currentRow;
				if (I_RUNE_SIZE == DOUBLE_SIZED_RUNES || I_RUNE_SIZE == QUAD_SIZED_RUNES) {
					++currentRow; // need to increment again, since we filled in 2 rows at once when painting to double it
					if (I_RUNE_SIZE == QUAD_SIZED_RUNES) {
						currentRow += 2; // to make up for doubling the size... again.
					}
				}
			}
			br.close();
		} catch (Exception e) {
			addText("Error trying to read file: " + filename + " - Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	
	
	// log stuff
	public void addText(String text) {
		
		// log everything before displaying in gui
		// TODO we dont technically ever close the buffered writer... we could open and close every write? use param that lets us append.. meh.
		//      see also: addLogOnly, more writing to the bw we never close.
		String logPath = "";
		if (!logInitialized) {
			try {
				// wipe the current log (if exists) and create the new log
				logPath = System.getProperty("user.dir") + "\\RuneQuizzerLog.txt";
				logWriter = new BufferedWriter(new FileWriter(new File(logPath)));
				String timeStamp = generateTimestamp();
				logWriter.write(timeStamp + "| " + text);
				logWriter.newLine();
				String successText = "Creating Log File at: " + logPath;
				text += "\n" + successText + "\n";
				logWriter.write(timeStamp + "| " + successText);
				logWriter.newLine();
				logWriter.flush();
			} catch (Exception e) {
				text += "NOTE: THE LOG COULD NOT BE INITIALIZED. THERE WILL BE NO LOGGING FOR THIS SESSION\nATTEMPTED TO CREATE LOG AT: [" + logPath + "]\n";
				text += "Error was: " + e.getMessage() + "\n";
			} finally {
				// regardless of pass or fail, only try to create the log once. if it failed, the gui will announce nothing will be logged this session
				logInitialized = true;
			}
		} else {
			try {
				String timeStamp = generateTimestamp();
				String[] lines = text.split("[\r\n]+");
				// need to manually insert a newline between each line
				for (int x = 0; x < lines.length; ++x) {
					logWriter.write(timeStamp + "| " + lines[x]);
					logWriter.newLine();
				}
				logWriter.flush();
			} catch (Exception e) {
				addText("Failure to write to logfile: " + e.getMessage());
			}
		}
		
		text = "*************************\n" + text;
		
		// Old style chopped everything off after x lines. Debated switching to a scrollpane, would need to make it stop resizing and focus on the bottom.
		// If we change that that, we only need the below line.
		//systemOut.append(text);
		
		
		// If the string passed in contains newlines, we need to break the string up into pieces based on \n, and loop over this code once per line.
		// Otherwise, if we just add \n to the text area, it will increase in size. We need to make sure we remove a line every time.
		String[] lines = text.split("[\r\n]+");
		
		String contents = "";
		for (int x = 0; x < lines.length; ++x) {
			contents = systemOut.getText();
			contents = contents.substring(contents.indexOf("\n")+1);
			contents += lines[x] + "\n";
			systemOut.setText(contents);
		}
		
	}

	public String generateTimestamp() {
		String path = "";
		Calendar cal = Calendar.getInstance(); // get a timestamp so we can have a unique file name
		path += cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "  " +
				cal.get(Calendar.HOUR) + "h" + cal.get(Calendar.MINUTE) + "m" + cal.get(Calendar.SECOND) + "s";
		if (cal.get(Calendar.AM_PM) == Calendar.AM) {
			path += "AM";
		} else { // must be PM
			path += "PM";
		}
		return path;
	}
	
	
	public class SetupException extends Exception {
		private static final long serialVersionUID = 671109248399282501L;
		public SetupException(String message) {
			super(message);
		}
	}
}
