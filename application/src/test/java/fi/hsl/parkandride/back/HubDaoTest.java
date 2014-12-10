package fi.hsl.parkandride.back;

import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;
import static fi.hsl.parkandride.core.domain.Spatial.fromWkt;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableSet;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.back.sql.QHub;
import fi.hsl.parkandride.back.sql.QHubFacility;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class HubDaoTest {

    private static final MultilingualString NAME = new MultilingualString("Malmi");

    public static final Point LOCATION = (Point) fromWkt("POINT(25.010563 60.251022)");
    public static final ImmutableSet<Long> FACILITY_IDS = ImmutableSet.of(1l, 2l, 3l);
    public static final String POSTAL_CODE = "00100";
    public static final MultilingualString STREET_ADDRESS = new MultilingualString("street");
    public static final MultilingualString CITY = new MultilingualString("city");

    @Inject
    TestHelper testHelper;

    @Inject
    HubRepository hubRepository;

    @Before
    public void cleanup() {
        testHelper.resetHubs();
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
        assertThat(hub.address.streetAddress).isEqualTo(STREET_ADDRESS);
        assertThat(hub.address.city).isEqualTo(CITY);
        assertThat(hub.address.postalCode).isEqualTo(POSTAL_CODE);
        assertThat(hub.location).isEqualTo(LOCATION);
        assertThat(hub.facilityIds).isEqualTo(FACILITY_IDS);

        // Update
        final Set<Long> newFacilityIds = ImmutableSet.of(5l, 6l);
        final Point newLocation = (Point) fromWkt("POINT(25.016392 60.254157)");
        final MultilingualString newName = new MultilingualString("Malminkaari");
        final MultilingualString newStreetAddress = new MultilingualString("new street");
        final MultilingualString newCity = new MultilingualString("new city");
        final String newPostalCode = "00200";
        hub.name = newName;
        hub.address = new Address(newStreetAddress, newPostalCode, newCity);
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
        assertThat(hub.address.streetAddress).isEqualTo(newStreetAddress);
        assertThat(hub.address.city).isEqualTo(newCity);
        assertThat(hub.address.postalCode).isEqualTo(newPostalCode);
        assertThat(hub.location).isEqualTo(newLocation);
        assertThat(hub.facilityIds).isEqualTo(newFacilityIds);

        hubs = findByGeometry(fromWkt("POLYGON((" +
                "25.015955 60.254351, " +
                "25.0178 60.254649, " +
                "25.01677 60.254548, " +
                "25.015955 60.254351))")).results;
        assertThat(hubs).isEmpty();

    }

    @Test
    public void sorting() {
        Hub h1 = new Hub();
        h1.name = new MultilingualString("a", "å", "C");
        h1.location = LOCATION;
        h1.id = hubRepository.insertHub(h1);

        Hub h2 = new Hub();
        h2.name = new MultilingualString("D", "Ä", "F");
        h2.location = LOCATION;
        h2.id = hubRepository.insertHub(h2);

        // Default sort
        PageableSpatialSearch search = new PageableSpatialSearch();
        assertResultOrder(hubRepository.findHubs(search), h1.id, h2.id);

        // name.fi desc
        search.sort = new Sort("name.fi", DESC);
        assertResultOrder(hubRepository.findHubs(search), h2.id, h1.id);


        // name.sv desc
        search.sort = new Sort("name.sv", DESC);
        assertResultOrder(hubRepository.findHubs(search), h2.id, h1.id);

        // name.en asc
        search.sort = new Sort("name.en", ASC);
        assertResultOrder(hubRepository.findHubs(search), h1.id, h2.id);
    }

    @Test(expected = NotFoundException.class)
    public void get_throws_an_exception_if_not_found() {
        hubRepository.getHub(0);
    }

    @Test(expected = NotFoundException.class)
    public void update_throws_an_exception_if_not_found() {
        hubRepository.updateHub(0, createHub());
    }

    private Hub createHub() {
        Hub hub = new Hub();
        hub.name = NAME;
        hub.location = LOCATION;
        hub.facilityIds = FACILITY_IDS;
        hub.address = new Address(STREET_ADDRESS, POSTAL_CODE, CITY);
        return hub;
    }

    private void assertResultOrder(SearchResults<Hub> results, long id1, long id2) {
        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0).id).isEqualTo(id1);
        assertThat(results.get(1).id).isEqualTo(id2);
    }

    private SearchResults<Hub> findByGeometry(Geometry geometry) {
        SpatialSearch search = new SpatialSearch();
        search.intersecting = geometry;
        return hubRepository.findHubs(search);
    }

}
