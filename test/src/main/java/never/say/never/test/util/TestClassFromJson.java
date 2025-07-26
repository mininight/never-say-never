/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.test.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-11
 */
public class TestClassFromJson {

    public static void main(String[] args) throws Exception {
        JSONObject jsonObject = JSON.parseObject(StreamUtils.copyToString(
                new ClassPathResource("test.json").getInputStream(), StandardCharsets.UTF_8));
        jsonObject.forEach((key, val) -> {
            if (val instanceof Number) {
                System.out.println("private Long " + key + ";");
                return;
            }
            if (val instanceof Map) {
                System.out.println("private JSONObject " + key + ";");
                return;
            }
            if (val instanceof List) {
                System.out.println("private JSONArray " + key + ";");
                return;
            }
            System.out.println("private String " + key + ";");
        });
    }
}
