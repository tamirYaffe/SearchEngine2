package SearchEngineTools;

import java.util.*;

public class PostingList {
    private List<PostingEntry> postingList;
    private String term;

    public PostingList(String term) {
        this.postingList = new ArrayList<>();
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public List<PostingEntry> toList() {
        return postingList;
    }

    /**
     *
     * @param postingEntry
     */
    public void add(PostingEntry postingEntry) {
        postingList.add(postingEntry);
    }
}
