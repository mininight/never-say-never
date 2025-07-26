INSERT IGNORE INTO pull_log_sub (
    sourceKey,
    parentKey,
    channel,
    type,
    level
) VALUES (
    :sourceKey,
    :parentKey,
    :channel,
    :type,
    :level
)