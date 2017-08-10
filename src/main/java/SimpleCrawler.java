import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Igor Klemenski on 09.08.17.
 *
 * This class represents the solution to the following problem:
 * 'given a URL, count the number of '<a href>' links contained
 * in the corresponding HTML document
 *
 * Assumptions made:
 * 1) allow links to self
 * 2) ignore duplicates of the same URL (use Set<> to store them)
 */

public class SimpleCrawler {

    private static final String USAGE_INFO = "Usage: provide desired URL as a command line argument";
    private static final String OUTPUT_INFO = "Output format: 'domain name'='# of links pointing to it'\n";
    private static final String TERMINATION_MSG = "\nProgram finished.";
    private static final String FETCHING_MSG = "Fetching %s...\n\n";
    private static final String FETCH_ERROR_MSG = "Error trying to fetch the specified URL: ";

    private static final String LINK_TAG = "a[href]";
    private static final String ELEMENT_LINK_ATTRIBUTE= "abs:href";
    private static final String URL_SPLIT_TOKEN = "/";
    private static final String EMPTY_STRING = "";

    public static void runCrawler(String[] args) {
        if (!validateInput(args)) {
            System.err.println(USAGE_INFO);
            return;
        }
        String url = args[0];
        System.out.format(FETCHING_MSG, url);

        try {
            Elements linkElements = fetchResource(url, LINK_TAG);
            List<List<String>> links = processURLs(linkElements);
            Map<String, Integer> domainMap = countDomainOccurences(links);

            System.out.println(OUTPUT_INFO);
            domainMap.entrySet().forEach(System.out::println);
        } catch (IOException e) {
            System.err.println(FETCH_ERROR_MSG + e.getMessage());
        } finally {
            System.out.println(TERMINATION_MSG);
        }
    }

    /**
     * This method takes a list of URLs and returns a collection
     * of string pairs ready for further processing
     *
     * It does so by splitting the string by '/' or '#' characters
     * ('#' is used to account for cases of linking to self)
     * and then discarding the 'http://' portion of the domain
     *
     * @param linkElements list of objects representing URLs obtained from parsing the HTML document
     * @return list of lists, each containing a pair: domain-resource
     */

    private static List<List<String>> processURLs(Elements linkElements) {
        return linkElements
                .stream()
                .map(elem -> elem.attr(ELEMENT_LINK_ATTRIBUTE))
                .map(link -> Arrays.asList(link.split(URL_SPLIT_TOKEN, 4))
                        .stream()
                        .skip(2)
                        .collect(Collectors.toList())
                )
                .filter(((Predicate<List<String>>) List::isEmpty).negate())
                .map(list -> {
                    if (list.size() == 1) {
                        list.add(EMPTY_STRING);
                    }
                    return list;
                })
                .collect(Collectors.toList());
    }

    /**
     * This method takes the list of domain-resource pairs and counts
     * the no. of occurences of each domain name
     *
     * @param links list of lists, each containing a pair: domain-resource
     * @return map of domain names and the corresponding numbers of outgoing links
     */

    private static Map<String, Integer> countDomainOccurences(List<List<String>> links) {
        Map<String, Set<String>> domainMap = new HashMap<>();
        for (List<String> l : links) {
            String domain = l.get(0);
            String resource = l.get(1);
            if (domainMap.containsKey(domain)) {
                domainMap.get(domain).add(resource);
            } else {
                domainMap.put(domain, new HashSet<>(Arrays.asList(resource)));
            }
        }
        return domainMap.entrySet()
                .stream()
                .collect(Collectors
                        .toMap(Map.Entry::getKey, e -> e.getValue().size())
                );
    }

    private static Elements fetchResource(String url, String htmlTag) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select(htmlTag);
    }

    private static boolean validateInput(String[] args) {
        return (args.length == 1) ? true : false;
    }
}
