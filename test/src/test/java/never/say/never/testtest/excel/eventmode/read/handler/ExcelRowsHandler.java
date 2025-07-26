package never.say.never.testtest.excel.eventmode.read.handler;

import never.say.never.testtest.excel.ExcelPoiStopReadingVoluntarilyException;
import never.say.never.testtest.excel.eventmode.read.xml.*;
import never.say.never.testtest.excel.util.func.HandyConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.model.SharedStrings;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Objects;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-14
 */
public class ExcelRowsHandler extends DefaultHandler {

    private final SharedStrings sharedStrings;
    private final HandyConsumer<ExcelXmlRow> rowConsumer;
    private final StringBuilder characters = new StringBuilder();
    private final ExcelXmlRow nowRow = new ExcelXmlRow();
    private final ExcelXmlCell nowCell = new ExcelXmlCell();
    private final StandardXmlElement nowElement = new StandardXmlElement();

    public ExcelRowsHandler(SharedStrings sharedStrings, HandyConsumer<ExcelXmlRow> rowConsumer) {
        this.sharedStrings = sharedStrings;
        this.rowConsumer = rowConsumer;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        characters.setLength(0);
        nowElement.reset();
        nowElement.setQName(qName);
        int len = attributes.getLength();
        for (int i = 0; i < len; i++) {
            String attrName = attributes.getQName(i);
            String attrValue = attributes.getValue(i);
            nowElement.addAttribute(attrName, attrValue);
        }
        nowElement.setState(XmlReadState.ELEMENT_BEGIN);
        switch (ExcelXmlElementKind.of(qName)) {
            case CELL:
                nowCell.reset();
                nowCell.setRow(nowRow);
                nowCell.applyMetadata(nowElement);
                break;
            case ROW:
                nowRow.reset();
                nowRow.applyMetadata(nowElement);
                break;
            default:
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (ExcelXmlElementKind.of(nowElement.getQName()) == ExcelXmlElementKind.CELL_TXT) {
            characters.append(ch, start, length);
        }
    }

    @Override
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        if (Objects.equals(nowElement.getQName(), qName)) {
            nowElement.setState(XmlReadState.ELEMENT_END);
        }
        ExcelXmlElementKind elementType = ExcelXmlElementKind.of(qName);
        switch (elementType) {
            case CELL_TXT:
                String content = characters.toString();
                String contentType = nowCell.getAttributes().get("t");
                if ("s".equalsIgnoreCase(contentType) && StringUtils.isNumeric(content)) {
                    content = sharedStrings.getItemAt(Integer.parseInt(content)).getString();
                }
                nowCell.setValue(content);
                break;
            case CELL:
                nowCell.setState(XmlReadState.ELEMENT_END);
                ExcelXmlCell preparedCell = nowCell.makeCopy();
                nowRow.addChild(preparedCell);
                break;
            case ROW:
                nowRow.setState(XmlReadState.ELEMENT_END);
                break;
            default:
                break;
        }
        // consume row data
        if (elementType == ExcelXmlElementKind.ROW && !rowConsumer.goon(nowRow)) {
            throw new ExcelPoiStopReadingVoluntarilyException("Manually stopped reading");
        }
    }

    @Override
    public void endDocument() throws SAXException {
        characters.setLength(0);
        nowCell.reset();
        nowRow.reset();
        nowElement.reset();
    }
}
