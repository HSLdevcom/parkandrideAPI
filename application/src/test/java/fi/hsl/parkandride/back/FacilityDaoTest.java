// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import fi.hsl.parkandride.core.back.ContactRepository;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.back.OperatorRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.ValidationException;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.*;

import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.DayType.*;
import static fi.hsl.parkandride.core.domain.FacilityStatus.EXCEPTIONAL_SITUATION;
import static fi.hsl.parkandride.core.domain.FacilityStatus.IN_OPERATION;
import static fi.hsl.parkandride.core.domain.PricingMethod.CUSTOM;
import static fi.hsl.parkandride.core.domain.PricingMethod.PARK_AND_RIDE_247_FREE;
import static fi.hsl.parkandride.core.domain.Service.*;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;
import static fi.hsl.parkandride.core.domain.Usage.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FacilityDaoTest extends AbstractDaoTest {

    public static final MultilingualString NAME = new MultilingualString("Facility");

    public static final MultilingualString STATUS_DESCRIPTION = new MultilingualString("Status description");

    public static final MultilingualString OPENING_HOURS_INFO = new MultilingualString("Opening Hours");

    public static final MultilingualUrl OPENING_HOURS_URL = new MultilingualUrl("http://www.hsl.fi");

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

    public static final NullSafeSortedSet<Service> SERVICES = new NullSafeSortedSet<>(asList(ELEVATOR, TOILETS, ACCESSIBLE_TOILETS));

    public static final Pricing PRICING1 = new Pricing(CAR, PARK_AND_RIDE, 50, SATURDAY, "8", "18", "2 EUR/H");

    public static final Pricing PRICING2 = new Pricing(CAR, PARK_AND_RIDE, 50, SUNDAY, "8", "18", "1 EUR/H");

    public static final Map<CapacityType, Integer> BUILT_CAPACITY = ImmutableMap.of(
            CAR, 50,
            ELECTRIC_CAR, 2
    );

    public static final List<UnavailableCapacity> UNAVAILABLE_CAPACITIES = Arrays.asList(
            new UnavailableCapacity(CAR, PARK_AND_RIDE, 1)
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
    public void all_usages_and_capacities_are_materialized_and_projected() {
        Facility facility = createFacility();
        facility.pricingMethod = CUSTOM;
        final Map<CapacityType, Integer> builtCapacity = new HashMap<>();
        facility.pricing = new ArrayList<>();

        CapacityType[] capacityTypes = CapacityType.values();
        Usage[] usages = Usage.values();
        for (int i = 0; i < capacityTypes.length || i < usages.length; i++) {
            CapacityType type = capacityTypes[i % capacityTypes.length];
            Usage usage = usages[i % usages.length];

            builtCapacity.put(type, i + 1);
            facility.pricing.add(new Pricing(type, usage, i + 1, BUSINESS_DAY, "0", "24", null));
        }
        facility.builtCapacity = builtCapacity;
        final long id = facilityDao.insertFacility(facility);
        facility = facilityDao.getFacility(id);
        assertThat(facility.builtCapacity).isEqualTo(builtCapacity);
        assertThat(facility.usages).isEqualTo(ImmutableSet.copyOf(Usage.values()));

        FacilitySummary facilitySummary = facilityDao.summarizeFacilities(new FacilitySearch());
        assertThat(facilitySummary.capacities).isEqualTo(builtCapacity);
    }

    @Test
    public void normalize_facility_on_create() {
        Facility facility = mock(Facility.class);
        try {
            facilityDao.insertFacility(facility);
        } catch (RuntimeException e) {
        }
        verify(facility).normalize();
    }

    @Test
    public void normalize_facility_on_update() {
        Facility facility = mock(Facility.class);
        try {
            facilityDao.updateFacility(123l, facility, facility);
        } catch (RuntimeException e) {
        }
        verify(facility).normalize();
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
        assertDefault(facilityDao.findFacilities(new PageableFacilitySearch()).get(0));

        // Update
        final MultilingualString newName = new MultilingualString("changed name");
        final SortedSet<String> newAliases = ImmutableSortedSet.of("clias");
        final List<Port> newPorts = ImmutableList.of(new Port(PORT_LOCATION2, true, true, true, true), new Port(PORT_LOCATION1, false, false, false, false));
        final NullSafeSortedSet<Service> newServices = new NullSafeSortedSet<>(asList(LIGHTING));

        facility.name = newName;
        facility.status = IN_OPERATION;
        facility.pricingMethod = PARK_AND_RIDE_247_FREE;
        facility.statusDescription = null;
        facility.aliases = newAliases;
        facility.ports = newPorts;
        facility.services = newServices;

        facilityDao.updateFacility(id, facility);
        facility = facilityDao.getFacility(id);
        assertThat(facility.name).isEqualTo(newName);
        assertThat(facility.status).isEqualTo(IN_OPERATION);
        assertThat(facility.statusDescription).isNull();
        assertThat(facility.aliases).isEqualTo(newAliases);
        assertThat(ImmutableSet.copyOf(facility.ports)).isEqualTo(ImmutableSet.copyOf(newPorts));
        assertThat(facility.services).isEqualTo(newServices);
        assertThat(facility.pricing).isEqualTo(asList(
                free24h(CAR, PARK_AND_RIDE, 50, BUSINESS_DAY),
                free24h(CAR, PARK_AND_RIDE, 50, SATURDAY),
                free24h(CAR, PARK_AND_RIDE, 50, SUNDAY),
                free24h(ELECTRIC_CAR, PARK_AND_RIDE, 2, BUSINESS_DAY),
                free24h(ELECTRIC_CAR, PARK_AND_RIDE, 2, SATURDAY),
                free24h(ELECTRIC_CAR, PARK_AND_RIDE, 2, SUNDAY)
        ));
        assertThat(facility.usages).isEqualTo(ImmutableSet.of(PARK_AND_RIDE));
        assertThat(facility.unavailableCapacities).isEqualTo(asList(
                new UnavailableCapacity(CAR, PARK_AND_RIDE, 1),
                new UnavailableCapacity(ELECTRIC_CAR, PARK_AND_RIDE, 0)
        ));

        // Remove aliases, capacities and ports
        facility.aliases = new HashSet<>();
        facility.ports = new ArrayList<>();
        facility.services = new NullSafeSortedSet<>();
        facility.contacts.service = null;
        facilityDao.updateFacility(id, facility);

        // Find by geometry
        List<FacilityInfo> facilities = findByGeometry(OVERLAPPING_AREA);
        assertThat(facilities).hasSize(1);

        // Not found by geometry
        assertThat(findByGeometry(NON_OVERLAPPING_AREA)).isEmpty();
    }

    private Pricing free24h(CapacityType type, Usage usage, int maxCapacity, DayType dayType) {
        return new Pricing(type, usage, maxCapacity, dayType, "00", "24", null);
    }

    private Long createDummyContact() {
        Contact contact = new Contact();
        contact.name = new MultilingualString("TEST " + UUID.randomUUID());
        contact.email = "test@example.com";
        return contactDao.insertContact(contact);
    }

    private Long createDummyOperator() {
        Operator operator = new Operator("SMOOTH");
        return operatorDao.insertOperator(operator);
    }

    private void assertDefault(FacilityInfo facility) {
        assertThat(facility).isNotNull();
        assertThat(facility.location).isEqualTo(LOCATION);
        assertThat(facility.operatorId).isEqualTo(operatorId);
        assertThat(facility.status).isEqualTo(EXCEPTIONAL_SITUATION);
        assertThat(facility.statusDescription).isEqualTo(STATUS_DESCRIPTION);
        assertThat(facility.name).isEqualTo(NAME);
        assertThat(facility.builtCapacity).isEqualTo(BUILT_CAPACITY);
        assertThat(facility.usages).isEqualTo(ImmutableSet.of(PARK_AND_RIDE));
    }

    private void assertDefault(Facility facility) {
        assertDefault((FacilityInfo) facility);
        assertThat(facility.aliases).isEqualTo(ALIASES);
        assertThat(facility.ports).isEqualTo(PORTS);
        assertThat(facility.services).isEqualTo(SERVICES);
        assertThat(facility.pricing).isEqualTo(asList(PRICING1, PRICING2));
        assertThat(facility.unavailableCapacities).isEqualTo(UNAVAILABLE_CAPACITIES);
        assertThat(facility.openingHours.byDayType).isEqualTo(ImmutableMap.of(
                SATURDAY, new TimeDuration("8", "18"),
                SUNDAY, new TimeDuration("8", "18")
        ));
        assertThat(facility.openingHours.info).isEqualTo(OPENING_HOURS_INFO);
        assertThat(facility.openingHours.url).isEqualTo(OPENING_HOURS_URL);
    }

    private List<FacilityInfo> findByGeometry(Polygon geometry) {
        PageableFacilitySearch search = new PageableFacilitySearch();
        search.setGeometry(geometry);
        return facilityDao.findFacilities(search).results;
    }

    private Facility createFacility() {
        return createFacility(operatorId, dummyContacts);
    }

    public static Facility createFacility(Long operatorId, FacilityContacts contacts) {
        Facility facility = new Facility();
        facility.id = 0l;
        facility.name = NAME;
        facility.location = LOCATION;
        facility.operatorId = operatorId;
        facility.status = EXCEPTIONAL_SITUATION;
        facility.pricingMethod = PricingMethod.CUSTOM;
        facility.statusDescription = STATUS_DESCRIPTION;
        facility.aliases = ALIASES;
        facility.ports = PORTS;
        facility.services = SERVICES;
        facility.contacts = contacts;

        facility.builtCapacity = BUILT_CAPACITY;
        // pricing in wrong order should be sorted on load
        facility.pricing.add(PRICING2);
        facility.pricing.add(PRICING1);
        facility.unavailableCapacities = UNAVAILABLE_CAPACITIES;

        facility.openingHours.info = OPENING_HOURS_INFO;
        facility.openingHours.url = OPENING_HOURS_URL;

        return facility;
    }

    @Test
    public void sorting() {
        Facility f1 = new Facility();
        f1.name = new MultilingualString("a", "å", "C");
        f1.status = IN_OPERATION;
        f1.pricingMethod = PricingMethod.CUSTOM;
        f1.location = LOCATION;
        f1.contacts = dummyContacts;
        f1.operatorId = operatorId;
        f1.id = facilityDao.insertFacility(f1);

        Facility f2 = new Facility();
        f2.name = new MultilingualString("D", "Ä", "F");
        f2.status = IN_OPERATION;
        f2.pricingMethod = PricingMethod.CUSTOM;
        f2.location = LOCATION;
        f2.operatorId = operatorId;
        f2.contacts = dummyContacts;
        f2.id = facilityDao.insertFacility(f2);

        // Default sort
        PageableFacilitySearch search = new PageableFacilitySearch();
        assertResultOrder(facilityDao.findFacilities(search), f1.id, f2.id);

        // name.fi desc
        search.sort = new Sort("name.fi", DESC);
        assertResultOrder(facilityDao.findFacilities(search), f2.id, f1.id);


        // name.sv desc
        search.sort = new Sort("name.sv", DESC);
        // NOTE: This doesn't work on mac/postgresql because it's fi-collation is broken
        assertResultOrder(facilityDao.findFacilities(search), f2.id, f1.id);

        // name.en asc
        search.sort = new Sort("name.en", ASC);
        assertResultOrder(facilityDao.findFacilities(search), f1.id, f2.id);
    }

    @Test(expected = ValidationException.class)
    public void illegal_sort_by() {
        PageableFacilitySearch search = new PageableFacilitySearch();
        search.sort = new Sort("foobar");
        facilityDao.findFacilities(search);
    }

    @Test
    public void unique_name() {
        Facility facility = createFacility();
        facilityDao.insertFacility(facility);
        verifyUniqueName(facility, "fi");
        verifyUniqueName(facility, "sv");
        verifyUniqueName(facility, "en");
    }

    private void verifyUniqueName(Facility facility, String lang) {
        facility.name = new MultilingualString("something else");
        try {
            facility.name.asMap().put(lang, NAME.asMap().get(lang));
            facilityDao.insertFacility(facility);
            fail("should not allow duplicate names");
        } catch (ValidationException e) {
            assertThat(e.violations).hasSize(1);
            assertThat(e.violations.get(0).path).isEqualTo("name." + lang);
        }
    }

    private void assertResultOrder(SearchResults<FacilityInfo> results, long id1, long id2) {
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

    @Test
    public void findLatestUtilization_when_nothing_to_find() {
        long facilityId = facilityDao.insertFacility(createFacility());

        List<Utilization> results = facilityDao.findLatestUtilization(facilityId);

        assertThat(results).isEmpty();
    }

    @Test
    public void findLatestUtilization_returns_latest_entry() {
        long facilityId = facilityDao.insertFacility(createFacility());
        Utilization u1 = newUtilization(new DateTime(2000, 1, 1, 12, 0), 100);
        Utilization u2 = newUtilization(new DateTime(2000, 1, 1, 13, 0), 200);
        facilityDao.insertUtilization(facilityId, asList(u1, u2));

        List<Utilization> results = facilityDao.findLatestUtilization(facilityId);

        assertThat(results).containsOnly(u2);
    }

    @Test
    public void findLatestUtilization_returns_each_capacity_type_and_usage_combination() {
        long facilityId = facilityDao.insertFacility(createFacility());
        Utilization u1 = newUtilization(new DateTime(2000, 1, 1, 12, 0), 100);
        u1.capacityType = CAR;
        u1.usage = HSL_TRAVEL_CARD;
        Utilization u2 = newUtilization(new DateTime(2000, 1, 1, 13, 0), 200);
        u2.capacityType = CAR;
        u2.usage = COMMERCIAL;
        Utilization u3 = newUtilization(new DateTime(2000, 1, 1, 14, 0), 300);
        u3.capacityType = MOTORCYCLE;
        u3.usage = HSL_TRAVEL_CARD;
        facilityDao.insertUtilization(facilityId, asList(u1, u2, u3));

        List<Utilization> results = facilityDao.findLatestUtilization(facilityId);

        assertThat(results).containsOnly(u1, u2, u3);
    }

    private static Utilization newUtilization(DateTime time, int spacesAvailable) {
        Utilization u = new Utilization();
        u.capacityType = CAR;
        u.usage = HSL_TRAVEL_CARD;
        u.timestamp = time;
        u.spacesAvailable = spacesAvailable;
        return u;
    }
}
