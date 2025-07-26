package never.say.never.testtest.excel.eventmode.read;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import never.say.never.test.util.FileToolkit;
import never.say.never.testtest.excel.ExcelPoiException;
import never.say.never.testtest.excel.ExcelPoiReadException;
import never.say.never.testtest.excel.ExcelPoiStopReadingVoluntarilyException;
import never.say.never.testtest.excel.cache.HandyCacheByHashMap;
import never.say.never.testtest.excel.eventmode.read.handler.ExcelRowsHandler;
import never.say.never.testtest.excel.eventmode.read.strings.AbstractSharedStringsTable;
import never.say.never.testtest.excel.eventmode.read.strings.CachedSharedStringsTable;
import never.say.never.testtest.excel.eventmode.read.xml.ExcelXmlRow;
import never.say.never.testtest.excel.util.func.HandyConsumer;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
@Slf4j
@Getter
@Setter
public class ExcelPoiReader {

    private static final SAXParserFactory SAX_FACTORY = SAXParserFactory.newInstance();

    private SAXParserFactory saxParserFactory;

    /**
     * @param excelFile   excel file
     * @param rowConsumer rows consumer
     */
    public void doWithRows(File excelFile, HandyConsumer<ExcelXmlRow> rowConsumer) {
        try {
            OPCPackage pkg = OPCPackage.open(excelFile, PackageAccess.READ);
            doWithRows(pkg, rowConsumer);
        } catch (Exception e) {
            handleErr(e);
        }
    }

    /**
     * {@link org.apache.poi.util.IOUtils#setByteArrayMaxOverride(int)}
     *
     * @param in          input stream (the maximum length for this input stream is 10M)
     * @param rowConsumer rows consumer
     */
    public void doWithRows(InputStream in, HandyConsumer<ExcelXmlRow> rowConsumer) {
        try (in) {
            doWithRows(OPCPackage.open(FileToolkit.openZipEntrySourceStream(in)), rowConsumer);
        } catch (Exception e) {
            handleErr(e);
        }
    }

    /**
     * @param opcPackage  {@link OPCPackage}
     * @param rowConsumer rows consumer
     * @see XSSFEventBasedExcelExtractor
     */
    public void doWithRows(OPCPackage opcPackage, HandyConsumer<ExcelXmlRow> rowConsumer) {
        long st = System.currentTimeMillis();
        try (opcPackage; AbstractSharedStringsTable sharedStrings = getSharedStringsTable(opcPackage);) {
            XSSFReader reader = new XSSFReader(opcPackage);
            // workbook
            // styles
            StylesTable stylesTable = reader.getStylesTable();
//            System.out.println(JSON.toJSONString(stylesTable));
            System.out.println("StylesTable大小：" + FileToolkit.sizeOf(stylesTable) / 1000 + " kb");
            System.out.println("基础数据用时：" + (System.currentTimeMillis() - st) / 1000 + "s");
            SAXParser saxParser = getSaxParserFactory().newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(getExcelRowsHandler(sharedStrings, rowConsumer));
            XSSFReader.SheetIterator sheetIterator = (XSSFReader.SheetIterator) reader.getSheetsData();
            while (sheetIterator.hasNext()) {
                try (InputStream sheet = sheetIterator.next();) {
                    long st1 = System.currentTimeMillis();
                    InputSource sheetSource = new InputSource(sheet);
                    xmlReader.parse(sheetSource);
                    System.out.println("读一个sheet用时：" + (System.currentTimeMillis() - st1) / 1000 + "s");
                }
            }
        } catch (Exception e) {
            handleErr(e);
        } finally {
            if ((System.currentTimeMillis() - st) > 10000) {
                System.gc();
            }
        }
    }

    public SAXParserFactory getSaxParserFactory() {
        return saxParserFactory == null ? SAX_FACTORY : saxParserFactory;
    }

    public AbstractSharedStringsTable getSharedStringsTable(OPCPackage opcPackage)
            throws IOException, SAXException {
        CachedSharedStringsTable sharedStringsTable = new CachedSharedStringsTable();
        String cacheName = sharedStringsTable.getUniqueCacheName();
        sharedStringsTable.setCache(new HandyCacheByHashMap(cacheName));
        sharedStringsTable.readFrom(opcPackage);
        return sharedStringsTable;
    }

    public DefaultHandler getExcelRowsHandler(AbstractSharedStringsTable sharedStringsTable,
                                              HandyConsumer<ExcelXmlRow> rowConsumer) throws IOException, SAXException {
        return new ExcelRowsHandler(sharedStringsTable, rowConsumer);
    }

    protected void handleErr(Exception err) {
        if (err instanceof ExcelPoiStopReadingVoluntarilyException) {
            if (log.isDebugEnabled()) {
                log.debug(err.getMessage(), err);
            }
        } else if (err instanceof ExcelPoiException) {
            throw (ExcelPoiException) err;
        } else {
            throw new ExcelPoiReadException(err.getMessage(), err);
        }
    }
}
