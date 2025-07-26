/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.test.util;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-07-28
 */
public class UnicodeChineseUtil {

    public static void main(String[] args) {
        String unicodeStr = "\\u5468\\u6625\\u6d9b";
        String chineseStr = unicodeToChinese(unicodeStr);
        System.out.println(chineseStr);
//        System.out.println(chineseToUnicode("芝麻开花居民服务有限公司"));
//        System.out.println(JSON.toJSONString(ImmutableMap.of("keyword","山东")));
    }

    public static String unicodeToChinese(String unicodeStr) {
        StringBuilder chinese = new StringBuilder();
        String[] strs = unicodeStr.split("\\\\u");
        for (int i = 1; i < strs.length; i++) {
            char c = (char) Integer.parseInt(strs[i], 16);
            chinese.append(c);
        }
        return chinese.toString();
    }

    public static String chineseToUnicode(String chinese) {
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            unicode.append("\\u").append(Integer.toHexString(chinese.charAt(i)));
        }
        return unicode.toString();
    }
}
