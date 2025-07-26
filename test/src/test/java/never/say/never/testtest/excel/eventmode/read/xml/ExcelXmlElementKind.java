package never.say.never.testtest.excel.eventmode.read.xml;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
public enum ExcelXmlElementKind {
    Noop,
    CELL_TXT,
    CELL,
    ROW,
    SHEET,
    ;

    public static ExcelXmlElementKind of(String qName) {
        if (qName == null) {
            return Noop;
        }
        switch (qName) {
            case "row":
                return ROW;
            case "c":
                return CELL;
            case "v":
                return CELL_TXT;
            default:
                return Noop;
        }
    }
}
