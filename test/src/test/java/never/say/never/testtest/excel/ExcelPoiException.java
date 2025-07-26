package never.say.never.testtest.excel;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
public class ExcelPoiException extends RuntimeException {

    public ExcelPoiException() {
    }

    public ExcelPoiException(String message) {
        super(message);
    }

    public ExcelPoiException(Exception e) {
        super(e);
    }

    public ExcelPoiException(String message, Exception e) {
        super(message, e);
    }
}
