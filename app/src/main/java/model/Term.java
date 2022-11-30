package model;

public class Term {

    String termText;
    int termID;

    public Term(String termText, int termID) {
        this.termText = termText;
        this.termID = termID;
    }

    public String getTermText() {
        return termText;
    }

    public int getTermID() {
        return termID;
    }
}
