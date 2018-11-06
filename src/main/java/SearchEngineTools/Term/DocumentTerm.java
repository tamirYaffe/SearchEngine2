package SearchEngineTools.Term;

public class DocumentTerm implements ITerm{

    private ATerm term;
    private int occurrencesInDocument;

    public DocumentTerm(ATerm term, int occurrencesInDocument){
        this.occurrencesInDocument = occurrencesInDocument;
        this.term = term;
    }
    public int getOccurrencesInDocument(){
        return this.occurrencesInDocument;
    }

    public String getTerm(){
        return term.getTerm();
    }

    @Override
    public int getOccurrences() {
        return 0;
    }

    public String getTermString(){
        return term.getTerm();
    }

    public String toString(){
        return getTermString() + ", has: " + occurrencesInDocument + "occurrences in document";
    }
}
