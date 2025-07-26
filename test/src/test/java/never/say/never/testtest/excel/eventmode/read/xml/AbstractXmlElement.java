package never.say.never.testtest.excel.eventmode.read.xml;

import lombok.Data;

import java.util.*;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
@Data
public abstract class AbstractXmlElement<T extends XmlElement> implements XmlElement, Iterable<T> {
    private String qName;
    private XmlReadState state;
    private Map<String, String> attributes;
    private List<T> childList;

    public AbstractXmlElement() {
        this(0);
    }

    public AbstractXmlElement(int childrenCount) {
        this.childList = childrenCount < 10 ? new ArrayList<>() : new ArrayList<>(childrenCount);
        this.attributes = new LinkedHashMap<>();
    }

    public void addChild(T element) {
        getChildList().add(element);
    }

    @Override
    public Iterator<T> iterator() {
        return childList.iterator();
    }

    @Override
    public void applyMetadata(XmlElement xmlElement) {
        setQName(xmlElement.getQName());
        getAttributes().putAll(xmlElement.getAttributes());
        setState(xmlElement.getState());
    }

    @Override
    public void reset() {
        setQName(null);
        setState(null);
        if (getAttributes() != null) {
            getAttributes().clear();
        }
        if (getChildList() != null) {
            getChildList().clear();
        }
    }
}
