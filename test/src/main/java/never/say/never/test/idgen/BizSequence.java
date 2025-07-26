package never.say.never.test.idgen;

import java.util.List;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-27
 */
public interface BizSequence {

    default void init() throws Throwable {
        // skip
    }

    default String next() {
        return next(true);
    }

    String next(boolean fixedLength);

    default List<String> nextList(int takeNum) {
        return nextList(takeNum, true);
    }

    List<String> nextList(int takeNum, boolean fixedLength);

    default void destroy() throws Throwable {
        // skip
    }
}
