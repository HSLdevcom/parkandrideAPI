package fi.hsl.parkandride.outbound;

import static fi.hsl.parkandride.core.domain.CapacityType.BICYCLE;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static fi.hsl.parkandride.core.domain.CapacityType.PARK_AND_RIDE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Inject;

import org.geolatte.geom.Polygon;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.outbound.sql.QCapacity;
import fi.hsl.parkandride.outbound.sql.QFacility;
import fi.hsl.parkandride.outbound.sql.QFacilityAlias;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class FacilityDaoTest {

    public static final String NAME = "Facility";

    private static final Polygon BORDER = (Polygon) Spatial.fromWkt("POLYGON((" +
            "25.010827 60.25055, " +
            "25.011867 60.250023, " +
            "25.012479 60.250337, " +
            "25.011454 60.250886, " +
            "25.010827 60.25055))");

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

    public static final Map<CapacityType, Capacity> CAPACITIES = ImmutableMap.of(CAR, new Capacity(100, 1), BICYCLE, new Capacity(10, 0));


    @Inject TestHelper testHelper;

    @Inject
    FacilityRepository facilityDao;

    @After
    public void cleanup() {
        testHelper.clear(QFacilityAlias.facilityAlias, QCapacity.capacity, QFacility.facility);
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
        assertThat(facility.name).isEqualTo(NAME);
        assertThat(facility.aliases).isEqualTo(ALIASES);
        assertThat(facility.capacities).isEqualTo(CAPACITIES);

        // Update
        final String newName = "changed name";
        final SortedSet<String> newAliases = ImmutableSortedSet.of("clias");
        final Map<CapacityType, Capacity> newCapacities = ImmutableMap.of(CAR, new Capacity(100, 50), PARK_AND_RIDE, new Capacity(5, 0));

        facility.name = newName;
        facility.aliases = newAliases;
        facility.capacities = newCapacities;

        facilityDao.updateFacility(id, facility);
        facility = facilityDao.getFacility(id);
        assertThat(facility.name).isEqualTo("changed name");
        assertThat(facility.aliases).isEqualTo(newAliases);
        assertThat(facility.capacities).isEqualTo(newCapacities);

        // Remove aliases and capacities
        facility.aliases = null;
        facility.capacities = null;
        facilityDao.updateFacility(id, facility);

        // Find by geometry
        List<Facility> facilities = findByGeometry(OVERLAPPING_AREA);
        assertThat(facilities).hasSize(1);
        assertThat(facilities.get(0).aliases).isEmpty();
        assertThat(facilities.get(0).capacities).isEmpty();

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
        facility.border = BORDER;
        facility.aliases = ALIASES;
        facility.capacities = CAPACITIES;
        return facility;
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
