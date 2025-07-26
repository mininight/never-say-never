package never.say.never.testtest.excel;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
public class ExcelPoiReadException extends ExcelPoiException {

    public ExcelPoiReadException() {
    }

    public ExcelPoiReadException(String message) {
        super(message);
    }

    public ExcelPoiReadException(Exception e) {
        super(e);
    }

    public ExcelPoiReadException(String message, Exception e) {
        super(message, e);
    }
}
