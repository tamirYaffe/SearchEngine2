package SearchEngineTools.Term;

public class CompoundFractionTerm extends FractionTerm {
    private  NumberTerm whole;

    NumberTerm getWholeNumber(){
        return whole;
    }

    public CompoundFractionTerm(NumberTerm whole, FractionTerm fractionTerm){
        super(fractionTerm.getNumerator(),fractionTerm.getDenominator());
        this.whole = whole;
    }

    @Override
    public String getTerm() {
        return whole.getTerm() + " "+ super.getTerm();
    }
}
