package never.say.never.testtest.excel.cache;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-22
 */
public interface HandyCache<K, V> extends AutoCloseable{

    String getName();

    void init();

    void put(K key, V value);

    V get(K key);
}
