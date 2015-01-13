package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Contact;
import fi.hsl.parkandride.core.domain.ContactSearch;
import fi.hsl.parkandride.core.domain.SearchResults;

public interface ContactRepository {

    long insertContact(Contact contact);

    Contact getContact(long contactId);

    Contact getContactForUpdate(long contactId);

    void updateContact(long contactId, Contact contact);

    SearchResults<Contact> findContacts(ContactSearch search);

}
