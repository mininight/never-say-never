package never.say.never.test.xml;

import lombok.Getter;
import lombok.Setter;

import javax.xml.stream.XMLStreamReader;

/**
 * Xml读取上下文
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-06-04
 */
@Getter
@Setter
public class XmlReadingContext {
    private static final ThreadLocal<XmlReadingContext> HOLDER = new ThreadLocal<>();
    private int eventType;
    private XMLStreamReader reader;
    private String rootPath;
    private XmlElement rootElement;
    private XmlElement parentElement;
    private XmlElement preElement;
    private XmlElement nowElement;
    private final ScalableXmlElementPath elementPath = new ScalableXmlElementPath();
    private final StringBuilder contentBuilder = new StringBuilder();

    public static void init() {
        if (HOLDER.get() != null) {
            throw new XmlReadException("上下文已初始化");
        }
        HOLDER.set(new XmlReadingContext());
    }

    public static void destroy() {
        HOLDER.remove();
    }

    public void onEvent(int eventType, XMLStreamReader reader) {
        this.eventType = eventType;
        this.reader = reader;
    }

    public static XmlReadingContext current() {
        return HOLDER.get();
    }

    public static String rootPath() {
        return current().rootPath;
    }

    public static XmlElement rootElement() {
        return current().rootElement;
    }

    public static XmlElement parentElement() {
        return current().parentElement;
    }

    public static XmlElement preElement() {
        return current().preElement;
    }

    public static XmlElement nowElement() {
        return current().nowElement;
    }

    public static ScalableXmlElementPath elementPath() {
        return current().elementPath;
    }

    public static StringBuilder contentBuilder() {
        return current().contentBuilder;
    }

    public static XmlElement findParent(int nowDepth) {
        XmlElement preElement = preElement();
        if (preElement == null) {
            return null;
        } else {
            int preDepth = preElement.getDepth();
            if (preDepth < nowDepth) {
                return preElement;
            } else if (preDepth == nowDepth) {
                return preElement.getParent();
            } else {
                XmlElement hasParentEl = preElement;
                int hasParentElDepth = hasParentEl.getDepth();
                while (hasParentElDepth > nowDepth) {
                    hasParentEl = hasParentEl.getParent();
                    if (hasParentEl == null) {
                        return null;
                    }
                    hasParentElDepth = hasParentEl.getDepth();
                }
                return hasParentEl.getParent();
            }
        }
    }
}
