package never.say.never.test.xml;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.Objects;

import static never.say.never.test.xml.XmlElementPath.AcceptMode.DO_NOTHING;
import static never.say.never.test.xml.XmlElementPath.AcceptMode.SKIP;
import static never.say.never.test.xml.XmlReadingContext.*;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-07
 */
public class XmlStreamToolkit {

    public static final XmlStreamToolkit INSTANCE = new XmlStreamToolkit();

    private static final XMLInputFactory2 XML_INPUT_FACTORY = (XMLInputFactory2) XMLInputFactory2.newFactory();

    public <T extends XmlElement> void readFrom(InputStream in, XmlElementCollector<T> elementCollector) throws Exception {
        int eventType;
        String nowElementName;
        int nowDepth;
        XmlElement currentElement;
        XmlElementPath.AcceptMode acceptMode;
        try (in; elementCollector) {
            XMLStreamReader2 reader = (XMLStreamReader2) XML_INPUT_FACTORY.createXMLStreamReader(in);
            XmlReadingContext.init();
            elementCollector.init();
            while (reader.hasNext()) {
                eventType = reader.next();
                XmlReadingContext.current().onEvent(eventType, reader);
                elementCollector.onEvent(eventType, reader);
                switch (eventType) {
                    case XMLEvent.START_ELEMENT:
                        contentBuilder().setLength(0);
                        nowElementName = reader.getLocalName();
                        nowDepth = reader.getDepth();
                        elementPath().addTokenToLast(nowElementName);
                        acceptMode = elementCollector.match(nowElementName, elementPath(), reader);
                        if (acceptMode == SKIP) {
                            elementPath().removeTokenLatest();
                            reader.skipElement();
                            break;
                        }
                        if (acceptMode == DO_NOTHING) {
                            break;
                        }
                        currentElement = elementCollector.prepareNewElement(nowDepth, nowElementName);
                        current().setNowElement(currentElement);
                        if (rootPath() == null || elementPath().sameAs(rootPath())) {
                            current().setRootElement(currentElement);
                            if (rootPath() == null) {
                                current().setRootPath(elementPath().getPath().toString());
                            }
                        }
                        // set now element as the previous
                        current().setPreElement(currentElement);
                        break;
                    case XMLEvent.CHARACTERS:
                        contentBuilder().append(reader.getText());
                        break;
                    case XMLEvent.END_ELEMENT:
                        currentElement = nowElement();
                        if (currentElement != null) {
                            // improve the content of the current element
                            String endElementName = reader.getLocalName();
                            int endElementDepth = reader.getDepth();
                            while (!(Objects.equals(endElementName, currentElement.getName())
                                    && endElementDepth == currentElement.getDepth())) {
                                currentElement = currentElement.getParent();
                            }
                            current().setNowElement(currentElement);
                            currentElement.setContent(contentBuilder().toString());
                        }
                        contentBuilder().setLength(0);
                        // take and reset root xml object
                        if (rootPath() != null && elementPath().sameAs(rootPath())) {
                            elementCollector.onTake((T) rootElement());
                            current().setRootElement(null);
                            current().setNowElement(null);
                        }
                        // set now element as the previous
                        current().setPreElement(nowElement());
                        // path regression to parent
                        elementPath().removeTokenLatest();
                        break;
                    case XMLEvent.END_DOCUMENT:
                        contentBuilder().setLength(0);
                        elementPath().reset();
                        elementCollector.onComplete();
                        break;
                    default:
                        break;
                }
            }
            reader.closeCompletely();
        } finally {
            XmlReadingContext.destroy();
        }
    }
}
