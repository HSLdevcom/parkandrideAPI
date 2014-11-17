package fi.hsl.parkandride.core.back;

import java.util.List;

import fi.hsl.parkandride.core.domain.Contact;
import fi.hsl.parkandride.core.domain.ContactSearch;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;

public interface ContactRepository {

    long insertContact(Contact contact);

    Contact getContact(long contactId);

    void updateContact(long contactId, Contact contact);

    List<Contact> findContacts(ContactSearch search);

}
