package never.say.never.test.xml;

import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamReader;

/**
 * Xml元素收集器
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-09
 */
public interface XmlElementCollector<T extends XmlElement> extends AutoCloseable {

    /**
     * 收集器初始化
     */
    default void init() {
        // do nothing
    }

    /**
     * Xml事件
     *
     * @param eventType 事件类型
     * @param reader    xml读取器
     */
    default void onEvent(int eventType, XMLStreamReader reader) {
        // do nothing
    }

    /**
     * 预构造新的xml元素
     *
     * @param depth 深度
     * @param name  元素名称
     * @return 新元素
     */
    T prepareNewElement(int depth, String name);

    /**
     * 匹配元素
     *
     * @param xmlElementName 元素名称
     * @param xmlElementPath 元素路径
     * @param reader         解析器
     * @return 处理模式
     */
    XmlElementPath.AcceptMode match(String xmlElementName, XmlElementPath xmlElementPath, XMLStreamReader2 reader);

    /**
     * xml元素收集完成回调
     *
     * @param xmlElement xml元素
     */
    void onTake(T xmlElement);

    /**
     * 全部收集完成回调
     */
    default void onComplete() {
        // do nothing
    }

    /**
     * 关闭收集器
     *
     * @throws Exception 异常
     */
    @Override
    default void close() throws Exception {
        // do nothing
    }
}
