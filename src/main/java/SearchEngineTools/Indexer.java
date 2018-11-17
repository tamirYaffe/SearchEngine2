package SearchEngineTools;

import SearchEngineTools.ParsingTools.Term.ATerm;
import SearchEngineTools.ParsingTools.Term.WordTerm;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Indexer {
    //dictionary that holds term->idf. used also for word check for big first word.
    private Map<String, Integer> dictionary;
    //dictionary and posting list in one hash
    private Map<String, PostingList> tempInvertedIndex;

    private int memoryBlockSize;
    private int usedMemory;
    private int blockNum;


    public Indexer() {
        dictionary = new LinkedHashMap<>();
        tempInvertedIndex = new LinkedHashMap<>();
    }

    public Indexer(Map<String, Integer> dictionary) {
        this.dictionary = dictionary;
        tempInvertedIndex = new LinkedHashMap<>();

    }

    public Indexer(int memoryBlockSize) {
        this.memoryBlockSize = memoryBlockSize;
        dictionary = new LinkedHashMap<>();
        tempInvertedIndex = new LinkedHashMap<>();
    }

    /**
     * Creates the dictionary and posting files.
     *
     * @param terms - list of the document terms(after parse).
     * @param docID
     */
    public void createInvertedIndex(Iterator<ATerm> terms, int docID) {
        System.out.println("started indexing");
        while (usedMemory < memoryBlockSize && terms.hasNext()) {
            ATerm aTerm = terms.next();
            if (aTerm instanceof WordTerm)
                handleCapitalWord(aTerm);
            String term = aTerm.getTerm();
            PostingList postingsList;
            PostingEntry postingEntry = new PostingEntry(docID, aTerm.getOccurrences());
            if (!tempInvertedIndex.containsKey(term)) {
                postingsList = new PostingList(term);
                //size of dictionary
                //                    String postingIndexS=""+postingIndex;
                //                    usedMemory+=term.length()+postingIndexS.length();

                tempInvertedIndex.put(term, postingsList);
                usedMemory += term.length() + 1;
            } else {
                postingsList = tempInvertedIndex.get(term);
            }
            usedMemory += postingsList.add(postingEntry);
            if (usedMemory > memoryBlockSize) {
                sortAndWriteInvertedIndexToDisk();
                //init dictionary and posting lists.
                tempInvertedIndex.clear();
                usedMemory = 0;
            }
        }
        System.out.println("finish: " + docID);
    }

    public void mergeBlocks() throws IOException {
        BufferedReader[] readers = new BufferedReader[blockNum];
        PriorityQueue<Pair<String, Integer>> queue = new PriorityQueue<>(Comparator.comparing(obj -> obj.getKey().split(" ")[0]));
        FileWriter fw = new FileWriter("postingLists.txt",true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        String curPostingList=null;
        String nextPostingList;
        //create readers and init queue.
        for (int i = 0; i < blockNum; i++) {
            String fileName = "block" + i + ".txt";
            readers[i] = new BufferedReader(new FileReader(fileName));
            queue.add(new Pair<>(readers[i].readLine(), i));
        }

        while (!queue.isEmpty()) {
            curPostingList=getNextPostingList(queue,readers);
            curPostingList=checkForMergeingPostingLines(queue,readers,curPostingList);
            out.println(curPostingList);
        }
        out.close();
    }

    /**
     * remove top of queue,and add the next line from the removed line block.
     * @param queue
     * @param readers
     * @return
     * @throws IOException
     */
    private String getNextPostingList(PriorityQueue<Pair<String, Integer>> queue, BufferedReader[] readers) throws IOException {
        Pair<String,Integer> postingListPair=queue.poll();
        String postingList=postingListPair.getKey();
        int blockIndex=postingListPair.getValue();

        String nextPostingList=readers[blockIndex].readLine();
        if(nextPostingList!=null)
            queue.add(new Pair<>(nextPostingList,blockIndex));
        return postingList;
    }

    private String checkForMergeingPostingLines(PriorityQueue<Pair<String, Integer>> queue, BufferedReader[] readers, String curPostingList) throws IOException {
        if(queue.isEmpty())
            return curPostingList;
        String nextPostingList=queue.peek().getKey();
        while (curPostingList.split(" ")[0].equals(nextPostingList.split(" ")[0])){
            curPostingList=mergePostingLists(curPostingList,nextPostingList);
            getNextPostingList(queue,readers);
            if(queue.isEmpty())
                break;
            nextPostingList=queue.peek().getKey();
        }
        return curPostingList;
    }

    private String mergePostingLists(String postingList1, String postingList2) {
        String[] splitPostingList1=postingList1.split(" ");
        String[] splitPostingList2=postingList2.split(" ");
        //compare last docId in the lists.
        if(Integer.parseInt(splitPostingList1[splitPostingList1.length-2])<Integer.parseInt(splitPostingList2[splitPostingList2.length-2])) {
            for (int i = 1; i < splitPostingList2.length; i++)
                postingList1 += " " + splitPostingList2[i];
            return postingList1;
        }
        else {
            for (int i = 1; i < splitPostingList1.length; i++)
                postingList2 += " " + splitPostingList1[i];
            return postingList2;
        }
    }

    public void sortAndWriteInvertedIndexToDisk() {
        System.out.println("writing to disk: " + usedMemory);
        String fileName = "block" + blockNum + ".txt";
        blockNum++;
        try (FileWriter fw = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            List<String> keys = new ArrayList<>(tempInvertedIndex.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                out.print(key);
                for (PostingEntry postingEntry : tempInvertedIndex.get(key).toList())
                    out.print(" " + postingEntry);
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
                int termIdf = dictionary.remove(termUpperCase);
                dictionary.put(termLowerCase, termIdf);
            }
        }
        //add or update dictionary.
        if (!dictionary.containsKey(term))
            dictionary.put(term, 1);
        else
            dictionary.replace(term, dictionary.get(term) + 1);


    }
}
