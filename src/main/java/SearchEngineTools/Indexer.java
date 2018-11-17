package SearchEngineTools;

import SearchEngineTools.ParsingTools.Term.ATerm;
import SearchEngineTools.ParsingTools.Term.WordTerm;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Indexer {
    //dictionary that holds term->idf. used also for word check for big first word.
    private Map<String, Pair<Integer,Integer>> dictionary;
    //dictionary and posting list in one hash
    private Map<String, List<PostingEntry>> tempInvertedIndex;

    private int memoryBlockSize;
    private int usedMemory;
    private int blockNum;


    public Indexer() {
        dictionary = new LinkedHashMap<>();
        tempInvertedIndex = new LinkedHashMap<>();
    }

    public Indexer(Map<String, Pair<Integer,Integer>> dictionary) {
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
        System.out.println("started indexing: "+docID);
        Document document=new Document(docID);
        while (terms.hasNext()) {
            ATerm aTerm = terms.next();
            if (aTerm instanceof WordTerm)
                handleCapitalWord(aTerm);
            String term = aTerm.getTerm();
            int termOccurrences=aTerm.getOccurrences();
            document.updateDocInfo(termOccurrences);
            //add or update dictionary.
            if (!dictionary.containsKey(term))
                dictionary.put(term, new Pair<>(1,-1));
            else
                dictionary.replace(term,new Pair<>(dictionary.get(term).getKey()+1,-1));
            List<PostingEntry> postingsList;
            PostingEntry postingEntry = new PostingEntry(docID, termOccurrences);
            if (!tempInvertedIndex.containsKey(term)) {
                postingsList = new ArrayList<>();
                tempInvertedIndex.put(term, postingsList);
                usedMemory += term.length() + 1;
            } else {
                postingsList = tempInvertedIndex.get(term);
            }
            postingsList.add(postingEntry);
            usedMemory+=postingEntry.getSizeInBytes();
            if (usedMemory > memoryBlockSize) {
                sortAndWriteInvertedIndexToDisk();
                //init dictionary and posting lists.
                tempInvertedIndex.clear();
                usedMemory = 0;
            }
        }
        document.writeDocInfoToDisk();
        System.out.println("finish: " + docID);
    }

    public void mergeBlocks() throws IOException {
        int postingListIndex=0;
        BufferedReader[] readers = new BufferedReader[blockNum];
        PostingListComparator comparator=new PostingListComparator();
        PriorityQueue<Pair<String, Integer>> queue = new PriorityQueue<>(comparator);
        FileWriter fw = new FileWriter("postingLists.txt",true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        String curPostingList=null;
        String nextPostingList;
        //create readers and init queue.
        for (int i = 0; i < blockNum; i++) {
            String fileName = "blocks/block" + i + ".txt";
            readers[i] = new BufferedReader(new FileReader(fileName));
            queue.add(new Pair<>(readers[i].readLine(), i));
        }

        while (!queue.isEmpty()) {
            curPostingList=getNextPostingList(queue,readers);
            curPostingList=checkForMergeingPostingLines(queue,readers,curPostingList);
            String term=curPostingList.split(" ")[0];
            dictionary.replace(term,new Pair<>(dictionary.get(term).getKey(),postingListIndex++));
//            out.println(curPostingList.substring(curPostingList.indexOf(" ")+1));
            out.println(curPostingList);
        }
        out.close();
        //writeDictionaryToDisk();
        sortAndWriteDictionaryToDisk();
    }

    private void writeDictionaryToDisk() {
        try (FileWriter fw = new FileWriter("dictionary.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for(Map.Entry entry: dictionary.entrySet())
                out.println(entry.getKey()+" "+((Pair<Integer,Integer>)entry.getValue()).getKey()+" "+((Pair<Integer,Integer>)entry.getValue()).getValue());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sortAndWriteDictionaryToDisk() {
        try (FileWriter fw = new FileWriter("dictionary.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            List<String> keys = new ArrayList<>(dictionary.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                Pair<Integer,Integer>dictionaryPair=dictionary.get(key);
                out.println(key+" "+dictionaryPair.getKey()+" "+dictionaryPair.getValue());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * remove top of queue,and add the next line from the removed line block.
     * @param queue
     * @param readers
     * @return
     * @throws IOException
     */
    private String getNextPostingList(PriorityQueue<Pair<String, Integer>> queue, BufferedReader[] readers) throws IOException {
        String postingList;
        while (true) {
            Pair<String,Integer> postingListPair=queue.poll();
            postingList = postingListPair.getKey();
            int blockIndex=postingListPair.getValue();

            String nextPostingList=readers[blockIndex].readLine();
            if(nextPostingList!=null)
                queue.add(new Pair<>(nextPostingList,blockIndex));

            //handling words lower/upper case
            String[] splitPostingList=postingList.split(" ");
            if(Character.isUpperCase(splitPostingList[0].charAt(0)) && dictionary.containsKey(splitPostingList[0].toLowerCase())){
                //change posting list
                String updatedPostingList=splitPostingList[0].toLowerCase()+postingList.substring(postingList.indexOf(" "));
                // add to queue
                queue.add(new Pair<>(updatedPostingList,blockIndex));
            }
            else
                break;
        }
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
            postingList1+=postingList2.substring(postingList2.indexOf(" "));
            return postingList1;
        }
        else {
            postingList2+=postingList1.substring(postingList1.indexOf(" "));
            return postingList2;
        }
    }

    public void sortAndWriteInvertedIndexToDisk() {
        System.out.println("writing to disk: " + usedMemory+" bytes");
        String fileName = "blocks/block" + blockNum + ".txt";
        blockNum++;
        try (FileWriter fw = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            List<String> keys = new ArrayList<>(tempInvertedIndex.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                out.print(key);
                for (PostingEntry postingEntry : tempInvertedIndex.get(key))
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
                Pair<Integer,Integer> dictionaryPair = dictionary.remove(termUpperCase);
                dictionary.put(termLowerCase, dictionaryPair);
            }
        }
    }

    public void loadDictionaryFromDisk(String path){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while (( line=reader.readLine())!=null){
                dictionary.put(line.split(" ")[0],new Pair<>(Integer.valueOf(line.split(" ")[1]),Integer.valueOf(line.split(" ")[2])) );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
