package never.say.never.testtest.excel.cache;

import lombok.extern.slf4j.Slf4j;
import never.say.never.test.util.FileToolkit;
import never.say.never.testtest.excel.eventmode.read.strings.StringList;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-22
 */
@Slf4j
public class HandyCacheByEhcache extends AbstractHandyCache<Integer, StringList> {

    private static final CacheManager cacheManager;

    static {
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        Path diskStoreDirPath = FileToolkit.TMP_DIR.resolve("ExcelReadingCache@JVM_" + mxBean.getPid());
        try {
            Files.deleteIfExists(diskStoreDirPath);
        } catch (IOException e) {
            // skip
        }
        File diskStoreDir = diskStoreDirPath.toFile();
        diskStoreDir.deleteOnExit();
        try {
            Files.createDirectories(diskStoreDirPath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(new CacheManagerPersistenceConfiguration(diskStoreDir))
                .build(true);
        Runtime.getRuntime().addShutdownHook(new Thread("Ehcache-ShutdownHook") {
            @Override
            public void run() {
                try {
                    cacheManager.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    Files.deleteIfExists(diskStoreDirPath);
                } catch (IOException e) {
                    // skip
                }
            }
        });
    }

    public HandyCacheByEhcache(String name) {
        super(name);
    }

    protected CacheManager getCacheManager() {
        return cacheManager;
    }

    protected Cache<Integer, StringList> getEhCache() {
        Cache<Integer, StringList> cache = getCacheManager().getCache(getName(), Integer.class, StringList.class);
        return Objects.requireNonNull(cache, "shard strings cached failure");
    }

    @Override
    public void init() {
        getCacheManager().createCache(getName(), CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Integer.class, StringList.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(1, MemoryUnit.MB)
                        .disk(32, MemoryUnit.GB, true)
                        .build())
                .build());
    }

    @Override
    public void put(Integer key, StringList value) {
        getEhCache().put(key, value);
    }

    @Override
    public StringList get(Integer key) {
        return getEhCache().get(key);
    }

    @Override
    public void close() throws Exception {
        getEhCache().clear();
        getCacheManager().removeCache(getName());
    }

}
