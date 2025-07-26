/*
 *
 *  *  Copyright (c) 2018-2022 the original author or authors.
 *  *  Author: 861828396@qq.com
 *
 */

package never.say.never.demo.ent_credit.entity;

import lombok.Data;
import never.say.never.demo.ent_credit.http.HttpApiSLB;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static never.say.never.demo.ent_credit.enums.SourceChannel.ID_DELIMITER;

/**
 * @author Ivan
 * @version 1.0.0
 * @date 2024-08-03
 */
@Data
public class Person implements HttpApiSLB.Res {
    private String personId;
    private String personName;
    private PersonHead personhead;
    private String json_str;
    private List<PersonCompany> companies;
    private SourceId sourceId;

    @Data
    public static class PersonHead {
        private String personName;
        private Introduction introduction;
    }

    @Data
    public static class Introduction {
        private List<CompItem> legalPerson;
        private List<CompItem> isstockholde;
        private List<CompItem> director;
    }

    @Data
    public static class CompItem {
        private String pid;
        private String name;
    }

    public final boolean basicValid() {
        return StringUtils.isNotBlank(personId) && StringUtils.isNotBlank(personName);
    }

    @Override
    public boolean O_K() {
        return basicValid();
    }

    public final boolean hasPersonIntro() {
        if (personhead == null || StringUtils.isBlank(personhead.getPersonName())) {
            return false;
        }
        setPersonName(personhead.getPersonName());
        return true;
    }

    public void mergeDetail(Person person) {
        if (person == null) {
            return;
        }
        if (person.getPersonhead() == null || person.getPersonhead().getIntroduction() == null) {
            return;
        }
        if (getPersonhead() == null) {
            setPersonId(person.getPersonId());
            setPersonName(person.getPersonName());
            setPersonhead(person.getPersonhead());
            return;
        }
        if (getPersonhead().getIntroduction() == null) {
            getPersonhead().setIntroduction(person.getPersonhead().getIntroduction());
            return;
        }
        Introduction introduction = getPersonhead().getIntroduction();
        Introduction newIntroduction = person.getPersonhead().getIntroduction();
        List<CompItem> newDirector = newIntroduction.getDirector();
        List<CompItem> newIsstockholde = newIntroduction.getIsstockholde();
        List<CompItem> newLegalPerson = newIntroduction.getLegalPerson();
        //
        if (CollectionUtils.isEmpty(introduction.getDirector())) {
            introduction.setDirector(newDirector);
        } else {
            for (CompItem compItem : newDirector) {
                if (introduction.getDirector().contains(compItem)) {
                    continue;
                }
                introduction.getDirector().add(compItem);
            }
        }
        //
        if (CollectionUtils.isEmpty(introduction.getIsstockholde())) {
            introduction.setIsstockholde(newIsstockholde);
        } else {
            for (CompItem compItem : newIsstockholde) {
                if (introduction.getIsstockholde().contains(compItem)) {
                    continue;
                }
                introduction.getIsstockholde().add(compItem);
            }
        }
        //
        if (CollectionUtils.isEmpty(introduction.getLegalPerson())) {
            introduction.setLegalPerson(newLegalPerson);
        } else {
            for (CompItem compItem : newLegalPerson) {
                if (introduction.getLegalPerson().contains(compItem)) {
                    continue;
                }
                introduction.getLegalPerson().add(compItem);
            }
        }
    }

    public String errId() {
        if (personName == null) {
            personName = "";
        }
        if (personId == null) {
            personId = "";
        }
        return personName + ID_DELIMITER + personId;
    }

    public static Person asParam(){
        return new Person();
    }

    public Person id(String id){
        setPersonId(id);
        return this;
    }

    public Person name(String name){
        setPersonName(name);
        return this;
    }
}
