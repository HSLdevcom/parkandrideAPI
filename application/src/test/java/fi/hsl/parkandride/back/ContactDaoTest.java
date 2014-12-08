package fi.hsl.parkandride.back;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import fi.hsl.parkandride.back.sql.QContact;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.domain.Address;
import fi.hsl.parkandride.core.domain.Contact;
import fi.hsl.parkandride.core.domain.ContactSearch;
import fi.hsl.parkandride.core.domain.MultilingualString;
import fi.hsl.parkandride.core.domain.Phone;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class ContactDaoTest {

    private static MultilingualString NAME = new MultilingualString("name");

    private static MultilingualString OPENING_HOURS = new MultilingualString("opening hours");

    private static MultilingualString INFO = new MultilingualString("info");

    private static String EMAIL = "test@example.com";

    private static Phone PHONE = new Phone("09 1234567");

    private static Address ADDRESS = new Address("street", "12345", "city");

    @Inject
    TestHelper testHelper;

    @Inject
    ContactRepository contactDao;

    @Before
    public void cleanup() {
        FacilityDaoTest.clearFacilities(testHelper);
        testHelper.clear(QContact.contact);
    }

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

        contactDao.updateContact(id, contact);
        contact = contactDao.getContact(id);
        assertThat(contact.name).isEqualTo(newName);
        assertThat(contact.email).isEqualTo(newEmail);
        assertThat(contact.phone).isEqualTo(newPhone);
        assertThat(contact.address).isEqualTo(newAddress);
        assertThat(contact.openingHours).isEqualTo(newOpeningHours);
        assertThat(contact.info).isEqualTo(newInfo);
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
