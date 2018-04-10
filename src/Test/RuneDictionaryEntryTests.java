package Test;

import java.util.LinkedList;

import org.junit.Test;

import Text2Auralann.RuneDictionaryEntry;

public class RuneDictionaryEntryTests {
	
	@Test
	public void testHelperMethodsWithOneForm() {

		RuneDictionaryEntry oRune = new RuneDictionaryEntry("A");
		oRune.addForm("A-1", "A-1 Path");
		
		LinkedList<String> forms1 = new LinkedList<String>();
		forms1.add("A-1");
		LinkedList<String> forms2 = new LinkedList<String>();
		
		System.out.println("Base Rune: " + oRune.getBaseForm());
		System.out.println("Get Different Form if Exists (String): " + oRune.getDifferentIfExists("A-1"));
		System.out.println("Get Different Form if Exists (Empty String): " + oRune.getDifferentIfExists(""));
		System.out.println("Get Different Form if Exists (Junk String): " + oRune.getDifferentIfExists("ABC"));
		System.out.println("Get Different Form if Exists (List of 1): " + oRune.getDifferentIfExists(forms1));
		System.out.println("Get Different Form if Exists (Empty List): " + oRune.getDifferentIfExists(forms2));
		System.out.println("File Path: " + oRune.getFilePath("A-1"));
		//System.out.println("File Path (empty): " + oRune.getFilePath(""));
		System.out.println("Get Random Alt if Exists: " + oRune.getRandomAltIfExists());
		System.out.println("Get Random Form: " + oRune.getRandomForm());
		System.out.println("Has any alts: " + oRune.hasAlt());
		System.out.println("How many alts: " + oRune.getAltCount());
		System.out.println("How many forms: " + oRune.getFormCount());
		
		
		// Don't have a real assert yet, just testing by eye
		assert(true);
	}
	
	@Test
	public void testHelperMethodsWithTwoForms() {

		RuneDictionaryEntry oRune = new RuneDictionaryEntry("O");
		oRune.addForm("O-1", "O-1 Path");
		oRune.addForm("O-2", "O-2 Path");
		
		LinkedList<String> forms1 = new LinkedList<String>();
		forms1.add("O-1");
		LinkedList<String> forms2 = new LinkedList<String>();
		forms2.add("O-2");
		LinkedList<String> forms3 = new LinkedList<String>();
		forms3.add("O-1");
		forms3.add("O-2");
		LinkedList<String> forms4 = new LinkedList<String>();
		
		System.out.println("Base Rune: " + oRune.getBaseForm());
		System.out.println("Get Different Form if Exists (String), O-1: " + oRune.getDifferentIfExists("O-1"));
		System.out.println("Get Different Form if Exists (String), O-2: " + oRune.getDifferentIfExists("O-2"));
		System.out.println("Get Different Form if Exists (Empty String): " + oRune.getDifferentIfExists(""));
		System.out.println("Get Different Form if Exists (Junk String): " + oRune.getDifferentIfExists("ABC"));
		System.out.println("Get Different Form if Exists (List of 1): " + oRune.getDifferentIfExists(forms1));
		System.out.println("Get Different Form if Exists (List of 2): " + oRune.getDifferentIfExists(forms2));
		System.out.println("Get Different Form if Exists (List of 1&2): " + oRune.getDifferentIfExists(forms3));
		System.out.println("Get Different Form if Exists (Empty List): " + oRune.getDifferentIfExists(forms4));
		System.out.println("File Path O-1: " + oRune.getFilePath("O-1"));
		System.out.println("File Path O-2: " + oRune.getFilePath("O-2"));
		//System.out.println("File Path (empty): " + oRune.getFilePath(""));
		System.out.println("Get Random Alt if Exists: " + oRune.getRandomAltIfExists());
		System.out.println("Get Random Form try 1: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 2: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 3: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 4: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 5: " + oRune.getRandomForm());
		System.out.println("Has any alts: " + oRune.hasAlt());
		System.out.println("How many alts: " + oRune.getAltCount());
		System.out.println("How many forms: " + oRune.getFormCount());

		// Don't have a real assert yet, just testing by eye
		assert(true);
	}
	
