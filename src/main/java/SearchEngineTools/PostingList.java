package SearchEngineTools;

import java.util.*;

public class PostingList {
    private Map<Integer,Integer> postingListMap;
    private String term;

    public PostingList(String term) {
        this.postingListMap = new LinkedHashMap<>();
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public List<PostingEntry> toList() {
        List<PostingEntry> postingList= new ArrayList<>();
        for(int docId:postingListMap.keySet()){
            postingList.add(new PostingEntry(docId,postingListMap.get(docId)));
        }
        return postingList;
    }

    /**
     *
     * @param postingEntry
     * @return added size in bytes
     */
    public int add(PostingEntry postingEntry) {
        if(!postingListMap.containsKey(postingEntry.getDocID())){
            postingListMap.put(postingEntry.getDocID(),postingEntry.getTermTF());
            return postingEntry.getSizeInBytes();
        }
        else{
            String oldSize=""+postingListMap.get(postingEntry.getDocID());
            addToOccurrences(postingEntry.getDocID(),postingEntry.getTermTF());
            String newSize=""+postingListMap.get(postingEntry.getDocID());
            int addedSize=newSize.length()-oldSize.length();
            return addedSize;
        }
    }


    private void addToOccurrences(int docID, int occurrencesToAdd) {
        postingListMap.replace(docID,postingListMap.get(docID)+occurrencesToAdd);
    }
}
