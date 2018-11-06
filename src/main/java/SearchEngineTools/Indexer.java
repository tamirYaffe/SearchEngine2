package SearchEngineTools;

import javafx.util.Pair;

import java.util.*;

public class Indexer {
    Map<String, String> termDictionary;
    Map<String, List<Integer>> dictionary = new LinkedHashMap<>();


    public Indexer() {
        termDictionary= new HashMap<String, String>();
    }

    public Indexer(Map<String, String> dictionary) {
        this.termDictionary = dictionary;
    }

    public void indexAllDocuments(Iterator<Pair<List<String>, Integer>> termStream) {
        int initialMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
        int usedMemory = 0;

        while (usedMemory < 65000000 && termStream.hasNext()) {
            int currentMemory = (int) java.lang.Runtime.getRuntime().freeMemory();
            usedMemory = initialMemory - currentMemory;
            final long usedMem = java.lang.Runtime.getRuntime().freeMemory();
            System.out.println(""+usedMemory+" "+usedMem);
            Pair<List<String>,Integer> terms_docID=termStream.next();
            List<String> docTerms=terms_docID.getKey();
            List<Integer> postingsList;
            for(String term:docTerms){
                if (!dictionary.containsKey(term)){
                   dictionary.put(term,new ArrayList<>());
                }
                postingsList=dictionary.get(term);
                if(!postingsList.contains(terms_docID.getValue()))
                    postingsList.add(terms_docID.getValue());
            }
        }
        if(usedMemory<65000000)
            System.out.println("finish");
    }

    /**
     * Creates the dictionary and posting files.
     * @param terms - list of the document terms(after parse).
     * @param docID
     */
    public void createInvertedIndex(List<String> terms, int docID){
        for (int i = 0; i < terms.size(); i++) {
            int termTF=computeTF(terms,terms.get(i));
            if(termDictionary.containsKey(docID)){

            }
            else{

            }
            //incrementDF
        }
    }

    private int computeTF(List<String> terms, String term) {
        int termCount=0;
        for (int i = 0; i < terms.size(); i++) {
            if(terms.get(i).equals(term))
                termCount++;
        }
        return termCount;
    }

}
