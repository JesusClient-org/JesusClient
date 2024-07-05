package cum.jesus.jesusclient.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtils {
    public static <T, R> List<T> distinctBy(Stream<T> stream, Function<? super T, ? extends R> keyExtractor) {
        Set<R> seen = new HashSet<>();
        return stream
                .filter(element -> seen.add(keyExtractor.apply(element)))
                .collect(Collectors.toList());
    }
}
