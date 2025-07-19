package re.sylfa.itemcreator.util;

import java.util.Arrays;
import java.util.stream.Stream;

public interface JavaUtils {
    static <T> Stream<T> arrayStream(T[] array) {
        return array != null
            ? Arrays.stream(array)
            : Stream.empty();
    }
}
