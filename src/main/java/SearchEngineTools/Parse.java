package SearchEngineTools;

import SearchEngineTools.Term.*;
import javafx.util.Pair;

import java.util.*;

public class Parse {

    //all words that depict a value after the number, and the values they represent
    //for example <m, 1000000>, <Thousand, 1000>
    private Map<String, Value> valuesAfterNumber;

    //all frases that depict a currency and their currency symbol
    //for example <U.S dollar, $>, <Dollars, $>
    private ParsingHashMap currencyTypes;

    //all currency symbols the parser will recognize
    //for example $
    private Collection<Character> currencySymbols;

    //months and their values
    //for example <december, 12>
    private Map<String, Integer> months;

    //months and last day
    //i.g <1,31>, <2,29>
    private Map<Integer, Integer> lastDayInMonth;


    //all the words that represent percentages
    //for example %, percent, etc...
    private Collection<String> percentWords;

    //stop words to be removed
    protected Collection<String> stopWords;

    //characters to be removed from beginning and end of words
    private Collection<Character> necessaryChars;

    private ParsingHashMap years;


    /**
     * default constructor
     */
    //initializes data structures
    public Parse(){
        initializeDataStructures();
        this.stopWords = new ArrayList<>();
    }
    /**
     * all strings that represent value, (i.g thousand, million)
     * @return
     */
    private Collection<String> getValueKeywords(){
        return valuesAfterNumber.keySet();
    }

    /**
     *
     * @returnall strings that represent a currency
     * for example: US Dollar, Dollar, etc...
     */
    private Collection<String> getCurrencyStrings(){
        return  currencyTypes.keySet();
    }

    /**
     *
     * @return all symbols that represent currency (i.g $)
     */
    private Collection<Character> getCurrencySymbols(){
        return currencySymbols;
    }

    private Collection<String> getMonthWords(){
        return months.keySet();
    }


    //initiazlize diffrent data structures
    //////////////////////////////////////////////////////
    private void initializeValuesAfterNumber(){
        this.valuesAfterNumber = new HashMap<>();
        valuesAfterNumber.put("thousand",Value.THOUSAND);
        valuesAfterNumber.put("Thousand",Value.THOUSAND);
        valuesAfterNumber.put("Million",Value.MILLION);
        valuesAfterNumber.put("million",Value.MILLION);
        valuesAfterNumber.put("billion",Value.BILLION);
        valuesAfterNumber.put("Billion",Value.BILLION);
        valuesAfterNumber.put("trillion",Value.TRILLION);
        valuesAfterNumber.put("Trillion",Value.TRILLION);
    }

    private void initializeCurrencyTypes() {
        this.currencyTypes = new ParsingHashMap();
        currencyTypes.put("Dollars","Dollars");
        currencyTypes.put("U.S Dollars","Dollars");
    }
    private void initializeDataStructures(){
        initializeValuesAfterNumber();
        initializeCurrencyTypes();
        initializeCurrencySymbols();
        initializeMonths();
        initializeLastDaysInMonth();
        initializePercentWords();
        initializeNecessaryChars();
        initializeYears();
    }

    private void initializeNecessaryChars() {
        necessaryChars = new HashSet<>();
        necessaryChars.add('+');
        necessaryChars.add('-');
        necessaryChars.addAll(currencySymbols);
    }

    private void initializePercentWords() {
        this.percentWords = new HashSet();
        percentWords.add("%");
        percentWords.add("percent");
        percentWords.add("Percent");
        percentWords.add("PERCENT");
        percentWords.add("percentage");
        percentWords.add("Percentage");
        percentWords.add("PERCENTAGE");
    }

    private void initializeLastDaysInMonth() {
        this.lastDayInMonth = new HashMap<>();
        lastDayInMonth.put(1,31);
        lastDayInMonth.put(2,29);
        lastDayInMonth.put(3,31);
        lastDayInMonth.put(4,31);
        lastDayInMonth.put(5,30);
        lastDayInMonth.put(6,31);
        lastDayInMonth.put(7,31);
        lastDayInMonth.put(8,31);
        lastDayInMonth.put(9,30);
        lastDayInMonth.put(10,31);
        lastDayInMonth.put(11,30);
        lastDayInMonth.put(12,31);
    }

    private void initializeMonths() {
        this.months = new HashMap<>();
        months.put("January",1);
        months.put("February",2);
        months.put("March",3);
        months.put("April",4);
        months.put("May",5);
        months.put("June",6);
        months.put("July",7);
        months.put("August",8);
        months.put("September",9);
        months.put("October",10);
        months.put("November",11);
        months.put("December",12);
    }

