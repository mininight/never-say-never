/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.test.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-24
 */
public class TestJSONParamFromJson {

    public static void main(String[] args) {
        String json = "{\"isSecondLevel\":false,\"pid\":\"0d87c96206d73f108aff4478b7e9d6cc\",\"startRow\":0,\"pageCurrent\":1,\"invInstoFrom\":\"\",\"invSubConAmFrom\":\"\",\"invSubConAmTo\":\"\",\"invInstoTo\":\"\",\"sort\":\"\",\"sortField\":\"\",\"pageSize\":10}";
        JSONObject object = JSON.parseObject(json);
        System.out.println("JSONObject body = new JSONObject();");
        for (String k : object.keySet()) {
            Object v = object.get(k);
            String vstr;
            if (v instanceof String) {
                vstr = "\"" + v + "\"";
            } else if (v == null) {
                vstr = "";
            } else {
                vstr = v.toString();
            }
            System.out.println("body.put(\"" + k + "\"," + vstr + ");");
        }
    }
}
