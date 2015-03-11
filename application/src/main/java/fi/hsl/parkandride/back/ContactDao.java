package fi.hsl.parkandride.back;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.back.sql.QContact;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;

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

    private final PostgresQueryFactory queryFactory;

    public ContactDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    @TransactionalWrite
    public long insertContact(Contact contact) {
        return insertContact(contact, queryFactory.query().singleResult(contactIdNextval));
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
        PostgresQuery qry = queryFactory.from(qContact).where(qContact.id.eq(contactId));
        if (forUpdate) {
            qry.forUpdate();
        }
        return qry.singleResult(contactMapping);
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
        PostgresQuery qry = queryFactory.from(qContact);
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        if (search.ids != null && !search.ids.isEmpty()) {
            qry.where(qContact.id.in(search.ids));
        }
        if (search.operatorId != null) {
            qry.where(qContact.operatorId.isNull().or(qContact.operatorId.eq(search.operatorId)));
        }
        if (search.name != null) {
            if (!isNullOrEmpty(search.name.fi)) {
                qry.where(qContact.nameFi.startsWith(search.name.fi));
            }
            if (!isNullOrEmpty(search.name.sv)) {
                qry.where(qContact.nameSv.startsWith(search.name.sv));
            }
            if (!isNullOrEmpty(search.name.en)) {
                qry.where(qContact.nameEn.startsWith(search.name.en));
            }
        }

        orderBy(search.sort, qry);
        return SearchResults.of(qry.list(contactMapping), search.limit);
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

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.by, DEFAULT_SORT.by)) {
            case "name.fi": sortField = qContact.nameFi.lower(); break;
            case "name.sv": sortField = qContact.nameSv.lower(); break;
            case "name.en": sortField = qContact.nameEn.lower(); break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.dir)) {
            qry.orderBy(sortField.desc(), qContact.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qContact.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }

}
