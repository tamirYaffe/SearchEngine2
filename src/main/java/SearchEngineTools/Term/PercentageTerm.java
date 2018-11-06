package SearchEngineTools.Term;

public class PercentageTerm extends NumberTerm {

    public PercentageTerm(NumberTerm term){
        super(term);
    }

    @Override
    public String getTerm() {
        return super.getTerm()+"%";
    }
}
