package fi.hsl.parkandride.front;

import static fi.hsl.parkandride.front.UrlSchema.CONTACT;
import static fi.hsl.parkandride.front.UrlSchema.CONTACTS;
import static fi.hsl.parkandride.front.UrlSchema.CONTACT_ID;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.inject.Inject;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hsl.parkandride.core.domain.Contact;
import fi.hsl.parkandride.core.domain.ContactSearch;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.service.ContactService;

@RestController
public class ContactController {

    @Inject
    ContactService contactService;

    @RequestMapping(method = POST, value = CONTACTS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact, UriComponentsBuilder builder) {
        Contact newContact = contactService.createContact(contact);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(CONTACT).buildAndExpand(newContact.id).toUri());
        return new ResponseEntity<>(newContact, headers, CREATED);
    }

    @RequestMapping(method = GET, value = CONTACT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> getContact(@PathVariable(CONTACT_ID) long contactId) {
        Contact contact = contactService.getContact(contactId);
        return new ResponseEntity<>(contact, OK);
    }

    @RequestMapping(method = PUT, value = CONTACT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> updateContact(@PathVariable(CONTACT_ID) long contactId,
                                                   @RequestBody Contact contact) {
        Contact response = contactService.updateContact(contactId, contact);
        return new ResponseEntity<>(contact, OK);
    }

    @RequestMapping(method = GET, value = CONTACTS, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchResults<Contact>> findContacts(ContactSearch search) {
        SearchResults<Contact> results = contactService.search(search);
        return new ResponseEntity<>(results, OK);
    }

}
