package never.say.never.test.xml;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * TODO
 *
 * @author Ivan
 * @version 1.0.0
 * @date 2023-05-07
 */
public class XmlObject extends JSONObject implements XmlElement {
    private static final long serialVersionUID = 1L;
    public static final String KEY_DEPTH = "$DEPTH$";
    public static final String KEY_NAME = "$NAME$";
    public static final String KEY_CONTENT = "$CONTENT$";
    private final transient XmlObject $PARENT$;

    public XmlObject() {
        this(null);
    }

    private XmlObject(Integer initCapacity) {
        this(null, null, null, initCapacity);
    }

    public XmlObject(XmlObject parent, Integer depth, String name) {
        this(parent, depth, name, null);
    }

    @SuppressWarnings("unchecked")
    private XmlObject(XmlObject parent, Integer depth, String name, Integer initCapacity) {
        super(initCapacity == null || initCapacity < 0 ? new LinkedHashMap<>() : new LinkedHashMap<>(initCapacity));
        $PARENT$ = parent;
        put(KEY_DEPTH, depth == null || depth < 0 ? 0 : depth);
        put(KEY_NAME, name);
        if ($PARENT$ != null) {
            Object oldObject = $PARENT$.putIfAbsent(name, this);
            if (oldObject instanceof XmlObject) {
                $PARENT$.put(name, Lists.newArrayList(oldObject, this));
            }
            if (oldObject instanceof List) {
                List<XmlObject> oldObjectList = (List<XmlObject>) oldObject;
                oldObjectList.add(this);
            }
        }
    }

    @Override
    public XmlObject getParent() {
        return $PARENT$;
    }

    @Override
    public int getDepth() {
        return getIntValue(KEY_DEPTH);
    }

    @Override
    public String getName() {
        return getString(KEY_NAME);
    }

    @Override
    public String getContent() {
        return getString(KEY_CONTENT);
    }

    @Override
    public void setContent(String content) {
        put(KEY_CONTENT, content);
    }

    @Override
    public void addAttribute(String key, Object value) {
        put(key, value);
    }
}
