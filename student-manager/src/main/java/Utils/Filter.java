package Utils;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class  Filter <E> {
    public static <E> List < E > filter(List < E > whatToFilter, Predicate < E > whatCriteria, Comparator < E > howToSort){
        return whatToFilter.stream().filter(whatCriteria).sorted(howToSort).collect(Collectors.toList());
    }
}
