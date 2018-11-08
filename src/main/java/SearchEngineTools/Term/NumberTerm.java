package SearchEngineTools.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NumberTerm extends ATerm{


    private char[] numberWithoutDecimal;
    private int locationOfDecimal;
    boolean containsDecimal;


    public NumberTerm(NumberTerm other){
        this.numberWithoutDecimal = other.getNumberWithoutDecimal();
        this.locationOfDecimal = other.getLocationOfDecimal();
        containsDecimal = other.containsDecimal;
        this.term = createTerm();
    }

    public NumberTerm(String s){
        locationOfDecimal = Integer.MAX_VALUE;
        String number = removeCommas(s);
        if(number.contains(".")){
            numberWithoutDecimal = new char[number.length()-1];
            containsDecimal =true;
        }
        else{
            numberWithoutDecimal = new char[number.length()];
            containsDecimal = false;
        }
        for (int i = 0, j= 0; i < number.length(); i++) {
            if(number.charAt(i)=='.') {
                locationOfDecimal = i;
            }
            else {
                numberWithoutDecimal[j] = number.charAt(i);
                j++;
            }
        }
        this.term = null;
    }

    private static String removeCommas(String s) {
        String toReturn = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            toReturn+= c == ',' ? "" : c;
        }
        return toReturn;
    }


    public boolean isWholeNumber(){
        return !this.containsDecimal;
    }


    public void multiply(Value multiplyBy){
        int digitsToAdd=0;
        switch (multiplyBy){
            case TRILLION:
                digitsToAdd=12;
                break;
            case BILLION:
                digitsToAdd=9;
                break;
            case MILLION:
                digitsToAdd=6;
                break;
            case THOUSAND:
                digitsToAdd=3;
                break;
        }
        //add digits
        char[] newNumberWithoutDecimal = new char[this.numberWithoutDecimal.length+digitsToAdd];
        int newPointer =0, oldPointer =0;
        while (oldPointer<this.numberWithoutDecimal.length){
            newNumberWithoutDecimal[newPointer] = this.numberWithoutDecimal[oldPointer];
            newPointer++;
            oldPointer++;
        }
        while (newPointer < newNumberWithoutDecimal.length){
            newNumberWithoutDecimal[newPointer] = '0';
            newPointer++;
        }
        this.numberWithoutDecimal = newNumberWithoutDecimal;
        //move decimal
        if(locationOfDecimal!=Integer.MAX_VALUE) {
            this.locationOfDecimal += digitsToAdd;
        }
        this.term = null;
    }

    public char[] getNumberWithoutDecimal() {
        char[] toReturn = new char[numberWithoutDecimal.length];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = numberWithoutDecimal[i];
        }
        return toReturn;
    }

    public int getLocationOfDecimal() {
        return locationOfDecimal;
    }

    /**
     * removes unnecessary digits and decimal point before printing term
     * @param s numberterm to print
     * @return valid string for term
     */
    private static String removeUnnecessaryDigits(String s){
        int lastNecessaryIndx = s.length()-1;
        boolean necessary = false;
        while (!necessary && lastNecessaryIndx>0){
            if(s.charAt(lastNecessaryIndx)=='0'){
                lastNecessaryIndx--;
            }
            else if(s.charAt(lastNecessaryIndx)=='.'){
                lastNecessaryIndx--;
                necessary=true;
            }
            else {
                necessary = true;
            }
        }
        String toReturn = s.substring(0,lastNecessaryIndx+1);
        return toReturn;
    }

    public static boolean isInteger (NumberTerm numberTerm){
        return numberTerm.locationOfDecimal==Integer.MAX_VALUE;
    }



    public String getValue(){
        return getValue(locationOfDecimal);
    }

    public float getValueOfNumber(){
        String val = "0";
        for (int i = 0; i < this.numberWithoutDecimal.length; i++) {
            if(i == locationOfDecimal)
                val+=".";
            val+=numberWithoutDecimal[i];
        }
        return Float.parseFloat(val);
    }

    private String getValue(int locationOfDecimal){
        StringBuilder value = new StringBuilder();
        int digitsToPrint = locationOfDecimal==numberWithoutDecimal.length ? this.numberWithoutDecimal.length : this.numberWithoutDecimal.length+1;
        boolean containsDecimal = false;
        for (int i = 0, j=0; i < digitsToPrint; i++) {
            if(i==locationOfDecimal){
                value.append('.');
                containsDecimal = true;
            }
            else {
                value.append(numberWithoutDecimal[j]);
                j++;
            }
        }
        return containsDecimal ? removeUnnecessaryDigits(value.toString()) : value.toString();
    }

    protected String createTerm(){
        String afterNumber = "";
        int digitsBeforeDecimal = containsDecimal ? locationOfDecimal : numberWithoutDecimal.length;
        if(digitsBeforeDecimal > 10){
            afterNumber = "B";
            digitsBeforeDecimal-=9;
        }
        else if(digitsBeforeDecimal > 7){
            afterNumber = "M";
            digitsBeforeDecimal-=6;
        }
        else if(digitsBeforeDecimal > 4){
            afterNumber = "K";
            digitsBeforeDecimal-=3;
        }

        return getValue(digitsBeforeDecimal)+afterNumber;
    }

    public String getTerm(){
        if(term==null)
            term = createTerm();
        return super.getTerm();
    }
}
