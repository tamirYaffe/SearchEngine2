package SearchEngineTools.Term;

public abstract class ATerm  {

    public String toString(){
        return getTerm();
    }

    public abstract String getTerm();

    /*public boolean isInteger(float f){
        float newF = f - (int)f;
        return (newF > (float)0);
    }*/

    public boolean equals(Object other){
        if(other instanceof ATerm)
            return this.getTerm().equals(((ATerm) other).getTerm());
        else if(other instanceof String)
            return this.getTerm().equals(other);
        return false;
    }

    @Override
    public int hashCode() {
        return getTerm().hashCode();
    }
}
