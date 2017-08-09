import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by igor on 09.08.17.
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
        Map<String, Integer> domainMap = countDomainOccurences(links);

        links.stream().forEach(System.out::println);
        domainMap.entrySet().forEach(System.out::println);
    }

    private static List<List<String>> processURLs(Elements linkElements) {
        return linkElements
                .stream()
                .map(elem -> elem.attr("abs:href"))
                .map(link -> Arrays.asList(link.split("/|#", 4))
                        .stream()
                        .skip(2)
                        .collect(Collectors.toList())
                )
                .filter(((Predicate<List<String>>) List::isEmpty).negate())
                .collect(Collectors.toList());
}

    private static Map<String, Integer> countDomainOccurences(List<List<String>> links) {
        Map<String, Integer> domainMap = new HashMap<>();
        for (List<String> el : links) {
            String domain = el.get(0);
            if (domainMap.containsKey(domain)) {
                domainMap.put(domain, domainMap.get(domain) + 1);
            } else {
                domainMap.put(domain, 1);
            }
        }
        return domainMap;
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}
