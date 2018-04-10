package RuneWebpageBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;

public class Main {
	
	// This class will output text to help build a webpage for the Rune Dictionaries
	// Very quick and dirty.
	
	public static void main(String[] args) {
		Main driver = new Main();
		String html = "";
		
		// flag to set which dictionary to use
		// 1 = Basic Dictionary
		// 2 = Expanded Dictionary
		// 3 = Elder Futhark Dictionary
		// 4 = Basic Dictionary (Hollowless)
		// 5 = Expanded Dictionary (Hollowless)
		// 6 = English Dictionary
		int dictionaryFlag = 6;
		
		String baseDir = "";
		if (dictionaryFlag == 1) {
			baseDir = "C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Website 2.0\\images\\Basic Dictionary";
			html += "<h2> Basic Dictionary </h2>";
		} else if (dictionaryFlag == 2) {
			baseDir = "C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Website 2.0\\images\\Expanded Dictionary";
			html += "<h2> Expanded Dictionary </h2>";
		} else if (dictionaryFlag == 3) {
			baseDir = "C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Website 2.0\\images\\Elder Futhark Dictionary";
			html += "<h2> Elder Futhark Dictionary </h2>";
		} else if (dictionaryFlag == 4) {
			baseDir = "C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Website 2.0\\images\\Basic Dictionary (Hollowless)";
			html += "<h2> Basic Dictionary (Hollowless) </h2>";
		} else if (dictionaryFlag == 5) {
			baseDir = "C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Website 2.0\\images\\Expanded Dictionary (Hollowless)";
			html += "<h2> Expanded Dictionary (Hollowless) </h2>";
		} else if (dictionaryFlag == 6) {
			baseDir = "C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\Website 2.0\\images\\English Dictionary";
			html += "<h2> English Dictionary </h2>";
		}
		
		// The tutorial site dictionaries only have the basics - no .txt rune files, no EMPTY, EMPTY-RUNE or other Aura runes, ...
		// Just parse them, and print out the contents of each folder as html
		
		// Applicable Categories:
		// Basic and Expanded: Single, Double, Triple, Quad, Numeric, Symbol, Special, Dead
		// Futhark: Single, Double
		
		try {
		
		// every x runes, we want a "newline" effectively
		html += driver.addNewCategory("Single Runes", baseDir + "\\SingleRunes");
		
		// for every category except english, do doubles
		if (dictionaryFlag != 6) { 
			html += driver.addNewCategory("Double Runes", baseDir + "\\DoubleRunes");
		}
			
		// if not futhark or english, do triples and quads
		if (dictionaryFlag != 3 && dictionaryFlag != 6) {
			html += driver.addNewCategory("Triple Runes", baseDir + "\\TripleRunes");
			html += driver.addNewCategory("Quad Runes", baseDir + "\\QuadRunes");
		}
		
		if (dictionaryFlag != 3) { // and then all these extra categories for everything but english
			html += driver.addNewCategory("Numeric Runes", baseDir + "\\NumericRunes");
			html += driver.addNewCategory("Symbol Runes", baseDir + "\\SymbolRunes");
			html += driver.addNewCategory("Special Runes", baseDir + "\\SpecialRunes");
			html += driver.addNewCategory("Dead Runes (Single)", baseDir + "\\DeadRunes\\SingleDeadRunes");
			html += driver.addNewCategory("Dead Runes (Double)", baseDir + "\\DeadRunes\\DoubleDeadRunes");
			html += driver.addNewCategory("Dead Runes (Triple)", baseDir + "\\DeadRunes\\TripleDeadRunes");
			html += driver.addNewCategory("Dead Runes (Quad)", baseDir + "\\DeadRunes\\QuadDeadRunes");
		}
		
		// actually, if futhark, we can do special runes to pull in "DESTINY"
		if (dictionaryFlag == 3) {
			html += driver.addNewCategory("Special Runes", baseDir + "\\SpecialRunes");
		}
		
		// print the html
		System.out.println(html);
		
		} catch (Exception e) {
			System.out.println("Exception caught: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public String addNewCategory(String categoryName, String filePath) throws Exception {
		String html = "";
		html += "<h3> " + categoryName + "</h3>\n";
		
		LinkedList<String> filenames = getFileNames(filePath);
		for (int x = 0; x < filenames.size(); ++x) {
			html += addRune(filenames.get(x), categoryName);
		}
		
		return html;
	}
	
	public String addRune(String filename, String categoryName) {
		String html = "";
		String runeName = interpretRuneName(filename, true);
		
		// for the site, we need to make the filename simpler: just the Dictionary and onward, ie Basic Dictionary\SingleRunes\A-1.png
		String siteFilename = filename;
		if (siteFilename.contains("Basic Dictionary (Hollowless)")) {
			siteFilename = siteFilename.substring(siteFilename.indexOf("Basic Dictionary (Hollowless)"));
		} else if (siteFilename.contains("Basic Dictionary")) {
			siteFilename = siteFilename.substring(siteFilename.indexOf("Basic Dictionary"));
		} else if (siteFilename.contains("Expanded Dictionary (Hollowless)")) {
			siteFilename = siteFilename.substring(siteFilename.indexOf("Expanded Dictionary (Hollowless)"));
		} else if (siteFilename.contains("Expanded Dictionary")) {
			siteFilename = siteFilename.substring(siteFilename.indexOf("Expanded Dictionary"));
		} else if (siteFilename.contains("Elder Futhark Dictionary")) {
			siteFilename = siteFilename.substring(siteFilename.indexOf("Elder Futhark Dictionary"));
		} else if (siteFilename.contains("English")) {
			siteFilename = siteFilename.substring(siteFilename.indexOf("English Dictionary"));
		}
			
		siteFilename = "images\\" + siteFilename;
		
		//html += "<h6> " + runeName + " </h6>\n";
		//html += "<img alt=\"\" src=\"" + siteFilename + "\" style=\"width: 50.00px; height: 50.00px;\" title=\"" + runeName + "\">\n";
		//html += runeName + ": <img alt=\"\" src=\"" + siteFilename + "\" style=\"width: 50.00px; height: 50.00px;\" title=\"" + runeName + "\">\n";
		//html += runeName.toUpperCase() + ": <img alt=\"" + runeName.toUpperCase() + "\" src=\"" + siteFilename + "\" style=\"width: 50.00px; height: 50.00px;\" title=\"" + runeName.toUpperCase() + "\">\n"; //&nbsp;&nbsp;&nbsp;&nbsp;\n";
		
		// now with my crazy divs
		// except symbol runes and special runes need more room for their long names...
		if (categoryName.equalsIgnoreCase("Symbol Runes")) {
			html += "<div id=\"longrd\"> <img id=\"rdimg\" alt=\""+ runeName.toUpperCase() + "\" title=\"" + runeName.toUpperCase() + "\" src=\"" + siteFilename + "\" style=\"width: 50.00px; height: 50.00px;\"> <span id=\"rdtext\"> " + runeName.toUpperCase() + ": </span> </div> \n";
		} else if (categoryName.equalsIgnoreCase("Special Runes")) {
			html += "<div id=\"midrd\"> <img id=\"rdimg\" alt=\""+ runeName.toUpperCase() + "\" title=\"" + runeName.toUpperCase() + "\" src=\"" + siteFilename + "\" style=\"width: 50.00px; height: 50.00px;\"> <span id=\"rdtext\"> " + runeName.toUpperCase() + ": </span> </div> \n";
		} else if (categoryName.contains("Dead Runes")) { // and we don't need to show the number for dead runes...
			html += "<div id=\"rd\"> <img id=\"rdimg\" alt=\""+ runeName.toUpperCase() + "\" title=\"" + runeName.toUpperCase() + "\" src=\"" + siteFilename + "\" style=\"width: 50.00px; height: 50.00px;\"> <span id=\"rdtext\"> </span> </div> \n";
		} else {
			html += "<div id=\"rd\"> <img id=\"rdimg\" alt=\""+ runeName.toUpperCase() + "\" title=\"" + runeName.toUpperCase() + "\" src=\"" + siteFilename + "\" style=\"width: 50.00px; height: 50.00px;\"> <span id=\"rdtext\"> " + runeName.toUpperCase() + ": </span> </div> \n";
		}
		
		
		return html;
	}
	
	// ripped from RuneDictionary.java
	public LinkedList<String> getFileNames(String path) throws Exception {
		LinkedList<String> filepaths = new LinkedList<String>();
		
		try {
			Files.walk(Paths.get(path)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					filepaths.add(filePath.toString());
				}
			});
		} catch (NoSuchFileException e) {
			throw new Exception("Error - RuneDictionary could not find file or directory: " + e.getMessage());
		} catch (IOException e) {
			throw new Exception("Unknown Error reading image file directory\nException caught: " + e.getMessage());
		}
		
		// now sort it in alpha order
		Collections.sort(filepaths);
		
		return filepaths;
	}
	
	// Ripped from RuneDictionary.java
	// returns the name of the rune, ie: "O" from "O-2", "RE" from "RE-3", "A" from "A", ...
	// sometimes we want to ignore the dash though, in things like "A-DEAD" or "LEFT-PARENTHESES", so set ignoreDash to true in those cases
	public String interpretRuneName(String filename, boolean ignoreDash) {
		String runeName = "";

		// gotta make sure we handle both kinds of slashes
		filename = filename.replace("\\", "/");
		
		if (filename.contains("-") && !ignoreDash) {
			runeName = filename.substring(filename.lastIndexOf("/")+1, filename.lastIndexOf("-"));
		} else {
			runeName = filename.substring(filename.lastIndexOf("/")+1, filename.lastIndexOf("."));
		}
		
		return runeName;
	}
	
}
