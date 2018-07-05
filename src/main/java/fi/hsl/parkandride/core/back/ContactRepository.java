// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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
