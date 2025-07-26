package never.say.never.test.xml;

/**
 * Xml元素
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-06-03
 */
public interface XmlElement {

    XmlElement getParent();

    int getDepth();

    String getName();

    void setContent(String content);

    String getContent();

    void addAttribute(String key, Object value);
}
