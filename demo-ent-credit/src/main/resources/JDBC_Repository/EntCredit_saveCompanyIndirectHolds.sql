insert ignore into company_indirect_holds (
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