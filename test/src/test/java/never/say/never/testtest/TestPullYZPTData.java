/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.testtest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;
import never.say.never.demo.ent_credit.EntCreditApplication;
import never.say.never.demo.ent_credit.api.HttpEntCreditApi;
import never.say.never.demo.ent_credit.entity.*;
import never.say.never.demo.ent_credit.enums.SourceChannel;
import never.say.never.demo.ent_credit.enums.SourceType;
import never.say.never.demo.ent_credit.http.HttpApiRequestContext;
import never.say.never.demo.ent_credit.jdbc.LightJdbcTemplate;
import never.say.never.demo.ent_credit.util.SimpleSLB;
import never.say.never.demo.ent_credit.util.StringKV;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static never.say.never.demo.ent_credit.configure.EntCreditBeanUnit.*;
import static never.say.never.demo.ent_credit.enums.SourceType.person;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EntCreditApplication.class)
@ActiveProfiles("dev")
public class TestPullYZPTData {

    static Charset CMD_GBK = Charset.forName("GBK");

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
    }

    @Test
    public void testAny() throws Throwable {

    }

    @Test
    public void testDWH() throws Throwable {
//        JSONObject param = new JSONObject();
//        param.put("keyword", "淄博一淼民间资本管理股份有限公司");
//        param.put("filter", new HashMap<>());
//        param.put("pageCurrent", 1);
//        param.put("pageSize", 10);
//        param.put("searchState", 1);
//        param.put("sortType", "");
//        param.put("type", 1);
        try {
            HttpApiRequestContext context = HttpApiRequestContext.getCurrent();
            context.setLevel(1);
            context.setBeanUnit(BELOW);
            Company company = new Company();
            company.setEntName("第四范式（北京）技术有限公司");
            company = SourceChannel.YiQiCha.api().getEntCredit(company);
            System.out.println(company);
        } finally {
            HttpApiRequestContext.clean();
        }
    }

    @Test
    public void testSourceId() throws Throwable {
        SourceId sourceId = new SourceId();
        sourceId.setChannel(SourceChannel.YiQiCha.name());
        sourceId.setType(SourceType.company.name());
        sourceId.setValue("333");
        sourceId = BELOW.getRepository().lookupId(sourceId);
        System.out.println(sourceId);
    }

    @Test
    public void testPullLog() throws Throwable {
        PullLog pullLog = new PullLog();
        pullLog.setSourceKey("姚凯泷#b5d7460d7e749da2a2a6d214ef03fc0e");
        pullLog.setType(person.name());
        pullLog.setLevel(2);
        pullLog.setFinished(false);
        pullLog.setBegin_time(LocalDateTime.now());
        pullLog.setEnd_time(LocalDateTime.now());
        BELOW.getRepository().savePullLog(pullLog);
//        PullLogSub pullLogSub = new PullLogSub();
//        pullLogSub.setId("1");
//        pullLogSub.setPid("0");
//        pullLogSub.setType(company.name());
//        pullLogSub.setLevel(1);
//        BELOW.getRepository().savePullLogSub(pullLogSub);
//        BELOW.getRepository().setPullFinished(pullLog.getId(), company);
//        List<PullLog> logList = BELOW.getRepository().selectAllUnFinished();
//        System.out.println();
    }

    @Test
    public void testCompanyApiSLB() throws Throwable {
        String companyName = "淄博一淼民间资本管理股份有限公司";
        Company company = Company.asParam().name(companyName);
        HttpEntCreditApi entCreditApi = SimpleSLB.newInstance(HttpEntCreditApi.class, SourceChannel.list(),
                new SimpleSLB.RouteSelector<>() {
                    @Override
                    public Decision beforeCall(SimpleSLB.Srv<HttpEntCreditApi> srv, int srvIndex,
                                               Method method, Object[] args) throws Throwable {
                        SourceChannel channel = SourceChannel.of(srv.getObject());
                        if (channel != SourceChannel.AiQiCha) {
                            srv.setValid(false);
                            return Decision.NEXT;
                        }
                        return Decision.ACCEPT;
                    }

                    @Override
                    public Decision afterCall(SimpleSLB.Srv<HttpEntCreditApi> srv, int srvIndex,
                                              Method method, Object[] args, Object result) throws Throwable {
                        if (method.getReturnType() != void.class) {
                            return result == null ? Decision.NEXT : Decision.ACCEPT;
                        }
                        return Decision.ACCEPT;
                    }
                });
        try {
            HttpApiRequestContext.getCurrent().setBeanUnit(BELOW);
            company = entCreditApi.getEntCredit(company);
        } finally {
            HttpApiRequestContext.clean();
        }
        System.out.println(company);
    }

    @Test
    public void testPersonApiSLB() throws Exception {
        Person person = new Person();
        person.setPersonId("a933129570277190eedc900c02cec4e5");
//        for (int i = 0; i < 100; i++) {
//            Thread.sleep(2000);
        person = BELOW.getHttpApiSLBPoints().getPersonInfo().apply(UUID.randomUUID().toString(), person);
        System.out.println(person);
//        }
    }

    @Data
    static class WinSrvData {
        private String SERVICE_NAME;
        private String DISPLAY_NAME;
        private String TYPE;
        private String WIN32_EXIT_CODE;
        private String SERVICE_EXIT_CODE;
        private String CHECKPOINT;
        private String WAIT_HINT;

        public static WinSrvData of(String winSrvData) {
            return new WinSrvData().fill(winSrvData);
        }

        public boolean match(List<String> srvNameList) {
            return srvNameList.contains(SERVICE_NAME);
        }

        public WinSrvData fill(String winSrvData) {
            for (String line : winSrvData.split("\n")) {
                if (!line.contains(":")) {
                    continue;
                }
                String[] kv = line.split(":");
                String key = kv[0];
                String value = kv[1];
                if (!StringUtils.hasText(key)) {
                    continue;
                }
                key = key.trim();
                switch (key) {
                    case "SERVICE_NAME" -> SERVICE_NAME = value.trim();
                    case "DISPLAY_NAME" -> DISPLAY_NAME = value.trim();
                    case "TYPE" -> TYPE = value.trim();
                    case "WIN32_EXIT_CODE" -> WIN32_EXIT_CODE = value.trim();
                    case "SERVICE_EXIT_CODE" -> SERVICE_EXIT_CODE = value.trim();
                    case "CHECKPOINT" -> CHECKPOINT = value.trim();
                    case "WAIT_HINT" -> WAIT_HINT = value.trim();
                }
            }
            return this;
        }
    }


    @Test
    public void testScDel() throws Throwable {
        int sp;
        for (; ; ) {
            delWinSrv();
            if (sp()) {
                sp = RandomUtils.nextInt(13) - 10;
                sp = sp < 0 ? 3 : sp;
                Thread.sleep(sp * 1000L);
            } else {
                sp = RandomUtils.nextInt(14) - 10;
                sp = sp < 0 ? 4 : sp;
                Thread.sleep(sp * 1000L);
            }
        }
    }

    private void delWinSrv() throws Throwable {
        //
        StringBuilder cmd = new StringBuilder();
        StringBuilder cmdLog = new StringBuilder(" \n============ " + DateFormatUtils.format(System.currentTimeMillis(),
                "yyyy-MM-dd HH:mm:ss.SSS") + " ============\n");
        StringBuilder srvOptTips = new StringBuilder();
        // sc query state=all
        StringKV execRes = execCmd("sc query state=all");
        String[] srvDataLineArr = execRes.getKey().split("\r\n");
//        String[] srvDataArr = execRes.getKey().split("\r\n\r\n");
        // store & compare
        ClassPathResource srvDataResource = new ClassPathResource("normalSrv_2025-07-18#20_38_10.data");
        List<String> normalSrvList;
        try (InputStream srvDataIns = srvDataResource.getInputStream()) {
            String[] normalSrvArr = StreamUtils.copyToString(srvDataIns, CMD_GBK).split("\n;");
            normalSrvList = Arrays.stream(normalSrvArr).map(String::trim).distinct().toList();
        }
//        List<String> normalSrv = Arrays.stream(srvDataArr)
//                .filter(srvData -> WinSrvData.of(srvData).match(normalSrvList))
//                .toList();
//        String normalSrvData = String.join("\r\n\r\n", normalSrv);
//        logThisAny(normalSrvData, UTF_8, "normalSrv" + DateFormatUtils.format(System.currentTimeMillis(),
//                "yyyy-MM-dd#HH_mm_ss") + ".data", false);

        // management
        Arrays.stream(srvDataLineArr)
                .filter(str -> str.contains("SERVICE_NAME")).map(str -> str.split(":")[1])
                .filter(str -> (str.contains("_") && strWithNum(str)) || !normalSrvList.contains(str.trim()))
                .map(String::trim)
                .forEach(srvName -> {
                    try {
                        srvOptTips.setLength(0);
                        //
                        winSrvMngOpt(WinSrvMngOpt.STOP, cmd, srvName, cmdLog);
                        //
                        StringKV cmdSrvQueryRes = winSrvMngOpt(WinSrvMngOpt.QUERY, cmd, srvName, cmdLog);
                        String cmdSrvQueryResTxt = cmdSrvQueryRes.getKey();
                        String srvStoppedState;
                        int checkCount = 0;
                        do {
                            srvStoppedState = Arrays.stream(cmdSrvQueryResTxt.split("\r\n"))
                                    .filter(str -> str.contains("STATE")).map(str -> str.split(":")[1])
                                    .filter(str -> str.contains("STOPPED") && str.contains("1 ")).findFirst()
                                    .orElse(null);
                            if (!StringUtils.hasText(srvStoppedState)) {
                                checkCount++;
                                if (srvOptTips.isEmpty()) {
                                    srvOptTips.append("=> ").append(srvName).append(" stopping...");
                                    System.out.println(srvOptTips);
                                } else {
//                                    System.out.print("\b.");
                                }
                                Thread.sleep(500);
                            } else {
                                System.out.println("=> " + srvName + " STOPPED");
                            }
                        } while (checkCount < 30 && !StringUtils.hasText(srvStoppedState));
                        if (!StringUtils.hasText(srvStoppedState)) {
                            System.err.println("\n=> Stop【 " + srvName + " 】failed！Still alive！");
                        }
                        //
                        winSrvMngOpt(WinSrvMngOpt.DEL, cmd, srvName, cmdLog);
                        System.out.println("=> " + srvName + " DELETED");
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
        // log
        logThisAny(cmdLog.toString(), CMD_GBK);

    }

    enum WinSrvMngOpt {
        STOP,
        DEL,
        QUERY
    }

    private StringKV winSrvMngOpt(WinSrvMngOpt opt, StringBuilder cmd, String srvName, StringBuilder cmdLog)
            throws Throwable {
        cmd.setLength(0);
        String optZl;
        switch (opt) {
            case DEL -> optZl = "delete";
            case STOP -> optZl = "stop";
            case QUERY -> optZl = "query";
            default -> throw new IllegalArgumentException("WinSrvMngOpt Not Supported");
        }
        String cmdStr = cmd.append("sc ").append(optZl).append(" \"").append(srvName).append("\"").toString();
        StringKV cmdRes = execCmd(cmdStr);
        cmdLog.append(cmdStr).append("\n");
        return cmdRes;
    }

    private static boolean sp() {
        long st = System.currentTimeMillis();
        return st % 1102 == 0 || st % 110 == 0 || st % 1001 == 0 || st % 120 == 0 || st % 902 == 0;
    }

    private void logThisAny(String str, Charset charset) throws Exception {
        logThisAny(str, charset, null);
    }

    private void logThisAny(String str, Charset charset, String logName) throws Exception {
        logThisAny(str, charset, logName, true);
    }

    private void logThisAny(String str, Charset charset, String logName, boolean append) throws Exception {
        ClassPathResource logResource = new ClassPathResource("/");
        Path dirPath = logResource.getFile().toPath();
        Path logDirPath = dirPath.resolveSibling("../logs");
        File logDir = logDirPath.toFile();
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        Path logPath = logDirPath.resolve(StringUtils.hasText(logName) ? logName : getClass().getSimpleName() + "#testScDel.log");
        File logFile = logPath.toFile();
        if (!logFile.exists()) {
            logFile.createNewFile();
        } else {
            // TODO
            if (sp()) {
                if (logFile.length() > 1024 * 1024 * 15) {
                    logFile.delete();
                }
            } else {
                if (logFile.length() > 1024 * 1024 * 16) {
                    logFile.delete();
                }
            }
        }
        FileUtils.write(logFile, str, charset, append);
    }

    private static boolean strWithNum(String str) {
        if (!StringUtils.hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private StringKV execCmd(String cmdLine) throws Throwable {
        StringKV skv = new StringKV();
        Process ps = Runtime.getRuntime().exec(cmdLine);
        try (InputStream is = ps.getInputStream(); InputStream errIs = ps.getErrorStream()) {
            skv.setKey(StreamUtils.copyToString(is, CMD_GBK));
            skv.setValue(StreamUtils.copyToString(errIs, CMD_GBK));
        }
        int exitCode = ps.waitFor();
        skv.setValue(exitCode + "");
        if (ps.isAlive()) {
            ps.destroy();
        }
        return skv;
    }

    @Test
    public void testPullPersonAllEnterprises() throws Exception {
        String personId = "e03325ead24989358d86ab940eca7e8a";
        BELOW.getHttpReadService().getPersonDetail("", personId);
    }

    @Test
    public void YZPT_TO_EXCEL() throws Exception {
        //
        int[] dataLevels = new int[]{1, 3};
        //
        String baseDirStr = "C:\\Users\\xulia\\Desktop\\新建文件夹\\云芝平台金融传销链_old";
        Path basePath = Paths.get(baseDirStr);
        File baseDir = basePath.toFile();
        baseDir.mkdirs();
        LightJdbcTemplate jdbcTemplate = AQC_BACKUP_0805.getJdbcTemplate();
        List<ChinaRegion> chinaRegionList = jdbcTemplate.selectList(
                "select * from china_region where type=1", ImmutableMap.of(), ChinaRegion.class);
//        ChinaRegion test = new ChinaRegion();
//        test.setCode("110000");
//        test.setP_code("100000");
//        test.setName("北京市");
//        test.setType(1);
//        List<ChinaRegion> chinaRegionList = Lists.newArrayList(test);
        //
        for (ChinaRegion chinaRegion : chinaRegionList) {
            exportYZPTDataToExcel(chinaRegion, basePath, dataLevels, jdbcTemplate);
        }
        deleteEmptyFolders(baseDir);
    }

    static void deleteEmptyFolders(File folder) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                deleteEmptyFolders(file);
            }
        } else {
            folder.delete();
        }
    }

    private void exportYZPTDataToExcel(ChinaRegion chinaRegion, Path basePath, int[] dataLevels,
                                       LightJdbcTemplate jdbcTemplate) {
        Preconditions.checkArgument(chinaRegion.getType() == 1, "需要省级数据");
        String shengName = chinaRegion.getName();
        String shengCode = chinaRegion.getCode();
        File shengDir = basePath.resolve(shengName).toFile();
        shengDir.mkdirs();
        List<Map<String, Object>> shiRegionRecords = jdbcTemplate.queryForList(
                "select * from china_region where p_code=:code",
                ImmutableMap.of("code", shengCode));
        if (CollectionUtils.isEmpty(shiRegionRecords)) {
            return;
        }
        boolean isDirectAdmin = shengName.endsWith("市");
        for (Map<String, Object> shiRegionRecord : shiRegionRecords) {
            ChinaRegion shi = new JSONObject(shiRegionRecord).toJavaObject(ChinaRegion.class);
            String shiName = shi.getName();
            File shiDir = shengDir.toPath().resolve(shi.getName()).toFile();
            if (!isDirectAdmin) {
                shiDir.mkdirs();
                exportYZPTDataToExcel(jdbcTemplate, shiDir, shengName, shiName, "自由贸易", dataLevels);
                exportYZPTDataToExcel(jdbcTemplate, shiDir, shengName, shiName, "经济开发", dataLevels);
                exportYZPTDataToExcel(jdbcTemplate, shiDir, shengName, shiName, "经济技术开发", dataLevels);
                exportYZPTDataToExcel(jdbcTemplate, shiDir, shengName, shiName, "产业开发", dataLevels);
                exportYZPTDataToExcel(jdbcTemplate, shiDir, shengName, shiName, "高新区", dataLevels);
                exportYZPTDataToExcel(jdbcTemplate, shiDir, shengName, shiName, "旅游度假", dataLevels);
            }
            //
            List<Map<String, Object>> quXianRegionRecords = isDirectAdmin ?
                    Lists.newArrayList(shiRegionRecord) : jdbcTemplate.queryForList(
                    "select * from china_region where p_code=:code",
                    ImmutableMap.of("code", shi.getCode()));
            if (CollectionUtils.isEmpty(quXianRegionRecords)) {
                continue;
            }
            for (Map<String, Object> quXianRegionRecord : quXianRegionRecords) {
                ChinaRegion quXian = new JSONObject(quXianRegionRecord).toJavaObject(ChinaRegion.class);
                Path quXianFileDirPath = isDirectAdmin ? shengDir.toPath() : shiDir.toPath();
                File quXianFile = quXianFileDirPath.resolve(quXian.getName() + ".xlsx").toFile();
                List<Map<String, Object>> quXianDataList = jdbcTemplate.queryForList(
                        "SELECT * FROM person_company" +
                                " WHERE level BETWEEN :levelL AND :levelR AND " +
                                "regAddr LIKE :shiKey  AND regAddr LIKE :quXianKey",
                        ImmutableMap.of(
                                "levelL", dataLevels[0],
                                "levelR", dataLevels[1],
                                "shiKey", "%" + shi.getName() + "%",
                                "quXianKey", "%" + quXian.getName() + "%")
                );
                if (CollectionUtils.isEmpty(quXianDataList)) {
                    continue;
                }
                List<PersonCompany> personCompanyList = quXianDataList.stream()
                        .map(quXianData -> new JSONObject(quXianData).toJavaObject(PersonCompany.class))
                        .collect(Collectors.toList());
                exportYZPTDataToExcel(quXianFile, personCompanyList);
            }
        }
    }

    private void exportYZPTDataToExcel(LightJdbcTemplate jdbcTemplate, File shiDir, String shengName, String shiName,
                                       String spRegionKey, int[] dataLevels) {
        String shengKey = shengName.substring(0, 2);
        String shiKey = shiName.replace("市", "");
        String spRegionName = spRegionKey.endsWith("区") ? spRegionKey : spRegionKey + "区";
        String spRegionKey1 = shiKey + spRegionKey;
        File spDataFile = shiDir.toPath().resolve(spRegionName + ".xlsx").toFile();
        List<Map<String, Object>> spDataList = jdbcTemplate.queryForList(
                "SELECT * FROM person_company" +
                        " WHERE level BETWEEN :levelL AND :levelR AND " +
                        "regAddr LIKE :shengKey AND regAddr LIKE :shiKey  " +
                        "AND (regAddr LIKE :spRegionKey1 OR (regAddr LIKE :shiName AND regAddr LIKE :spRegionKey))",
                ImmutableMap.of(
                        "levelL", dataLevels[0],
                        "levelR", dataLevels[1],
                        "shengKey", "%" + shengKey + "%",
                        "shiKey", "%" + shiKey + "%",
                        "spRegionKey1", "%" + spRegionKey1 + "%",
                        "shiName", "%" + shiName + "%",
                        "spRegionKey", "%" + spRegionKey + "%"
                )
        );
        if (CollectionUtils.isEmpty(spDataList)) {
            return;
        }
        List<PersonCompany> personCompanyList = spDataList.stream()
                .map(spData -> new JSONObject(spData).toJavaObject(PersonCompany.class))
                .collect(Collectors.toList());
        exportYZPTDataToExcel(spDataFile, personCompanyList);
    }

    private void exportYZPTDataToExcel(File file, List<PersonCompany> personCompanyList) {
        EasyExcel.write(file, PersonCompany.class).sheet("sheet1")
                .doWrite(personCompanyList);
    }


    @Test
    public void PULL_YZPT_TOP_FINANCIAL_CHAIN() throws Exception {
        String compListTxt = StreamUtils.copyToString(new ClassPathResource("YZPT_BELOW_COMPANY").getInputStream(), UTF_8);
        String[] companyNames;
        companyNames = compListTxt.contains("\r\n") ? compListTxt.split("\r\n") : compListTxt.split("\n");
        TOP.getManger().pullFinancialChain(companyNames);
    }

    @Test
    public void PULL_YZPT_BELOW_FINANCIAL_CHAIN() throws Exception {
        String compListTxt = StreamUtils.copyToString(new ClassPathResource("YZPT_BELOW_COMPANY").getInputStream(), UTF_8);
        String[] companyNames;
        companyNames = compListTxt.contains("\r\n") ? compListTxt.split("\r\n") : compListTxt.split("\n");
        BELOW.getManger().pullFinancialChain(companyNames);
    }
}
