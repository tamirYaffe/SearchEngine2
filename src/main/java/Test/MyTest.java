package Test;

import SearchEngineTools.Document;
import SearchEngineTools.Indexer;
import SearchEngineTools.Parse;
import SearchEngineTools.ReadFile;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MyTest {
    public static void main(String[] args){
        ReadFile readFile=new ReadFile();
        Parse parse=new Parse();
        Indexer indexer=new Indexer();
//        int numOfDocs=readFile.listAllFiles("/Users/tamiryaffe/Downloads/corpus/FB396001/FB396001");
        int numOfDocs=readFile.listAllFiles("/Users/tamiryaffe/Desktop/corpus");

        /*
        List<Pair<List<String>,Integer>> terms=new ArrayList<>();
        for (int i = 1; i <= numOfDocs; i++) {
            List<String> docTerms=parse.parseDocument(readFile.extractFileText(new Document(i).getDocumentsLines()));
            terms.add(new Pair<>(docTerms,i));
        }
        */
        List<Pair<List<String>,Integer>> terms=parseAllDocuments(parse,readFile,numOfDocs);
        indexer.indexAllDocuments(terms.iterator());
        readFile.deleteAllDocuments();
    }

    private static List<Pair<List<String>, Integer>> parseAllDocuments(Parse parse, ReadFile readFile, int numOfDocs) {
        List<Pair<List<String>,Integer>> syncTermsList = Collections.synchronizedList(new ArrayList<>());
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        class ParseTask implements Runnable {
            private int docID;

            public ParseTask(int docID)
            {
                this.docID = docID;
            }
            @Override
            public void run()
            {
                System.out.println("task "+docID+" started");
               // List<String> docTerms=parse.parseDocument(readFile.extractFileText(new Document(docID).getDocumentsLines()));
               // syncTermsList.add(new Pair<>(docTerms,docID));
                System.out.println("task "+docID+" finished");
            }
        }

        for (int i = 1; i <= numOfDocs; i++) {
            ParseTask parseTask=new ParseTask(i);
            executor.execute(parseTask);
        }
        executor.shutdown();
        while (!executor.isTerminated());
        return syncTermsList;
    }
}