	@Test
	public void testHelperMethodsWithThreeForms() {

		RuneDictionaryEntry oRune = new RuneDictionaryEntry("O");
		oRune.addForm("O-1", "O-1 Path");
		oRune.addForm("O-2", "O-2 Path");
		oRune.addForm("O-3", "O-3 Path"); // fake one for testing
		
		LinkedList<String> forms1 = new LinkedList<String>();
		forms1.add("O-2");
		forms1.add("O-3");
		LinkedList<String> forms2 = new LinkedList<String>();
		forms2.add("O-1");
		forms2.add("O-3");
		LinkedList<String> forms3 = new LinkedList<String>();
		forms3.add("O-1");
		forms3.add("O-2");
		LinkedList<String> forms4 = new LinkedList<String>();
		forms4.add("O-1");
		LinkedList<String> forms5 = new LinkedList<String>();
		forms5.add("O-2");
		LinkedList<String> forms6 = new LinkedList<String>();
		forms6.add("O-3");
		LinkedList<String> forms7 = new LinkedList<String>();
		forms7.add("O-1");
		forms7.add("O-2");
		forms7.add("O-3");
		LinkedList<String> forms8 = new LinkedList<String>();
		
		System.out.println("Base Rune: " + oRune.getBaseForm());
		System.out.println("Get Different Form if Exists (String), O-1 try 1: " + oRune.getDifferentIfExists("O-1"));
		System.out.println("Get Different Form if Exists (String), O-1 try 2: " + oRune.getDifferentIfExists("O-1"));
		System.out.println("Get Different Form if Exists (String), O-1 try 3: " + oRune.getDifferentIfExists("O-1"));
		System.out.println("Get Different Form if Exists (String), O-1 try 4: " + oRune.getDifferentIfExists("O-1"));
		System.out.println("Get Different Form if Exists (String), O-1 try 5: " + oRune.getDifferentIfExists("O-1"));
		System.out.println("Get Different Form if Exists (String), O-2 try 1: " + oRune.getDifferentIfExists("O-2"));
		System.out.println("Get Different Form if Exists (String), O-2 try 2: " + oRune.getDifferentIfExists("O-2"));
		System.out.println("Get Different Form if Exists (String), O-2 try 3: " + oRune.getDifferentIfExists("O-2"));
		System.out.println("Get Different Form if Exists (String), O-2 try 4: " + oRune.getDifferentIfExists("O-2"));
		System.out.println("Get Different Form if Exists (String), O-2 try 5: " + oRune.getDifferentIfExists("O-2"));
		System.out.println("Get Different Form if Exists (String), O-3 try 1: " + oRune.getDifferentIfExists("O-3"));
		System.out.println("Get Different Form if Exists (String), O-3 try 2: " + oRune.getDifferentIfExists("O-3"));
		System.out.println("Get Different Form if Exists (String), O-3 try 3: " + oRune.getDifferentIfExists("O-3"));
		System.out.println("Get Different Form if Exists (String), O-3 try 4: " + oRune.getDifferentIfExists("O-3"));
		System.out.println("Get Different Form if Exists (String), O-3 try 5: " + oRune.getDifferentIfExists("O-3"));
		System.out.println("Get Different Form if Exists (List of 2&3): " + oRune.getDifferentIfExists(forms1));
		System.out.println("Get Different Form if Exists (List of 1&3): " + oRune.getDifferentIfExists(forms2));
		System.out.println("Get Different Form if Exists (List of 1&2): " + oRune.getDifferentIfExists(forms3));
		System.out.println("Get Different Form if Exists (List of 1): " + oRune.getDifferentIfExists(forms4));
		System.out.println("Get Different Form if Exists (List of 2): " + oRune.getDifferentIfExists(forms5));
		System.out.println("Get Different Form if Exists (List of 3): " + oRune.getDifferentIfExists(forms6));
		System.out.println("Get Different Form if Exists (List of 1&2&3): " + oRune.getDifferentIfExists(forms7));
		System.out.println("Get Different Form if Exists (Empty List): " + oRune.getDifferentIfExists(forms8));
		System.out.println("File Path O-1: " + oRune.getFilePath("O-1"));
		System.out.println("File Path O-2: " + oRune.getFilePath("O-2"));
		System.out.println("File Path O-3: " + oRune.getFilePath("O-3"));
		//System.out.println("File Path (empty): " + oRune.getFilePath(""));
		System.out.println("Get Random Alt if Exists try 1: " + oRune.getRandomAltIfExists());
		System.out.println("Get Random Alt if Exists try 2: " + oRune.getRandomAltIfExists());
		System.out.println("Get Random Alt if Exists try 3: " + oRune.getRandomAltIfExists());
		System.out.println("Get Random Alt if Exists try 4: " + oRune.getRandomAltIfExists());
		System.out.println("Get Random Alt if Exists try 5: " + oRune.getRandomAltIfExists());
		System.out.println("Get Random Form try 1: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 2: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 3: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 4: " + oRune.getRandomForm());
		System.out.println("Get Random Form try 5: " + oRune.getRandomForm());
		System.out.println("Has any alts: " + oRune.hasAlt());
		System.out.println("How many alts: " + oRune.getAltCount());
		System.out.println("How many forms: " + oRune.getFormCount());
		
		System.out.println("Get form [0] of O: " + oRune.getForm(0));
		System.out.println("Get form [1] of O: " + oRune.getForm(1));
		System.out.println("Get form [2] of O: " + oRune.getForm(2));
		System.out.println("Get form [3] (doesn't exist) of O: " + oRune.getForm(3));
		System.out.println("Get form [-1] (doesn't exist) of O: " + oRune.getForm(-1));
		
		System.out.println("Get index of O-1: " + oRune.getIndexOf("O-1"));
		System.out.println("Get index of O-2: " + oRune.getIndexOf("O-2"));
		System.out.println("Get index of O-3: " + oRune.getIndexOf("O-3"));
		System.out.println("Get index of O-3: " + oRune.getIndexOf("O-4"));

		// Don't have a real assert yet, just testing by eye
		assert(true);
	}	
}
