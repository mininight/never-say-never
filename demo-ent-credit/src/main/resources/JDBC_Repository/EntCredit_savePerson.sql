INSERT INTO person (
    personId,
    personName,
    json_str
) VALUES (
    :personId,
    :personName,
    :json_str
) ON DUPLICATE KEY UPDATE
    json_str = :json_str