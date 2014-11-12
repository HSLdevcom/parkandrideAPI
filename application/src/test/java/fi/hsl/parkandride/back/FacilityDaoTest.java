package fi.hsl.parkandride.back;

import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.PARK_AND_RIDE;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Inject;

import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

import fi.hsl.parkandride.back.sql.QPort;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.service.ValidationException;
import fi.hsl.parkandride.back.sql.QCapacity;
import fi.hsl.parkandride.back.sql.QFacility;
import fi.hsl.parkandride.back.sql.QFacilityAlias;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class FacilityDaoTest {

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

    public static final List<Port> PORTS = ImmutableList.of(new Port(PORT_LOCATION1, true, false, true, "street", "00100", "city", "info"));

    public static final Map<CapacityType, Capacity> CAPACITIES = ImmutableMap.of(CAR, new Capacity(100, 1), BICYCLE, new Capacity(10, 0));


    @Inject TestHelper testHelper;

    @Inject
    FacilityRepository facilityDao;

    @After
    public void cleanup() {
        testHelper.clear(QFacilityAlias.facilityAlias, QCapacity.capacity, QPort.port, QFacility.facility);
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
        assertThat(facility).isNotNull();
        assertThat(facility.location).isEqualTo(LOCATION);
        assertThat(facility.name).isEqualTo(NAME);
        assertThat(facility.aliases).isEqualTo(ALIASES);
        assertThat(facility.capacities).isEqualTo(CAPACITIES);
        assertThat(facility.ports).isEqualTo(PORTS);

        // Update
        final MultilingualString newName = new MultilingualString("changed name");
        final SortedSet<String> newAliases = ImmutableSortedSet.of("clias");
        final Map<CapacityType, Capacity> newCapacities = ImmutableMap.of(CAR, new Capacity(100, 50), PARK_AND_RIDE, new Capacity(5, 0));
        final List<Port> newPorts = ImmutableList.of(new Port(PORT_LOCATION2, true, true, true), new Port(PORT_LOCATION1, false, false, false));

        facility.name = newName;
        facility.aliases = newAliases;
        facility.capacities = newCapacities;
        facility.ports = newPorts;

        facilityDao.updateFacility(id, facility);
        facility = facilityDao.getFacility(id);
        assertThat(facility.name).isEqualTo(newName);
        assertThat(facility.aliases).isEqualTo(newAliases);
        assertThat(facility.capacities).isEqualTo(newCapacities);
        assertThat(facility.ports).isEqualTo(newPorts);

        // Remove aliases, capacities and ports
        facility.aliases = null;
        facility.capacities = null;
        facility.ports = null;
        facilityDao.updateFacility(id, facility);

        // Find by geometry
        List<Facility> facilities = findByGeometry(OVERLAPPING_AREA);
        assertThat(facilities).hasSize(1);
        assertThat(facilities.get(0).aliases).isEmpty();
        assertThat(facilities.get(0).capacities).isEmpty();
        assertThat(facilities.get(0).ports).isEmpty();

        // Not found by geometry
        assertThat(findByGeometry(NON_OVERLAPPING_AREA)).isEmpty();
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
        facility.aliases = ALIASES;
        facility.capacities = CAPACITIES;
        facility.ports = PORTS;
        return facility;
    }

    @Test
    public void summary_with_no_capacities() {
        Facility facility = createFacility();
        facility.capacities = ImmutableMap.of();
        facilityDao.insertFacility(facility);
        FacilitySummary summary = facilityDao.summarizeFacilities(new SpatialSearch());
        assertThat(summary.capacities).isEmpty();
    }

    @Test
    public void sorting() {
        Facility f1 = new Facility();
        f1.name = new MultilingualString("a", "å", "C");
        f1.location = LOCATION;
        f1.id = facilityDao.insertFacility(f1);

        Facility f2 = new Facility();
        f2.name = new MultilingualString("D", "Ä", "F");
        f2.location = LOCATION;
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

    @Test
    public void summarize_facilities() {
        facilityDao.insertFacility(createFacility());
        facilityDao.insertFacility(createFacility());
        FacilitySummary summary = facilityDao.summarizeFacilities(new SpatialSearch()); // all

        assertThat(summary.facilityCount).isEqualTo(2);
        assertThat(summary.capacities).hasSize(2);
        assertThat(summary.capacities.get(CAR)).isEqualTo(new Capacity(200, 2));
        assertThat(summary.capacities.get(BICYCLE)).isEqualTo(new Capacity(20, 0));
    }
}
