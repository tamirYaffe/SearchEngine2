package SearchEngineTools;

import javafx.util.Pair;

import java.util.Comparator;

public class PostingListComparator implements Comparator<Pair<String, Integer>> {
    @Override
    public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        int compareResult=o1.getKey().split(" ")[0].compareTo(o2.getKey().split(" ")[0]);
        if(compareResult==0){
            int  docID1= Integer.parseInt((o1.getKey().split(" ")[o1.getKey().split(" ").length-2]));
            int docID2= Integer.parseInt((o2.getKey().split(" ")[o2.getKey().split(" ").length-2]));
            return docID1 - docID2;
        }
         else
             return compareResult;
    }
}