    private void initializeCurrencySymbols(){
        this.currencySymbols = new HashSet<>();
        currencySymbols.add('$');
    }

    private void initializeYears(){
        this.years = new ParsingHashMap();
        String AD = "AD";
        String BC = "BC";
        years.put("AD",AD);
        years.put("A.D",AD);
        years.put("A.D.E",AD);
        years.put("ADE",AD);
        years.put("Year of our Lord",AD);
        years.put("Year of our Lourd",AD);
        years.put("BC",BC);
        years.put("B.C",BC);
        years.put("B.C.E",BC);
        years.put("BCE",BC);
        years.put("Before Christ",BC);
    }

    /////////////////////////////////////////////


    public Collection<ATerm> parseDocument(List<String> document){
        Map<ATerm,Integer> occurrencesOfTerms = new HashMap<>();
        List<ATerm> terms=new ArrayList<>();
        List<String> tokens=tokenize(document);
        //get terms
        List<ATerm> next = null;
        do{
            next = getNextTerm(tokens);
            if(next!=null) {
                terms.addAll(next);
            }
        }while (next != null);

        //remove stop words
        removeStopWords(terms);
        //addTermsToList(terms, occurrencesOfTerms);
        return getFinalList(terms, occurrencesOfTerms);
        //remove stop words.
        //allow stemming.
    }


    protected void removeStopWords(List<ATerm> toRemoveFrom){
        if(stopWords!=null){
            for (ATerm term: toRemoveFrom) {
                if(stopWords.contains(term.getTerm().toLowerCase()))
                    toRemoveFrom.remove(term);
            }
        }
    }

    protected void addToOccurancesList(ATerm term, Map<ATerm, Integer> occurances) {
        occurances.putIfAbsent(term,0);
        occurances.replace(term,occurances.get(term)+1);
    }

    protected void addWordTermToList(WordTerm term, Map<ATerm,Integer> occurrencesOfTerms, boolean isLowerCase){
        term.toLowerCase();
        boolean existsLowercase = occurrencesOfTerms.containsKey(term);
        term.toUperCase();
        boolean existsUppercase = occurrencesOfTerms.containsKey(term);

        if(isLowerCase && existsUppercase){
            int occurrancesOfTerm = occurrencesOfTerms.get(term);
            occurrencesOfTerms.remove(term);
            term.toLowerCase();
            if(existsLowercase)
                occurrencesOfTerms.replace(term,occurrencesOfTerms.get(term)+occurrancesOfTerm+1);
            else
                occurrencesOfTerms.put(term,occurrancesOfTerm+1);
        }
        else if(isLowerCase){
            term.toLowerCase();
            addToOccurancesList(term, occurrencesOfTerms);
        }
        else if (existsLowercase){
            term.toLowerCase();
            addToOccurancesList(term,occurrencesOfTerms);
        }
        else
            addToOccurancesList(term,occurrencesOfTerms);
    }
    Collection<ATerm> getFinalList(List<ATerm> from, Map<ATerm,Integer> occurrencesOfTerms){
        for (ATerm t:from) {
            if(t instanceof WordTerm){
                addWordTermToList((WordTerm) t,occurrencesOfTerms,Character.isLowerCase(t.getTerm().charAt(0)));
            }
            else{
                addToOccurancesList(t,occurrencesOfTerms);
            }
        }
        for (ATerm t:occurrencesOfTerms.keySet()) {
            t.setOccurrences(occurrencesOfTerms.get(t));
        }
        return occurrencesOfTerms.keySet();
    }
    protected void addTermsToList(List<ATerm> addFrom, Map<ATerm, Integer> occurrencesOfTerms){
        for (ATerm t: addFrom) {
            if(occurrencesOfTerms.containsKey(t)){
                int oldValue = occurrencesOfTerms.get(t);
                occurrencesOfTerms.replace(t,oldValue+1);
            }
            else {
                occurrencesOfTerms.put(t,1);
            }
        }
    }


    /**
     * removes unnecessary chars from the token list, e.g '.' ','
     * @param tokens- the token list we remove chars from
     */
    protected void removeUnnecessaryChars(List<String> tokens) {
        List<Character> unnecessaryChars=new ArrayList<>();
        unnecessaryChars.add('.');
        unnecessaryChars.add(',');
        tokens.replaceAll(token->{
            if(unnecessaryChars.contains(token.charAt(0)))
                token=token.substring(1);
            if(unnecessaryChars.contains(token.substring(token.length() - 1)))
                token=token.substring(0,token.length()-1);
            return token;
        });
    }

