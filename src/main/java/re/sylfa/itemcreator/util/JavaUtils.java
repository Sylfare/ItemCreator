package re.sylfa.itemcreator.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface JavaUtils {
    static <T> Stream<T> arrayStream(T[] array) {
        return array != null
            ? Arrays.stream(array)
            : Stream.empty();
    }

    static <T> Stream<T> iteratorStream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }
}
