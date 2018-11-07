package SearchEngineTools.Term;

public abstract class ATerm  implements ITerm, Comparable<ITerm>{

    private int occurrences=0;

    public void setOccurrences(int occurrences){
        this.occurrences = occurrences;
    }

    public int getOccurrences(){
        return occurrences;
    }

    public String toString(){
        return getTerm();
    }

    public abstract String getTerm();

    /*public boolean isInteger(float f){
        float newF = f - (int)f;
        return (newF > (float)0);
    }*/

    public boolean equals(Object other){
        if(other instanceof ITerm)
            return this.getTerm().equals(((ITerm) other).getTerm());
        return false;
    }

    @Override
    public int hashCode() {
        return getTerm().hashCode();
    }

    public int compareTo(ITerm other){
        return this.getTerm().compareTo(other.getTerm());
    }
}
