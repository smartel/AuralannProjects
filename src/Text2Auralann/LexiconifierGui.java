package Text2Auralann;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LexiconifierGui extends Lexiconifier {
	
	private class Lexidere extends JFrame {
		
		private static final long serialVersionUID = -4265431481957089410L;
		public static final String READY = "READY!";
		public static final String NOT_READY = "NOT READY!";
		
		private BufferedWriter logWriter;
		private boolean logInitialized;
		
		private JTabbedPane tabPane;
		private JTextArea systemOut;
		private JPanel initTab, lexiTab, rdTab, runeTab, auraTab, displayTab, setTab;
		private boolean setSettings, setRd, setOD;
		// lexify buttons - foreground changes to red on failures, like missing rune dictionary entries when attempting to lexify
		private JButton lexTextBtn, lexImageBtn, lexBothBtn;
		
		// these fields/labels get updated based on buttons being hit (sometimes in other panels, like initialization):
		private JTextField outField; // initialization tab, output dir field, updated when loading in from a settings file
		private JTextField rdField; // initialization tab, rune dictionary field, updated when loading in from a settings file
		
		public Lexidere(Settings settings) {
			
			// initialization before allowing lexification
			setSettings = false; // settings
			setRd = false; // rune dictionary
			setOD = false; // output directory
			
			setTitle("Auralann Lexiconifier");
			setSize(750, 600);
			
			tabPane = new JTabbedPane();
			initTab = getInitPanel();
			
			
			tabPane.addTab("Initialization", initTab);
			addTemporaryTabs();
			
			
			
			
			this.setLayout(new BorderLayout());
			this.add(tabPane, BorderLayout.CENTER);

			logInitialized = false;
			systemOut = new JTextArea("\n\n\n\n\n\n\n\n\n\n\n");
			addText("Lexiconifier started"); // so it gets logged
			this.add(new JScrollPane(systemOut), BorderLayout.SOUTH);
		}
		
		public JPanel getInitPanel() {
			JPanel panel = new JPanel();
			
			// top row of labels
			//JLabel label1 = new JLabel("Lexiconifier Initialization Tab");
			JLabel label1 = new JLabel("Initialize these values before lexifying text!");
			label1.setHorizontalAlignment(JLabel.CENTER);
			//JLabel label2 = new JLabel("Initialize these values before lexifying text!");
			JLabel label2 = new JLabel("Lexiconifier Status:");
			label2.setHorizontalAlignment(JLabel.RIGHT);
			//JLabel label3 = new JLabel("Status: NOT READY");
			JLabel label3 = new JLabel(NOT_READY);
			label3.setForeground(Color.RED);
			label3.setHorizontalAlignment(JLabel.CENTER);
			
			// buttons for highlighting colors
			JButton loadSettingsBtn = new JButton("Load this Settings File!");
			JButton dbBtn = new JButton("Use Beginner Default Settings!");
			JButton deBtn = new JButton("Use Expert Default Settings!");
			JButton loadRdBtn = new JButton("Load this Rune Dictionary!");
			JButton outBtn = new JButton("Set Output Directory!");
			
			// ask for settings file
			JLabel label4 = new JLabel("Load a Settings File:");
			label4.setHorizontalAlignment(JLabel.CENTER);
			JTextField settingsField = new JTextField(personalPath);
			loadSettingsBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {

					File file = new File(settingsField.getText());
					if (file.isFile()) {
						settings.setSettingsPath(settingsField.getText());
						String msgs = guiLoadFileSettings(settings, new Scanner(System.in));
						if (msgs.contains("Error")) {
							addText(msgs);
							loadSettingsBtn.setForeground(Color.RED);
						} else {
							setSettings = true;
							// settings file (not default settings) also sets the rune dictionary and output dir paths on the buttons, and loaded them in guiLoadFileSettings
							setRd = true;
							setOD = true;
							rdField.setText(settings.getBaseImageDirectory());
							
							outField.setText(settings.getOutputDirectory());
							addText("Program Settings set!   From file: " + settingsField.getText() + "\nRune Dictionary set!   Path: " + settings.getBaseImageDirectory() + "\n" + msgs + "\nOutput Directory set!   Path: " + settings.getOutputDirectory());
							checkIfReady(label3);
							loadSettingsBtn.setForeground(Color.BLUE);
							loadRdBtn.setForeground(Color.BLUE);
							outBtn.setForeground(Color.BLUE);
							dbBtn.setForeground(Color.BLACK);
							deBtn.setForeground(Color.BLACK);
						}
					} else {
						addText("Could not find a Settings File to use at: " + settingsField.getText() + "\n");
						loadSettingsBtn.setForeground(Color.RED);
					}
				}
			});
			
			// alternatively, hit a default settings button!
			JLabel label5 = new JLabel("Alternatively, use Default Settings:");
			label5.setHorizontalAlignment(JLabel.CENTER);
			dbBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					loadDefaultSettingsBeginner(settings);
					setSettings = true;
					addText("Program Settings set! (Beginner)");
					checkIfReady(label3);
					dbBtn.setForeground(Color.BLUE);
					deBtn.setForeground(Color.BLACK);
					loadSettingsBtn.setForeground(Color.BLACK);
				}
			});
			deBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					loadDefaultSettingsExpert(settings);
					setSettings = true;
					addText("Program Settings set! (Expert)");
					checkIfReady(label3);
					deBtn.setForeground(Color.BLUE);
					dbBtn.setForeground(Color.BLACK);
					loadSettingsBtn.setForeground(Color.BLACK);
				}
			});
			
			// rune dictionary row
			JLabel label6 = new JLabel("Load a Rune Dictionary:");
			label6.setHorizontalAlignment(JLabel.CENTER);
			rdField = new JTextField(defBaseDir);
			loadRdBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					File file = new File(rdField.getText());
					if (file.isDirectory()) {
						String msgs = settings.setImageDirForGui(rdField.getText());
						if (msgs.contains("Error")) {
							addText("Loading Rune Dictionary...\n" + msgs);
							loadRdBtn.setForeground(Color.RED);
						} else {
							setRd = true;
							addText("Loading Rune Dictionary...\n" + msgs + "Rune Dictionary set!   Path: " + rdField.getText());
							checkIfReady(label3);
							loadRdBtn.setForeground(Color.BLUE);
						}
					} else {
						addText("Error with " + Settings.STR_RUNE_DICTIONARY_LOCATION + ": value is not a valid directory. Value was: " + rdField.getText() + "\n");
						loadRdBtn.setForeground(Color.RED);
					}
				}
			});
			
			// output dir row
			JLabel label7 = new JLabel("Set the Output Directory:");
			label7.setHorizontalAlignment(JLabel.CENTER);
			outField = new JTextField(outputDir);
			outBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					File file = new File(outField.getText());
					if (file.isDirectory()) {
						settings.setOutputDirectory(outField.getText());
						setOD = true;
						addText("Output Directory set!   Path: " + outField.getText());
						checkIfReady(label3);
						outBtn.setForeground(Color.BLUE);
					} else {
						addText("Error with " + Settings.STR_OUTPUT_DIR_LOCATION + ": value is not a valid directory. Value was: " + outField.getText() + "\n");
						outBtn.setForeground(Color.RED);
					}
				}
			});
			

			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();	
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0.0;
			
			// header row
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(label3);
			panel.add(headerPanel, cons);
			cons.weighty = 0.5;
			
			// spacing
			cons.gridy = 1;
			panel.add(new JLabel(""), cons);
			
			cons.gridy = 2;
			cons.ipady = 25;
			JPanel settingsPanel = new JPanel();
			settingsPanel.setLayout(new GridLayout(2, 3));
			settingsPanel.add(label4);
			settingsPanel.add(new JScrollPane(settingsField));
			settingsPanel.add(loadSettingsBtn);
			settingsPanel.add(label5);
			settingsPanel.add(dbBtn);
			settingsPanel.add(deBtn);
			settingsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			panel.add(settingsPanel, cons);
			
			// spacing
			cons.ipady = 0;
			cons.weighty = 0.5;
			cons.gridy = 3;
			panel.add(new JLabel(""), cons);
			
			// rune dictionary row
			cons.gridy = 4;
			cons.ipady = 15;
			JPanel rdPanel = new JPanel();
			rdPanel.setLayout(new GridLayout(1,3));
			rdPanel.add(label6);
			rdPanel.add(new JScrollPane(rdField));
			rdPanel.add(loadRdBtn);
			rdPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			panel.add(rdPanel, cons);
			
			// spacing
			cons.gridy = 5;
			cons.ipady = 0;
			panel.add(new JLabel(""), cons);
			
			// output dir row
			cons.gridy = 6;
			cons.ipady = 15;
			JPanel outPanel = new JPanel();
			outPanel.setLayout(new GridLayout(1,3));
			outPanel.add(label7);
			outPanel.add(new JScrollPane(outField));
			outPanel.add(outBtn);
			outPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			panel.add(outPanel, cons);
			
			return panel;
		}
		
		public JPanel getLexifyPanel() {
			JPanel panel = new JPanel();
			JTextArea lexifyArea = new JTextArea("Input text to lexify here...", 5, 20);
			JTextArea resultsArea = new JTextArea("Text output will go here...", 8, 20);
			resultsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
			
			// header row
			JLabel label1 = new JLabel("Lexify Text to Auralann!");
			JLabel label2 = new JLabel("Lexify Status:");
			JLabel label3 = new JLabel(READY);
			label2.setHorizontalAlignment(JLabel.RIGHT);
			label3.setForeground(Color.BLUE);
			label3.setHorizontalAlignment(JLabel.CENTER);
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(label3);
			
			lexTextBtn = new JButton("Lexify to Text File!");
			lexTextBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Lexifying and saving to text file...\n");
					addLogOnly("Lexifying with these settings...\n" + getAbridgedSettingsString(settings, false));
					if (lexifyArea.getText().isEmpty()) {
						addText("No valid text to lexify!");
					} else {
						String results = lexifyTextReturnOutput(lexifyArea.getText(), settings, true, false);
						resultsArea.setText(results);
					}
				}
			});
			lexImageBtn = new JButton("Lexify to Image File!");
			lexImageBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Lexifying and saving to image file...\n");
					addLogOnly("Lexifying with these settings...\n" + getAbridgedSettingsString(settings, false));
					if (lexifyArea.getText().isEmpty()) {
						addText("No valid text to lexify!");
					} else {
						String results = lexifyTextReturnOutput(lexifyArea.getText(), settings, false, true);
						resultsArea.setText(results);
					}
				}
			});
			lexBothBtn = new JButton("Lexify to Text & Image Files!");
			lexBothBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Lexifying and saving to text and image files...\n");
					addLogOnly("Lexifying with these settings...\n" + getAbridgedSettingsString(settings, false));
					if (lexifyArea.getText().isEmpty()) {
						addText("No valid text to lexify!");
					} else {
						String results = lexifyTextReturnOutput(lexifyArea.getText(), settings, true, true);
						resultsArea.setText(results);
					}
				}
			});
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(1,3));
			buttonPanel.add(lexTextBtn);
			buttonPanel.add(lexImageBtn);
			buttonPanel.add(lexBothBtn);
			
			JScrollPane lexScroll = new JScrollPane(lexifyArea);
			JScrollPane resultsScroll = new JScrollPane(resultsArea);
			resultsArea.setFocusable(false);
			
			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();

			// header row
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0;
			panel.add(headerPanel, cons);
			
			

			// area label 1
			cons.gridy = 1;			
			cons.weighty = 0.5;
			JPanel areaPanel = new JPanel();
			JLabel lexLabel = new JLabel("Text to Lexify:");
			JLabel dedLabel = new JLabel("(Use ^ to insert Dead Runes)");
			lexLabel.setHorizontalAlignment(JLabel.LEFT);
			dedLabel.setHorizontalAlignment(JLabel.LEFT);
			areaPanel.setLayout(new GridLayout(1,3));
			areaPanel.add(lexLabel);
			areaPanel.add(dedLabel);
			areaPanel.add(new JLabel(""));
			panel.add(areaPanel, cons);
			
			
			// text to lexify row
			cons.gridy = 2;
			panel.add(lexScroll, cons);

			// button row
			cons.gridy = 3;
			panel.add(buttonPanel, cons);

			// area label 2
			cons.gridy = 4;			
			cons.weighty = 0.5;
			panel.add(new JLabel("Lexify text results (Image results will be made lucent in the Videre window):"), cons);
			
			// text to lexify row
			cons.gridy = 5;
			panel.add(resultsScroll, cons);
	
			return panel;
		}

		public JPanel getRuneDictionaryPanel() {
			
			JPanel panel = new JPanel();
			
			// top row of labels
			JLabel label1 = new JLabel("Rune Dictionary Contents!");
			JLabel label2 = new JLabel("Rune Dictionary Status / Location:");
			label2.setHorizontalAlignment(JLabel.CENTER);
			JLabel rdReadyStatus = new JLabel(settings.getBaseImageDirectory());
			rdReadyStatus.setForeground(Color.BLUE);
			rdReadyStatus.setHorizontalAlignment(JLabel.CENTER);
			
			// The functionality of the text interface (minus the ability to change the file path) is:
			// 		Display Stats, Display Singles / Doubles / Triples / Quads / Numeric / Symbol / Special
			
			// Stats button
			JButton statsBtn = new JButton("Display Rune Dictionary Stats!");
			statsBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Rune Stats...\n" + settings.getRuneDictionary().getRdStats(false));
				}
			});

			// Singles button
			JButton singlesBtn = new JButton("Display Single Runes!");
			singlesBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Single Runes...\n" + settings.getRuneDictionary().getContents(RuneDictionary.SINGLE_RDES, 15));
				}
			});
			
			// Doubles button
			JButton doublesBtn = new JButton("Display Double Runes!");
			doublesBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Double Runes...\n" + settings.getRuneDictionary().getContents(RuneDictionary.DOUBLE_RDES, 15));
				}
			});
			
			// Triples button
			JButton triplesBtn = new JButton("Display Triple Runes!");
			triplesBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Triple Runes...\n" + settings.getRuneDictionary().getContents(RuneDictionary.TRIPLE_RDES, 15));
				}
			});
			
			// Quads button
			JButton quadsBtn = new JButton("Display Quad Runes!");
			quadsBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Quad Runes...\n" + settings.getRuneDictionary().getContents(RuneDictionary.QUAD_RDES, 15));
				}
			});
			
			// Numerics button
			JButton numericsBtn = new JButton("Display Numeric Runes!");
			numericsBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Numeric Runes...\n" + settings.getRuneDictionary().getContents(RuneDictionary.NUMBER_RDES, 15));
				}
			});
			
			// Symbols button
			JButton symbolsBtn = new JButton("Display Symbol Runes!");
			symbolsBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Symbol Runes...\n" + settings.getRuneDictionary().getContents(RuneDictionary.SYMBOL_RDES, 15));
				}
			});
			
			// Specials button
			JButton specialsBtn = new JButton("Display Special Runes!");
			specialsBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Displaying Special Runes...\n" + settings.getRuneDictionary().getContents(RuneDictionary.SPECIAL_RDES, 15));
				}
			});
			
			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();	
			
			// header row
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0.0;
			cons.ipady = 12;
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(new JScrollPane(rdReadyStatus));
			panel.add(headerPanel, cons);
			cons.weighty = 0.5;
			cons.ipady = 0;
			
			// stats row
			cons.gridy = 1;
			cons.weightx = 0.5;
			JPanel statsPanel = new JPanel();
			statsPanel.setLayout(new GridLayout(1,3));
			statsPanel.add(new JLabel(""));
			statsPanel.add(statsBtn);
			statsPanel.add(new JLabel(""));
			panel.add(statsPanel, cons);
			
			// single/double/triple row
			JPanel btnsPanel1 = new JPanel();
			btnsPanel1.setLayout(new GridLayout(1,3));
			btnsPanel1.add(singlesBtn);
			btnsPanel1.add(doublesBtn);
			btnsPanel1.add(triplesBtn);
			cons.gridy = 2;
			panel.add(btnsPanel1, cons);
			
			// quad/numeric/symbol row
			JPanel btnsPanel2 = new JPanel();
			btnsPanel2.setLayout(new GridLayout(1,3));
			btnsPanel2.add(quadsBtn);
			btnsPanel2.add(numericsBtn);
			btnsPanel2.add(symbolsBtn);
			cons.gridy = 3;
			panel.add(btnsPanel2, cons);
	
			// special row
			cons.gridy = 4;
			cons.weightx = 0.5;
			JPanel specialPanel = new JPanel();
			specialPanel.setLayout(new GridLayout(1,3));
			specialPanel.add(new JLabel(""));
			specialPanel.add(specialsBtn);
			specialPanel.add(new JLabel(""));
			panel.add(specialPanel, cons);
			
			return panel;
		}
		
		
		public JPanel getRuneSettingsPanel() {
			JPanel panel = new JPanel();
			
			// header row
			JLabel label1 = new JLabel("Set Rune Settings!");
			JLabel label2 = new JLabel("Rune Settings Status:");
			JLabel label3 = new JLabel(READY);
			label2.setHorizontalAlignment(JLabel.RIGHT);
			label3.setForeground(Color.BLUE);
			label3.setHorizontalAlignment(JLabel.CENTER);

			JLabel label4 = new JLabel("Random Dead Chance (%):");
			label4.setHorizontalAlignment(JLabel.CENTER);
			
			JLabel label5 = new JLabel("Alternate Runes:");
			label5.setHorizontalAlignment(JLabel.CENTER);
			
			
			
			// toggles
			JButton doublesBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_DOUBLES)) {
				doublesBtn = new JButton("Rune Doubles: ON");
				doublesBtn.setForeground(Color.BLUE);
			} else {
				doublesBtn = new JButton("Rune Doubles: OFF");
				doublesBtn.setForeground(Color.RED);
			}
			doublesBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (doublesBtn.getForeground().equals(Color.RED)) {
						doublesBtn.setForeground(Color.BLUE);
						doublesBtn.setText("Rune Doubles: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_DOUBLES, true);
						addText("Turning Rune Doubles ON");
					} else {
						doublesBtn.setForeground(Color.RED);
						doublesBtn.setText("Rune Doubles: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_DOUBLES, false);
						addText("Turning Rune Doubles OFF");
					}
				}
			});
			
			JButton triplesBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_TRIPLES)) {
				triplesBtn = new JButton("Rune Triples: ON");
				triplesBtn.setForeground(Color.BLUE);
			} else {
				triplesBtn = new JButton("Rune Triples: OFF");
				triplesBtn.setForeground(Color.RED);
			}
			triplesBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (triplesBtn.getForeground().equals(Color.RED)) {
						triplesBtn.setForeground(Color.BLUE);
						triplesBtn.setText("Rune Triples: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_TRIPLES, true);
						addText("Turning Rune Triples ON");
					} else {
						triplesBtn.setForeground(Color.RED);
						triplesBtn.setText("Rune Triples: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_TRIPLES, false);
						addText("Turning Rune Triples OFF");
					}
				}
			});
			
			JButton quadsBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_QUADS)) {
				quadsBtn = new JButton("Rune Quads: ON");
				quadsBtn.setForeground(Color.BLUE);
			} else {
				quadsBtn = new JButton("Rune Quads: OFF");
				quadsBtn.setForeground(Color.RED);
			}
			quadsBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (quadsBtn.getForeground().equals(Color.RED)) {
						quadsBtn.setForeground(Color.BLUE);
						quadsBtn.setText("Rune Quads: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_QUADS, true);
						addText("Turning Rune Quads ON");
					} else {
						quadsBtn.setForeground(Color.RED);
						quadsBtn.setText("Rune Quads: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_QUADS, false);
						addText("Turning Rune Quads OFF");
					}
				}
			});
			
			JButton possBtn;
			if (settings.getBoolSetting(Settings.B_POSSESSIVE_S)) {
				possBtn = new JButton("Possessive Rune: ON");
				possBtn.setForeground(Color.BLUE);
			} else {
				possBtn = new JButton("Possessive Rune: OFF");
				possBtn.setForeground(Color.RED);
			}
			possBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (possBtn.getForeground().equals(Color.RED)) {
						possBtn.setForeground(Color.BLUE);
						possBtn.setText("Possessive Rune: ON");
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, true);
						addText("Turning Possessive S Rune ON");
					} else {
						possBtn.setForeground(Color.RED);
						possBtn.setText("Possessive Rune: OFF");
						settings.changeBoolSetting(Settings.B_POSSESSIVE_S, false);
						addText("Turning Possessive S Rune OFF");
					}
				}
			});
			
			JButton pluralBtn;
			if (settings.getBoolSetting(Settings.B_PLURAL_S)) {
				pluralBtn = new JButton("Plural Rune: ON");
				pluralBtn.setForeground(Color.BLUE);
			} else {
				pluralBtn = new JButton("Plural Rune: OFF");
				pluralBtn.setForeground(Color.RED);
			}
			pluralBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (pluralBtn.getForeground().equals(Color.RED)) {
						pluralBtn.setForeground(Color.BLUE);
						pluralBtn.setText("Plural Rune: ON");
						settings.changeBoolSetting(Settings.B_PLURAL_S, true);
						addText("Turning Plural S Rune ON");
					} else {
						pluralBtn.setForeground(Color.RED);
						pluralBtn.setText("Plural Rune: OFF");
						settings.changeBoolSetting(Settings.B_PLURAL_S, false);
						addText("Turning Plural S Rune OFF");
					}
				}
			});
			
			// Note - this logic is a bit backwards. If the filter is ON, the end runes are OFF, and vice versa
			JButton endsBtn;
			if (!settings.getBoolSetting(Settings.B_FILTER_ENDS)) {
				endsBtn = new JButton("End Runes: ON");
				endsBtn.setForeground(Color.BLUE);
			} else {
				endsBtn = new JButton("End Runes: OFF");
				endsBtn.setForeground(Color.RED);
			}
			endsBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (endsBtn.getForeground().equals(Color.RED)) {
						endsBtn.setForeground(Color.BLUE);
						endsBtn.setText("End Runes: ON");
						settings.changeBoolSetting(Settings.B_FILTER_ENDS, false); // notice, logic is a bit flipped here: if FILTER is OFF, END RUNES are PRESENT
						addText("Turning End Runes ON");
					} else {
						endsBtn.setForeground(Color.RED);
						endsBtn.setText("End Runes: OFF");
						settings.changeBoolSetting(Settings.B_FILTER_ENDS, true); // notice, logic is a bit flipped here: if FILTER is ON, END RUNES are GONE
						addText("Turning End Runes OFF");
					}
				}
			});
			
			JButton aiBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_AI_DEAD)) {
				aiBtn = new JButton("Dead A/I Runes: ON");
				aiBtn.setForeground(Color.BLUE);
			} else {
				aiBtn = new JButton("Dead A/I Runes: OFF");
				aiBtn.setForeground(Color.RED);
			}
			aiBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (aiBtn.getForeground().equals(Color.RED)) {
						aiBtn.setForeground(Color.BLUE);
						aiBtn.setText("Dead A/I Runes: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
						addText("Turning Dead A/I Rune ON");
					} else {
						aiBtn.setForeground(Color.RED);
						aiBtn.setText("Dead A/I Runes: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						addText("Turning Dead A/I Rune OFF");
					}
				}
			});
			
			JButton aposBtn;
			if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == 0) {
				aposBtn = new JButton("Dead Apos Runes: OFF");
				aposBtn.setForeground(Color.RED);
			} else if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == 1) {
				aposBtn = new JButton("Dead Apos Runes: As needed");
				aposBtn.setForeground(Color.BLUE);
			} else if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == 2) {
				aposBtn = new JButton("Dead Apos Runes: Always Left");
				aposBtn.setForeground(Color.BLUE);
			} else if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == 3) {
				aposBtn = new JButton("Dead Apos Runes: Always Right");
				aposBtn.setForeground(Color.BLUE);
			} else { //if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == 4) {
				aposBtn = new JButton("Dead Apos Runes: Always Both");
				aposBtn.setForeground(Color.BLUE);
			}
			aposBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, increment the value by 1, swap colors when we get to 0
					int aposVal = settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD);
					aposVal++;
					if (aposVal > Settings.APOS_BOTH) {
						aposVal = Settings.APOS_NONE;
					}
					settings.changeIntSetting(Settings.I_ALLOW_APOS_DEAD, aposVal);
					
					if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == Settings.APOS_NONE) {
						addText("Setting Apos Dead Runes to: OFF");
						aposBtn.setText("Dead Apos Runes: OFF");
						aposBtn.setForeground(Color.RED);
					} else if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == Settings.APOS_NEEDED) {
						addText("Setting Apos Dead Runes to: As Needed");
						aposBtn.setText("Dead Apos Runes: As needed");
						aposBtn.setForeground(Color.BLUE);
					} else if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == Settings.APOS_LEFT) {
						addText("Setting Apos Dead Runes to: Always Left");
						aposBtn.setText("Dead Apos Runes: Always Left");
						aposBtn.setForeground(Color.BLUE);
					} else if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == Settings.APOS_RIGHT) {
						addText("Setting Apos Dead Runes to: Always Right");
						aposBtn.setText("Dead Apos Runes: Always Right");
						aposBtn.setForeground(Color.BLUE);
					} else { //if (settings.getIntSetting(Settings.I_ALLOW_APOS_DEAD) == Settings.APOS_BOTH) {
						addText("Setting Apos Dead Runes to: Always Both");
						aposBtn.setText("Dead Apos Runes: Always Both");
						aposBtn.setForeground(Color.BLUE);
					}

				}
			});
			
			JButton orlBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_COMMON_DEAD)) {
				orlBtn = new JButton("Common One-Rune Lore Dead: ON");
				orlBtn.setForeground(Color.BLUE);
			} else {
				orlBtn = new JButton("Common One-Rune Lore Dead: OFF");
				orlBtn.setForeground(Color.RED);
			}
			orlBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (orlBtn.getForeground().equals(Color.RED)) {
						orlBtn.setForeground(Color.BLUE);
						orlBtn.setText("Common One-Rune Lore Dead: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true);
						addText("Turning Common One-Rune Lore Dead ON");
					} else {
						orlBtn.setForeground(Color.RED);
						orlBtn.setText("Common One-Rune Lore Dead: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						addText("Turning Common One-Rune Lore Dead OFF");
					}
				}
			});
			
			JButton randBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_RANDOM_DEAD)) {
				randBtn = new JButton("Random Dead Runes: ON");
				randBtn.setForeground(Color.BLUE);
			} else {
				randBtn = new JButton("Random Dead Runes: OFF");
				randBtn.setForeground(Color.RED);
			}
			randBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (randBtn.getForeground().equals(Color.RED)) {
						randBtn.setForeground(Color.BLUE);
						randBtn.setText("Random Dead Runes: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, true);
						addText("Turning Random Dead Runes ON");
					} else {
						randBtn.setForeground(Color.RED);
						randBtn.setText("Random Dead Runes: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
						addText("Turning Random Dead Runes OFF");
					}
				}
			});
			
			JTextField chanceField = new JTextField(Settings.RANDOM_DEAD_CHANCE_DEFAULT + "");
			
			JButton setChanceBtn = new JButton("Set Dead Chance!");
			setChanceBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// If Random Dead Runes was turned off, turn it on
					if (randBtn.getForeground().equals(Color.RED)) {
						addText("Setting Dead Rune Chance to " + chanceField.getText() + "%\nTurning Random Dead Runes ON");
						randBtn.setForeground(Color.BLUE);
						randBtn.setText("Random Dead Runes: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, true);
					} else {
						addText("Setting Dead Rune Chance to " + chanceField.getText() + "%");
					}
					try {
						int amt = Integer.parseInt(chanceField.getText());
						if (amt < 0) {
							addText("Invalid valid. Must be greater than 0. Dead Rune Chance will not be changed.");
						} else if (amt > 100) {
							addText("Value was greater than 100%, so Dead Rune Chance will be set to 100%.");
							settings.changeIntSetting(Settings.I_RANDOM_DEAD_CHANCE, 100);
						} else {
							settings.changeIntSetting(Settings.I_RANDOM_DEAD_CHANCE, amt);
						}
					} catch (NumberFormatException e) {
						addText("Invalid value. Dead Rune Chance will not be changed.");
					}
				}
			});
			
			// massive radio section
			JRadioButton radioNone = new JRadioButton("Alt Runes: No Alts");
			radioNone.setMnemonic(KeyEvent.VK_B);
			radioNone.setSelected(false);
			radioNone.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Alt Mode to No Alternate Runes");
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_NO_ALTS);
				}
			});
			JRadioButton radioEqual = new JRadioButton("Alt Runes: Equal Chance");
			radioEqual.setMnemonic(KeyEvent.VK_B);
			radioEqual.setSelected(false);
			radioEqual.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Alt Mode to Equal Chance for all Forms");
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_EQUAL_CHANCE);
				}
			});
			JRadioButton radioAltAlt = new JRadioButton("Alt Runes: Alternate Alts");
			radioAltAlt.setMnemonic(KeyEvent.VK_B);
			radioAltAlt.setSelected(false);
			radioAltAlt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Alt Mode to Alternate through Alts");
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALT_ALTS);
				}
			});
			JRadioButton radioAlways = new JRadioButton("Alt Runes: Always Alt");
			radioAlways.setMnemonic(KeyEvent.VK_B);
			radioAlways.setSelected(false);
			radioAlways.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Alt Mode to Always use Alternates");
					settings.changeIntSetting(Settings.I_ALT_MODE, Settings.ALTS_ALTS_ONLY);
				}
			});
			ButtonGroup group = new ButtonGroup();
			group.add(radioNone);
			group.add(radioEqual);
			group.add(radioAltAlt);
			group.add(radioAlways);
			
			int radioToSet = settings.getIntSetting(Settings.I_ALT_MODE);
			if (radioToSet == Settings.ALTS_NO_ALTS) {
				radioNone.setSelected(true);
			} else if (radioToSet == Settings.ALTS_EQUAL_CHANCE) {
				radioEqual.setSelected(true);
			} else if (radioToSet == Settings.ALTS_ALT_ALTS) {
				radioAltAlt.setSelected(true);
			} else if (radioToSet == Settings.ALTS_ALTS_ONLY) {
				radioAlways.setSelected(true);
			}
			
			
			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();	

			// header row
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0.0;
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(label3);
			panel.add(headerPanel, cons);
			
			cons.gridy = 1;
			cons.weighty = 0.5;
			JPanel btnsPanel1 = new JPanel();
			btnsPanel1.setLayout(new GridLayout(1,3));
			btnsPanel1.add(doublesBtn);
			btnsPanel1.add(triplesBtn);
			btnsPanel1.add(quadsBtn);
			panel.add(btnsPanel1, cons);
			
			cons.gridy = 2;
			JPanel btnsPanel2 = new JPanel();
			btnsPanel2.setLayout(new GridLayout(1,3));
			btnsPanel2.add(possBtn);
			btnsPanel2.add(pluralBtn);
			btnsPanel2.add(endsBtn);
			panel.add(btnsPanel2, cons);
			
			cons.gridy = 3;
			JPanel btnsPanel3 = new JPanel();
			btnsPanel3.setLayout(new GridLayout(1,3));
			btnsPanel3.add(aiBtn);
			btnsPanel3.add(aposBtn);
			btnsPanel3.add(orlBtn);
			panel.add(btnsPanel3, cons);
			
			cons.gridy = 4;
			JPanel btnsPanel4 = new JPanel();
			btnsPanel4.setLayout(new GridLayout(2,3));
			btnsPanel4.add(randBtn);
			btnsPanel4.add(new JLabel(""));
			btnsPanel4.add(new JLabel(""));
			btnsPanel4.add(label4);
			btnsPanel4.add(chanceField);
			btnsPanel4.add(setChanceBtn);
			btnsPanel4.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			panel.add(btnsPanel4, cons);
			
			cons.gridy = 6;
			JPanel btnsPanel5 = new JPanel();
			btnsPanel5.setLayout(new GridLayout(2,3));
			btnsPanel5.add(label5);
			btnsPanel5.add(radioNone);
			btnsPanel5.add(radioEqual);
			btnsPanel5.add(new JLabel(""));
			btnsPanel5.add(radioAltAlt);
			btnsPanel5.add(radioAlways);
			btnsPanel5.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			panel.add(btnsPanel5, cons);
			
			return panel;
		}
		
		public JPanel getAuraSettingsPanel() {
			JPanel panel = new JPanel();
			
			// header row
			JLabel label1 = new JLabel("Set Aura Settings!");
			JLabel label2 = new JLabel("Aura Settings Status:");
			JLabel label3 = new JLabel(READY);
			label2.setHorizontalAlignment(JLabel.RIGHT);
			label3.setForeground(Color.BLUE);
			label3.setHorizontalAlignment(JLabel.CENTER);
			
			JLabel label4 = new JLabel("Set Major Style:");
			//label4.setHorizontalAlignment(JLabel.CENTER); // this one can stay, looks better this way

			JRadioButton radioNone = new JRadioButton("Major Style: No Majors");
			radioNone.setMnemonic(KeyEvent.VK_B);
			radioNone.setSelected(false);
			radioNone.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Major Style to No Majors or Meagers");
					settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_NONE);
				}
			});
			JRadioButton radioMixed = new JRadioButton("Major Style: Mixed");
			radioMixed.setMnemonic(KeyEvent.VK_B);
			radioMixed.setSelected(false);
			radioMixed.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Major Style to Mixed (Majors at start and end)");
					settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_FEW);
				}
			});
			JRadioButton radioAll = new JRadioButton("Major Style: Always Majors");
			radioAll.setMnemonic(KeyEvent.VK_B);
			radioAll.setSelected(false);
			radioAll.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Major Style to Always Majors");
					settings.changeIntSetting(Settings.I_MAJOR_STYLE, Settings.MAJORS_ALL);
				}
			});
			ButtonGroup group = new ButtonGroup();
			group.add(radioNone);
			group.add(radioMixed);
			group.add(radioAll);
			
			int radioToSet = settings.getIntSetting(Settings.I_MAJOR_STYLE);
			if (radioToSet == Settings.MAJORS_NONE) {
				radioNone.setSelected(true);
			} else if (radioToSet == Settings.MAJORS_FEW) {
				radioMixed.setSelected(true);
			} else if (radioToSet == Settings.MAJORS_ALL) {
				radioAll.setSelected(true);
			}
			
			JButton parseBtn;
			if (settings.getIntSetting(Settings.I_ORIENTATION) == Settings.LEFT_TO_RIGHT) {
				parseBtn = new JButton("Parsing Orientation: Left-to-Right");
				parseBtn.setForeground(Color.BLUE);
			} else {
				parseBtn = new JButton("Parsing Orientation: Right-to-Left");
				parseBtn.setForeground(Color.RED);
			}
			parseBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (parseBtn.getForeground().equals(Color.RED)) {
						parseBtn.setForeground(Color.BLUE);
						parseBtn.setText("Parsing Orientation: Left-to-Right");
						settings.changeIntSetting(Settings.I_ORIENTATION, Settings.LEFT_TO_RIGHT);
						addText("Changing Parsing Orientation to Left-to-Right");
					} else {
						parseBtn.setForeground(Color.RED);
						parseBtn.setText("Parsing Orientation: Right-to-Left");
						settings.changeIntSetting(Settings.I_ORIENTATION, Settings.RIGHT_TO_LEFT);
						addText("Changing Parsing Orientation to Right-to-Left");
					}
				}
			});
			
			
			// aura btns
			JButton aurasBtn;
			JButton lowerBtn;
			JButton inquisBtn;
			JButton welkinBtn;
			JButton abysmBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_AURAS)) {
				aurasBtn = new JButton("Allow Auras: ON");
				aurasBtn.setForeground(Color.BLUE);
			} else {
				aurasBtn = new JButton("Allow Auras: OFF");
				aurasBtn.setForeground(Color.RED);
			}
			if (settings.getBoolSetting(Settings.B_LOWER_AURAS)) {
				lowerBtn = new JButton("Allow Lower Auras: ON");
				lowerBtn.setForeground(Color.BLUE);
			} else {
				lowerBtn = new JButton("Allow Lower Auras: OFF");
				lowerBtn.setForeground(Color.RED);
			}
			if (settings.getBoolSetting(Settings.B_AUTO_INQUISITIVE)) {
				inquisBtn = new JButton("Inquisitive Auras(?): ON");
				inquisBtn.setForeground(Color.BLUE);
			} else {
				inquisBtn = new JButton("Inquisitive Auras(?): OFF");
				inquisBtn.setForeground(Color.RED);
			}
			if (settings.getBoolSetting(Settings.B_AUTO_WELKIN)) {
				welkinBtn = new JButton("Welkin Auras(!): ON");
				welkinBtn.setForeground(Color.BLUE);
			} else {
				welkinBtn = new JButton("Welkin Auras(!): OFF");
				welkinBtn.setForeground(Color.RED);
			}
			if (settings.getBoolSetting(Settings.B_AUTO_ABYSM)) {
				abysmBtn = new JButton("Abysm Auras(...): ON");
				abysmBtn.setForeground(Color.BLUE);
			} else {
				abysmBtn = new JButton("Abysm Auras(...): OFF");
				abysmBtn.setForeground(Color.RED);
			}
			
			aurasBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (aurasBtn.getForeground().equals(Color.RED)) {
						aurasBtn.setForeground(Color.BLUE);
						aurasBtn.setText("Allow Auras: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, true);
						addText("Turning Auras ON");
					} else {
						aurasBtn.setForeground(Color.RED);
						aurasBtn.setText("Allow Auras: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_AURAS, false);

						String offMsg = "Turning Auras OFF";
						if (lowerBtn.getForeground().equals(Color.BLUE)) {
							lowerBtn.setForeground(Color.RED);
							lowerBtn.setText("Allow Lower Auras: OFF");
							settings.changeBoolSetting(Settings.B_LOWER_AURAS, false);
							offMsg += "\nTurning Lower Auras OFF";
						}
						if (inquisBtn.getForeground().equals(Color.BLUE)) {
							inquisBtn.setForeground(Color.RED);
							inquisBtn.setText("Inquisitive Auras(?): OFF");
							settings.changeBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
							offMsg += "\nTurning Inquisitive Auras OFF";
						}
						if (welkinBtn.getForeground().equals(Color.BLUE)) {
							welkinBtn.setForeground(Color.RED);
							welkinBtn.setText("Welkin Auras(!): OFF");
							settings.changeBoolSetting(Settings.B_AUTO_WELKIN, false);
							offMsg += "\nTurning Welkin Auras OFF";
						}
						if (abysmBtn.getForeground().equals(Color.BLUE)) {
							abysmBtn.setForeground(Color.RED);
							abysmBtn.setText("Abysm Auras(...): OFF");
							settings.changeBoolSetting(Settings.B_AUTO_ABYSM, false);
							offMsg += "\nTurning Abysm Auras OFF";
						}
						addText(offMsg);
						
					}
				}
			});
			
			
			lowerBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (lowerBtn.getForeground().equals(Color.RED)) {
						lowerBtn.setForeground(Color.BLUE);
						lowerBtn.setText("Allow Lower Auras: ON");
						settings.changeBoolSetting(Settings.B_LOWER_AURAS, true);

						String onMsg = "Turning Lower Auras ON";
						if (aurasBtn.getForeground().equals(Color.RED)) {
							aurasBtn.setForeground(Color.BLUE);
							aurasBtn.setText("Allow Auras: ON");
							settings.changeBoolSetting(Settings.B_ALLOW_AURAS, true);
							onMsg += "\nTurning Auras ON";
						}
						addText(onMsg);
						
					} else {
						lowerBtn.setForeground(Color.RED);
						lowerBtn.setText("Allow Lower Auras: OFF");
						settings.changeBoolSetting(Settings.B_LOWER_AURAS, false);
						
						String offMsg = "Turning Lower Auras OFF";
						if (inquisBtn.getForeground().equals(Color.BLUE)) {
							inquisBtn.setForeground(Color.RED);
							inquisBtn.setText("Inquisitive Auras(?): OFF");
							settings.changeBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
							offMsg += "\nTurning Inquisitive Auras OFF";
						}
						if (welkinBtn.getForeground().equals(Color.BLUE)) {
							welkinBtn.setForeground(Color.RED);
							welkinBtn.setText("Welkin Auras(!): OFF");
							settings.changeBoolSetting(Settings.B_AUTO_WELKIN, false);
							offMsg += "\nTurning Welkin Auras OFF";
						}
						if (abysmBtn.getForeground().equals(Color.BLUE)) {
							abysmBtn.setForeground(Color.RED);
							abysmBtn.setText("Abysm Auras(...): OFF");
							settings.changeBoolSetting(Settings.B_AUTO_ABYSM, false);
							offMsg += "\nTurning Abysm Auras OFF";
						}
						addText(offMsg);
					}
				}
			});
			
			inquisBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (inquisBtn.getForeground().equals(Color.RED)) {
						inquisBtn.setForeground(Color.BLUE);
						inquisBtn.setText("Inquisitive Auras(?): ON");
						settings.changeBoolSetting(Settings.B_AUTO_INQUISITIVE, true);
						
						String onMsg = "Turning Inquisitive Auras ON";
						if (aurasBtn.getForeground().equals(Color.RED)) {
							aurasBtn.setForeground(Color.BLUE);
							aurasBtn.setText("Allow Auras: ON");
							settings.changeBoolSetting(Settings.B_ALLOW_AURAS, true);
							onMsg += "\nTurning Auras ON";
						}
						if (lowerBtn.getForeground().equals(Color.RED)) {
							lowerBtn.setForeground(Color.BLUE);
							lowerBtn.setText("Allow Lower Auras: ON");
							settings.changeBoolSetting(Settings.B_LOWER_AURAS, true);
							onMsg += "\nTurning Lower Auras ON";
						}
						addText(onMsg);
						
					} else {
						inquisBtn.setForeground(Color.RED);
						inquisBtn.setText("Inquisitive Auras(?): OFF");
						settings.changeBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
						addText("Turning Inquisitive Auras OFF");
					}
				}
			});

			welkinBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (welkinBtn.getForeground().equals(Color.RED)) {
						welkinBtn.setForeground(Color.BLUE);
						welkinBtn.setText("Welkin Auras(!): ON");
						settings.changeBoolSetting(Settings.B_AUTO_WELKIN, true);
						
						String onMsg = "Turning Welkin Auras ON";
						if (aurasBtn.getForeground().equals(Color.RED)) {
							aurasBtn.setForeground(Color.BLUE);
							aurasBtn.setText("Allow Auras: ON");
							settings.changeBoolSetting(Settings.B_ALLOW_AURAS, true);
							onMsg += "\nTurning Auras ON";
						}
						if (lowerBtn.getForeground().equals(Color.RED)) {
							lowerBtn.setForeground(Color.BLUE);
							lowerBtn.setText("Allow Lower Auras: ON");
							settings.changeBoolSetting(Settings.B_LOWER_AURAS, true);
							onMsg += "\nTurning Lower Auras ON";
						}
						addText(onMsg);
					} else {
						welkinBtn.setForeground(Color.RED);
						welkinBtn.setText("Welkin Auras(!): OFF");
						settings.changeBoolSetting(Settings.B_AUTO_WELKIN, false);
						addText("Turning Welkin Auras OFF");
					}
				}
			});

			abysmBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (abysmBtn.getForeground().equals(Color.RED)) {
						abysmBtn.setForeground(Color.BLUE);
						abysmBtn.setText("Abysm Auras(...): ON");
						settings.changeBoolSetting(Settings.B_AUTO_ABYSM, true);
						
						String onMsg = "Turning Abysm Auras ON";
						if (aurasBtn.getForeground().equals(Color.RED)) {
							aurasBtn.setForeground(Color.BLUE);
							aurasBtn.setText("Allow Auras: ON");
							settings.changeBoolSetting(Settings.B_ALLOW_AURAS, true);
							onMsg += "\nTurning Auras ON";
						}
						if (lowerBtn.getForeground().equals(Color.RED)) {
							lowerBtn.setForeground(Color.BLUE);
							lowerBtn.setText("Allow Lower Auras: ON");
							settings.changeBoolSetting(Settings.B_LOWER_AURAS, true);
							onMsg += "\nTurning Lower Auras ON";
						}
						addText(onMsg);
					} else {
						abysmBtn.setForeground(Color.RED);
						abysmBtn.setText("Abysm Auras(...): OFF");
						settings.changeBoolSetting(Settings.B_AUTO_ABYSM, false);
						addText("Turning Abysm Auras OFF");
					}
				}
			});
			
			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();	

			// header row
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0.0;
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(label3);
			panel.add(headerPanel, cons);
			
			cons.gridy = 1;
			cons.weighty = 0.5;
			JPanel btnsPanel1 = new JPanel();
			btnsPanel1.setLayout(new GridLayout(2,3));
			btnsPanel1.add(label4);
			btnsPanel1.add(new JLabel(""));
			btnsPanel1.add(new JLabel(""));
			btnsPanel1.add(radioNone);
			btnsPanel1.add(radioMixed);
			btnsPanel1.add(radioAll);
			btnsPanel1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			panel.add(btnsPanel1, cons);
			
			// auras
			cons.gridy = 2;
			JPanel btnsPanel2 = new JPanel();
			btnsPanel2.setLayout(new GridLayout(1,3));
			btnsPanel2.add(parseBtn);
			btnsPanel2.add(aurasBtn);
			btnsPanel2.add(lowerBtn);
			panel.add(btnsPanel2, cons);
			
			// personality auras
			cons.gridy = 3;
			JPanel btnsPanel3 = new JPanel();
			btnsPanel3.setLayout(new GridLayout(1,3));
			btnsPanel3.add(inquisBtn);
			btnsPanel3.add(welkinBtn);
			btnsPanel3.add(abysmBtn);
			panel.add(btnsPanel3, cons);
			
			cons.gridy = 4;
			JPanel helpPanel1 = new JPanel();
			helpPanel1.setLayout(new GridLayout(1,3));
			helpPanel1.add(new JLabel("Inquis/Welkin/Abysm require Lower Auras!"));
			helpPanel1.add(new JLabel(""));
			helpPanel1.add(new JLabel("Lower Auras require Auras Allowed!"));
			panel.add(helpPanel1, cons);

			cons.gridy = 5;
			JPanel helpPanel2 = new JPanel();
			helpPanel2.setLayout(new GridLayout(1,3));
			helpPanel2.add(new JLabel("Welkin stacks with Inquisitive"));
			helpPanel2.add(new JLabel(""));
			helpPanel2.add(new JLabel("Welkin has priority over Abysm"));
			panel.add(helpPanel2, cons);

			cons.gridy = 6;
			JPanel helpPanel3 = new JPanel();
			helpPanel3.setLayout(new GridLayout(1,3));
			helpPanel3.add(new JLabel("Abysm stacks with Inquisitive"));
			helpPanel3.add(new JLabel(""));
			helpPanel3.add(new JLabel(""));
			panel.add(helpPanel3, cons);
			
			return panel;
		}
		
		public JPanel getDisplaySettingsPanel() {
			JPanel panel = new JPanel();
			
			// header row
			JLabel label1 = new JLabel("Set Display Settings!");
			JLabel label2 = new JLabel("Display Settings Status:");
			label2.setHorizontalAlignment(JLabel.RIGHT);
			JLabel displayReadyStatus = new JLabel(READY);
			displayReadyStatus.setForeground(Color.BLUE);
			displayReadyStatus.setHorizontalAlignment(JLabel.CENTER);
			
			JRadioButton radioRune = new JRadioButton("Text Display: Rune Only");
			radioRune.setMnemonic(KeyEvent.VK_B);
			radioRune.setSelected(false);
			radioRune.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Text Display to Rune Only");
					settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_ONLY);
				}
			});
			JRadioButton radioRF = new JRadioButton("Text Display: Rune + Form");
			radioRF.setMnemonic(KeyEvent.VK_B);
			radioRF.setSelected(false);
			radioRF.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Text Display to Rune and Form");
					settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FORM);
				}
			});
			JRadioButton radioPath = new JRadioButton("Text Display: File Path");
			radioPath.setMnemonic(KeyEvent.VK_B);
			radioPath.setSelected(false);
			radioPath.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Text Display to File Path");
					settings.changeIntSetting(Settings.I_DISPLAY_STYLE, Settings.DISPLAY_RUNE_FILE);
				}
			});
			ButtonGroup group = new ButtonGroup();
			group.add(radioRune);
			group.add(radioRF);
			group.add(radioPath);
			
			int radioToSet = settings.getIntSetting(Settings.I_DISPLAY_STYLE);
			if (radioToSet == Settings.DISPLAY_RUNE_ONLY) {
				radioRune.setSelected(true);
			} else if (radioToSet == Settings.DISPLAY_RUNE_FORM) {
				radioRF.setSelected(true);
			} else if (radioToSet == Settings.DISPLAY_RUNE_FILE) {
				radioPath.setSelected(true);
			}
			
			JLabel label3 = new JLabel("Max # of Runes in a Line (Text):");
			label3.setHorizontalAlignment(JLabel.CENTER);
			JTextField lineField = new JTextField(settings.getIntSetting(Settings.I_TEXT_LINE_LENGTH) + "");
			JButton lineBtn = new JButton("Set Length!");
			lineBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Max # of Runes in a Line to " + lineField.getText());
					try {
						int amt = Integer.parseInt(lineField.getText());
						if (amt <= 0) {
							addText("Invalid valid. Must be greater than 0. Max # of Runes will not be changed.");
						} else {
							settings.changeIntSetting(Settings.I_TEXT_LINE_LENGTH, amt);
						}
					} catch (NumberFormatException e) {
						addText("Invalid value. Max # of Runes will not be changed.");
					}
				}
			});
			
			JLabel label4 = new JLabel("Max # of Runes in a Line (Image):");
			label4.setHorizontalAlignment(JLabel.CENTER);
			JTextField imageField = new JTextField(settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH) + "");
			JButton imageBtn = new JButton("Set Length!");
			imageBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addText("Setting Max # of Runes in an Image to " + imageField.getText());
					try {
						int amt = Integer.parseInt(imageField.getText());
						if (amt < 0 || amt > Settings.IMAGE_LINE_LENGTH_MAX) {
							addText("Invalid valid. Must be greater than 0 and less than or equal to " + (Settings.IMAGE_LINE_LENGTH_MAX) + ". Max # of Runes will not be changed.");
						} else {
							settings.changeIntSetting(Settings.I_IMAGE_LINE_LENGTH, amt);
						}
					} catch (NumberFormatException e) {
						addText("Invalid value. Max # of Runes will not be changed.");
					}
				}
			});
			
			JButton overBtn;
			if (settings.getBoolSetting(Settings.B_RUNE_OVERFLOW)) {
				overBtn = new JButton("Rune Overflow: ON");
				overBtn.setForeground(Color.BLUE);
			} else {
				overBtn = new JButton("Rune Overflow: OFF");
				overBtn.setForeground(Color.RED);
			}
			overBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (overBtn.getForeground().equals(Color.RED)) {
						overBtn.setForeground(Color.BLUE);
						overBtn.setText("Rune Overflow: ON");
						settings.changeBoolSetting(Settings.B_RUNE_OVERFLOW, true);
						addText("Turning Rune Overflow ON");
					} else {
						overBtn.setForeground(Color.RED);
						overBtn.setText("Rune Overflow: OFF");
						settings.changeBoolSetting(Settings.B_RUNE_OVERFLOW, false);
						addText("Turning Rune Overflow OFF");
					}
				}
			});
			
			JButton spaceBtn;
			if (settings.getBoolSetting(Settings.B_INSERT_SPACES)) {
				spaceBtn = new JButton("Add Space Between Lores: ON");
				spaceBtn.setForeground(Color.BLUE);
			} else {
				spaceBtn = new JButton("Add Space Between Lores: OFF");
				spaceBtn.setForeground(Color.RED);
			}
			spaceBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (spaceBtn.getForeground().equals(Color.RED)) {
						spaceBtn.setForeground(Color.BLUE);
						spaceBtn.setText("Add Space Between Lores: ON");
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, true);
						addText("Add spaces between Lores ON");
					} else {
						spaceBtn.setForeground(Color.RED);
						spaceBtn.setText("Add Space Between Lores: OFF");
						settings.changeBoolSetting(Settings.B_INSERT_SPACES, false);
						addText("Add spaces between Lores OFF");
					}
				}
			});

			JButton staffBtn;
			if (settings.getBoolSetting(Settings.B_ALLOW_STAFF)) {
				staffBtn = new JButton("Allow Staff: ON");
				staffBtn.setForeground(Color.BLUE);
			} else {
				staffBtn = new JButton("Allow Staff: OFF");
				staffBtn.setForeground(Color.RED);
			}
			staffBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// toggle, swap colors
					if (staffBtn.getForeground().equals(Color.RED)) {
						staffBtn.setForeground(Color.BLUE);
						staffBtn.setText("Allow Staff: ON");
						settings.changeBoolSetting(Settings.B_ALLOW_STAFF, true);
						addText("Allow Staff ON");
					} else {
						staffBtn.setForeground(Color.RED);
						staffBtn.setText("Allow Staff: OFF");
						settings.changeBoolSetting(Settings.B_ALLOW_STAFF, false);
						addText("Allow Staff OFF");
					}
				}
			});
			
			// previews first so we can alter them later
			JTextField runePreview = new JTextField("Rune Preview");
			runePreview.setFocusable(false);
			runePreview.setForeground(new Color(settings.getIntSetting(Settings.I_RUNE_RGB_RED),settings.getIntSetting(Settings.I_RUNE_RGB_GREEN),settings.getIntSetting(Settings.I_RUNE_RGB_BLUE)));
			JTextField backPreview = new JTextField("Rune + Background Preview");
			backPreview.setFocusable(false);
			backPreview.setBackground(new Color(settings.getIntSetting(Settings.I_BACK_RGB_RED),settings.getIntSetting(Settings.I_BACK_RGB_GREEN),settings.getIntSetting(Settings.I_BACK_RGB_BLUE)));
			backPreview.setForeground(new Color(settings.getIntSetting(Settings.I_RUNE_RGB_RED),settings.getIntSetting(Settings.I_RUNE_RGB_GREEN),settings.getIntSetting(Settings.I_RUNE_RGB_BLUE)));
			JTextField rayPreview = new JTextField("Ray Background Color Preview");
			rayPreview.setFocusable(false);
			rayPreview.setBackground(new Color(settings.getIntSetting(Settings.I_RAY_RGB_RED),settings.getIntSetting(Settings.I_RAY_RGB_GREEN),settings.getIntSetting(Settings.I_RAY_RGB_BLUE)));
			rayPreview.setForeground(new Color(settings.getIntSetting(Settings.I_RUNE_RGB_RED),settings.getIntSetting(Settings.I_RUNE_RGB_GREEN),settings.getIntSetting(Settings.I_RUNE_RGB_BLUE)));
			
			
			// rune rbg
			JLabel label5 = new JLabel("Rune RGB values: ");
			label5.setHorizontalAlignment(JLabel.CENTER);
			JTextField runeR = new JTextField(settings.getIntSetting(Settings.I_RUNE_RGB_RED) + "");
			JTextField runeG = new JTextField(settings.getIntSetting(Settings.I_RUNE_RGB_GREEN) + "");
			JTextField runeB = new JTextField(settings.getIntSetting(Settings.I_RUNE_RGB_BLUE) + "");
			JButton runeRGBBtn = new JButton("Set Rune RGB Values!");
			runeRGBBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						int rAmt = Integer.parseInt(runeR.getText());
						int gAmt = Integer.parseInt(runeG.getText());
						int bAmt = Integer.parseInt(runeB.getText());
						if (rAmt < 0 || rAmt > 255) {
							addText("Invalid value for Rune - Red. Must be greater than -1 and less than 256. Rune RGBs will not be changed.");
						} else {
							if (gAmt < 0 || gAmt > 255) {
								addText("Invalid value for Rune - Green. Must be greater than -1 and less than 256. Rune RGBs will not be changed.");
							} else {
								if (bAmt < 0 || bAmt > 255) {
									addText("Invalid value for Rune - Blue. Must be greater than -1 and less than 256. Rune RGBs will not be changed.");
								} else {
									settings.changeIntSetting(Settings.I_RUNE_RGB_RED, rAmt);
									settings.changeIntSetting(Settings.I_RUNE_RGB_GREEN, gAmt);
									settings.changeIntSetting(Settings.I_RUNE_RGB_BLUE, bAmt);
									// update the previews...
									runePreview.setForeground(new Color(rAmt, gAmt, bAmt));
									backPreview.setForeground(new Color(rAmt, gAmt, bAmt));
									addText("Setting Rune RGB Values: " + rAmt + " | " + gAmt + " | " + bAmt);
								}
							}
						}
					} catch (NumberFormatException e) {
						addText("Invalid value. Rune RBGs will not be changed.");
					}
				}
			});

			// back rgb
			JLabel label6 = new JLabel("Background RGB values: ");
			label6.setHorizontalAlignment(JLabel.CENTER);
			JTextField backR = new JTextField(settings.getIntSetting(Settings.I_BACK_RGB_RED) + "");
			JTextField backG = new JTextField(settings.getIntSetting(Settings.I_BACK_RGB_GREEN) + "");
			JTextField backB = new JTextField(settings.getIntSetting(Settings.I_BACK_RGB_BLUE) + "");
			JButton backRGBBtn = new JButton("Set Background RGB Values!");
			backRGBBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						int rAmt = Integer.parseInt(backR.getText());
						int gAmt = Integer.parseInt(backG.getText());
						int bAmt = Integer.parseInt(backB.getText());
						if (rAmt < 0 || rAmt > 255) {
							addText("Invalid value for Background - Red. Must be greater than 0 and less than 256. Background RGBs will not be changed.");
						} else {
							if (gAmt < 0 || gAmt > 255) {
								addText("Invalid value for Background - Green. Must be greater than 0 and less than 256. Background RGBs will not be changed.");
							} else {
								if (bAmt < 0 || bAmt > 255) {
									addText("Invalid value for Background - Blue. Must be greater than 0 and less than 256. Background RGBs will not be changed.");
								} else {
									settings.changeIntSetting(Settings.I_BACK_RGB_RED, rAmt);
									settings.changeIntSetting(Settings.I_BACK_RGB_GREEN, gAmt);
									settings.changeIntSetting(Settings.I_BACK_RGB_BLUE, bAmt);
									// update the preview...
									backPreview.setBackground(new Color(rAmt, gAmt, bAmt));
									addText("Setting Background RGB Values: " + rAmt + " | " + gAmt + " | " + bAmt);
								}
							}
						}
					} catch (NumberFormatException e) {
						addText("Invalid value. Background RBGs will not be changed.");
					}
				}
			});
			
			// back rgb
			JLabel label7 = new JLabel("Ray RGB values: ");
			label7.setHorizontalAlignment(JLabel.CENTER);
			JTextField rayR = new JTextField(settings.getIntSetting(Settings.I_RAY_RGB_RED) + "");
			JTextField rayG = new JTextField(settings.getIntSetting(Settings.I_RAY_RGB_GREEN) + "");
			JTextField rayB = new JTextField(settings.getIntSetting(Settings.I_RAY_RGB_BLUE) + "");
			JButton rayRGBBtn = new JButton("Set Ray RGB Values!");
			rayRGBBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						int rAmt = Integer.parseInt(rayR.getText());
						int gAmt = Integer.parseInt(rayG.getText());
						int bAmt = Integer.parseInt(rayB.getText());
						if (rAmt < 0 || rAmt > 255) {
							addText("Invalid value for Ray - Red. Must be greater than 0 and less than 256. Ray RGBs will not be changed.");
						} else {
							if (gAmt < 0 || gAmt > 255) {
								addText("Invalid value for Ray - Green. Must be greater than 0 and less than 256. Ray RGBs will not be changed.");
							} else {
								if (bAmt < 0 || bAmt > 255) {
									addText("Invalid value for Ray - Blue. Must be greater than 0 and less than 256. Ray RGBs will not be changed.");
								} else {
									settings.changeIntSetting(Settings.I_RAY_RGB_RED, rAmt);
									settings.changeIntSetting(Settings.I_RAY_RGB_GREEN, gAmt);
									settings.changeIntSetting(Settings.I_RAY_RGB_BLUE, bAmt);
									// update the preview...
									rayPreview.setBackground(new Color(rAmt, gAmt, bAmt));
									addText("Setting Ray RGB Values: " + rAmt + " | " + gAmt + " | " + bAmt);
								}
							}
						}
					} catch (NumberFormatException e) {
						addText("Invalid value. Ray RBGs will not be changed.");
					}
				}
			});
			

			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();	

			// header row
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0.0;
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(displayReadyStatus);
			panel.add(headerPanel, cons);
			
			// radio row
			cons.gridy = 1;
			cons.weighty = 0.5;
			JPanel btnsPanel1 = new JPanel();
			btnsPanel1.setLayout(new GridLayout(1,3));
			btnsPanel1.add(radioRune);
			btnsPanel1.add(radioRF);
			btnsPanel1.add(radioPath);
			panel.add(btnsPanel1, cons);
			
			// text line length
			cons.gridy = 2;
			JPanel btnsPanel2 = new JPanel();
			btnsPanel2.setLayout(new GridLayout(1,3));
			btnsPanel2.add(label3);
			btnsPanel2.add(lineField);
			btnsPanel2.add(lineBtn);
			panel.add(btnsPanel2, cons);
			
			// image line length
			cons.gridy = 3;
			JPanel btnsPanel3 = new JPanel();
			btnsPanel3.setLayout(new GridLayout(1,3));
			btnsPanel3.add(label4);
			btnsPanel3.add(imageField);
			btnsPanel3.add(imageBtn);
			panel.add(btnsPanel3, cons);
			
			// rune overflow
			cons.gridy = 4;
			JPanel btnsPanel4 = new JPanel();
			btnsPanel4.setLayout(new GridLayout(1,3));
			btnsPanel4.add(overBtn);
			btnsPanel4.add(spaceBtn);
			btnsPanel4.add(staffBtn);
			panel.add(btnsPanel4, cons);
			
			// rune rbgs
			cons.gridy = 5;
			JPanel btnsPanel5 = new JPanel();
			btnsPanel5.setLayout(new GridLayout(2,3));
			btnsPanel5.add(label5);
			btnsPanel5.add(runeRGBBtn);
			btnsPanel5.add(runePreview);
			btnsPanel5.add(runeR);
			btnsPanel5.add(runeG);
			btnsPanel5.add(runeB);
			btnsPanel5.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			panel.add(btnsPanel5, cons);
			
			// back rbgs
			cons.gridy = 6;
			JPanel btnsPanel6 = new JPanel();
			btnsPanel6.setLayout(new GridLayout(2,3));
			btnsPanel6.add(label6);
			btnsPanel6.add(backRGBBtn);
			btnsPanel6.add(backPreview);
			btnsPanel6.add(backR);
			btnsPanel6.add(backG);
			btnsPanel6.add(backB);
			btnsPanel6.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			panel.add(btnsPanel6, cons);
			
			
			// ray rbgs
			cons.gridy = 7;
			JPanel btnsPanel7 = new JPanel();
			btnsPanel7.setLayout(new GridLayout(2,3));
			btnsPanel7.add(label7);
			btnsPanel7.add(rayRGBBtn);
			btnsPanel7.add(rayPreview);
			btnsPanel7.add(rayR);
			btnsPanel7.add(rayG);
			btnsPanel7.add(rayB);
			btnsPanel7.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			panel.add(btnsPanel7, cons);
			
			
			
			
			return panel;
		}
		
		public JPanel getSettingsFilePanel() {
			JPanel panel = new JPanel();
			
			// header row
			JLabel label1 = new JLabel("Save Current Settings to a Settings File!");
			JLabel label2 = new JLabel("Save File Status:");
			label2.setHorizontalAlignment(JLabel.RIGHT);
			JLabel saveReadyStatus = new JLabel(READY);
			saveReadyStatus.setForeground(Color.BLUE);
			saveReadyStatus.setHorizontalAlignment(JLabel.CENTER);

			// path row
			JLabel label3 = new JLabel("Path & Filename to save to:");
			label3.setHorizontalAlignment(JLabel.CENTER);
			JTextField saveFileField = new JTextField(settings.getOutputDirectory() + "Example.txt");
			
			JButton dirBtn = new JButton("Save Settings File!");
			dirBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						String saveDir = saveFileField.getText();
						
						// since they may have used either slash in the textfield, just grab the highest value
						int slashLoc1 = saveDir.lastIndexOf("\\");
						int slashLoc2 = saveDir.lastIndexOf("/");
						int lastLoc = slashLoc1 > slashLoc2 ? slashLoc1 : slashLoc2 ;
						saveDir = saveDir.substring(0, lastLoc);
						
						File file = new File(saveDir);
						if (file.isDirectory()) {

							// add .txt if it doesnt have it at the end
							String savePath = saveFileField.getText();
							if (!savePath.endsWith(".txt")) {
								savePath += ".txt";
							}
							
							addText("Saving to Settings File: " + savePath);
							saveSettingsFile(settings, saveFileField.getText());
							dirBtn.setForeground(Color.BLACK);
						} else {
							addText("Error - " + saveDir + " is not a valid directory!");
							dirBtn.setForeground(Color.RED);
						}
					} catch (Exception e) {
						addText("There was an error saving the file: " + e.toString());
						dirBtn.setForeground(Color.RED);
					}
				}
			});
			
			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();	
			
			// header row
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0.0;
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(saveReadyStatus);
			panel.add(headerPanel, cons);
			
			// path row
			cons.weighty = 0.5;
			cons.gridy = 1;
			cons.ipady = 30;
			JPanel pathPanel = new JPanel();
			pathPanel.setLayout(new GridLayout(3,1));
			pathPanel.add(label3);
			pathPanel.add(new JScrollPane(saveFileField));
			pathPanel.add(dirBtn);
			panel.add(pathPanel, cons);
			cons.ipady = 0;
			
			// settings help rows
			cons.gridy = 2;
			cons.weighty = 0;
			panel.add(new JLabel("Help Information: Settings File Contents"), cons);
			
			// some spacing to make field heights more consistent with other tabs
			cons.gridy = 3;
			cons.weighty = 0;
			JTextArea helpArea = new JTextArea(10, 20);
			helpArea.setText(getSettingsHelp());
			helpArea.setFocusable(false);
			panel.add(new JScrollPane(helpArea), cons);
			
			return panel;
		}
		
		
		public JPanel getTemporaryPanel(String headerText, String statusText) {
			JPanel panel = new JPanel();
			
			// header row
			JLabel label1 = new JLabel(headerText);
			JLabel label2 = new JLabel(statusText);
			JLabel label3 = new JLabel(NOT_READY);
			label2.setHorizontalAlignment(JLabel.RIGHT);
			label3.setForeground(Color.RED);
			label3.setHorizontalAlignment(JLabel.CENTER);

			panel.setLayout(new GridBagLayout());
			GridBagConstraints cons = new GridBagConstraints();	

			// header row
			cons.fill = GridBagConstraints.HORIZONTAL;
			cons.gridx = 0;
			cons.gridy = 0;			
			cons.weightx = 0.5;
			cons.weighty = 0.0;
			JPanel headerPanel = new JPanel();
			headerPanel.setLayout(new GridLayout(1,3));
			headerPanel.add(label1);
			headerPanel.add(label2);
			headerPanel.add(label3);
			panel.add(headerPanel, cons);
			
			cons.gridy = 1;
			cons.weighty = 0.5;
			panel.add(new JLabel(""), cons);

			cons.gridy = 2;
			panel.add(new JLabel(""), cons);

			cons.gridy = 3;
			panel.add(new JLabel(""), cons);

			cons.gridy = 4;
			panel.add(new JLabel(""), cons);

			cons.gridy = 5;
			panel.add(new JLabel(""), cons);

			cons.gridy = 6;
			JPanel initPanel = new JPanel();
			initPanel.setLayout(new GridLayout(1,3));
			initPanel.add(new JLabel(""));
			initPanel.add(new JLabel("Initialize on the Initialization Tab first!"), cons);
			initPanel.add(new JLabel(""));
			panel.add(initPanel, cons);

			cons.gridy = 7;
			panel.add(new JLabel(""), cons);
			
			cons.gridy = 8;
			panel.add(new JLabel(""), cons);

			cons.gridy = 9;
			panel.add(new JLabel(""), cons);
			
			cons.gridy = 10;
			panel.add(new JLabel(""), cons);

			cons.gridy = 11;
			panel.add(new JLabel(""), cons);

			cons.gridy = 12;
			panel.add(new JLabel(""), cons);
			
			return panel;
		}
		
		
		
		
		public void addLogOnly(String text) {
			// must be called after the log has been initialized
			if (logInitialized) {
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
			} else {
				addText("ERROR - Log is not initialized yet! Call addLogOnly() only after it is!");
			}
		}
		
		public void addText(String text) {
			
			// log everything before displaying in gui
			// TODO we dont technically ever close the buffered writer... we could open and close every write? use param that lets us append.. meh.
			//      see also: addLogOnly, more writing to the bw we never close.
			String logPath = "";
			if (!logInitialized) {
				try {
					// wipe the current log (if exists) and create the new log
					logPath = System.getProperty("user.dir") + "\\LexiconifierLog.txt";
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
		
		public void checkIfReady(JLabel readyLabel) {
			if (setSettings && setRd && setOD) {
				// if we just went from non-ready to ready, log a message
				if (readyLabel.getForeground().equals(Color.RED)) {
					addText("All required settings have been initialized!\nLexiconifier ready!");
					addRealTabs(); // replace temporary tabs with real ones
				}
				//readyLabel.setText("Status: Ready!");
				readyLabel.setText(READY);
				readyLabel.setForeground(Color.BLUE);
			}
		}

		public void addTemporaryTabs() {
			// We just want these to have a header and a status of NOT-READY
			lexiTab = getTemporaryPanel("Lexify Text to Auralann!", "Lexify Status:");
			rdTab = getTemporaryPanel("Rune Dictionary Contents!", "Rune Dictionary Status / Location:");
			runeTab = getTemporaryPanel("Set Rune Settings!", "Rune Settings Status:");
			auraTab = getTemporaryPanel("Set Aura Settings!", "Aura Settings Status:");
			displayTab = getTemporaryPanel("Set Display Settings!", "Display Settings Status:");
			setTab = getTemporaryPanel("Save Current Settings to a Settings File!", "Save File Status:");
			tabPane.addTab("Lexify Text", lexiTab);
			tabPane.addTab("Rune Dictionary", rdTab);
			tabPane.addTab("Rune Settings", runeTab);
			tabPane.addTab("Aura Settings", auraTab);
			tabPane.addTab("Display Settings", displayTab);
			tabPane.addTab("Save Settings File", setTab);
		}
		
		public void removeNonInitTabs() {
			int numTabs = tabPane.getTabCount();
			for (int x = 1; x < numTabs; ++x) {
				tabPane.removeTabAt(1);
			}
		}
		
		public void addRealTabs() {
			removeNonInitTabs();
			lexiTab = getLexifyPanel();
			rdTab = getRuneDictionaryPanel();
			runeTab = getRuneSettingsPanel();
			auraTab = getAuraSettingsPanel();
			displayTab = getDisplaySettingsPanel();
			setTab = getSettingsFilePanel();
			tabPane.addTab("Lexify Text", lexiTab);
			tabPane.addTab("Rune Dictionary", rdTab);
			tabPane.addTab("Rune Settings", runeTab);
			tabPane.addTab("Aura Settings", auraTab);
			tabPane.addTab("Display Settings", displayTab);
			tabPane.addTab("Save Settings File", setTab);
		}
		
		// used for the gui
		public String lexifyTextReturnOutput(String input, Settings settings, boolean saveTextFile, boolean saveImageFile) {
			String msg = "Input: [" + input + "]\n";
			Grimoire textGrim = new Grimoire(settings);
			textGrim.createKnowlsFromBlob(input);
			String output = "";
			String outPath = "";
			try {
				lexTextBtn.setForeground(Color.BLACK);
				lexImageBtn.setForeground(Color.BLACK);
				lexBothBtn.setForeground(Color.BLACK);
				output = textGrim.getTextDisplayByRuneCount(settings.getIntSetting(Settings.I_TEXT_LINE_LENGTH));
				if (saveTextFile) {
					try {
						outPath = settings.getOutputDirectory() + "/" + generateTimestamp() + ".txt";
						BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outPath)));
						bw.write(output);
						bw.close();
						msg += "Input written to text file: [" + outPath + "]\n";
					} catch (Exception e) {
						msg += ("Error! Exception caught: " + e.getMessage() + "\n");
					}
				}
				if (saveImageFile) {
					// Image may have a different LINE_LENGTH, so get the output again with the correct LINE_LENGTH
					Grimoire imageGrim = new Grimoire(settings);
					imageGrim.createKnowlsFromBlob(input);
					imageGrim.getTextDisplayByRuneCount(settings.getIntSetting(Settings.I_IMAGE_LINE_LENGTH));
					LinkedList<LinkedList<String>> filepaths = imageGrim.getFilePaths();
					if (filepaths.size() > 0) {
						int numRunes = filepaths.get(0).size();
						Videre vi = new Videre(settings, filepaths, numRunes, filepaths.size());
						vi.setVisible(true);
						vi.paint(this.getGraphics());
						this.repaint();

						try {
							if (outPath.isEmpty()) {
								outPath = settings.getOutputDirectory() + "/" + generateTimestamp() + ".png";
							} else {
								// reuse the text file's name, so they are both recognized as being the same lexified content.
								// we also replace in WriteImage, but I want to display the right name before potentially hitting an exception
								outPath = outPath.replaceAll(".txt", ".png");
							}
							vi.writeImage(outPath);
							msg += "Input written to image file: [" + outPath + "]\n";
							
						} catch (Exception e) {
							msg += ("Error! Could not save the image to directory. Exception: " + e.getMessage() + "\n");
						}
					} else {
						msg += ("No valid text to lexify!");
					}
				}
			} catch (Exception e) {
				msg += e.getMessage();
				lexTextBtn.setForeground(Color.RED);
				lexImageBtn.setForeground(Color.RED);
				lexBothBtn.setForeground(Color.RED);
			}
			
			addText(msg);
			return output;
		}
		
		public String guiLoadFileSettings(Settings settings, Scanner input) {
			String msgs = "";
			String errorMsgs = "";
			String setting = "";
			String value = "";
			int fieldCount = 0;
			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(settings.getSettingsPath())));
				while (br.ready()) {
					String lineContents = br.readLine();
					setting = lineContents.substring(0, lineContents.indexOf(" "));
					value = lineContents.substring(lineContents.indexOf("[")+1, lineContents.indexOf("]"));
					
					if (setting.equalsIgnoreCase(Settings.I_MAJOR_STYLE)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal != Settings.MAJORS_NONE && intVal != Settings.MAJORS_FEW && intVal != Settings.MAJORS_ALL) {
							errorMsgs += "Error with " + Settings.I_MAJOR_STYLE + ": value is not 0, 1, or 2. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_MAJOR_STYLE, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_DISPLAY_STYLE)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal != Settings.DISPLAY_RUNE_FILE && intVal != Settings.DISPLAY_RUNE_FORM && intVal != Settings.DISPLAY_RUNE_ONLY) {
							errorMsgs += "Error with " + Settings.I_DISPLAY_STYLE + ": value is not 0, 1, or 2. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_DISPLAY_STYLE, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_ORIENTATION)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal != Settings.LEFT_TO_RIGHT && intVal != Settings.RIGHT_TO_LEFT) {
							errorMsgs += "Error with " + Settings.I_ORIENTATION + ": value is not 1 or 2. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_ORIENTATION, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_TEXT_LINE_LENGTH)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 1) {
							errorMsgs += "Error with " + Settings.I_TEXT_LINE_LENGTH + ": value is less than 1. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_TEXT_LINE_LENGTH, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_IMAGE_LINE_LENGTH)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 1 || intVal > Settings.IMAGE_LINE_LENGTH_MAX) {
							errorMsgs += "Error with " + Settings.I_IMAGE_LINE_LENGTH + ": value is less than 1 or greater than " + Settings.IMAGE_LINE_LENGTH_MAX + ". Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_IMAGE_LINE_LENGTH, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_RANDOM_DEAD_CHANCE)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 100) {
							errorMsgs += "Error with " + Settings.I_RANDOM_DEAD_CHANCE + ": value is less than 0 or greater than 100. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_RANDOM_DEAD_CHANCE, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_ALT_MODE)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal != Settings.ALTS_ALT_ALTS && intVal != Settings.ALTS_ALTS_ONLY && intVal != Settings.ALTS_EQUAL_CHANCE && intVal != Settings.ALTS_NO_ALTS) {
							errorMsgs += "Error with " + Settings.I_ALT_MODE + ": value is not 0, 1, 2 or 3. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_ALT_MODE, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_RUNE_RGB_RED)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_RUNE_RGB_RED + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_RUNE_RGB_RED, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_RUNE_RGB_GREEN)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_RUNE_RGB_GREEN + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_RUNE_RGB_GREEN, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_RUNE_RGB_BLUE)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_RUNE_RGB_BLUE + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_RUNE_RGB_BLUE, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_BACK_RGB_RED)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_BACK_RGB_RED + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_BACK_RGB_RED, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_BACK_RGB_GREEN)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_BACK_RGB_GREEN + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_BACK_RGB_GREEN, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_BACK_RGB_BLUE)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_BACK_RGB_BLUE + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_BACK_RGB_BLUE, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_STAFF)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_STAFF, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_STAFF, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_STAFF + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.I_RAY_RGB_RED)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_RAY_RGB_RED + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_RAY_RGB_RED, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_RAY_RGB_GREEN)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_RAY_RGB_GREEN + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_RAY_RGB_GREEN, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.I_RAY_RGB_BLUE)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal < 0 || intVal > 255) {
							errorMsgs += "Error with " + Settings.I_RAY_RGB_BLUE + ": value is less than 0 or greater than 255. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_RAY_RGB_BLUE, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_RANDOM_DEAD)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_RANDOM_DEAD, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_RANDOM_DEAD + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_DOUBLES)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_DOUBLES, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_DOUBLES + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_TRIPLES)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_TRIPLES, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_TRIPLES + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_QUADS)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_QUADS, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_QUADS, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_QUADS + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_POSSESSIVE_S)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_POSSESSIVE_S, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_POSSESSIVE_S, true);
						} else {
							errorMsgs += "Error with " + Settings.B_POSSESSIVE_S + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_PLURAL_S)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_PLURAL_S, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_PLURAL_S, true);
						} else {
							errorMsgs += "Error with " + Settings.B_PLURAL_S + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_AURAS)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_AURAS, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_AURAS, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_AURAS + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_LOWER_AURAS)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_LOWER_AURAS, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_LOWER_AURAS, true);
						} else {
							errorMsgs += "Error with " + Settings.B_LOWER_AURAS + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_AI_DEAD)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_AI_DEAD, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_AI_DEAD + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.I_ALLOW_APOS_DEAD)) {
						fieldCount++;
						int intVal = Integer.parseInt(value);
						if (intVal != Settings.APOS_NONE && intVal != Settings.APOS_NEEDED && intVal != Settings.APOS_LEFT && intVal != Settings.APOS_RIGHT && intVal != Settings.APOS_BOTH) {
							errorMsgs += "Error with " + Settings.I_ALLOW_APOS_DEAD + ": value is not 0, 1, 2, 3 or 4. Value was: " + intVal + "\n";
						} else {
							settings.addIntSetting(Settings.I_ALLOW_APOS_DEAD, intVal);
						}
					} else if (setting.equalsIgnoreCase(Settings.B_ALLOW_COMMON_DEAD)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_ALLOW_COMMON_DEAD, true);
						} else {
							errorMsgs += "Error with " + Settings.B_ALLOW_COMMON_DEAD + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_RUNE_OVERFLOW)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_RUNE_OVERFLOW, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_RUNE_OVERFLOW, true);
						} else {
							errorMsgs += "Error with " + Settings.B_RUNE_OVERFLOW + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_AUTO_INQUISITIVE)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_AUTO_INQUISITIVE, true);
							if (settings.getBoolSetting(Settings.B_ALLOW_AURAS) == null || settings.getBoolSetting(Settings.B_ALLOW_AURAS) == false) {
								errorMsgs += "Error with " + Settings.B_AUTO_INQUISITIVE + ": " + Settings.B_ALLOW_AURAS + " is set to 0 or failed to set, so Inquisitive Auras can't be used.\n";
							}
							if (settings.getBoolSetting(Settings.B_LOWER_AURAS) == null || settings.getBoolSetting(Settings.B_LOWER_AURAS) == false) {
								errorMsgs += "Error with " + Settings.B_AUTO_INQUISITIVE + ": " + Settings.B_LOWER_AURAS + " is set to 0 or failed to set, so Inquisitive Auras can't be used.\n";
							}
						} else {
							errorMsgs += "Error with " + Settings.B_AUTO_INQUISITIVE + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_AUTO_WELKIN)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_AUTO_WELKIN, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_AUTO_WELKIN, true);
							if (settings.getBoolSetting(Settings.B_ALLOW_AURAS) == null || settings.getBoolSetting(Settings.B_ALLOW_AURAS) == false) {
								errorMsgs += "Error with " + Settings.B_AUTO_WELKIN + ": " + Settings.B_ALLOW_AURAS + " is set to 0 or failed to set, so Welkin Auras can't be used.\n";
							}
							if (settings.getBoolSetting(Settings.B_LOWER_AURAS) == null || settings.getBoolSetting(Settings.B_LOWER_AURAS) == false) {
								errorMsgs += "Error with " + Settings.B_AUTO_WELKIN + ": " + Settings.B_LOWER_AURAS + " is set to 0 or failed to set, so Welkin Auras can't be used.\n";
							}
						} else {
							errorMsgs += "Error with " + Settings.B_AUTO_WELKIN + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_AUTO_ABYSM)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_AUTO_ABYSM, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_AUTO_ABYSM, true);
							if (settings.getBoolSetting(Settings.B_ALLOW_AURAS) == null || settings.getBoolSetting(Settings.B_ALLOW_AURAS) == false) {
								errorMsgs += "Error with " + Settings.B_AUTO_ABYSM + ": " + Settings.B_ALLOW_AURAS + " is set to 0 or failed to set, so Abysm Auras can't be used.\n";
							}
							if (settings.getBoolSetting(Settings.B_LOWER_AURAS) == null || settings.getBoolSetting(Settings.B_LOWER_AURAS) == false) {
								errorMsgs += "Error with " + Settings.B_AUTO_ABYSM + ": " + Settings.B_LOWER_AURAS + " is set to 0 or failed to set, so Abysm Auras can't be used.\n";
							}
						} else {
							errorMsgs += "Error with " + Settings.B_AUTO_ABYSM + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_FILTER_ENDS)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_FILTER_ENDS, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_FILTER_ENDS, true);
						} else {
							errorMsgs += "Error with " + Settings.B_FILTER_ENDS + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.B_INSERT_SPACES)) {
						fieldCount++;
						if (value.equalsIgnoreCase("0")) {
							settings.addBoolSetting(Settings.B_INSERT_SPACES, false);
						} else if (value.equalsIgnoreCase("1")) {
							settings.addBoolSetting(Settings.B_INSERT_SPACES, true);
						} else {
							errorMsgs += "Error with " + Settings.B_INSERT_SPACES + ": value is not 0 or 1. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.STR_RUNE_DICTIONARY_LOCATION)) {
						fieldCount++;
						// validate the dictionary location
						File file = new File(value);
						if (file.isDirectory()) {
							msgs += settings.setImageDirForGui(value);
						} else {
							errorMsgs += "Error with " + Settings.STR_RUNE_DICTIONARY_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
						}
					} else if (setting.equalsIgnoreCase(Settings.STR_OUTPUT_DIR_LOCATION)) {
						fieldCount++;
						// validate the output location
						File file = new File(value);
						if (file.isDirectory()) {
							settings.setOutputDirectory(value);
						} else {
							errorMsgs += "Error with " + Settings.STR_OUTPUT_DIR_LOCATION + ": value is not a valid directory. Value was: " + value + "\n";
						}
					}
				}
				
				// ensure the settings file actually contained all settings
				if (fieldCount != Settings.LOAD_FIELD_COUNT) {
					errorMsgs += "Error - The settings file does not have all required settings! Fields counted: " + fieldCount + "    Expected # of Fields: " + Settings.LOAD_FIELD_COUNT + "\n";
				}
				
				if (!errorMsgs.isEmpty()) {
					msgs += ("\nErrors while processing the Settings file:\n" + errorMsgs + "SEE THE LOG FOR ALL ERRORS\n");
				}
				
				br.close();
			} catch (NumberFormatException e) {
				msgs += ("\nError reading in settings file. A number value is needed for the following setting:\n" + setting + "\nValue was: " + value);
			} catch (FileNotFoundException e) {
				msgs += ("\nError reading in settings file. The Settings File could not be found at: " + settings.getSettingsPath());
			} catch (Exception e) {
				msgs += ("\nUnknown Error reading in settings file. Program will now exit. Exception was: " + e.getMessage());
			}
			return msgs;
		}
	} // ~Display
	
	
	
	public LexiconifierGui() {
		// lexiconifier ctor will populate defBaseDir, create settings object, etc ...
	}
	
	
	public void runGuiMode() {
		Lexidere display = new Lexidere(settings);
		display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		display.setVisible(true);
		//display.pack();
	}
	
	
	
}

