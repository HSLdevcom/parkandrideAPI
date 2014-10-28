package fi.hsl.parkandride.outbound;

import static fi.hsl.parkandride.core.domain.Spatial.fromWkt;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableSet;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.MultilingualString;
import fi.hsl.parkandride.core.domain.NotFoundException;
import fi.hsl.parkandride.core.domain.SearchResults;
import fi.hsl.parkandride.core.domain.SpatialSearch;
import fi.hsl.parkandride.core.outbound.HubRepository;
import fi.hsl.parkandride.outbound.sql.QHub;
import fi.hsl.parkandride.outbound.sql.QHubFacility;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class HubDaoTest {

    private static final MultilingualString NAME = new MultilingualString("Malmi");

    public static final Point LOCATION = (Point) fromWkt("POINT(25.010563 60.251022)");
    public static final ImmutableSet<Long> FACILITY_IDS = ImmutableSet.of(1l, 2l, 3l);

    @Inject
    TestHelper testHelper;

    @Inject
    HubRepository hubRepository;

    @After
    public void cleanup() {
        testHelper.clear(QHubFacility.hubFacility, QHub.hub);
    }

    @Test
    public void create_read_update() {
        // Insert
        Hub hub = createHub();

        final long hubId = hubRepository.insertHub(hub);
        assertThat(hubId).isGreaterThan(0l);

        // Get
        hub = hubRepository.getHub(hubId);
        assertThat(hub.name).isEqualTo(NAME);
        assertThat(hub.location).isEqualTo(LOCATION);
        assertThat(hub.facilityIds).isEqualTo(FACILITY_IDS);

        // Update
        final Set<Long> newFacilityIds = ImmutableSet.of(5l, 6l);
        final Point newLocation = (Point) fromWkt("POINT(25.016392 60.254157)");
        final MultilingualString newName = new MultilingualString("Malminkaari");
        hub.name = newName;
        hub.location = newLocation;
        hub.facilityIds = newFacilityIds;
        hubRepository.updateHub(hubId, hub);

        // Find
        List<Hub> hubs = findByGeometry(fromWkt("POLYGON((" +
                "25.015955 60.254351, " +
                "25.0178 60.254649, " +
                "25.016395 60.253675, " +
                "25.015955 60.254351))")).results;
        assertThat(hubs.size()).isEqualTo(1);
        hub = hubs.get(0);
        assertThat(hub.name).isEqualTo(newName);
        assertThat(hub.location).isEqualTo(newLocation);
        assertThat(hub.facilityIds).isEqualTo(newFacilityIds);

        hubs = findByGeometry(fromWkt("POLYGON((" +
                "25.015955 60.254351, " +
                "25.0178 60.254649, " +
                "25.01677 60.254548, " +
                "25.015955 60.254351))")).results;
        assertThat(hubs).isEmpty();

    }

    private Hub createHub() {
        Hub hub = new Hub();
        hub.name = NAME;
        hub.location = LOCATION;
        hub.facilityIds = FACILITY_IDS;
        return hub;
    }

    @Test(expected = NotFoundException.class)
    public void get_throws_an_exception_if_not_found() {
        hubRepository.getHub(0);
    }

    @Test(expected = NotFoundException.class)
    public void update_throws_an_exception_if_not_found() {
        hubRepository.updateHub(0, createHub());
    }

    private SearchResults<Hub> findByGeometry(Geometry geometry) {
        SpatialSearch search = new SpatialSearch();
        search.intersecting = geometry;
        return hubRepository.findHubs(search);
    }

}
