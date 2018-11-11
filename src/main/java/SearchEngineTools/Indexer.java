package SearchEngineTools;

import SearchEngineTools.Term.ATerm;
import SearchEngineTools.Term.WordTerm;

import java.io.*;
import java.util.*;

public class Indexer {
    private Map<String, Integer> dictionary;
    private HashSet termsHash;
    List<PostingList> tempInvertedIndex;
    private int memoryBlockSize;
    private int usedMemory;
    private boolean write = true;


    public Indexer() {
        dictionary = new LinkedHashMap<>();
        tempInvertedIndex = new ArrayList<>();
        termsHash = new HashSet();
    }

    public Indexer(Map<String, Integer> dictionary) {
        this.dictionary = dictionary;
        tempInvertedIndex = new ArrayList<>();
        termsHash = new HashSet();

    }

    public Indexer(int memoryBlockSize) {
        this.memoryBlockSize = memoryBlockSize;
        dictionary = new LinkedHashMap<>();
        tempInvertedIndex = new ArrayList<>();
        termsHash = new HashSet();
    }

    /**
     * Creates the dictionary and posting files.
     *
     * @param terms - list of the document terms(after parse).
     * @param docID
     */
    public void createInvertedIndex(Iterator<ATerm> terms, int docID) {
        int postingIndex = 0;
        while (terms.hasNext()) {
            ATerm aTerm = terms.next();
            if (aTerm instanceof WordTerm)
                handleCapitalWord(aTerm);
            String term = aTerm.getTerm();
            PostingList postingsList;
            PostingEntry postingEntry = new PostingEntry(docID, aTerm.getOccurrences());
            if (!dictionary.containsKey(term)) {
                dictionary.put(term, postingIndex);

                //size of dictionary
//                    String postingIndexS=""+postingIndex;
//                    usedMemory+=term.length()+postingIndexS.length();

                postingsList = new PostingList(term);
                postingsList.add(postingEntry);
                System.out.println(postingIndex);
                tempInvertedIndex.add(postingIndex, postingsList);
                postingIndex++;
            } else {
                postingsList = tempInvertedIndex.get(dictionary.get(term));
            }
            usedMemory += postingEntry.getSizeInBytes();
        }
        //check if we need to write to disk.
        /*
        if (!(usedMemory < memoryBlockSize)) {
            sortInvertedIndex();
            writeInvertedIndexToDisk();
            //init dictionary and posting lists.
            tempInvertedIndex.clear();
            dictionary.clear();
            usedMemory = 0;
            postingIndex = 0;
        }
        */
        writeInvertedIndexToDisk();

        System.out.println("finish: " + docID);
    }

    private void sortInvertedIndex() {

    }

    private void writeInvertedIndexToDisk() {
        System.out.println(usedMemory);
        try (FileWriter fw = new FileWriter("Documents1.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            /*
            for (int i = 0; i <tempInvertedIndex.size() ; i++) {
                List<PostingEntry> PostingList=tempInvertedIndex.get(i);
                for (int j = 0; j < PostingList.size(); j++) {
                    PostingEntry postingEntry=PostingList.get(j);
                    out.print(postingEntry);
                    PostingList.remove(j);
                }
                out.println();
                tempInvertedIndex.remove(i);
            }
            */
            for (PostingList postingList : tempInvertedIndex) {
//                out.print(postingList.getTerm()+" ");
                boolean first=true;
                for (PostingEntry postingEntry : postingList.toPostingList()) {
                    if(first){
                        out.print(postingEntry);
                    }
                    out.print(" "+postingEntry);
                }
                out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void handleCapitalWord(ATerm aTerm) {
        String term = aTerm.getTerm();
        String termLowerCase = term.toLowerCase();
        String termUpperCase = term.toUpperCase();
        //System.out.println(term);
        if (term.equals(""))
            return;
        //term is upper case.
        if (Character.isUpperCase(term.charAt(0))) {
            if (dictionary.containsKey(termLowerCase)) {
                ((WordTerm) aTerm).toLowerCase();
            }
        }
        //term is lower case.
        else {
            if (dictionary.containsKey(termUpperCase)) {
                //change termUpperCase in dictionary to termLowerCase
                int postingIndex = dictionary.remove(termUpperCase);
                dictionary.put(termLowerCase, postingIndex);
            }
        }


    }
}
