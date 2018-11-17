package SearchEngineTools;

import SearchEngineTools.Indexer;
import SearchEngineTools.ParsingTools.Parse;
import SearchEngineTools.ParsingTools.Term.ATerm;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class ReadFile {
    private static int numOfDocs;
    private Parse parse;
    private Indexer indexer;


    public ReadFile() {
        parse = new Parse();
        indexer = new Indexer(4000);
    }

    public int listAllFiles(String path) {
        Document.corpusPath = path;
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        divideFileToDocs(readContent(filePath), filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //write remaining posting lists to disk
        indexer.sortAndWriteInvertedIndexToDisk();
        try {
            indexer.mergeBlocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numOfDocs;
    }

    private List<String> readContent(Path filePath) throws IOException {
        /*
        List<String> fileList=null;
        try {
            fileList = Files.readAllLines(filePath, Charset.forName("UTF-8"));

        } catch (IOException e) {
            //for foreign file coding.
            fileList = Files.readAllLines(filePath, Charset.forName("ISO-8859-1"));
        }
        return fileList;
        */
        BufferedReader br = null;
        FileReader fr = null;
        List<String> fileList = new ArrayList<>();
        String line = null;
        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(filePath.toString());
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                fileList.add(line);
            }
        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return fileList;
    }

    private void divideFileToDocs(List<String> fileList, Path filePath) {

        List<String> docLines = new ArrayList<>();
        String docName;
        int startLineNumInt = 0;
        int endLineNumInt = 0;
        int numOfLinesInt = 0;
        int s = 0;
        for (String line : fileList) {
            docLines.add(line);
            endLineNumInt++;
            numOfLinesInt++;
            if (line.contains("<DOCNO>"))
                docName = extractDocID(line);
            if (line.equals("</DOC>")) {
                createDoc(filePath, startLineNumInt, numOfLinesInt, numOfDocs);
                processdocument(docLines, numOfDocs);
                //extractDocCity(docLines);
                startLineNumInt = endLineNumInt + 1;
                numOfLinesInt = 0;
                docLines.clear();
                numOfDocs++;
                System.out.println("num of docs: " + numOfDocs);
            }
        }

    }


    private String extractDocID(String line) {
        String ans = line.substring(7, line.length() - 8);
        return ans;
    }

    private void processdocument(List<String> doc, int docID) {
        Collection<ATerm> terms = parse.parseDocument(extractDocText(doc));
        indexer.createInvertedIndex(terms.iterator(), docID);
    }

    private void createDoc(Path filePath, int startLineNum, int numOfLines, int docID) {
        try (FileWriter fw = new FileWriter("Documents.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String fileName = extractFileName(filePath.toString());
            out.println(fileName + " " + startLineNum + " " + numOfLines);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String extractFileName(String path) {
//      return path.split("corpus")[1];
        String[] splitPath;
        String fileName;
        if (path.contains("\\")) {
            splitPath = path.split("\\\\");
            fileName = "\\" + splitPath[splitPath.length - 1] + "\\" + splitPath[splitPath.length - 2];
        } else {
            splitPath = path.split("/");
            fileName = "/" + splitPath[splitPath.length - 1] + "/" + splitPath[splitPath.length - 2];
        }
        return fileName;
    }

    public static void deletePrevFiles() {
        try {
            Files.delete(Paths.get("dictionary.txt"));
            Files.delete(Paths.get("postingLists.txt"));
            Files.delete(Paths.get("Documents.txt"));
            Files.delete(Paths.get("DocumentsInfo.txt"));
            File dir = new File("blocks");
            for (File file : dir.listFiles())
                if (!file.isDirectory())
                    file.delete();
        } catch (IOException e) {
            System.out.println("all files did not deleted");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    /////***this functions may be moved to the Parse class.***////////////////////////

    public List<String> readDocument(String path) {
        List<String> lineList = null;
        try {
            lineList = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));

        } catch (IOException e) {
            //for foemer file coding.
            try {
                lineList = Files.readAllLines(Paths.get(path), Charset.forName("ISO-8859-1"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return extractDocText(lineList);


    }

    public List<String> extractDocText(List<String> lineList) {
        List<String> fileText = new ArrayList<>();
        boolean isText = false;
        for (int i = 0; i < lineList.size(); i++) {
            String line = lineList.get(i);
            if (line.equals("<TEXT>")) {
                isText = true;
            }
            if (line.equals("</TEXT>"))
                isText = false;
            if (isText)
                fileText.add(line);
        }
        fileText.remove(0);
        return fileText;
    }

    public List<String> extractDocCity(List<String> lineList) {
        for (int i = 0; i < lineList.size(); i++) {
            String line = lineList.get(i);
            if (line.contains("<F P=104>")) {
                line=line.split(">")[1];
                if(!line.equals(""))
                    System.out.println(line.substring(2).split(" ")[0]+" "+line.substring(2).split(" ")[1]);
            }
        }
        return null;
    }



}
