package never.say.never.test.xml;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 不可变的Xml元素路径
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-21
 */
@Getter
public class ImmutableXmlElementPath implements XmlElementPath {

    private final String path;

    private final List<String> tokens;

    private final String latestToken;

    public ImmutableXmlElementPath(String path) {
        if (path == null || StringUtils.isBlank(path)) {
            throw new XmlElementPathException("Illegal xml node path:" + path);
        }
        this.tokens = Arrays.stream(path.trim().split(PATH_TOKEN_SEPARATOR_REGEX))
                .filter(token -> StringUtils.isNotBlank(token) && !token.contains(PATH_TOKEN_SEPARATOR))
                .collect(Collectors.toList());
        if (tokens.isEmpty()) {
            throw new XmlElementPathException("Illegal xml node path:" + path);
        }
        this.latestToken = tokens.get(tokens.size() - 1);
        this.path = path;
    }

    @Override
    public int indexOf(String str) {
        return path.indexOf(str);
    }

    @Override
    public boolean sameAs(String path) {
        return this.path.equals(path);
    }
}