package Test;

import SearchEngineTools.Indexer;
import SearchEngineTools.ParsingTools.Parse;
import SearchEngineTools.ReadFile;

public class MyTest {
    public static void main(String[] args){
        ReadFile readFile=new ReadFile();
        Parse parse=new Parse();
        Indexer indexer=new Indexer();
        int numOfDocs=readFile.listAllFiles("/Users/tamiryaffe/Downloads/corpus/FB396001/FB396001");
        readFile.listAllFiles("C:\\Users\\liadb\\Documents\\School\\DataRetrievel\\SearchEngine\\Corpus\\corpus\\FB396001");
        readFile.deleteAllDocuments();
    }

}