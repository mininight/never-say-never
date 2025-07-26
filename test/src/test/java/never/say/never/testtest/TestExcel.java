package never.say.never.testtest;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.singularsys.jep.Jep;
import com.singularsys.jep.bigdecimal.BigDecNumberFactory;
import com.singularsys.jep.standard.StandardComponents;
import never.say.never.test.util.SimpleFormula;
import never.say.never.testtest.excel.util.ExcelPoiUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.junit.AfterClass;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-13
 */
public class TestExcel {

    private static final File file = new File("C:\\Users\\Ivan\\Desktop\\test.xlsx");
    private static final File file2 = new File("C:\\Users\\Ivan\\Desktop\\部门职员工资表.xlsx");

    @Test
    public void read() throws Exception {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println(String.format("HeapUsage: %s", memoryMXBean.getHeapMemoryUsage().toString()));
        System.out.println(String.format("OffHeapUsage: %s", memoryMXBean.getNonHeapMemoryUsage().toString()));
        AtomicBoolean print = new AtomicBoolean(true);
        long st = System.currentTimeMillis();

        Map<String, String> header = new HashMap<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        ExcelPoiUtil.readRows(file, row -> {
            if (print.get()) {
                System.out.println("前置用时：" + (System.currentTimeMillis() - st) / 1000 + "s");
                print.set(false);
            }
//            Map<String, Object> map = row.toDataMap();
//            System.out.println(map);
//            map.clear();
//            map = null;
        });
        System.out.println(String.format("HeapUsage: %s", memoryMXBean.getHeapMemoryUsage().toString()));
        System.out.println(String.format("OffHeapUsage: %s", memoryMXBean.getNonHeapMemoryUsage().toString()));
    }

    @Test
    public void staxRead() throws Exception {
        try (OPCPackage pkg = OPCPackage.open(file, PackageAccess.READ);) {
            getExcelPkgPart(pkg, XSSFRelation.WORKBOOK);
        }
    }

    @Test
    public void testFileMagic() throws Exception {
        FileMagic fileMagic;
        try (FileInputStream fis = new FileInputStream(file); FileInputStream fis2 = new FileInputStream(file2);) {
            // read as many bytes as possible, up to the required number of bytes
            byte[] data = new byte[19];
            int read = IOUtils.readFully(fis, data, 0, 19);
            byte[] data2 = new byte[19];
            int read2 = IOUtils.readFully(fis2, data2, 0, 19);
            if (read == -1) {
                fileMagic = FileMagic.UNKNOWN;
            } else {
                fileMagic = FileMagic.valueOf(Arrays.copyOf(data, read));
            }
        }
        System.out.println();
    }

    /**
     * Opens up the Styles Table, parses it, and
     * returns a handy object for working with cell styles
     */
    public static PackagePart getExcelPkgPart(OPCPackage pkg, XSSFRelation relation) throws IOException, InvalidFormatException {
        ArrayList<PackagePart> parts = pkg.getPartsByContentType(relation.getContentType());
        if (parts.isEmpty()) return null;
        return parts.get(0);
    }


    static ThreadPoolExecutor threadPoolExecutor;

    static {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("TestPool").setDaemon(false).build();
        RejectedExecutionHandler rejectedExecutionHandler = (r, executor) -> {
            r.run();
        };
        threadPoolExecutor = new ThreadPoolExecutor(10, 300,
                30, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(300),
                threadFactory,
                rejectedExecutionHandler
        );
    }

    @AfterClass
    public static void destroy() {
        threadPoolExecutor.shutdown();
    }

    @Test
    public void testPool() throws Exception {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int j = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("@" + j);
                long st = System.currentTimeMillis();
                while ((System.currentTimeMillis() - st) < 1000) {
                }
                System.out.println("xx");
            }, threadPoolExecutor);
            futures.add(future);
        }
        System.out.println("开始获取结果。。。");
        for (CompletableFuture<Void> future : futures) {
            future.get(1000, TimeUnit.MILLISECONDS);
        }
        System.out.println("完成");
    }

    @Test
    public void testExpr() throws Exception {
        BigDecimal 总额 = new BigDecimal("100.12");
        BigDecimal 重量 = new BigDecimal("12.01");
        BigDecimal 体积 = new BigDecimal("1.11");
        BigDecimal 手续费 = new BigDecimal("2.21");
        Map<String, BigDecimal> varsMap = ImmutableMap.of(
                "总额", 总额,
                "重量", 重量,
                "体积", 体积,
                "手续费", 手续费
        );
        double a = 100.12 - 12.01 * 1.11 * ((2.21 - 1) / 1 + ((2.21 + 1) * 1.01)) / 12.01 * 1.11;
        System.out.println("应得: " + a);
        String expr = "总额 - 重量 * 体积 * ((手续费 - 1)/1+((手续费 + 1)*1.01)) / 重量 * 体积";
        SimpleFormula formula = SimpleFormula.newInstance();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                long st = System.currentTimeMillis();
//                BigDecimal result = calculate(expr, varsMap);
                BigDecimal result = formula.calculate(expr, varsMap);
                System.out.println("结果: " + result);
                System.out.println("用时: " + (System.currentTimeMillis() - st) + "ms");
            }).start();
        }
        Thread.sleep(2000);
    }

    private static BigDecimal calculate(String expr, Map<String, BigDecimal> varsMap) {
        StandardComponents components = new StandardComponents();
        components.setNumberFactory(new BigDecNumberFactory(new MathContext(16)));
        try (Reader reader = new StringReader(expr)) {
            Jep jep = new Jep(components);
            jep.parse(reader);
            for (Map.Entry<String, BigDecimal> entry : varsMap.entrySet()) {
                String key = entry.getKey();
                BigDecimal value = entry.getValue();
                jep.addVariable(key, value);
            }
            BigDecimal result = new BigDecimal(jep.evaluateD() + "");
            return result.setScale(8, RoundingMode.HALF_EVEN);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
