package never.say.never.test.xml;

/**
 * Xml元素路径异常
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-21
 */
public class XmlElementPathException extends XmlException {
    private static final long serialVersionUID = -8613913164530965033L;

    public XmlElementPathException(String msg) {
        super(msg);
    }

    public XmlElementPathException(String msg, Throwable th) {
        super(msg, th);
    }
}
