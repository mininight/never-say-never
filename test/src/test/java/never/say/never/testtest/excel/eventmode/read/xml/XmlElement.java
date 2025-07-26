package never.say.never.testtest.excel.eventmode.read.xml;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-15
 */
public interface XmlElement {

    String getQName();

    Map<String, String> getAttributes();

    default void addAttribute(String attrName, String attrValue) {
        if (StringUtils.isEmpty(attrName)) {
            return;
        }
        getAttributes().put(attrName, attrValue);
    }

    List<? extends XmlElement> getChildList();

    XmlReadState getState();


    void applyMetadata(XmlElement xmlElement);

    void reset();

}
