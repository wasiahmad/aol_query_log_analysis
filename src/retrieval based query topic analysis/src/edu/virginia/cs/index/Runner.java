package edu.virginia.cs.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Runner {

    private final static String INDEX_PATH = "lucene-DMOZ-index";

    public static void main(String[] args) throws IOException {
        //Interactive searching function with your selected ranker
        //NOTE: you have to create the index before searching!
        String method = "--ok";//specify the ranker you want to test
        interactiveSearch(method);
    }

    /**
     * Feel free to modify this function, if you want different display!
     *
     * @throws IOException
     */
    private static void interactiveSearch(String method) throws IOException {
        Searcher searcher = new Searcher(INDEX_PATH);
        setSimilarity(searcher, method);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Type text to search, blank to quit.");
        System.out.print("> ");
        String input;
        while ((input = br.readLine()) != null && !input.equals("")) {
            SearchResult result = searcher.search(input);
            ArrayList<ResultDoc> results = result.getDocs();
            int rank = 1;
            if (results.isEmpty()) {
                System.out.println("No results found!");
            }
            for (ResultDoc rdoc : results) {
                System.out.println("\n------------------------------------------------------");
                System.out.println(rank + ". " + rdoc.url());
                System.out.println("topic: " + rdoc.topic());
                System.out.println("------------------------------------------------------");
                System.out.println(result.getSnippet(rdoc)
                        .replaceAll("\n", " "));
                ++rank;
            }
            System.out.print("> ");
        }
    }

    private static void setSimilarity(Searcher searcher, String method) {
        if (method == null) {
            return;
        } else if (method.equals("--ok")) {
            searcher.setSimilarity(new OkapiBM25());
        } else {
            System.out.println("[Error]Unknown retrieval function specified!");
            printUsage();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("To specify a ranking function, make your last argument one of the following:");
        System.out.println("\t--ok\tOkapi BM25");
    }
}
