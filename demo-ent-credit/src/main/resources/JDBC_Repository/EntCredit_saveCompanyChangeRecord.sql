INSERT IGNORE INTO company_change_record (
    compId, date, fieldName,
    oldValue, newValue
) VALUES (
    :compId, :date, :fieldName,
    :oldValue, :newValue
)