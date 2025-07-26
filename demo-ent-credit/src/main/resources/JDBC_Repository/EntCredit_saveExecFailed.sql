insert ignore into exec_failed (
    id,
    type,
    method,
    err_msg,
    association_reqeust,
    level
) values (
    :id,
    :type,
    :method,
    :err_msg,
    :association_reqeust,
    :level
)