package never.say.never.test.xml;

/**
 * Xml读取异常
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-06-04
 */
public class XmlReadException extends XmlException {
    private static final long serialVersionUID = 7909021774493202183L;

    public XmlReadException() {
    }

    public XmlReadException(String message) {
        super(message);
    }

    public XmlReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlReadException(Throwable cause) {
        super(cause);
    }

    public XmlReadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
