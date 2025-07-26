INSERT IGNORE INTO company_stock (
    compId, stockId, stockName,
    personId, haveCompNum, subRate,
    subMoney, subDate
) VALUES (
    :compId, :stockId, :stockName,
    :personId, :haveCompNum, :subRate,
    :subMoney, :subDate
)