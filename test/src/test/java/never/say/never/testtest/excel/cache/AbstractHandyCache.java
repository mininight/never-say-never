package never.say.never.testtest.excel.cache;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-22
 */
public abstract class AbstractHandyCache<K, V> implements HandyCache<K, V> {

    private final String name;

    public AbstractHandyCache(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
