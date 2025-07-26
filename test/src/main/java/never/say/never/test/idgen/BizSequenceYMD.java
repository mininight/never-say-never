package never.say.never.test.idgen;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p> 序列格式组成： <i>bizCode | yyyyMMdd | incrPart</i>
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-27
 */
@Getter
@Setter
public class BizSequenceYMD implements BizSequence {
    private final String bizCode;
    private final int incrPartLength;
    private DataSource dataSource;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private DataSourceTransactionManager txMng;
    private TransactionTemplate txOpt;
    private String seqTableName = "biz_seq";

    public BizSequenceYMD(String bizCode, int incrPartLength) {
        Preconditions.checkArgument(StringUtils.isNotBlank(bizCode), "业务码缺失");
        Preconditions.checkArgument(incrPartLength > 0, "未定义序列自增部分长度");
        this.bizCode = bizCode;
        this.incrPartLength = incrPartLength;
    }

    @Override
    public void init() throws Throwable {
        Preconditions.checkNotNull(dataSource, "数据连接池缺失");
        Preconditions.checkArgument(StringUtils.isNotBlank(seqTableName), "未定义序列表名");
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        txMng = new DataSourceTransactionManager(this.dataSource);
        txMng.afterPropertiesSet();
        txOpt = new TransactionTemplate(txMng);
        txOpt.setReadOnly(false);
        txOpt.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        txOpt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        txOpt.afterPropertiesSet();
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS `" + seqTableName + "` (\n" +
                "  `id` bigint NOT NULL AUTO_INCREMENT,\n" +
                "  `biz_code` varchar(255) COLLATE utf8mb4_bin NOT NULL,\n" +
                "  `val` bigint unsigned NOT NULL DEFAULT '0',\n" +
                "  `latest_timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `uk_code` (`biz_code`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;", Collections.emptyMap());
    }

    @Override
    public String next(boolean fixedLength) {
        return nextList(1, fixedLength).get(0);
    }

    @Override
    public List<String> nextList(int takeNum, boolean fixedLength) {
        return takeSequence(takeNum, fixedLength);
    }

    private synchronized List<String> takeSequence(int takeNum, boolean fixedLen) {
        Preconditions.checkArgument(takeNum > 0, "无效的序列数量: %s", takeNum);
        LatestSequence latestSequence = new LatestSequence();
        boolean ok;
        int retryTime = 0;
        do {
            latestSequence.apply(selectLatestSequence());
            ok = dispatchByLatest(latestSequence, fixedLen, takeNum);
            if (!ok) {
                retryTime++;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // skip
                }
            }
        } while (!ok && retryTime < 10);
        if (!ok) {
            ok = txOpt.execute(s -> {
                latestSequence.apply(selectLatestSequence(true, false, true));
                return dispatchByLatest(latestSequence, fixedLen, takeNum);
            });
        }
        Preconditions.checkArgument(ok, "未能获取到'%s'序列", bizCode);
        List<String> seqList = new ArrayList<>(takeNum);
        String timePart = FastDateFormat.getInstance("yyyyMMdd").format(latestSequence.latestTimestamp);
        String incrPart;
        StringBuilder seqBuilder = new StringBuilder();
        for (int i = 1; i <= takeNum; i++) {
            incrPart = (latestSequence.val + i) + "";
            if (incrPart.length() < incrPartLength) {
                incrPart = "0".repeat(incrPartLength - incrPart.length()) + incrPart;
            }
            seqList.add(seqBuilder.append(bizCode).append(timePart).append(incrPart).toString());
            seqBuilder.setLength(0);
        }
        return seqList;
    }


    private boolean dispatchByLatest(LatestSequence latestSequence, boolean fixedLen, int takeNum) {
        takeNum = takeNum <= 0 ? 1 : takeNum;
        long dataId = latestSequence.getId();
        long latestVal = latestSequence.getVal();
        long afterVal = latestVal + takeNum;
        String latestTimestamp = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(latestSequence.latestTimestamp);
        if (fixedLen && (afterVal + "").length() > incrPartLength) {
            throw new UnsupportedOperationException("派号达到上限, biz_code: " + bizCode);
        }
        return 1 == jdbcTemplate.update("UPDATE " + seqTableName + " SET val = " + afterVal + ", latest_timestamp = now() WHERE id = " + dataId + " AND val=" + latestVal + " AND latest_timestamp='" + latestTimestamp + "'", Collections.emptyMap());
    }


    private LatestSequence selectLatestSequence() {
        return selectLatestSequence(false, true, true);
    }

    private LatestSequence selectLatestSequence(boolean lockMode, boolean initIfNotExist, boolean syncTime) {
        List<LatestSequence> queryResult = jdbcTemplate.query(
                "SELECT * FROM " + seqTableName + " WHERE biz_code =:bizCode " + (lockMode ? "for update" : ""),
                ImmutableMap.of("bizCode", bizCode), new BeanPropertyRowMapper<>(LatestSequence.class));
        LatestSequence latestSequence = queryResult.isEmpty() ? null : queryResult.get(0);
        if (latestSequence == null && initIfNotExist) {
            if (!lockMode) {
                txOpt.executeWithoutResult(s -> {
                    jdbcTemplate.update("INSERT IGNORE INTO " + seqTableName + " (biz_code) VALUES (:bizCode)",
                            ImmutableMap.of("bizCode", bizCode));
                });
                latestSequence = this.selectLatestSequence();
            }
        }
        Preconditions.checkNotNull(latestSequence, "无法获取'%s'序列", bizCode);
        Preconditions.checkNotNull(latestSequence.getId(), "无法获取'%s'序列", bizCode);
        Preconditions.checkNotNull(latestSequence.getVal(), "无法获取'%s'序列", bizCode);
        Preconditions.checkNotNull(latestSequence.getLatestTimestamp(), "无法获取'%s'序列", bizCode);
        if (syncTime) {
            Date lastTimestamp = latestSequence.getLatestTimestamp();
            long dataId = latestSequence.getId();
            LocalDate lastDay = lastTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int compareResult = LocalDate.now().compareTo(lastDay);
            if (compareResult < 0) {
                throw new IllegalStateException("序列时间位异常");
            }
            if (compareResult > 0) {
                txOpt.executeWithoutResult(s -> {
                    if (!lockMode) {
                        jdbcTemplate.queryForObject("SELECT id FROM " + seqTableName + " WHERE id = " + dataId + " for update", Collections.emptyMap(), Long.class);
                    }
                    jdbcTemplate.update("UPDATE " + seqTableName + " SET val = 0, latest_timestamp = now() WHERE id = " + dataId, Collections.emptyMap());
                });
                latestSequence = this.selectLatestSequence();
                lastTimestamp = latestSequence.getLatestTimestamp();
                lastDay = lastTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                compareResult = LocalDate.now().compareTo(lastDay);
                if (compareResult != 0) {
                    throw new IllegalStateException("序列时间位异常");
                }
            }
        }
        return latestSequence;
    }

    @Data
    static class LatestSequence {
        private Long id;
        private String bizCode;
        private Long val;
        private Date latestTimestamp;

        public void apply(LatestSequence latestSequence) {
            id = latestSequence.id;
            bizCode = latestSequence.bizCode;
            val = latestSequence.val;
            latestTimestamp = latestSequence.latestTimestamp;
        }
    }
}
