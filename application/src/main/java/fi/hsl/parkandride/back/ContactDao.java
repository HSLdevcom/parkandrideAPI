// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import com.querydsl.core.Tuple;
import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.postgresql.PostgreSQLQuery;
import com.querydsl.sql.postgresql.PostgreSQLQueryFactory;
import fi.hsl.parkandride.back.sql.QContact;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

public class ContactDao implements ContactRepository {

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    public static final String CONTACT_ID_SEQ = "contact_id_seq";

    private static final SimpleExpression<Long> contactIdNextval = SQLExpressions.nextval(CONTACT_ID_SEQ);

    private static QContact qContact = QContact.contact;

    private static MultilingualStringMapping nameMapping = new MultilingualStringMapping(qContact.nameFi, qContact.nameSv, qContact.nameEn);

    private static MultilingualStringMapping openingHoursMapping = new MultilingualStringMapping(qContact.openingHoursFi, qContact.openingHoursSv,
            qContact.openingHoursEn);

    private static MultilingualStringMapping infoMapping = new MultilingualStringMapping(qContact.infoFi, qContact.infoSv, qContact.infoEn);

    private static AddressMapping addressMapping = new AddressMapping(qContact);

    private static MappingProjection<Contact> contactMapping = new MappingProjection<Contact>(Contact.class, qContact.all()) {
        @Override
        protected Contact map(Tuple row) {
            Long id = row.get(qContact.id);
            if (id == null) {
                return null;
            }
            Contact contact = new Contact();
            contact.id = id;
            contact.operatorId = row.get(qContact.operatorId);
            contact.email = row.get(qContact.email);
            contact.phone = row.get(qContact.phone);
            contact.name = nameMapping.map(row);
            contact.address = addressMapping.map(row);
            contact.openingHours = openingHoursMapping.map(row);
            contact.info = infoMapping.map(row);
            return contact;
        }
    };

    private final PostgreSQLQueryFactory queryFactory;

    public ContactDao(PostgreSQLQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalWrite
    public long insertContact(Contact contact) {
        return insertContact(contact, queryFactory.query().select(contactIdNextval).fetchOne());
    }

    @TransactionalWrite
    public long insertContact(Contact contact, Long contactId) {
        SQLInsertClause insert = queryFactory.insert(qContact);
        insert.set(qContact.id, contactId);
        populate(contact, insert);
        insert.execute();
        return contactId;
    }

    @Override
    @TransactionalRead
    public Contact getContact(long contactId) {
        return getContact(contactId, false);
    }

    @Override
    @TransactionalRead
    public Contact getContactForUpdate(long contactId) {
        return getContact(contactId, true);
    }

    private Contact getContact(long contactId, boolean forUpdate) {
        PostgreSQLQuery<Contact> qry = queryFactory.from(qContact).select(contactMapping).where(qContact.id.eq(contactId));
        if (forUpdate) {
            qry.forUpdate();
        }
        return qry.fetchOne();
    }

    @Override
    @TransactionalWrite
    public void updateContact(long contactId, Contact contact) {
        SQLUpdateClause update = queryFactory.update(qContact).where(qContact.id.eq(contactId));
        populate(contact, update);
        if (update.execute() != 1) {
            notFound(contactId);
        }
    }

    private void notFound(long contactId) {
        throw new NotFoundException("Contact by id '%s'", contactId);
    }

    @Override
    @TransactionalRead
    public SearchResults<Contact> findContacts(ContactSearch search) {
        PostgreSQLQuery<Contact> qry = queryFactory.from(qContact).select(contactMapping);
        qry.limit(search.getLimit() + 1);
        qry.offset(search.getOffset());

        if (search.getIds() != null && !search.getIds().isEmpty()) {
            qry.where(qContact.id.in(search.getIds()));
        }
        if (search.getOperatorId() != null) {
            qry.where(qContact.operatorId.isNull().or(qContact.operatorId.eq(search.getOperatorId())));
        }

        orderBy(search.getSort(), qry);
        return SearchResults.of(qry.fetch(), search.getLimit());
    }

    private void populate(Contact contact, StoreClause<?> store) {
        store
                .set(qContact.operatorId, contact.operatorId)
                .set(qContact.phone, contact.phone)
                .set(qContact.email, contact.email);

        nameMapping.populate(contact.name, store);
        addressMapping.populate(contact.address, store);
        openingHoursMapping.populate(contact.openingHours, store);
        infoMapping.populate(contact.info, store);
    }

    private void orderBy(Sort sort, PostgreSQLQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.getBy(), DEFAULT_SORT.getBy())) {
            case "name.fi": sortField = qContact.nameFi.lower(); break;
            case "name.sv": sortField = qContact.nameSv.lower(); break;
            case "name.en": sortField = qContact.nameEn.lower(); break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.getDir())) {
            qry.orderBy(sortField.desc(), qContact.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qContact.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }

}
