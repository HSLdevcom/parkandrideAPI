package fi.hsl.parkandride.back;

import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE;
import static fi.hsl.parkandride.core.domain.CapacityType.ELECTRIC_CAR;
import static fi.hsl.parkandride.core.domain.DayType.BUSINESS_DAY;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;
import static fi.hsl.parkandride.core.domain.Usage.COMMERCIAL;
import static fi.hsl.parkandride.core.domain.Usage.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;

import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.ValidationException;

public class FacilityDaoTest extends AbstractDaoTest {

    public static final MultilingualString NAME = new MultilingualString("Facility");

    private static final Point PORT_LOCATION1 = (Point) Spatial.fromWkt("POINT(25.010822 60.25054)");

    private static final Point PORT_LOCATION2 = (Point) Spatial.fromWkt("POINT(25.012479 60.250337)");

    private static final Polygon LOCATION = (Polygon) Spatial.fromWkt("POLYGON((" +
            "25.010822 60.25054, " +
            "25.010822 60.250023, " +
            "25.012479 60.250337, " +
            "25.011449 60.250885, " +
            "25.010822 60.25054))");

    public static final Polygon OVERLAPPING_AREA = (Polygon) Spatial.fromWkt("POLYGON((" +
            "25.011942 60.251343, " +
            "25.011717 60.250454, " +
            "25.013487 60.250848, " +
            "25.011942 60.251343))");

    public static final Polygon NON_OVERLAPPING_AREA = (Polygon) Spatial.fromWkt("POLYGON((" +
            "25.011942 60.251343, " +
            "25.012211 60.250816, " +
            "25.013487 60.250848, " +
            "25.011942 60.251343))");

    public static final SortedSet<String> ALIASES = ImmutableSortedSet.of("alias", "blias");

    public static final List<Port> PORTS = ImmutableList.of(new Port(PORT_LOCATION1, true, false, true, false, "street", "00100", "city", "info"));

    public static final Set<Long> SERVICES = ImmutableSet.of(1l, 2l, 3l);

    public static final Pricing PRICING1 = new Pricing(PARK_AND_RIDE, CAR, 50, BUSINESS_DAY, "8", "18", "2 EUR/H");

    public static final Pricing PRICING2 = new Pricing(PARK_AND_RIDE, CAR, 50, BUSINESS_DAY, "18", "24", "1 EUR/H");

    public static final Map<CapacityType, Integer> BUILT_CAPACITY = ImmutableMap.of(
            CAR, 50,
            ELECTRIC_CAR, 2
    );

    @Inject
    ContactRepository contactDao;

    @Inject
    FacilityRepository facilityDao;

    @Inject
    OperatorRepository operatorDao;

    private FacilityContacts dummyContacts;

    private Long operatorId;

    @Before
    public void initialize() {
        dummyContacts = new FacilityContacts(createDummyContact(), createDummyContact());
        operatorId = createDummyOperator();
    }

    @Test
    public void create_read_update() {
        Facility facility = createFacility();

        // Insert
        final long id = facilityDao.insertFacility(facility);
        assertThat(id).isGreaterThan(0);
        assertThat(facility.id).isNotEqualTo(id);

        // Find by id
        facility = facilityDao.getFacility(id);
        assertDefault(facility);
        assertThat(facility.contacts).isEqualTo(dummyContacts);

        // Search
        facility = facilityDao.findFacilities(new PageableSpatialSearch()).get(0);
        assertDefault(facility);

        // Update
        final MultilingualString newName = new MultilingualString("changed name");
        final SortedSet<String> newAliases = ImmutableSortedSet.of("clias");
        final List<Port> newPorts = ImmutableList.of(new Port(PORT_LOCATION2, true, true, true, true), new Port(PORT_LOCATION1, false, false, false, false));
        final Set<Long> newServices = ImmutableSet.of(4l);
        final SortedSet<Pricing> newPricing = ImmutableSortedSet.of(new Pricing(COMMERCIAL, CAR, 50, BUSINESS_DAY, "8", "18", "10 EUR/H"));

        facility.name = newName;
        facility.aliases = newAliases;
        facility.ports = newPorts;
        facility.serviceIds = newServices;
        facility.pricing = newPricing;

        facilityDao.updateFacility(id, facility);
        facility = facilityDao.getFacility(id);
        assertThat(facility.name).isEqualTo(newName);
        assertThat(facility.aliases).isEqualTo(newAliases);
        assertThat(facility.ports).isEqualTo(newPorts);
        assertThat(facility.serviceIds).isEqualTo(newServices);
        assertThat(facility.pricing).isEqualTo(newPricing);

        // Remove aliases, capacities and ports
        facility.aliases = null;
        facility.ports = null;
        facility.serviceIds = null;
        facility.contacts.service = null;
        facilityDao.updateFacility(id, facility);

        // Find by geometry
        List<Facility> facilities = findByGeometry(OVERLAPPING_AREA);
        assertThat(facilities).hasSize(1);
        assertThat(facilities.get(0).aliases).isEmpty();
        assertThat(facilities.get(0).ports).isEmpty();
        assertThat(facilities.get(0).serviceIds).isEmpty();
        assertThat(facilities.get(0).contacts).isNotNull();
        assertThat(facilities.get(0).contacts.service).isNull();

        // Not found by geometry
        assertThat(findByGeometry(NON_OVERLAPPING_AREA)).isEmpty();
    }

