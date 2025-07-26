package never.say.never.test.xml;

import never.say.never.test.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.stax2.XMLStreamReader2;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Xml元素收集器基类
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-09
 */
public abstract class AbstractXmlElementCollector<T extends XmlElement> implements XmlElementCollector<T> {

    private List<ImmutableXmlElementPath> includeXmlElPaths;
    private List<ImmutableXmlElementPath> excludeXmlElPaths;
    private boolean needPathIgnore = false;
    private boolean miniStructure = false;

    public AbstractXmlElementCollector() {
    }

    public AbstractXmlElementCollector(List<String> includeXmlElPaths) {
        this(includeXmlElPaths, null, true);
    }

    public AbstractXmlElementCollector(List<String> includeXmlElPaths, boolean miniStructure) {
        this(includeXmlElPaths, null, miniStructure);
    }

    public AbstractXmlElementCollector(List<String> includeXmlElPaths, List<String> excludeXmlElPaths) {
        this(includeXmlElPaths, excludeXmlElPaths, true);
    }

    public AbstractXmlElementCollector(List<String> includeXmlElPaths, List<String> excludeXmlElPaths,
                                       boolean miniStructure) {
        this.includeXmlElPaths = transformXmlElementPaths(includeXmlElPaths);
        this.excludeXmlElPaths = transformXmlElementPaths(excludeXmlElPaths);
        this.miniStructure = miniStructure;
    }

    @Override
    public void init() {
        this.needPathIgnore = CollectionUtil.isNotEmpty(this.includeXmlElPaths)
                || CollectionUtil.isNotEmpty(this.excludeXmlElPaths);
    }

    @Override
    public XmlElementPath.AcceptMode match(String xmlElementName, XmlElementPath xmlElementPath, XMLStreamReader2 reader) {
        if (!needPathIgnore) {
            return XmlElementPath.AcceptMode.PROCESS;
        }
        // Prioritize the paths that need to be included
        XmlElementPath.AcceptMode acceptMode = XmlElementPath.AcceptMode.PROCESS;
        if (CollectionUtil.isNotEmpty(includeXmlElPaths)) {
            acceptMode = XmlElementPath.AcceptMode.SKIP;
            for (ImmutableXmlElementPath includePath : includeXmlElPaths) {
                if (xmlElementPath.beginWith(includePath)) {
                    acceptMode = XmlElementPath.AcceptMode.PROCESS;
                    break;
                }
                if (includePath.beginWith(xmlElementPath)) {
                    acceptMode = miniStructure ? XmlElementPath.AcceptMode.DO_NOTHING : XmlElementPath.AcceptMode.PROCESS;
                    break;
                }
            }
        }
        // by exclude
        if (acceptMode != XmlElementPath.AcceptMode.SKIP && CollectionUtil.isNotEmpty(excludeXmlElPaths)) {
            for (ImmutableXmlElementPath excludePath : excludeXmlElPaths) {
                if (xmlElementPath.beginWith(excludePath)) {
                    acceptMode = XmlElementPath.AcceptMode.SKIP;
                    break;
                }
            }
        }
        return acceptMode;
    }

    protected List<ImmutableXmlElementPath> transformXmlElementPaths(List<String> paths) {
        if (CollectionUtil.isEmpty(paths)) {
            return null;
        }
        return paths.stream().filter(StringUtils::isNotBlank).distinct()
                .map(ImmutableXmlElementPath::new)
                .sorted(Comparator.comparingInt(path -> path.getTokens().size()))
                .collect(Collectors.toList());
    }

}
