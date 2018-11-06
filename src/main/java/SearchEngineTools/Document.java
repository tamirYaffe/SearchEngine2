package SearchEngineTools;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Document {
    //static vars
    public static String corpusPath;


    private String path;
    private Long startLine;
    private Long numOfLines;

    public Document(int docNum) {
        String[] line ;
        try (BufferedReader br = new BufferedReader(new FileReader("Documents.txt"))) {
            for (int i = 0; i < docNum-1; i++)
                br.readLine();
            /*
            line = br.readLine();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(line);
            path = (String) json.get("filePath");
            startLine= (Long) json.get("startLineNum");
            numOfLines=(Long) json.get("numOfLines");
            */
            line=br.readLine().split(" ");
            String fileName=line[0];
            startLine= Long.valueOf(line[1]);
            numOfLines= Long.valueOf(line[2]);
            path=corpusPath+fileName;
            getDocumentsLines();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getDocumentsLines() {
        List<String> fileList = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            for (int i = 1; i < startLine; i++) {
                reader.readLine();
            }
            for (int i = 0; i < numOfLines; i++) {
                fileList.add(reader.readLine());
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public void getDocumentsLinesAlt() {
        List<String> fileList=new ArrayList<>();
        try {
            RandomAccessFile file = new RandomAccessFile(path, "rw");
            file.seek(startLine);
            for (int i = 0; i <numOfLines ; i++) {
                fileList.add(file.readLine());
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileList);
    }
}
