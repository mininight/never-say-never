/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

INSERT INTO source_id (
    id,
    name,
    channel,
    value,
    type,
    sign
) VALUES (
    :id,
    :name,
    :channel,
    :value,
    :type,
    :sign
) ON DUPLICATE KEY UPDATE
    sign = :sign