    /**
     * divided the input queue of documents lines into list of words/tokens.
     * @param document- the documents lines.
     * @return the queue of document words.
     */
    protected List<String> tokenize(List<String> document) {
        List<String> tokens=new LinkedList<>();
        document.forEach(line -> {
            String[]lineTokens = line.split(" ");
            //remove unnecessarry chars and add to list
            for (int i = 0; i < lineTokens.length; i++) {
                lineTokens[i] = removeUnnecessaryChars(lineTokens[i]);
                tokens.add(lineTokens[i]);
            }

        });
        return tokens;
    }

    private String removeUnnecessaryChars(String lineToken) {
        int firstNecessary = 0;
        int lastNecessary = lineToken.length()-1;
        //find first necessary index
       if(!necessaryChars.contains(lineToken.charAt(firstNecessary)) &&
               (!(Character.isDigit(lineToken.charAt(firstNecessary)) ||
                       Character.isLetter(lineToken.charAt(firstNecessary))))/*!(Character.isDigit(lineToken.charAt(firstNecessary)) ||//first digit is not digit
               Character.isLetter(lineToken.charAt(firstNecessary)) ||//first digit is not letter
               currencySymbols.contains(""+lineToken.charAt(firstNecessary)))&&
            && !necessaryChars.contains(lineToken.charAt(firstNecessary))*/){
           firstNecessary++;
       }
        if(!(Character.isDigit(lineToken.charAt(lastNecessary)) ||//first digit is not digit
                Character.isLetter(lineToken.charAt(lastNecessary)) ||//first digit is not letter
                currencySymbols.contains(""+lineToken.charAt(lastNecessary)))){ //first digit is not currency
            lastNecessary--;
        }
        if(firstNecessary!=0 || lastNecessary!=lineToken.length()-1)
            lineToken = lineToken.substring(firstNecessary,lastNecessary+1);
        return lineToken;
    }