    private Long createDummyContact() {
        Contact contact = new Contact();
        contact.name = new MultilingualString("TEST");
        contact.email = "test@example.com";
        return contactDao.insertContact(contact);
    }

    private Long createDummyOperator() {
        Operator operator = new Operator("SMOOTH");
        return operatorDao.insertOperator(operator);
    }

    private void assertDefault(Facility facility) {
        assertThat(facility).isNotNull();
        assertThat(facility.location).isEqualTo(LOCATION);
        assertThat(facility.name).isEqualTo(NAME);
        assertThat(facility.aliases).isEqualTo(ALIASES);
        assertThat(facility.ports).isEqualTo(PORTS);
        assertThat(facility.serviceIds).isEqualTo(SERVICES);
        assertThat(facility.builtCapacity).isEqualTo(BUILT_CAPACITY);
        assertThat(facility.pricing).isEqualTo(ImmutableSortedSet.of(PRICING1, PRICING2));
    }

    private List<Facility> findByGeometry(Polygon geometry) {
        PageableSpatialSearch search = new PageableSpatialSearch();
        search.intersecting = geometry;
        return facilityDao.findFacilities(search).results;
    }

    private Facility createFacility() {
        Facility facility = new Facility();
        facility.id = 0l;
        facility.name = NAME;
        facility.location = LOCATION;
        facility.operatorId = operatorId;
        facility.aliases = ALIASES;
        facility.ports = PORTS;
        facility.serviceIds = SERVICES;
        facility.contacts = dummyContacts;

        facility.builtCapacity = BUILT_CAPACITY;
        facility.pricing.add(PRICING1);
        facility.pricing.add(PRICING2);

        return facility;
    }

    @Test
    public void sorting() {
        Facility f1 = new Facility();
        f1.name = new MultilingualString("a", "å", "C");
        f1.location = LOCATION;
        f1.contacts = dummyContacts;
        f1.operatorId = operatorId;
        f1.id = facilityDao.insertFacility(f1);

        Facility f2 = new Facility();
        f2.name = new MultilingualString("D", "Ä", "F");
        f2.location = LOCATION;
        f2.operatorId = operatorId;
        f2.contacts = dummyContacts;
        f2.id = facilityDao.insertFacility(f2);

        // Default sort
        PageableSpatialSearch search = new PageableSpatialSearch();
        assertResultOrder(facilityDao.findFacilities(search), f1.id, f2.id);

        // name.fi desc
        search.sort = new Sort("name.fi", DESC);
        assertResultOrder(facilityDao.findFacilities(search), f2.id, f1.id);


        // name.sv desc
        search.sort = new Sort("name.sv", DESC);
        assertResultOrder(facilityDao.findFacilities(search), f2.id, f1.id);

        // name.en asc
        search.sort = new Sort("name.en", ASC);
        assertResultOrder(facilityDao.findFacilities(search), f1.id, f2.id);
    }

    @Test(expected = ValidationException.class)
    public void illegal_sort_by() {
        PageableSpatialSearch search = new PageableSpatialSearch();
        search.sort = new Sort("foobar");
        facilityDao.findFacilities(search);
    }

    private void assertResultOrder(SearchResults<Facility> results, long id1, long id2) {
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0).id).isEqualTo(id1);
        assertThat(results.get(1).id).isEqualTo(id2);
    }

    @Test(expected = NotFoundException.class)
    public void get_throws_an_exception_if_not_found() {
        facilityDao.getFacility(0);
    }

    @Test(expected = NotFoundException.class)
    public void update_throws_an_exception_if_not_found() {
        facilityDao.updateFacility(0, createFacility());
    }
}
