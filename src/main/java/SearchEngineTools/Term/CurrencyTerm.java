package SearchEngineTools.Term;

public class CurrencyTerm extends ATerm{

    protected NumberTerm numberTerm;
    protected String currency;

    public CurrencyTerm(NumberTerm numberTerm, String currency){
        this.numberTerm = numberTerm;
        this.currency = currency;
    }


    protected String getValueTermString(){
        char [] numberWithoutDecimal = numberTerm.getNumberWithoutDecimal();
        String afterNumber = "";
        int digitsBeforeDecimal = numberTerm.isWholeNumber() ? numberWithoutDecimal.length : numberTerm.getLocationOfDecimal();
        if(digitsBeforeDecimal > 7){
            digitsBeforeDecimal = digitsBeforeDecimal - 6;
            afterNumber = " M";
        }
        int digitsToPrint = digitsBeforeDecimal==numberWithoutDecimal.length ? numberWithoutDecimal.length : numberWithoutDecimal.length+1;
        StringBuilder term = new StringBuilder();
        for (int i = 0, j=0; i < digitsToPrint; i++) {
            if(i==digitsBeforeDecimal){
                term.append(".");
            }
            else {
                term.append(numberWithoutDecimal[j]);
                j++;
            }
        }
        term.append(afterNumber);
        return term.toString();
    }

    @Override
    protected String createTerm() {
        return getValueTermString()+" "+currency;
    }
}
