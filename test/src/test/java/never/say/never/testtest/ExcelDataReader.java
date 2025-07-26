package never.say.never.testtest;

import lombok.Data;
import never.say.never.test.util.ExcelFileMagic;
import never.say.never.test.util.FileToolkit;
import never.say.never.testtest.excel.cache.HandyCacheByHashMap;
import never.say.never.testtest.excel.eventmode.read.strings.AbstractSharedStringsTable;
import never.say.never.testtest.excel.eventmode.read.strings.CachedSharedStringsTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.poifs.storage.HeaderBlockConstants;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.extractor.XSSFBEventBasedExcelExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-02
 */
public class ExcelDataReader {

    private String headerSheetName;

    private Integer headerSheetIndex;

    private Integer headerRowIndex;

    private final List<String> sheetNames = new ArrayList<>();

    private final List<Integer> sheetIndexes = new ArrayList<>();

    private ExcelFileMagic fileMagic;

    public void header(String sheetName, Integer rowIndex) {
        this.headerSheetName = sheetName;
        this.headerRowIndex = rowIndex;
    }

    public void header(Integer sheetIndex, Integer rowIndex) {
        this.headerSheetIndex = sheetIndex;
        this.headerRowIndex = rowIndex;
    }

    public void sheetNames(String... sheetNames) {
        for (String sheetName : sheetNames) {
            if (StringUtils.isBlank(sheetName) || this.sheetNames.contains(sheetName)) {
                continue;
            }
            this.sheetNames.add(sheetName);
        }
    }

    public void sheetNames(List<String> sheetNames) {
        for (String sheetName : sheetNames) {
            if (StringUtils.isBlank(sheetName) || this.sheetNames.contains(sheetName)) {
                continue;
            }
            this.sheetNames.add(sheetName);
        }
    }

    public void sheetIndexes(Integer... sheetIndexes) {
        for (Integer sheetIndex : sheetIndexes) {
            if (sheetIndex == null || sheetIndex < 0 || this.sheetIndexes.contains(sheetIndex)) {
                continue;
            }
            this.sheetIndexes.add(sheetIndex);
        }
    }

    public void sheetIndexes(List<Integer> sheetIndexes) {
        for (Integer sheetIndex : sheetIndexes) {
            if (sheetIndex == null || sheetIndex < 0 || this.sheetIndexes.contains(sheetIndex)) {
                continue;
            }
            this.sheetIndexes.add(sheetIndex);
        }
    }

    public void readFrom(File excelFile) throws Exception {
        FileInputStream inputStream = new FileInputStream(excelFile);
        readFrom(excelFile.getName(), inputStream);
    }

    public void readFrom(InputStream inputStream) throws Exception {
        readFrom(null, inputStream);
    }

    public void readFrom(String fileName, InputStream inputStream) throws Exception {
        try (inputStream) {
            FileType fileType = FileType.of(fileName, inputStream);
            if (fileType == FileType.UNKNOWN) {
                throw new RuntimeException("文件格式异常");
            }
            switch (fileType) {
                case CSV:
                    readCsv(inputStream);
                    break;
                case XSL:
                    readXsl(inputStream);
                    break;
                case XLSX_XLSB:
                    OPCPackage pkg = OPCPackage.open(inputStream);
                    PackageRelationshipCollection core;
                    core = pkg.getRelationshipsByType(PackageRelationshipTypes.CORE_DOCUMENT);
                    if (core.isEmpty()) {
                        core = pkg.getRelationshipsByType(PackageRelationshipTypes.STRICT_CORE_DOCUMENT);
                    }
                    if (core.size() != 1) {
                        throw new IllegalArgumentException("Invalid Excel OOXML Package received - expected 1 core document, found " + core.size());
                    }
                    final PackagePart corePart = pkg.getPart(core.getRelationship(0));
                    final String contentType = corePart.getContentType();
                    // Xlsx
                    for (XSSFRelation rel : XSSFExcelExtractor.SUPPORTED_TYPES) {
                        if (rel.getContentType().equals(contentType)) {
                            readXlsx(pkg);
                        }
                    }
                    // Xlsb
                    for (XSSFRelation rel : XSSFBEventBasedExcelExtractor.SUPPORTED_TYPES) {
                        if (rel.getContentType().equals(contentType)) {
                            readXlsb(pkg);
                        }
                    }
                    break;
                default:
                    throw new RuntimeException(String.format("未受支持的文件格式'%s'", fileType.name()));
            }
        }
    }

    protected void readCsv(InputStream inputStream) {

    }

    protected void readXsl(InputStream inputStream) {

    }

    public void readXlsx(OPCPackage pkg) throws Exception {
        try (pkg; AbstractSharedStringsTable sharedStrings = getSharedStringsTable(pkg);) {
            XSSFReader reader = new XSSFReader(pkg);
            reader.getWorkbookData();
        }
    }

