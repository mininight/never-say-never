package never.say.never.test.xml;

/**
 * Xml异常
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-21
 */
public class XmlException extends RuntimeException {
    private static final long serialVersionUID = -3346822608942740564L;

    public XmlException() {
    }

    public XmlException(String message) {
        super(message);
    }

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlException(Throwable cause) {
        super(cause);
    }

    public XmlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
