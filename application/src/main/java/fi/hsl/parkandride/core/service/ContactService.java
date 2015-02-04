package fi.hsl.parkandride.core.service;

import static fi.hsl.parkandride.core.domain.Permission.CONTACT_CREATE;
import static fi.hsl.parkandride.core.domain.Permission.CONTACT_UPDATE;
import static fi.hsl.parkandride.core.service.AuthenticationService.authorize;

import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.domain.Contact;
import fi.hsl.parkandride.core.domain.ContactSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.User;

public class ContactService {

    private final ContactRepository repository;

    private final ValidationService validationService;

    public ContactService(ContactRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @TransactionalWrite
    public Contact createContact(Contact contact, User currentUser) {
        authorize(currentUser, contact, CONTACT_CREATE);

        validationService.validate(contact);
        contact.id = repository.insertContact(contact);
        return contact;
    }

    @TransactionalWrite
    public Contact updateContact(long contactId, Contact contact, User currentUser) {
        authorize(currentUser, contact, CONTACT_UPDATE);
        Contact oldContact = repository.getContactForUpdate(contactId);
        authorize(currentUser, oldContact, CONTACT_UPDATE);

        validationService.validate(contact);

        repository.updateContact(contactId, contact);
        contact.id = contactId;
        return contact;
    }

    @TransactionalRead
    public Contact getContact(long id) {
        return repository.getContact(id);
    }

    @TransactionalRead
    public SearchResults search(ContactSearch search) {
        return repository.findContacts(search);
    }

}