    public void readXlsb(OPCPackage pkg) throws Exception {

    }

//    public List<Worksheet> getWorksheets(OPCPackage pkg) throws IOException, InvalidFormatException {
//        ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.WORKBOOK.getContentType());
//        if (parts.isEmpty()) {
//            return null;
//        }
//        JacksonUtil
//    }

    public AbstractSharedStringsTable getSharedStringsTable(OPCPackage opcPackage)
            throws IOException, SAXException {
        CachedSharedStringsTable sharedStringsTable = new CachedSharedStringsTable();
        String cacheName = sharedStringsTable.getUniqueCacheName();
        sharedStringsTable.setCache(new HandyCacheByHashMap(cacheName));
        sharedStringsTable.readFrom(opcPackage);
        return sharedStringsTable;
    }

    @FunctionalInterface
    public interface RowConsumer<T> {
        boolean goon(T t);
    }

    @FunctionalInterface
    public interface RowConsumerWithoutResult<T> extends RowConsumer<T> {
        void accept(T t);

        @Override
        default boolean goon(T t) {
            accept(t);
            return true;
        }
    }

    @Data
    public static class Worksheet implements Serializable{
        private static final long serialVersionUID = 305143530233136894L;
        private Integer id;
        private String name;
    }


    /**
     * @see org.apache.poi.poifs.filesystem.FileMagic
     */
    public enum FileType {
        /**
         * CSV
         */
        CSV(-17, -69, -65),

        /**
         * XSL
         */
        XSL(HeaderBlockConstants._signature),

        /**
         * XLSX or XLSB
         */
        XLSX_XLSB(0x50, 0x4b, 0x03, 0x04),

        /**
         * UNKNOWN
         */
        UNKNOWN(new byte[0]);

        public static final int MAX_PATTERN_LENGTH = 8;

        final byte[][] magic;

        FileType(long magic) {
            this.magic = new byte[1][8];
            LittleEndian.putLong(this.magic[0], 0, magic);
        }

        FileType(int... magic) {
            byte[] one = new byte[magic.length];
            for (int i = 0; i < magic.length; i++) {
                one[i] = (byte) (magic[i] & 0xFF);
            }
            this.magic = new byte[][]{one};
        }

        FileType(byte[]... magic) {
            this.magic = magic;
        }

        FileType(String... magic) {
            this.magic = new byte[magic.length][];
            int i = 0;
            for (String s : magic) {
                this.magic[i++] = s.getBytes(StandardCharsets.UTF_8);
            }
        }

        public static FileType of(String fileName, InputStream inputStream) throws IOException {
            FileType fileTypeByName = null;
            if (fileName != null) {
                fileTypeByName = of(fileName);
                if (fileTypeByName == FileType.UNKNOWN) {
                    return fileTypeByName;
                }
            }
            FileType fileType = of(inputStream);
            if (fileType == FileType.UNKNOWN) {
                return fileType;
            }
            if (fileTypeByName != null && (fileTypeByName != fileType)) {
                return FileType.UNKNOWN;
            }
            return fileType;
        }

        public static FileType of(String fileName) {
            String fileSuffix = FileToolkit.getSuffix(fileName);
            if (fileSuffix == null) {
                return FileType.UNKNOWN;
            }
            fileSuffix = fileSuffix.toUpperCase();
            for (FileType fileType : values()) {
                String[] suffixes = fileType.name().split("_");
                for (String suffix : suffixes) {
                    if (fileSuffix.equals(suffix)) {
                        return fileType;
                    }
                }
            }
            return FileType.UNKNOWN;
        }

        public static FileType of(InputStream inputStream) throws IOException {
            byte[] data = new byte[ExcelFileMagic.MAX_PATTERN_LENGTH];
            PushbackInputStream in = new PushbackInputStream(inputStream, data.length);
            int magicTest = in.read(data);
            if (magicTest > -1) {
                FileType fileType = FileType.valueOf(data);
                in.unread(data);
                return fileType;
            }
            return FileType.UNKNOWN;
        }

        public static FileType valueOf(byte[] magic) {
            for (FileType fm : values()) {
                for (byte[] ma : fm.magic) {
                    // don't try to match if the given byte-array is too short
                    // for this pattern anyway
                    if (magic.length < ma.length) {
                        continue;
                    }

                    if (findMagic(ma, magic)) {
                        return fm;
                    }
                }
            }
            return UNKNOWN;
        }

        private static boolean findMagic(byte[] expected, byte[] actual) {
            int i = 0;
            for (byte expectedByte : expected) {
                if (actual[i++] != expectedByte && expectedByte != '?') {
                    return false;
                }
            }
            return true;
        }

    }
}
