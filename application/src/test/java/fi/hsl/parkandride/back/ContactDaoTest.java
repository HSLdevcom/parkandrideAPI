// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Test;

import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.ValidationException;

public class ContactDaoTest extends AbstractDaoTest {

    private static MultilingualString NAME = new MultilingualString("name");

    private static MultilingualString OPENING_HOURS = new MultilingualString("opening hours");

    private static MultilingualString INFO = new MultilingualString("info");

    private static String EMAIL = "test@example.com";

    private static Phone PHONE = new Phone("09 1234567");

    private static Address ADDRESS = new Address("street", "12345", "city");

    @Inject
    ContactRepository contactDao;

    @Inject
    OperatorRepository operatorRepository;

    @Test
    public void create_read_update() {
        Contact contact = createContact();
        final long id = contactDao.insertContact(contact);
        assertThat(id).isGreaterThan(0);

        contact = contactDao.getContact(id);
        assertDefault(contact);

        List<Contact> contacts = contactDao.findContacts(new ContactSearch()).results;
        assertThat(contacts).hasSize(1);
        assertDefault(contacts.get(0));

        // Generic contacts are returned with operatoId search option
        ContactSearch search = new ContactSearch();
        search.setOperatorId(-123l);
        assertThat(contactDao.findContacts(search).results).hasSize(1);

        final MultilingualString newName = new MultilingualString("changed name");
        final MultilingualString newOpeningHours = new MultilingualString("changed opening hours");
        final MultilingualString newInfo = new MultilingualString("changed info");
        final String newEmail = "example@example.com";
        final Phone newPhone = new Phone("0800 123456");
        final Address newAddress = new Address("changed street", "23456", "changed city");
        contact.name = newName;
        contact.openingHours = newOpeningHours;
        contact.info = newInfo;
        contact.email = newEmail;
        contact.phone = newPhone;
        contact.address = newAddress;

        contact.operatorId = operatorRepository.insertOperator(new Operator(UUID.randomUUID().toString()));
        contactDao.updateContact(id, contact);
        contact = contactDao.getContact(id);
        assertThat(contact.name).isEqualTo(newName);
        assertThat(contact.email).isEqualTo(newEmail);
        assertThat(contact.phone).isEqualTo(newPhone);
        assertThat(contact.address).isEqualTo(newAddress);
        assertThat(contact.openingHours).isEqualTo(newOpeningHours);
        assertThat(contact.info).isEqualTo(newInfo);

        // Matches given operatorId
        search.setOperatorId(contact.operatorId);
        assertThat(contactDao.findContacts(search).results).hasSize(1);

        // But doesn't match other operatorId
        search.setOperatorId(-123l);
        assertThat(contactDao.findContacts(search).results).isEmpty();
    }

    @Test
    public void unique_name() {
        Contact contact = createContact();
        contactDao.insertContact(contact);
        verifyUniqueName(contact, "fi");
        verifyUniqueName(contact, "sv");
        verifyUniqueName(contact, "en");
    }

    private void verifyUniqueName(Contact contact, String lang) {
        contact.name = new MultilingualString("something else");
        try {
            contact.name.asMap().put(lang, NAME.asMap().get(lang));
            contactDao.insertContact(contact);
            fail("should not allow duplicate names");
        } catch (ValidationException e) {
            assertThat(e.violations).hasSize(1);
            assertThat(e.violations.get(0).path).isEqualTo("name." + lang);
        }
    }

    private void assertDefault(Contact contact) {
        assertThat(contact.name).isEqualTo(NAME);
        assertThat(contact.email).isEqualTo(EMAIL);
        assertThat(contact.phone).isEqualTo(PHONE);
        assertThat(contact.address).isEqualTo(ADDRESS);
        assertThat(contact.openingHours).isEqualTo(OPENING_HOURS);
        assertThat(contact.info).isEqualTo(INFO);
    }

    private Contact createContact() {
        Contact contact = new Contact();
        contact.name = NAME;
        contact.email = EMAIL;
        contact.phone = PHONE;
        contact.address = ADDRESS;
        contact.openingHours = OPENING_HOURS;
        contact.info = INFO;
        return contact;
    }
}
