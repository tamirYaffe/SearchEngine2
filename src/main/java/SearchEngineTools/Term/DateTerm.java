package SearchEngineTools.Term;

public class DateTerm extends ATerm {

    private int day;
    private int month;

    public DateTerm (int month, int day){
        this.day = day;
        this.month = month;
    }

    @Override
    public String getTerm() {
        return month+"-"+day;
    }
}
