/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.util;

import lombok.Data;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-17
 */
@Data
public class KeyValuePair<K, V> {
    private K key;
    private V value;
}
