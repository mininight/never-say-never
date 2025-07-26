package never.say.never.testtest.excel.eventmode.read.xml;

import com.alibaba.fastjson2.JSONObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
public class ExcelXmlRow extends ExcelXmlElement<ExcelXmlCell> {

    private Integer index;

    public ExcelXmlRow() {
        super();
    }

    public ExcelXmlRow(int childrenCount) {
        super(childrenCount);
    }

    @Override
    public void setState(XmlReadState state) {
        super.setState(state);
        if (state == XmlReadState.ELEMENT_BEGIN) {
            index = Integer.parseInt(getAttributes().get("r")) - 1;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ExcelXmlRow makeCopy() {
        ExcelXmlRow newOne = super.makeCopy(false);
        newOne.setIndex(getIndex());
        newOne.setState(getState());
        return newOne;
    }

    public <T> T toJavaObject(Function<ExcelXmlCell, String> headerMapper, Class<T> clazz) {
        JSONObject jsonObject = new JSONObject();
        supply(headerMapper, null, jsonObject::put);
        return jsonObject.toJavaObject(clazz);
    }

    public Map<String, Object> toDataMap() {
        return toDataMap(null, null);
    }

    public Map<String, Object> toDataMap(Function<ExcelXmlCell, String> headerMapper, Function<String, Object> valueMapper) {
        Map<String, Object> map = new LinkedHashMap<>();
        supply(headerMapper, valueMapper, map::put);
        return map;
    }

    public void supply(Function<ExcelXmlCell, String> headerMapper, Function<String, Object> valueMapper,
                       BiConsumer<String, Object> consumer) {
        for (int i = 0; i < getChildList().size(); i++) {
            ExcelXmlCell cell = getChildList().get(i);
            String content = cell.getValue();
            String key = headerMapper == null ? cell.getPosition() : headerMapper.apply(cell);
            Object value = valueMapper == null ? content : valueMapper.apply(content);
            consumer.accept(key, value);
        }
    }
}
