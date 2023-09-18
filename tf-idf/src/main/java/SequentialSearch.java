import model.DocumentData;
import search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SequentialSearch {
    public static final String BOOKS_DIRECTORY = "./resources/books";
    public static final String SEARCH_QUERY1 = "The best detective that catches many criminals using his deductive methods";
    public static final String SEARCH_QUERY2 = "The girl the falls through a rabbit hole into a fantasy wonderland";
    public static final String SEARCH_QUERY3 = "A war between Russia and France in the cold winter";

    public static void main(String[] args) throws FileNotFoundException {
        File documentDirectory = new File(BOOKS_DIRECTORY);

        List<String> documents = Arrays.asList(documentDirectory.list())
                .stream()
                .map(documentName -> BOOKS_DIRECTORY + "/" + documentName)
                .collect(Collectors.toList());

        List<String> terms = TFIDF.getWordsFromLine(SEARCH_QUERY1);

        findMostRelevantDocuments(documents, terms);
    }
    
    private static void findMostRelevantDocuments(List<String> documents, List<String> terms) throws FileNotFoundException {
        Map<String, DocumentData> documentsResults = new HashMap<>();

        for (String document : documents) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = TFIDF.getWordsFromLine(lines);
            DocumentData documentData = TFIDF.createDocumentData(words, terms);
            documentsResults.put(document, documentData);
        }

        Map<Double, List<String>> documentsByScore = TFIDF.getDocumentsSortedByScore(terms, documentsResults);
        printResult(documentsByScore);
    }

    private static void printResult(Map<Double, List<String>> documentsByScore) {
        for (Map.Entry<Double, List<String>> docScorePair : documentsByScore.entrySet()) {
            Double score = docScorePair.getKey();
            for (String document : docScorePair.getValue()) {
                System.out.println(String.format("Book : %s - score : %f", document.split("/")[3], score));
            }
        }
    }
}
