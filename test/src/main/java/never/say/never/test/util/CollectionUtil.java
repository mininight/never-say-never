package never.say.never.test.util;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-03
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static <C extends Collection<E>, E> C addAllIgnoreNil(C collection, E[] elements) {
        return addAllIgnoreNil(collection, elements, null);
    }

    public static <C extends Collection<E>, E> C addAllIgnoreNil(C collection, E[] elements, Function<E, E> func) {
        for (E el : elements) {
            if (func != null) {
                el = func.apply(el);
            }
            if (el == null) continue;
            collection.add(el);
        }
        return collection;
    }

    public static boolean startsWith(List<?> source, List<?> prefix) {
        if (isEmpty(source) || isEmpty(prefix)) {
            throw new RuntimeException("Parameters cannot be empty");
        }
        if (source.size() < prefix.size()) {
            return false;
        }
        boolean match = true;
        for (int i = 0; i < prefix.size(); i++) {
            if (!Objects.equals(prefix.get(i), source.get(i))) {
                match = false;
                break;
            }
        }
        return match;
    }

    public static boolean startsWithNoCareSource(List<?> left, List<?> right) {
        List<?> smallList;
        List<?> bigList;
        if (left.size() > right.size()) {
            bigList = left;
            smallList = right;
        } else {
            bigList = right;
            smallList = left;
        }
        boolean match = true;
        for (int i = 0; i < smallList.size(); i++) {
            if (!Objects.equals(smallList.get(i), bigList.get(i))) {
                match = false;
                break;
            }
        }
        return match;
    }
}
