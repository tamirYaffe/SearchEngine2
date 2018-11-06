package SearchEngineTools.Term;

public class WordTerm extends ATerm {

    private String term;

    public WordTerm(String term){
        this.term=term;
    }
    @Override
    public String getTerm() {
        return term;
    }
}
