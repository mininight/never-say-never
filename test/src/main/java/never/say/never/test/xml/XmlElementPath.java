package never.say.never.test.xml;

import never.say.never.test.util.CollectionUtil;

import java.util.List;

/**
 * Xml元素路径
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-21
 */
public interface XmlElementPath {

    String PATH_TOKEN_SEPARATOR = ".";

    String PATH_TOKEN_SEPARATOR_REGEX = "\\.";

    CharSequence getPath();

    List<String> getTokens();

    String getLatestToken();

    default int getDepth() {
        return getTokens().size();
    }

    int indexOf(String str);

    default boolean sameAs(String path) {
        return CharSequence.compare(getPath(), path) == 0;
    }

    default boolean beginWith(XmlElementPath xmlElementPath) {
        if (xmlElementPath.getPath() instanceof Appendable) {
            return CollectionUtil.startsWith(getTokens(), xmlElementPath.getTokens());
        }
        return indexOf(xmlElementPath.getPath().toString()) == 0;
    }

    enum AcceptMode {
        /**
         * 跳过
         */
        SKIP,
        /**
         * 不处理
         */
        DO_NOTHING,
        /**
         * 处理
         */
        PROCESS
    }
}
