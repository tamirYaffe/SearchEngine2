package SearchEngineTools.Term;

public class CompoundFractionCurrencyTerm extends CurrencyTerm {

    private FractionTerm fraction;

    public CompoundFractionCurrencyTerm(CompoundFractionTerm compoundFractionTerm, String currency){
        super(compoundFractionTerm.getWholeNumber(), currency);
        this.fraction = new FractionTerm(compoundFractionTerm.getNumerator(), compoundFractionTerm.getDenominator());
    }

    public CompoundFractionCurrencyTerm(NumberTerm numberTerm, String currency, FractionTerm fraction){
        super(numberTerm, currency);
        this.fraction = fraction;
    }

    public String getTerm(){
        return getValueTermString()+" "+fraction.getTerm()+" "+currency;
    }
}
