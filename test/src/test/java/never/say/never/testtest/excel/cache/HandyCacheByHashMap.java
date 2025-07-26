package never.say.never.testtest.excel.cache;

import never.say.never.testtest.excel.eventmode.read.strings.StringList;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-23
 */
public class HandyCacheByHashMap extends AbstractHandyCache<Integer, StringList> {

    private final Map<Integer, StringList> cache = new HashMap<>();

    public HandyCacheByHashMap(String name) {
        super(name);
    }

    @Override
    public void init() {

    }

    @Override
    public void put(Integer key, StringList value) {
        cache.put(key, value);
    }

    @Override
    public StringList get(Integer key) {
        return cache.get(key);
    }

    @Override
    public void close() throws Exception {
        cache.clear();
    }
}
