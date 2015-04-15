// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;
import static fi.hsl.parkandride.core.domain.Spatial.fromWkt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import fi.hsl.parkandride.core.back.HubRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.ValidationException;

public class HubDaoTest extends AbstractDaoTest {

    private static final MultilingualString NAME = new MultilingualString("malmi");

    public static final Point LOCATION = (Point) fromWkt("POINT(25.010563 60.251022)");
    public static final ImmutableSet<Long> FACILITY_IDS = ImmutableSet.of(1l, 2l, 3l);
    public static final String POSTAL_CODE = "00100";
    public static final MultilingualString STREET_ADDRESS = new MultilingualString("street");
    public static final MultilingualString CITY = new MultilingualString("city");

    @Inject
    HubRepository hubRepository;

    @Test
    public void create_read_update() {
        // Insert
        Hub hub = createHub();

        final long hubId = hubRepository.insertHub(hub);
        assertThat(hubId).isGreaterThan(0l);

        // Get
        hub = hubRepository.getHub(hubId);
        assertDefaultHub(hub);

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
                "25.015955 60.254351))"));
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
                "25.015955 60.254351))"));
        assertThat(hubs).isEmpty();

        hubs = findByDistance(fromWkt("POINT(25.016390 60.254160)"), 1);
        assertThat(hubs).hasSize(1);
        assertThat(hubs.get(0).id).isEqualTo(hubId);

        hubs = findByDistance(fromWkt("POINT(23.016392 58.254157)"), 1);
        assertThat(hubs).isEmpty();
    }

    private void assertDefaultHub(Hub hub) {
        assertThat(hub.name).isEqualTo(NAME);
        assertThat(hub.address.streetAddress).isEqualTo(STREET_ADDRESS);
        assertThat(hub.address.city).isEqualTo(CITY);
        assertThat(hub.address.postalCode).isEqualTo(POSTAL_CODE);
        assertThat(hub.location).isEqualTo(LOCATION);
        assertThat(hub.facilityIds).isEqualTo(FACILITY_IDS);
    }

    @Test
    public void search_exclusion_test() {
        final Hub hub1 = new Hub();
        hub1.name = NAME;
        hub1.location = LOCATION;
        hub1.facilityIds = FACILITY_IDS;
        hub1.address = new Address(STREET_ADDRESS, POSTAL_CODE, CITY);

        final Hub hub2 = new Hub();
        hub2.name = new MultilingualString("Nalmi");
        hub2.location = (Point) fromWkt("POINT(24.010563 59.251022)");
        hub2.facilityIds = ImmutableSet.of(3l, 4l, 5l);
        hub2.address = new Address(STREET_ADDRESS, POSTAL_CODE, CITY);

        final long hub1Id = hubRepository.insertHub(hub1);
        final long hub2Id = hubRepository.insertHub(hub2);

        // ids = hub1.id
        HubSearch search = new HubSearch();
        search.setIds(ImmutableSet.of(hub1Id));
        SearchResults<Hub> hubs = hubRepository.findHubs(search);
        assertThat(hubs.size()).isEqualTo(1);
        assertDefaultHub(hubs.get(0));

        // ids = hub2.id
        search.setIds(ImmutableSet.of(hub2Id));
        hubs = hubRepository.findHubs(search);
        assertThat(hubs.size()).isEqualTo(1);
        assertThat(hubs.get(0).id).isEqualTo(hub2Id);

        // ids = [hub1.id, hub2.id]
        search.setIds(ImmutableSet.of(hub1Id, hub2Id));
        hubs = hubRepository.findHubs(search);
        assertThat(hubs.size()).isEqualTo(2);
        assertThat(hubs.get(0).facilityIds).isEqualTo(hub1.facilityIds);
        assertThat(hubs.get(1).facilityIds).isEqualTo(hub2.facilityIds);

        // facilityIds in hub1.facilityIds
        search.setIds(null);
        search.setFacilityIds(ImmutableSet.of(2l));
        hubs = hubRepository.findHubs(search);
        assertThat(hubs.size()).isEqualTo(1);
        assertDefaultHub(hubs.get(0));

        // facilityIds in both hub1.facilityIds and hub2.facilityIds, with limit
        search.setFacilityIds(ImmutableSet.of(3l));
        search.setLimit(1);
        hubs = hubRepository.findHubs(search);
        assertThat(hubs.size()).isEqualTo(1);
        assertThat(hubs.hasMore).isEqualTo(true);
        assertDefaultHub(hubs.get(0));

        // facilityIds in both hub1.facilityIds and hub2.facilityIds, with offset
        search.setOffset(1);
        hubs = hubRepository.findHubs(search);
        assertThat(hubs.size()).isEqualTo(1);
        assertThat(hubs.hasMore).isEqualTo(false);
        assertThat(hubs.get(0).id).isEqualTo(hub2Id);
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
        HubSearch search = new HubSearch();
        assertResultOrder(hubRepository.findHubs(search), h1.id, h2.id);

        // name.fi desc
        search.setSort(new Sort("name.fi", DESC));
        assertResultOrder(hubRepository.findHubs(search), h2.id, h1.id);


        // name.sv desc
        // NOTE: This doesn't work on mac/postgresql because it's fi-collation is broken
        search.setSort(new Sort("name.sv", DESC));
        assertResultOrder(hubRepository.findHubs(search), h2.id, h1.id);

        // name.en asc
        search.setSort(new Sort("name.en", ASC));
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

    @Test
    public void unique_name() {
        Hub hub = createHub();
        hubRepository.insertHub(hub);
        verifyUniqueName(hub, "fi");
        verifyUniqueName(hub, "sv");
        verifyUniqueName(hub, "en");
    }

    private void verifyUniqueName(Hub hub, String lang) {
        hub.name = new MultilingualString("something else");
        try {
            hub.name.asMap().put(lang, NAME.asMap().get(lang));
            hubRepository.insertHub(hub);
            fail("should not allow duplicate names");
        } catch (ValidationException e) {
            assertThat(e.violations).hasSize(1);
            assertThat(e.violations.get(0).path).isEqualTo("name." + lang);
        }
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

    private List<Hub> findByGeometry(Geometry geometry) {
        HubSearch search = new HubSearch();
        search.setGeometry(geometry);
        return hubRepository.findHubs(search).results;
    }

    private List<Hub> findByDistance(Geometry geometry, double distance) {
        HubSearch search = new HubSearch();
        search.setGeometry(geometry);
        search.setMaxDistance(distance);
        return hubRepository.findHubs(search).results;
    }

}
