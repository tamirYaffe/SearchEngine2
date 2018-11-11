package SearchEngineTools;

import java.util.ArrayList;
import java.util.List;

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

    public PostingEntry get(int index){
        return postingList.get(index);
    }

    public List<PostingEntry> toPostingList() {
        return postingList;
    }

    public void add(PostingEntry postingEntry) {
        postingList.add(postingEntry);
    }
}
