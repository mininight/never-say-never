INSERT IGNORE INTO company_key_person (
    compId, personId, personName,
    haveCompNum, positionTitle
) VALUES (
    :compId, :personId, :personName,
    :haveCompNum, :positionTitle
)