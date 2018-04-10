package RuneQuizzer;

import java.text.DecimalFormat;
import java.util.Objects;

// RuneStats.java
// Keeps track of the statistics for a single rune (Such as A-1), including the number of times it appeared, the number of correct guesses, the number of skips, ...

public class RuneStats implements Comparable<RuneStats> {
	
	final String SEP = "    "; // separator between fields in detail line
	
	String runeName;
	String runeForm;
	String filePath;
	
	int numTimesAppeared;
	int numAttempts;
	int numCorrect;
	// int numWrong; - number wrong can be calculated: numAttempts - numCorrect
	int numCreditsRequested; // when the user says "give me credit!"
	int numSkips;
	// double accuracy - accuracy percentages can be calculated: numCorrect / numAttempts
	
	public RuneStats(String runeName, String runeForm, String filePath) {
		this.runeName = runeName;
		this.runeForm = runeForm;
		this.filePath = filePath;
	}
	
	public void addAppearance() {
		++numTimesAppeared;
	}
	
	public void removeAppearance() {
		--numTimesAppeared;
	}
	
	public int getNumAppearances() {
		return numTimesAppeared;
	}
	
	public void addAttempt() {
		++numAttempts;
	}
	
	public void addCorrect() {
		++numCorrect;
	}
	
	public void addSkip() {
		++numSkips;
	}
	
	public void addCreditRequest() {
		++numCreditsRequested;
	}
	
	public int getNumWrong() {
		return numAttempts - (numCorrect + numCreditsRequested);
	}
	
	public String getAccuracy() {
		if ((numCorrect+numCreditsRequested) == 0) {
			return "0";
		}
		double amount = (double)(numCorrect + numCreditsRequested) / (double)numAttempts;
		amount *= 100; // to go from 0.33...% to 33.33...%, for example
		DecimalFormat df = new DecimalFormat("###.##");
		return df.format(amount);
	}
	
	public String getDetailLine() {
		String det = "Rune: " + runeForm;
		det += SEP + "#Appearances: " + numTimesAppeared + SEP + "#Attempts: " + numAttempts + SEP + "#Correct: " + numCorrect + SEP + "#Wrong: " + getNumWrong();
		det += SEP + "#CreditsRequested: " + numCreditsRequested + SEP + "#Skips: " + numSkips + SEP + "Accuracy: " + getAccuracy() + "%";
		
		return det;
	}

	
	
	
	// Overridden Comparator section:

	public String getKeyString() {
		return (runeName.toUpperCase() + runeForm.toUpperCase() + filePath.toUpperCase()); // unique key string describing this RuneStats object, for use with compareTo
	}
	
	@Override
	public int compareTo(RuneStats other) {
		return (this.getKeyString().compareTo(other.getKeyString()));
	}
	
	@Override
    public int hashCode(){
        return Objects.hashCode(getKeyString());
    }

    @Override
    public boolean equals(Object obj){
        if (! (obj instanceof RuneStats) ) {
            return false;
        }
        RuneStats other = (RuneStats)obj;
        if (other.getKeyString().equals(this.getKeyString())) {
        	return true;
        }
        return false;
    }
}
