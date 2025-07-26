package never.say.never.testtest.excel.cache;

import lombok.extern.slf4j.Slf4j;
import never.say.never.test.util.FileToolkit;
import never.say.never.testtest.excel.eventmode.read.strings.StringList;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.ShutdownPolicy;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.processors.cache.persistence.wal.reader.StandaloneNoopCommunicationSpi;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-22
 */
@Slf4j
public class HandyCacheByIgnite extends AbstractHandyCache<Integer, StringList> {

    private static final Ignite ignite;

    private org.apache.ignite.IgniteCache<Integer, StringList> igniteCache;

    static {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String instanceName = "ExcelReadingCache_" + runtime.getPid();
        Path diskStoreDirPath = FileToolkit.TMP_DIR.resolve(instanceName);
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
        // 本地模式
        IgniteConfiguration rootCfg = new IgniteConfiguration();
        rootCfg.setIgniteInstanceName(instanceName);
        rootCfg.setCommunicationSpi(new StandaloneNoopCommunicationSpi());
        rootCfg.setWorkDirectory(diskStoreDir.getAbsolutePath());
        rootCfg.setShutdownPolicy(ShutdownPolicy.IMMEDIATE);
        // 存储配置
        DataStorageConfiguration storageCfg = new DataStorageConfiguration()
                .setDefaultDataRegionConfiguration(new DataRegionConfiguration()
                        .setInitialSize(10L * 1024L * 1024) // 初始化内存大小为10M
                        .setMaxSize(80L * 1024L * 1024) // 使用内存限制为80M
                        .setPersistenceEnabled(true) // 开启持久化
                        .setCheckpointPageBufferSize(10L * 1024L * 1024) // 检查点页面缓冲区大小为2M
                        .setEvictionThreshold(0.9) // 执行缓存淘汰的阈值
                );
        rootCfg.setDataStorageConfiguration(storageCfg);
        //
        ignite = Ignition.start(rootCfg);
        ignite.active(true);
        Runtime.getRuntime().addShutdownHook(new Thread("IgniteShardStrings-ShutdownHook") {
            @Override
            public void run() {
                try {
                    ignite.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    Ignition.kill(true);
                }
            }
        });
    }

    public HandyCacheByIgnite(String name) {
        super(name);
    }

    public synchronized org.apache.ignite.IgniteCache<Integer, StringList> getCache() {
        if (igniteCache == null) {
            igniteCache = ignite.getOrCreateCache(getName());
        }
        return igniteCache;
    }

    @Override
    public void init() {
        CacheConfiguration<Integer, StringList> cacheCfg = new CacheConfiguration<>();
        cacheCfg.setName(getName());
        cacheCfg.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES));
        ignite.createCache(cacheCfg);
    }

    @Override
    public void put(Integer key, StringList value) {
        getCache().put(key, value);
    }

    @Override
    public StringList get(Integer key) {
        return getCache().get(key);
    }

    @Override
    public void close() throws Exception{
        try {
            if (igniteCache != null) {
                igniteCache.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        ignite.destroyCache(getName());
        ignite.close(); // TODO
    }
}
