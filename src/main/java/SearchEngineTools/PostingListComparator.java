package SearchEngineTools;

import javafx.util.Pair;

import java.util.Comparator;

public class PostingListComparator implements Comparator<Pair<String, Integer>> {
    @Override
    public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        if(o1.getKey().split(" ")[0].equals(o2.getKey().split(" ")[0]))
            return -1;
        return 0;
    }
}
