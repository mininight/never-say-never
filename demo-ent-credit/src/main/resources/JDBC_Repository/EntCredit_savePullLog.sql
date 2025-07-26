INSERT IGNORE INTO pull_log (
    sourceKey,
    channel,
    type,
    finished,
    level,
    begin_time,
    end_time
) VALUES (
    :sourceKey,
    :channel,
    :type,
    :finished,
    :level,
    :begin_time,
    :end_time
)