    /**
     * returns number value for string if it is an array with single number, returns null if no such number
     * will ignore commas, and work for strings without letters
     * will work for 10.3,  100,000  , 5
     * will not work for 10m
     * @param s string to getValue of value
     * @return Value of s in number
     */
    private float[] getNumberValue(String s){
        //check if it is already a number
        try {
            float toReturn = Float.parseFloat(s);
            float[] floats = {toReturn};
            return floats;
        }
        //not a double
        catch (Exception e){
            //check if is because of commas
            String [] split = s.split(",");
            StringBuilder toCheck = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                toCheck.append(split[i]);
            }
            //check if it is a number
            try {
                float toReturn = Float.parseFloat(toCheck.toString());
                float[] floats = {toReturn};
                return floats;
            }
            catch (Exception e2){
                return null;
            }

        }
    }

    private boolean isNumber(String s){
        float [] floats = getNumberValue(s);
        return floats != null;
    }

    private boolean isCurrency(String s){
        return s.length()>1 && getCurrencySymbols().contains(s.substring(0,1)) && isNumber(s.substring(1));
    }

    private boolean isFraction(String s){
        String[] split = null;
        if(s.contains("/")) {
            split = s.split("/");
        }
        //check two different parts
        if(split==null || split.length!=2)
            return false;
        //check both are numbers
        return isNumber(split[0]) && isNumber(split[1]);
    }

    private FractionTerm getFractionTerm(String s){
        String[] split = s.split("/");
        NumberTerm numerator = new NumberTerm(split[0]);
        NumberTerm denominator = new NumberTerm(split[1]);
        return new FractionTerm(numerator,denominator);
    }

    private void AddNextNumberTerm(List<String> tokens, ATerm nextTerm, List<ATerm> toReturn){
        //get next word
        if(!tokens.isEmpty()) {
            String nextToken = tokens.get(0);
            //check year
            Pair<String,Integer> year = null;
            if(isInteger(((NumberTerm)nextTerm).getValue())){
                year=getNextRelevantTerm(tokens,years);
            }
            if(year!=null){
                nextTerm = new YearTerm((NumberTerm) nextTerm,this.years.get(year.getKey()));
                for (int i = 0; i <= year.getValue(); i++) {
                    tokens.remove(0);
                }
            }
            //check if percentage
            else if (percentWords.contains(nextToken)) {
                nextTerm = new PercentageTerm((NumberTerm)nextTerm);
                tokens.remove(0);
            }
            //check if month
            else if((((NumberTerm) nextTerm).isInteger(((NumberTerm) nextTerm))) //number is integer
                    && getMonthWords().contains(nextToken) && //next word is month
                    (((NumberTerm) nextTerm).getValueOfNumber()>0) && //number is at least one
                    ((NumberTerm) nextTerm).getValueOfNumber()<=lastDayInMonth.get(months.get(nextToken)) //number is smaller than last day in month
            ){
                nextTerm = new DateTerm(months.get(nextToken),(int)((NumberTerm) nextTerm).getValueOfNumber());
                tokens.remove(0);
            }
            else {
                boolean isFraction = false;
                //check if value
                if (getValueKeywords().contains(nextToken)) {
                    Value val = valuesAfterNumber.get(nextToken);
                    ((NumberTerm) nextTerm).multiply(val);
                    //remove keyword after use
                    tokens.remove(0);

                }
                //check if fraction
                else if(isFraction(nextToken)){
                    nextTerm = new CompoundFractionTerm((NumberTerm)nextTerm,getFractionTerm(nextToken));
                    tokens.remove(0);
                    isFraction = true;
                }
                //check if currency
                Pair<String,Integer> currencyNameAndLocation = null;
                if(!tokens.isEmpty()) {
                    currencyNameAndLocation = getNextRelevantTerm(tokens,currencyTypes);
                }
                if(currencyNameAndLocation != null){
                    nextTerm = isFraction ? new CompoundFractionCurrencyTerm((CompoundFractionTerm)nextTerm, this.currencyTypes.get(currencyNameAndLocation.getKey()))
                            : new CurrencyTerm((NumberTerm)nextTerm, currencyTypes.get(currencyNameAndLocation.getKey()));
                    for (int i = 0; i<=currencyNameAndLocation.getValue(); i++){
                        tokens.remove(0);
                    }
                }
            }
        }
        //no suitable next word found, return number
        toReturn.add(nextTerm);
    }

    private void addWordTerm(List<String> tokens, String token,List<ATerm> toReturn){
        ATerm nextTerm = null;
        //check percentage
        if(isPercentage(token)){
            nextTerm = getPercentageTerm(token);
            toReturn.add(nextTerm);
            return;
        }
        //check currency
        else if(isCurrency(token)){
            nextTerm = getCurrencyTerm(token);
            toReturn.add(nextTerm);
            return;
        }
        //check month
        else if(getMonthWords().contains(token)){
            String nextToken = tokens.isEmpty() ? null : tokens.get(1);
            if(isNumber(nextToken) && isInteger(nextToken)){
                int day = Integer.parseInt(nextToken);
                if(day>0 && day<lastDayInMonth.get(token)){
                    nextTerm = new DateTerm(months.get(token),day);
                    toReturn.add(nextTerm);
                    return;
                }
            }
        }
        //check hyphenated word
        else if(isHyphenatedWord(token)){
            toReturn.addAll(getHyphenatedTokens(token));
            return;
        }
        boolean isNumber = false;
        boolean isFraction = false;
        //check number with value
        if(isNumberWithValue(token)){
            isNumber =true;
            nextTerm = splitWord(token);
            //if list is now empty, return, else switch token to next word
            if(tokens.isEmpty()){
                toReturn.add(nextTerm);
                return;
            }
            else{
                token = tokens.get(0);
            }
        }
        //check fraction
        if(isFraction(token)){
            isFraction = true;
            nextTerm = isNumber ? new CompoundFractionTerm((NumberTerm) nextTerm, getFractionTerm(token)) : getFractionTerm(token);
            isNumber = true;
            tokens.remove(0);
            //if list is now empty, return, else switch token to next word
            if(tokens.isEmpty()){
                toReturn.add(nextTerm);
                return;
            }
            else{
                token = tokens.get(0);
            }
        }
        //check currency
        if(isNumber && getCurrencyStrings().contains(token)){
            nextTerm = isFraction ? new CompoundFractionCurrencyTerm((CompoundFractionTerm) nextTerm,token) : new CurrencyTerm((NumberTerm) nextTerm,token);
            tokens.remove(0);
            toReturn.add(nextTerm);
            return;
        }

        //no special case
        //check not a stop word
        if(isStopWord(token) || token.length()==0 || (token.length()==1 && (!(Character.isLetter(token.charAt(0)) || Character.isDigit(token.charAt(0))))))
            return;
        toReturn.add(new WordTerm(token));
    }

    private boolean isStopWord(String token) {
        String lowerCaseToken = token.toLowerCase();
        return stopWords.contains(lowerCaseToken);
    }

    protected List<ATerm> getNextTerm(List<String> tokens){
        List<ATerm> toReturn = new ArrayList<>();
        ATerm nextTerm = null;
        //if list is empty, no tokens
        if(tokens.size() == 0)
            return null;

        String token = tokens.get(0);
        tokens.remove(0);
        //if is number
        if(isNumber( token)) {
            nextTerm = new NumberTerm(token);
            AddNextNumberTerm(tokens,nextTerm,toReturn);
        }
        //word
        else {
            addWordTerm(tokens,token,toReturn);
        }
        return toReturn;
    }

    private List<ATerm> getHyphenatedTokens(String token) {
        List<ATerm> toReturn = new ArrayList<>();
        toReturn.add(new WordTerm(token));
        String[] words= token.split("-");
        List<String> individualWords = new ArrayList<>(words.length);
        for (int i = 0; i < words.length; i++) {
            if(words[i]!=null && words[i].length()>0)
                individualWords.add(words[i]);
        }
        //add individual words to list
        List<ATerm> next = null;
        do{
            next = getNextTerm(individualWords);
            if(next!=null){
                toReturn.addAll(next);
            }
        }while (next!=null);
        return toReturn;
    }

    private boolean isHyphenatedWord(String token) {
        if(token.contains("-")){
            String[] split = token.split("-");
            return (split!=null && split.length>1 && split[0].length()>0 && split[1].length()>0);
        }
        return false;
    }

    private boolean isPercentage(String token) {
        return (token.length()>1 && token.charAt(token.length()-1)=='%' && isNumber(token.substring(0,token.length()-1)));
    }

    private boolean isInteger(CharSequence s){
        String string;
        if(s instanceof String)
            string = (String)s;
        else{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                sb.append(s.charAt(i));
            }
            string = sb.toString();
        }
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private boolean isNumberWithValue(String s){
        //get last index of number
        boolean number = true;
        int pointer = 0;
        while (pointer<s.length() && number){
            number = Character.isDigit(s.charAt(pointer)) || s.charAt(pointer)=='.';
            if(number)
                pointer++;
        }
        //check if is number and word after it represents value
        if(pointer>0){
            String numString = s.substring(0,pointer+1);
            String word = s.substring(pointer+1);
            if(isNumber(numString) && getValueKeywords().contains(word))
                return true;
            return false;
        }
        return false;
    }

    private NumberTerm splitWord(String s){
        boolean number = true;
        int pointer = 0;
        while (pointer<s.length() && number){
            number = Character.isDigit(s.charAt(pointer)) || s.charAt(pointer)=='.';
            if(number)
                pointer++;
        }
        String numString = s.substring(0,pointer+1);
        String word = s.substring(pointer+1);

        NumberTerm toReturn = new NumberTerm(numString);
        toReturn.multiply(valuesAfterNumber.get(word));
        return toReturn;
    }

    private PercentageTerm getPercentageTerm(String s){
        if(isPercentage(s)){
            NumberTerm term = new NumberTerm(s.substring(0,s.length()-1));
            return new PercentageTerm(term);
        }
        else if(isNumber(s)){
            NumberTerm term = new NumberTerm(s);
            return new PercentageTerm(term);
        }
        return null;
    }

    private CurrencyTerm getCurrencyTerm(String s){
        if(isCurrency(s)){
            String currency = currencyTypes.get(s.substring(0,1));
            NumberTerm term = new NumberTerm(s.substring(1));
            return new CurrencyTerm(term,currency);
        }
        return null;
    }

    private static Pair<String, Integer> getNextRelevantTerm(List<String> tokens, ParsingHashMap toGetFrom){
        Pair<String,Integer> toReturn = null;
        String toCheck = "";
        Collection<String> keys = toGetFrom.keySet();
        for (int i = 0; i < toGetFrom.getWordsInLongestKey() && i<=tokens.size(); i++) {
            String toAdd = tokens.get(i);
            if(i!=0)
                toCheck+=(" "+toAdd);
            else
                toCheck+=toAdd;
            if(keys.contains(toCheck)) {
                toReturn = new Pair<>(toCheck, i);
                break;
            }
        }
        return toReturn;
    }



}
