package never.say.never.test.xml;

import org.apache.commons.lang3.StringUtils;

import javax.xml.stream.XMLStreamReader;
import java.util.List;
import java.util.function.Consumer;

import static never.say.never.test.xml.XmlReadingContext.current;
import static never.say.never.test.xml.XmlReadingContext.findParent;

/**
 * Xml对象收集器
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-06-04
 */
public class XmlObjectCollector extends AbstractXmlElementCollector<XmlObject> {

    private Consumer<XmlObject> consumer;

    public XmlObjectCollector() {
    }

    public XmlObjectCollector(List<String> includeXmlElPaths) {
        super(includeXmlElPaths);
    }

    public XmlObjectCollector(List<String> includeXmlElPaths, boolean miniStructure) {
        super(includeXmlElPaths, miniStructure);
    }

    public XmlObjectCollector(List<String> includeXmlElPaths, List<String> excludeXmlElPaths) {
        super(includeXmlElPaths, excludeXmlElPaths);
    }

    public XmlObjectCollector(List<String> includeXmlElPaths, List<String> excludeXmlElPaths, boolean miniStructure) {
        super(includeXmlElPaths, excludeXmlElPaths, miniStructure);
    }

    public void onTake(Consumer<XmlObject> consumer) {
        this.consumer = consumer;
    }

    @Override
    public XmlObject prepareNewElement(int depth, String name) {
        XmlObject xmlObject = new XmlObject((XmlObject) findParent(depth), depth, name);
        XMLStreamReader reader = current().getReader();
        int attrCount = reader.getAttributeCount();
        if (attrCount > 0) {
            for (int i = 0; i < attrCount; i++) {
                String attrNamePrefix = reader.getAttributePrefix(i);
                String attrName;
                if (StringUtils.isNotEmpty(attrNamePrefix)) {
                    attrName = attrNamePrefix + ":" + reader.getAttributeLocalName(i);
                } else {
                    attrName = reader.getAttributeLocalName(i);
                }
                String attrValue = reader.getAttributeValue(i);
                xmlObject.addAttribute(attrName, attrValue);
            }
        }
        return xmlObject;
    }

    @Override
    public void onTake(XmlObject xmlElement) {
        this.consumer.accept(xmlElement);
    }

}
