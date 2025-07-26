package never.say.never.testtest.excel.eventmode.read.xml;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellType;

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
public class ExcelXmlCell extends ExcelXmlElement<StandardXmlElement>{

    private Integer index;

    private String position;

    private ExcelXmlRow row;

    private String value;

    private CellType type;

    private String column;

    public ExcelXmlCell() {
        super();
    }

    public ExcelXmlCell(int childrenCount) {
        super(childrenCount);
    }

    @Override
    public void setState(XmlReadState state) {
        super.setState(state);
        if (state == XmlReadState.ELEMENT_BEGIN) {
            if (row == null) {
                throw new IllegalStateException("No current row found");
            }
            position = getAttributes().get("r");
            index = row.getChildList().size();
        }

        if (state == XmlReadState.ELEMENT_END) {
            if (row == null) {
                throw new IllegalStateException("No current row found");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ExcelXmlCell makeCopy() {
        ExcelXmlCell newOne = super.makeCopy(false);
        newOne.setRow(getRow());
        newOne.setIndex(getIndex());
        newOne.setPosition(getPosition());
        newOne.setValue(getValue());
        newOne.setState(getState());
        return newOne;
    }

    public String getValue() {
        if (getState() != XmlReadState.ELEMENT_END) {
            throw new IllegalStateException("Cell object not ready");
        }
        return value;
    }
}
