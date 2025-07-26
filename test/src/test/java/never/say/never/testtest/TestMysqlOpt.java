/*
 *  Copyright (c) 2018-2022 the original author or authors.
 *  Author: 861828396@qq.com
 */

package never.say.never.testtest;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariDataSource;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;
import never.say.never.test.idgen.BizSequenceYMD;
import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-02-16
 */
public class TestMysqlOpt {

    private static final HikariDataSource dataSource;

    private static final NamedParameterJdbcTemplate jdbcTemplate;

    private static final DataSourceTransactionManager transactionManager;

    private static final DataSourceTransactionManager transactionManager2;

    private static final TransactionTemplate txOpt;

    private static final TransactionTemplate txOpt_rc_new;

    private static final TransactionTemplate txOpt_readonly;

    static String[] operators = new String[]{"+", "-", "*", "/", "%", ">", "<", "=", "|", "&", "^", "~"};

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/dev?useSSL=false&useUnicode=true&characterEncoding=UTF8&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&useAffectedRows=true");
        dataSource.setAutoCommit(true);
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setMinimumIdle(10);
        dataSource.setMaximumPoolSize(200);
        dataSource.setConnectionTimeout(1800000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        transactionManager = new DataSourceTransactionManager(dataSource);
        transactionManager.afterPropertiesSet();
        transactionManager2 = new DataSourceTransactionManager(dataSource);
        transactionManager2.afterPropertiesSet();
        //
        txOpt = new TransactionTemplate(transactionManager);
        txOpt.setReadOnly(false);
        txOpt.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        txOpt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txOpt.afterPropertiesSet();
        //
        txOpt_readonly = new TransactionTemplate(transactionManager);
        txOpt_readonly.setReadOnly(true);
        txOpt_readonly.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        txOpt_readonly.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        txOpt_readonly.afterPropertiesSet();
        //
        txOpt_rc_new = new TransactionTemplate(transactionManager2);
        txOpt_rc_new.setReadOnly(false);
        txOpt_rc_new.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        txOpt_rc_new.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        txOpt_rc_new.afterPropertiesSet();
        //
    }

    @BeforeClass
    public static void init() {
        jdbcTemplate.queryForList("select * from test", (Map<String, ?>) null);
    }

    @AfterClass
    public static void destroy() {
        IOUtils.closeQuietly(dataSource);
    }


    @Test
    public void testFunc() {
        System.out.println(checkStr("sssssssssssssss ssssssssssssss"));
    }

