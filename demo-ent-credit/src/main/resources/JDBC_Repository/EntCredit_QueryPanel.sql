#-- selectSourceIdsByValueAndType
select *
from source_id
where value = :value
  and type = :type;

#-- selectSourceIdsByNameAndType
select *
from source_id
where name = :name
  and type = :type;

#-- selectSourceIdsByIdAndChannelAndType
select *
from source_id
where id = :id
  and channel = :channel
  and type = :type;

#-- selectCompanyByName
select *
from company
where entName = :entName;

#-- selectCompanyStock
select *
from company_stock
where compId = :id;

#-- selectCompanyInvest
select *
from company_invest
where compId = :id;

#-- selectCompanyKeyPerson
select *
from company_key_person
where compId = :id;

#-- selectCompanyHolds
select *
from company_holds
where compId = :id;

#-- selectCompanyIndirectHolds
select *
from company_indirect_holds
where compId = :id;

#-- selectCompanyChangeRecord
select *
from company_change_record
where compId = :id;

#-- selectPersonCompanyList
select *
from person_company
where personId = :personId