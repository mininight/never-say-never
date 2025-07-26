package never.say.never.testtest.excel;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
public class ExcelPoiStopReadingVoluntarilyException extends ExcelPoiReadException {

    public ExcelPoiStopReadingVoluntarilyException() {
    }

    public ExcelPoiStopReadingVoluntarilyException(String message) {
        super(message);
    }

    public ExcelPoiStopReadingVoluntarilyException(Exception e) {
        super(e);
    }

    public ExcelPoiStopReadingVoluntarilyException(String message, Exception e) {
        super(message, e);
    }
}
