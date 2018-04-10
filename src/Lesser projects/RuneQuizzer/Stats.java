package RuneQuizzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.LinkedList;

public class Stats {
	
	private LinkedList<RuneStats> allStats;
	private String errorMsgs;
	
	public Stats() {
		allStats = new LinkedList<RuneStats>();
	}
	
	// attempts to look up the stat - returns it if it exists, otherwise returns null
	public RuneStats lookUpStats(String runeName, String runeForm, String filePath) {
		errorMsgs = "";
		RuneStats stats = new RuneStats(runeName, runeForm, filePath); // create this for comparator's sake
		if (allStats.contains(stats)) {
			int index = allStats.indexOf(stats);
			return allStats.get(index);
		}
		return null;
	}
	
	// A rune was seen - if this was the first time, add it to the collection. otherwise, just increment the existing object's appearance count.
	public void addStats(String runeName, String runeForm, String filePath) {
		RuneStats stats = lookUpStats(runeName, runeForm, filePath);
		if (stats != null) {
			stats.addAppearance();
		} else {
			stats = new RuneStats(runeName, runeForm, filePath);
			stats.addAppearance();
			allStats.add(stats);
		}
	}
	
	public boolean attempt(String guess, String runeName, String runeForm, String filePath) {
		boolean isCorrect = false;
		RuneStats stats = lookUpStats(runeName, runeForm, filePath);
		if (stats != null) {
			stats.addAttempt();
			if (guess.equalsIgnoreCase(runeName) || (guess.equalsIgnoreCase("DEAD") && runeName.startsWith("DEAD"))) { // sloppy way to handle deads, but it took 2 seconds so whatever
				stats.addCorrect();
				isCorrect = true;
			}
		} else {
			System.out.println("Could not find RuneStats for: " + runeName + " " + runeForm + " " + filePath);
		}
		return isCorrect;
	}

	public void addSkip(String runeName, String runeForm, String filePath) {
		RuneStats stats = lookUpStats(runeName, runeForm, filePath);
		if (stats != null) {
			stats.addSkip();
		} else {
			System.out.println("Could not find RuneStats for: " + runeName + " " + runeForm + " " + filePath);
		}
	}
	
	public void addCredit(String runeName, String runeForm, String filePath) {
		RuneStats stats = lookUpStats(runeName, runeForm, filePath);
		if (stats != null) {
			stats.addCreditRequest();
		} else {
			System.out.println("Could not find RuneStats for: " + runeName + " " + runeForm + " " + filePath);
		}
	}
	
	
	public String getSingleDetails(String runeName, String runeForm, String filePath) {
		String details = "";
		RuneStats stats = lookUpStats(runeName, runeForm, filePath);
		if (stats != null) {
			details = stats.getDetailLine();
		} else {
			System.out.println("Could not find RuneStats for: " + runeName + " " + runeForm + " " + filePath);
		}
		return details;
	}
	
	public String getAllDetails(boolean writeToFile, String outPath) {
		Collections.sort(allStats);
		BufferedWriter bw = null;
		String allDetails = "";
		
		try {
			
			if (writeToFile) {
				bw = new BufferedWriter(new FileWriter(new File(outPath)));
			}
			
			for (int x = 0; x < allStats.size(); ++x) {
				String det = allStats.get(x).getDetailLine();
				allDetails += det + "\n";
				
				if (writeToFile) {
					bw.write(det);
					bw.newLine();
				}
				
			}
			
			if (writeToFile) {
				bw.close();
			}
		} catch (Exception e) {
			//System.out.println("Error caught: " + e.getMessage());
			errorMsgs += "Error caught: " + e.getMessage();
		}
		
		return allDetails;
	}
	
	// This allows us to not include the current rune displayed in the gui in the report.
	public String getAllDetailsIgnoreCurrent(String runeName, String runeForm, String filePath, boolean writeToFile, String outPath) {
		Collections.sort(allStats);
		BufferedWriter bw = null;
		String allDetails = "";
		
		try {
			
			if (writeToFile) {
				bw = new BufferedWriter(new FileWriter(new File(outPath)));
			}
			
			for (int x = 0; x < allStats.size(); ++x) {
				RuneStats currStats = allStats.get(x);
				RuneStats skipRune = lookUpStats(runeName, runeForm, filePath);
				
				String det = null;
				if (currStats.equals(skipRune)) {
					currStats.removeAppearance();
					if (currStats.getNumAppearances() != 0) {
						det = currStats.getDetailLine();
					}
					currStats.addAppearance();
				} else {
					det = currStats.getDetailLine();
				}
				
				if (det != null) {
					allDetails += det + "\n";
					if (writeToFile) {
						bw.write(det);
						bw.newLine();
					}
				}
				
			}
			
			if (writeToFile) {
				bw.close();
			}
		} catch (Exception e) {
			//System.out.println("Error caught: " + e.getMessage());
			errorMsgs += "Error caught: " + e.getMessage();
		}
		
		return allDetails;
	}
	
	// returns and deletes error messages. should be checked after attempting to write a log to a file
	public String removeErrorMsgs() {
		String result = errorMsgs;
		errorMsgs = "";
		return result;
	}
}
