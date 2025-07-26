package never.say.never.testtest.excel.eventmode.read.strings;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.poi.xssf.usermodel.XSSFRelation.NS_SPREADSHEETML;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-04-28
 * @see org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable
 */
public abstract class AbstractSharedStringsTable extends DefaultHandler implements SharedStrings, AutoCloseable {

    private final AtomicInteger idxMaker = new AtomicInteger(-1);

    protected final boolean includePhoneticRuns;

    /**
     * An integer representing the total count of strings in the workbook.
     */
    protected int count;

    /**
     * An integer representing the total count of unique strings in the Shared String Table.
     */
    protected int uniqueCount;

    //// ContentHandler methods ////
    protected StringBuilder characters;
    private boolean tIsOpen;
    private boolean inRPh;

    public AbstractSharedStringsTable() {
        this(true);
    }

    public AbstractSharedStringsTable(boolean includePhoneticRuns) {
        this.includePhoneticRuns = includePhoneticRuns;
    }

    public void readFrom(OPCPackage pkg) throws IOException, SAXException {
        ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        // Some workbooks have no shared strings table.
        if (!parts.isEmpty()) {
            PackagePart sstPart = parts.get(0);
            readFrom(sstPart.getInputStream());
        }
    }

    public void readFrom(PackagePart part) throws IOException, SAXException {
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws IOException, SAXException {
        // test if the file is empty, otherwise parse it
        PushbackInputStream pis = new PushbackInputStream(is, 1);
        int emptyTest = pis.read();
        if (emptyTest > -1) {
            pis.unread(emptyTest);
            InputSource sheetSource = new InputSource(pis);
            try {
                beforeParsing(sheetSource);
                XMLReader sheetParser = XMLHelper.newXMLReader();
                sheetParser.setContentHandler(this);
                sheetParser.parse(sheetSource);
            } catch (ParserConfigurationException e) {
                throw new SAXException("SAX parser appears to be broken - " + e.getMessage());
            }
        }
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public int getUniqueCount() {
        return this.uniqueCount;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (uri != null && !uri.equals(NS_SPREADSHEETML)) {
            return;
        }
        if ("sst".equals(localName)) {
            String count = attributes.getValue("count");
            if (count != null) this.count = Integer.parseInt(count);
            String uniqueCount = attributes.getValue("uniqueCount");
            if (uniqueCount != null) this.uniqueCount = Integer.parseInt(uniqueCount);
            onParsingStart();
        } else if ("si".equals(localName)) {
            characters.setLength(0);
            onTextStart();
        } else if ("t".equals(localName)) {
            tIsOpen = true;
        } else if ("rPh".equals(localName)) {
            inRPh = true;
            //append space...this assumes that rPh always comes after regular <t>
            if (includePhoneticRuns && characters.length() > 0) {
                characters.append(" ");
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (tIsOpen) {
            if (inRPh && includePhoneticRuns) {
                onTextCharacters(ch, start, length);
            } else if (!inRPh) {
                onTextCharacters(ch, start, length);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (uri != null && !uri.equals(NS_SPREADSHEETML)) {
            return;
        }
        if ("si".equals(localName)) {
            onTextEnd(idxMaker.incrementAndGet(), characters);
        } else if ("t".equals(localName)) {
            tIsOpen = false;
        } else if ("rPh".equals(localName)) {
            inRPh = false;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        characters.setLength(0);
        onParsingEnd(idxMaker.get());
    }

    protected void beforeParsing(InputSource sheetSource) {
        // do nothing
    }

    protected void onParsingStart() {
        characters = new StringBuilder(64);
    }

    protected void onTextStart() {
        // do nothing
    }

    protected void onTextCharacters(char[] ch, int start, int length) throws SAXException {
        characters.append(ch, start, length);
    }

    abstract void onTextEnd(int idx, StringBuilder characters);

    abstract void onParsingEnd(int idx);
}
