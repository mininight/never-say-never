package never.say.never.testtest.excel.eventmode.read.xml;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-14
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ExcelXmlElement<T extends XmlElement> extends AbstractXmlElement<T> {

    private ExcelXmlElementKind kind = ExcelXmlElementKind.Noop;

    public ExcelXmlElement() {
        super();
    }

    public ExcelXmlElement(int childrenCount) {
        super(childrenCount);
    }

    public void setQName(String qName) {
        super.setQName(qName);
        kind = ExcelXmlElementKind.of(qName);
    }

    public <E extends ExcelXmlElement<T>> E makeCopy() {
        return makeCopy(true);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <E extends ExcelXmlElement<T>> E makeCopy(boolean copyState) {
        int childrenCount = getChildList().size();
        E newOne;
        if (childrenCount > 10) {
            newOne = (E) getClass().getDeclaredConstructor(int.class)
                    .newInstance(getChildList().size());
        } else {
            newOne = (E) getClass().getDeclaredConstructor().newInstance();
        }
        getChildList().forEach(newOne::addChild);
        newOne.setQName(getQName());
        newOne.setKind(getKind());
        if (copyState) {
            newOne.setState(getState());
        }
        return newOne;
    }

}
