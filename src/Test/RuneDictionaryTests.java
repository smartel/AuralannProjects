package Test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;

import Text2Auralann.RuneDictionary;
import Text2Auralann.RuneDictionaryEntry;

public class RuneDictionaryTests {

	@Test
	public void testDeadRunes() {
		try {
			// Pass in the base rune directory.
			RuneDictionary rd = new RuneDictionary(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
	
			//ctor now calls this...
			//rd.readInDeadRunes();
			
			// now get and print out some random dead runes
			RuneDictionaryEntry rde = rd.getDeadRune();
			System.out.println(rde.getBaseForm() + " - " + rde.getFilePath(rde.getBaseForm()));
			rde = rd.getDeadRune();
			System.out.println(rde.getBaseForm() + " - " + rde.getFilePath(rde.getBaseForm()));
			rde = rd.getDeadRune();
			System.out.println(rde.getBaseForm() + " - " + rde.getFilePath(rde.getBaseForm()));
			rde = rd.getDeadRune();
			System.out.println(rde.getBaseForm() + " - " + rde.getFilePath(rde.getBaseForm()));
			rde = rd.getDeadRune();
			System.out.println(rde.getBaseForm() + " - " + rde.getFilePath(rde.getBaseForm()));
			
			// Don't have a real assert yet, just testing by eye
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testNormalRunes() {
		try {
			// Pass in the base rune directory.
			RuneDictionary rd = new RuneDictionary(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
			
			//ctor now calls this...
			//rd.readInNormalRunes(1); rd.readInNormalRunes(2); rd.readInNormalRunes(3); rd.readInNormalRunes(4);
			
			System.out.println("Is  'A'    a rune? " + rd.isRune("A", true, true, true));
			System.out.println("Is  '@'    a rune? " + rd.isRune("@", true, true, true));
			System.out.println("Is  'OR'   a rune? " + rd.isRune("OR", true, true, true));
			System.out.println("Is  'ZZ'   a rune? " + rd.isRune("ZZ", true, true, true));
			System.out.println("Is  'THE'  a rune? " + rd.isRune("THE", true, true, true));
			System.out.println("Is  'ZZZ'  a rune? " + rd.isRune("ZZZ", true, true, true));
			System.out.println("Is  'RUNE' a rune? " + rd.isRune("RUNE", true, true, true));
			System.out.println("Is  'ZZZZ' a rune? " + rd.isRune("ZZZZ", true, true, true));
			
			// Don't have a real assert yet, just testing by eye
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testNumberRunes() {
		try {
			// Pass in the base rune directory.
			RuneDictionary rd = new RuneDictionary(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
			
			//ctor now calls this...
			//rd.readInNumberRunes();
			
			System.out.println("Is  '0' a number? " + rd.isNumber("0"));
			System.out.println("Is  '1' a number? " + rd.isNumber("1"));
			System.out.println("Is  '2' a number? " + rd.isNumber("2"));
			System.out.println("Is  '3' a number? " + rd.isNumber("3"));
			System.out.println("Is  '4' a number? " + rd.isNumber("4"));
			System.out.println("Is  '5' a number? " + rd.isNumber("5"));
			System.out.println("Is  '6' a number? " + rd.isNumber("6"));
			System.out.println("Is  '7' a number? " + rd.isNumber("7"));
			System.out.println("Is  '8' a number? " + rd.isNumber("8"));
			System.out.println("Is  '9' a number? " + rd.isNumber("9"));
			System.out.println("Is '10' a number? " + rd.isNumber("10")); // not on its own it's not!
			System.out.println("Is  'A' a number? " + rd.isNumber("a"));
			
			// Don't have a real assert yet, just testing by eye
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void testHelperMethods() {
		try {
			// Pass in the base rune directory.
			RuneDictionary rd = new RuneDictionary(System.getProperty("user.dir") + "\\RuneDictionaries\\Expanded Dictionary\\");
			
			System.out.println("\nO-1.jpg Name: " + rd.interpretRuneName("O-1.jpg", false));
			System.out.println("O-2.jpg Name: " + rd.interpretRuneName("O-2.jpg", false));
			System.out.println("O.jpg Name: " + rd.interpretRuneName("O.jpg", false));
			System.out.println("RE.jpg Name: " + rd.interpretRuneName("RE.jpg", false));
			System.out.println("RE-3.jpg Name: " + rd.interpretRuneName("RE-3.jpg", false));
			System.out.println("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/DoubleRunes/O-2.png Name: " +
					rd.interpretRuneName("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/DoubleRunes/O-2.png", false));
			System.out.println("C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\RuneDictionaries\\Expanded Dictionary\\DoubleRunes\\O-2.png Name: " +
					rd.interpretRuneName("C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\RuneDictionaries\\Expanded Dictionary\\DoubleRunes\\O-2.png", false));
			
			
			System.out.println("O-1.jpg Num: " + rd.interpretRuneNum("O-1.jpg", false));
			System.out.println("O-2.jpg Num: " + rd.interpretRuneNum("O-2.jpg", false));
			System.out.println("O.jpg Num: " + rd.interpretRuneNum("O.jpg", false));
			System.out.println("RE.jpg Num: " + rd.interpretRuneNum("RE.jpg", false));
			System.out.println("RE-3.jpg Num: " + rd.interpretRuneNum("RE-3.jpg", false));
			System.out.println("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/DoubleRunes/O-2.png Num: " +
					rd.interpretRuneNum("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/DoubleRunes/O-2.png", false));
			System.out.println("C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\RuneDictionaries\\Expanded Dictionary\\DoubleRunes\\O-2.png Num: " +
					rd.interpretRuneNum("C:\\Users\\Porvelm\\Desktop\\Auralann\\Text2Auralann\\RuneDictionaries\\Expanded Dictionary\\DoubleRunes\\O-2.png", false));
			
			System.out.println("IgnoreDash (don't blow up trying to get the name): " +
					rd.interpretRuneName("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/SymbolRunes/Left-Parentheses.png", true));
			System.out.println("IgnoreDash (don't blow up trying to get the form): " +
					rd.interpretRuneNum("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/SymbolRunes/Left-Parentheses.png", true));
			
			System.out.println("\nPrinting out directory contents (in alpha order)");
			LinkedList<String> filepaths = rd.getFileNames("C:/Users/Porvelm/Desktop/Auralann/Text2Auralann/RuneDictionaries/Expanded Dictionary/TripleRunes");
			for (int x = 0; x < filepaths.size(); ++x) {
				System.out.println(filepaths.get(x));
			}
			
			System.out.println("\nIs-This-A-Rune Tests:");
			System.out.println("Is 'A' a rune?: " + rd.isRune("A", true, true, true));
			System.out.println("Is 'OR' a rune?: " + rd.isRune("OR", true, true, true));
			System.out.println("Is 'OR' a rune (no doubles allowed)?: " + rd.isRune("OR", false, true, true));
			System.out.println("Is 'THE' a rune?: " + rd.isRune("THE", true, true, true));
			System.out.println("Is 'THE' a rune (no triples allowed)?: " + rd.isRune("THE", true, false, true));
			System.out.println("Is 'RUNE' a rune?: " + rd.isRune("RUNE", true, true, true));
			System.out.println("Is 'RUNE' a rune (no quads allowed)?: " + rd.isRune("RUNE", true, true, false));
			
			System.out.println("\nSymbol/End Tests:");
			System.out.println("Is ''' a symbol (no aura)?: " + rd.isSymbol("'"));
			System.out.println("Is ':' a symbol (no aura)?: " + rd.isSymbol(":"));
			System.out.println("Is ';' a symbol (no aura)?: " + rd.isSymbol(";"));
			System.out.println("Is '-' a symbol (no aura)?: " + rd.isSymbol("-"));
			System.out.println("Is '(' a symbol (no aura)?: " + rd.isSymbol("("));
			System.out.println("Is '\"' a symbol (no aura)?: " + rd.isSymbol("\""));
			System.out.println("Is '[' a symbol (no aura)?: " + rd.isSymbol("["));
			System.out.println("Is ')' a symbol (no aura)?: " + rd.isSymbol(")"));
			System.out.println("Is ']' a symbol (no aura)?: " + rd.isSymbol("]"));
			System.out.println("Is '\\' a symbol (no aura)?: " + rd.isSymbol("\\"));
			System.out.println("Is '/' a symbol (no aura)?: " + rd.isSymbol("/"));
			System.out.println("Is ',' a symbol (no aura)?: " + rd.isSymbol(","));
			System.out.println("Is '.' a symbol (it's an end)?: " + rd.isSymbol("."));
			System.out.println("Is '!' a symbol (it's an end)?: " + rd.isSymbol("!"));
			System.out.println("Is '?' a symbol (it's an end)?: " + rd.isSymbol("?"));
			System.out.println("Is '.' an end?: " + rd.isEndGlyph("."));
			System.out.println("Is '!' an end?: " + rd.isEndGlyph("!"));
			System.out.println("Is '?' an end?: " + rd.isEndGlyph("?"));
			
			// Don't have a real assert yet, just testing by eye
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	
}
