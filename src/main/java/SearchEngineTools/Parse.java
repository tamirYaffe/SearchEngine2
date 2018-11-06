package SearchEngineTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parse {
    public List<String> parseDocument(List<String> document){
        List<String> tokens=tokenize(document);
        //do text operations(remove unnecessary chars, change words by the assignment rules).
        removeUnnecessaryChars(tokens);
        //remove stop words.
        //allow stemming.
        return tokens;
    }

    /**
     * removes unnecessary chars from the token list, e.g '.' ','
     * @param tokens- the token list we remove chars from
     */
    private void removeUnnecessaryChars(List<String> tokens) {
        List<Character> unnecessaryChars=new ArrayList<>();
        unnecessaryChars.add('.');
        unnecessaryChars.add(',');
        tokens.replaceAll(token->{
            if(unnecessaryChars.contains(token.charAt(0)))
                token=token.substring(1);
            if(token.equals(""))
                return token;
            if(unnecessaryChars.contains(token.charAt(token.length()-1)))
                token=token.substring(0,token.length()-1);
            return token;
        });
    }

    /**
     * divided the input list of documents lines into list of words/tokens.
     * @param document- the documents lines.
     * @return the list of document words.
     */
    private List<String> tokenize(List<String> document) {
        List<String> tokens=new ArrayList<>();
        document.forEach(line -> {
            //String[]tokens = line.split(" |\\.|\\,");
            String[]lineTokens = line.split(" ");
            lineTokens=Arrays.stream(lineTokens)
                    .filter(s -> !s.equals(""))
                    .toArray(String[]::new);
            tokens.addAll(Arrays.asList(lineTokens));
        });
        return tokens;
    }

}
