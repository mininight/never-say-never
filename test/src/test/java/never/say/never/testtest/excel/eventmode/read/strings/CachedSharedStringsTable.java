package never.say.never.testtest.excel.eventmode.read.strings;

import lombok.Getter;
import lombok.Setter;
import never.say.never.testtest.excel.cache.HandyCache;
import never.say.never.testtest.excel.cache.HandyCacheByHashMap;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.InputSource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-30
 */
public class CachedSharedStringsTable extends AbstractSharedStringsTable {

    private static final AtomicLong CACHE_NAME_SEQUENCE = new AtomicLong(0);

    private static final int DEFAULT_STRINGS_BATCH_CACHING_SIZE = 100;

    @Getter
    private final String uniqueCacheName = "SharedStrings_" + CACHE_NAME_SEQUENCE.incrementAndGet();

    @Getter
    @Setter
    private int stringsBatchCachingSize;

    @Getter
    @Setter
    private StringList strList;

    @Getter
    @Setter
    private HandyCache<Integer, StringList> cache;

    public CachedSharedStringsTable() {
        super();
    }

    public CachedSharedStringsTable(boolean includePhoneticRuns) {
        super(includePhoneticRuns);
    }

    @Override
    public RichTextString getItemAt(int idx) {
        int key = idx / stringsBatchCachingSize;
        StringList stringList = Objects.requireNonNull(getCache().get(key),
                String.format("shard strings for key '%s' cached failure", key));
        String txt = stringList.isEmpty() ? "" : stringList.get(idx % stringsBatchCachingSize);
        return new XSSFRichTextString(txt);
    }

    @Override
    protected void beforeParsing(InputSource sheetSource) {
        super.beforeParsing(sheetSource);
        stringsBatchCachingSize = stringsBatchCachingSize < DEFAULT_STRINGS_BATCH_CACHING_SIZE ?
                DEFAULT_STRINGS_BATCH_CACHING_SIZE : stringsBatchCachingSize;
        if (strList == null) {
            strList = new StringList(stringsBatchCachingSize);
        }
        if (cache == null) {
            cache = new HandyCacheByHashMap(uniqueCacheName);
        }
    }

    @Override
    protected void onParsingStart() {
        super.onParsingStart();
        cache.init();
    }

    @Override
    void onTextEnd(int idx, StringBuilder characters) {
        String richTextValue = characters.toString();
        strList.add(richTextValue);
        if ((idx + 1) % stringsBatchCachingSize == 0) {
            int regionKey = idx / stringsBatchCachingSize;
            cache.put(regionKey, strList.copy());
            strList.reset();
        }
    }

    @Override
    void onParsingEnd(int idx) {
        if (!strList.isEmpty()) {
            int regionKey = idx / stringsBatchCachingSize;
            cache.put(regionKey, strList.copy());
            strList.reset();
        }
    }

    @Override
    public void close() throws Exception {
        cache.close();
    }
}
