package fi.hsl.parkandride.core.service;

import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.domain.Contact;
import fi.hsl.parkandride.core.domain.ContactSearch;
import fi.hsl.parkandride.core.domain.SearchResults;

public class ContactService {

    private final ContactRepository repository;

    private final ValidationService validationService;

    public ContactService(ContactRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    @TransactionalWrite
    public Contact createContact(Contact contact) {
        validationService.validate(contact);
        contact.id = repository.insertContact(contact);
        return contact;
    }

    @TransactionalWrite
    public Contact updateContact(long contactId, Contact contact) {
        validationService.validate(contact);
        repository.updateContact(contactId, contact);
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
