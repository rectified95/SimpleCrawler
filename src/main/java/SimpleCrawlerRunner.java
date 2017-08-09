import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Igor Klemenski on 09.08.17.
 *
 * This class represent the solution to the following problem:
 * 'given a URL, count the number of '<a href> links contained
 * in the corresponding HTML document
 */
public class SimpleCrawlerRunner {

    public static void main(String[] args) throws IOException {
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];
        print("Fetching %s...", url);

        Document doc = Jsoup.connect(url).get();
        Elements linkElements = doc.select("a[href]");

        print("\nLinks: (%d)", linkElements.size());

        List<List<String>> links = processURLs(linkElements);
        links.stream().forEach(System.out::println);
        System.out.println(links.size());
        Map<String, Integer> domainMap = countDomainOccurences(links);


        domainMap.entrySet().forEach(System.out::println);
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
                .map(elem -> elem.attr("abs:href"))
                .map(link -> Arrays.asList(link.split("/|#", 4))
                        .stream()
                        .skip(2)
                        .collect(Collectors.toList())
                )
                .filter(list -> list.size() > 1)
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

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
