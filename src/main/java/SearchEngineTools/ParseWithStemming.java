package SearchEngineTools;

import SearchEngineTools.Term.ATerm;
import SearchEngineTools.Term.WordTerm;

import java.util.*;

public class ParseWithStemming extends Parse {

    private Stemmer stemmer;

    public ParseWithStemming(){
        super();
        stemmer = new Stemmer();
    }

    public Collection<ATerm> parseDocument(List<String> document){
        Map<ATerm,Integer> occurrencesOfTerms = new HashMap<>();
        List<ATerm> terms=new ArrayList<>();
        List<String> tokens=tokenize(document);
        //do text operations(remove unnecessary chars, change words by the assignment rules).
        removeUnnecessaryChars(tokens);
        //get terms
        List<ATerm> next = null;
        do{
            next = getNextTerm(tokens);
            if(next!=null) {
                //allow stemming.
                handleAll(next, occurrencesOfTerms);
            }
        }while (next != null);

        return getFinalList(occurrencesOfTerms);
    }

    private void handleAll(List<ATerm> toHandle, Map<ATerm,Integer> occurances) {
        for (ATerm term:toHandle) {
            if (term instanceof WordTerm){
                //remove stop words
                boolean isLowerCase = isLowerCase((WordTerm) term);
                if(this.stopWords.contains(term.getTerm())){
                    //remove term and continue
                    toHandle.remove(term);
                    continue;
                }
                //stem
                stemTerm((WordTerm) term);
                //add to occurrance list
                addToOccurancesList((WordTerm) term,occurances, isLowerCase);
            }
            else
                addToOccurancesList(term,occurances);
        }

    }

    private void addToOccurancesList(WordTerm term, Map<ATerm, Integer> occurances, boolean isLowerCase) {
        term.toLowerCase();
        boolean existsLowercase = occurances.containsKey(term);
        term.toUperCase();
        boolean existsUppercase = occurances.containsKey(term);

        if(isLowerCase && existsUppercase){
            int occurrancesOfTerm = occurances.get(term);
            occurances.remove(term);
            term.toLowerCase();
            if(existsLowercase)
                occurances.replace(term,occurances.get(term)+occurrancesOfTerm+1);
            else
                occurances.put(term,occurrancesOfTerm+1);
        }
        else if(isLowerCase){
            term.toLowerCase();
            addToOccurancesList(term, occurances);
        }
        else if (existsLowercase){
            term.toLowerCase();
            addToOccurancesList(term,occurances);
        }
        else
            addToOccurancesList(term,occurances);

    }

    private void addToOccurancesList(ATerm term, Map<ATerm, Integer> occurances) {
        occurances.putIfAbsent(term,0);
        occurances.replace(term,occurances.get(term)+1);
    }

    private void stemTerm(WordTerm term){
        (term).toLowerCase();
        (term).setTerm(stemmer.stem(term.getTerm()));
    }

    private boolean isLowerCase(WordTerm term){
        if(Character.isLetter(term.getTerm().charAt(0)) && Character.isLowerCase(term.getTerm().charAt(0)))
            return true;
        return false;
    }

    protected Collection<ATerm> getFinalList(Map<ATerm,Integer> occurrances){
        Collection<ATerm> finalList = occurrances.keySet();
        for (ATerm term:finalList) {
            term.setOccurrences(occurrances.get(term));
        }
        return finalList;
    }
}