    public boolean checkStr(String str) {
        // 初始校验 + 规则 a)&c) 校验
        if (str == null || str.length() <= 10 || str.trim().length() <= 10 || !Character.isLetter(str.charAt(0))) {
            return false;
        }
        // 规则b)校验
        for (String s : operators) {
            if (str.contains(s)) {
                return false;
            }
        }
        int hasNumCase = Pattern.compile(".*\\d.*").matcher(str).matches() ? 1 : 0;
        int hasUpperCase = Pattern.compile(".*[A-Z].*").matcher(str).matches() ? 1 : 0;
        int hasLowerCase = Pattern.compile(".*[a-z].*").matcher(str).matches() ? 1 : 0;
        int hasSpecCase = Pattern.compile(".*\\W.*").matcher(str).matches() ? 1 : 0;
        if ((hasNumCase + hasUpperCase + hasLowerCase + hasSpecCase) < 3) {
            return false;
        }
        // 规则d)校验
        for (int i = 0; i < str.length() && i + 3 < str.length(); i++) {
            String subStr = str.substring(i, i + 3);
            if (str.indexOf(subStr) != str.lastIndexOf(subStr)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testPath() {
        String path = "#c:/abc嘿嘿.pdf";
        System.out.println(path);
        path = Path.of(path).normalize().toString();
        System.out.println(path);
    }

    @Test
    public void testRegex() {
        String str = "# -    ";
        Pattern pattern = Pattern.compile("^[\\u4E00-\\u9FA5A-Za-z0-9_#\\-[\\u3000|\\u0020|\\u00A0]]+$");
        Matcher matcher = pattern.matcher(str);
        System.out.println(matcher.matches());
    }

    @Test
    public void testLock() throws Throwable {
        TestRunnable[] test = new TestRunnable[500];
        for (int i = 0; i < test.length; i++) {
            test[i] = new TestTask(i);
        }
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(test);
        mttr.runTestRunnables();
        System.out.println("FINISHED.");
    }

    @Test
    public void testSeq() throws Throwable {
        TestRunnable[] test = new TestRunnable[500];
        for (int i = 0; i < test.length; i++) {
            test[i] = new TestTask(i);
        }
        MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(test);
        mttr.runTestRunnables();
        System.out.println("FINISHED.");
    }

    private static BizSequenceYMD bizSequenceYMD = new BizSequenceYMD("TZ", 4);

    static {
        bizSequenceYMD.setDataSource(dataSource);
        try {
            bizSequenceYMD.init();
        } catch (RuntimeException re) {
            throw re;
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    private static void nextSeq() throws Throwable {
        System.out.println(bizSequenceYMD.nextList(1000,true));
    }

    static class TestTask extends TestRunnable {
        private final int index;

        TestTask(int index) {
            this.index = index;
        }

        @Override
        public void runTest() throws Throwable {
            String m = "";
            try {
//                if (index % 2 == 0) {
//                    m = "delAndInsert=>";
//                    delAndInsert();
//                } else {
//                    m = "insert=>";
//                    insert(index);
//                }
                nextSeq();
            } catch (Throwable e) {
                if (e.getMessage().toLowerCase().contains("deadlock")) {
                    System.err.println(m + "死锁");
                } else {
                    throw e;
                }
            }
        }
    }

    private static List<Map<String, Object>> dataList1 = new ArrayList<>(30);

    private static List<Map<String, Object>> dataList2 = new ArrayList<>(30);

    static {
        String qId = "1";
        for (int i = 1; i <= 30; i++) {
            dataList1.add(ImmutableMap.of(
                    "q_id", qId, "r_id", "r_id", "rp_id", "rp_id",
                    "yinsu", "yinsu", "ziduan", "ziduan", "start_", i,
                    "end_", i + 30
            ));
        }
        for (Map<String, Object> data : dataList1) {
            jdbcTemplate.update(
                    "INSERT INTO test (q_id, r_id, rp_id, yinsu, ziduan, start_, end_) " +
                            "VALUES " +
                            "(:q_id, :r_id, :rp_id, :yinsu, :ziduan, :start_, :end_)"
                            + "on duplicate key update q_id=:q_id, r_id=:r_id, rp_id=:rp_id, yinsu=:yinsu, ziduan=:ziduan, start_=:start_, end_=:end_"
                    , data);
        }
    }

    private static void delAndInsert() {
        txOpt_rc_new.executeWithoutResult(s -> {
            prepareLock();
        });
        txOpt.executeWithoutResult(status -> {
            lock();
            jdbcTemplate.update("delete from test WHERE id in (SELECT a.id FROM(SELECT id FROM test WHERE q_id = :q_id)as a)", ImmutableMap.of("q_id", "1"));
            jdbcTemplate.batchUpdate(
                    "INSERT INTO test (q_id, r_id, rp_id, yinsu, ziduan, start_, end_) " +
                            "VALUES " +
                            "(:q_id, :r_id, :rp_id, :yinsu, :ziduan, :start_, :end_) "
                    , dataList1.toArray(new Map[0]));
        });
    }

    private static void insert(int index) {
        txOpt_rc_new.executeWithoutResult(s -> {
            prepareLock();
        });
        txOpt.executeWithoutResult(status -> {
            lock();
            List<Map<String, Object>> datas = new ArrayList<>();
            for (int i = 1; i <= 30; i++) {
                datas.add(ImmutableMap.of(
                        "q_id", UUID.randomUUID().toString() + index, "r_id", "r_id", "rp_id", "rp_id",
                        "yinsu", "yinsu", "ziduan", "ziduan", "start_", i,
                        "end_", i + 30
                ));
            }
            jdbcTemplate.batchUpdate(
                    "INSERT INTO test (q_id, r_id, rp_id, yinsu, ziduan, start_, end_) " +
                            "VALUES " +
                            "(:q_id, :r_id, :rp_id, :yinsu, :ziduan, :start_, :end_) "
                    , datas.toArray(new Map[0]));
        });
    }

    private static void prepareLock() {
        jdbcTemplate.update("INSERT IGNORE INTO optimistic_lock (`id`, `resource`, `version`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, 1, 1, '2023-05-24 03:08:54', '2023-05-24 03:08:56', '2023-05-24 03:08:58')",
                ImmutableMap.of());
    }

    private static void lock() {
        jdbcTemplate.queryForObject("select id from optimistic_lock where id=:id for update", ImmutableMap.of("id", 1), Long.class);
    }
}
