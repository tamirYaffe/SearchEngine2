package SearchEngineTools.Term;

public abstract class ATerm  implements Comparable<ATerm>{

    private int occurrences=0;
    private String term;

    public void setOccurrences(int occurrences){
        this.occurrences = occurrences;
    }

    public int getOccurrences(){
        return occurrences;
    }

    public String toString(){
        return getTerm();
    }

    public String getTerm(){
        return term;
    }

    /*public boolean isInteger(float f){
        float newF = f - (int)f;
        return (newF > (float)0);
    }*/

    public boolean equals(Object other){
        if(other instanceof ATerm)
            return this.getTerm().equals(((ATerm) other).getTerm());
        return false;
    }

    @Override
    public int hashCode() {
        return getTerm().hashCode();
    }

    public int compareTo(ATerm other){
        return this.getTerm().compareTo(other.getTerm());
    }
}
