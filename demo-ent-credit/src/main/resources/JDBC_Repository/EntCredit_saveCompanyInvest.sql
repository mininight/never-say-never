insert ignore into company_invest (
    compId,
    to_compId,
    compName,
    to_compName
) values (
    :compId,
    :to_compId,
    :compName,
    :to_compName
)