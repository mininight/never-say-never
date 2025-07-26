package never.say.never.test.xml;

import lombok.Getter;

import java.util.LinkedList;

/**
 * 可变的Xml元素路径(用于解析时动态标记)
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-21
 */
@Getter
public class ScalableXmlElementPath implements XmlElementPath {

    private final StringBuilder path = new StringBuilder();

    private LinkedList<String> tokens = new LinkedList<>();

    private int len = 0;

    private String latestToken;

    private int lastTokenOffset = -1;

    @Override
    public int indexOf(String str) {
        return path.indexOf(str);
    }

    public void addTokenToLast(String pathToken) {
        lastTokenOffset = len;
        latestToken = pathToken;
        path.append(pathToken).append(PATH_TOKEN_SEPARATOR);
        tokens.addLast(pathToken);
        len = len + pathToken.length() + 1;
    }

    public void removeTokenLatest() {
        if (lastTokenOffset < 0 || latestToken == null || tokens.isEmpty()) {
            return;
        }
        path.delete(lastTokenOffset, len);
        tokens.removeLast();
        if (tokens.isEmpty()) {
            len = 0;
            lastTokenOffset = -1;
            latestToken = null;
        } else {
            len = lastTokenOffset;
            latestToken = tokens.getLast();
            lastTokenOffset = len - latestToken.length() - 1;
        }
    }

    public void reset() {
        path.setLength(0);
        tokens.clear();
        len = 0;
        latestToken = null;
        lastTokenOffset = -1;
    }

    @Override
    public boolean sameAs(String path) {
        if (len != path.length()) {
            return false;
        }
        return this.path.lastIndexOf(path) == 0;
    }
}
