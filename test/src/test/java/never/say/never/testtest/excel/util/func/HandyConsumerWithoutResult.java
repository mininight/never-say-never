package never.say.never.testtest.excel.util.func;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
@FunctionalInterface
public interface HandyConsumerWithoutResult<T> extends HandyConsumer<T> {

    void accept(T t);

    @Override
    default boolean goon(T t) {
        accept(t);
        return true;
    }
}
