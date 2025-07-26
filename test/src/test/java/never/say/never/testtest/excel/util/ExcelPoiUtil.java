package never.say.never.testtest.excel.util;

import never.say.never.testtest.excel.eventmode.read.ExcelPoiReader;
import never.say.never.testtest.excel.eventmode.read.xml.ExcelXmlRow;
import never.say.never.testtest.excel.util.func.HandyConsumer;
import never.say.never.testtest.excel.util.func.HandyConsumerWithoutResult;

import java.io.File;
import java.io.InputStream;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-25
 */
public class ExcelPoiUtil {

    public static void readRows(File excelFile, HandyConsumerWithoutResult<ExcelXmlRow> rowConsumer) {
        readRows(excelFile, (HandyConsumer<ExcelXmlRow>) rowConsumer);
    }

    public static void readRows(File excelFile, HandyConsumer<ExcelXmlRow> rowConsumer) {
        new ExcelPoiReader().doWithRows(excelFile, rowConsumer);
    }

    public static void readRows(InputStream in, HandyConsumerWithoutResult<ExcelXmlRow> rowConsumer) {
        readRows(in, (HandyConsumer<ExcelXmlRow>) rowConsumer);
    }

    public static void readRows(InputStream in, HandyConsumer<ExcelXmlRow> rowConsumer) {
        new ExcelPoiReader().doWithRows(in, rowConsumer);
    }
}
