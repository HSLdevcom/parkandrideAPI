// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.MultilingualString;
import fi.hsl.parkandride.core.domain.Region;
import fi.hsl.parkandride.core.domain.RegionWithHubs;
import org.geolatte.geom.Points;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class RegionDaoTest extends AbstractDaoTest {

    @Inject
    RegionDao regionDao;

    @Inject
    HubDao hubDao;

    private long helsinkiHubId;
    private long helsinkiHub2Id;
    private long keravaHubId;

    @Before
    public void initFixture() {
        devHelper.deleteAll();
        final Collection<Region> regions = regionDao.getRegions();
        final Region region = getRegionWithName(regions, "Helsinki");

        final Hub helsinkiHub = new Hub();
        helsinkiHub.name = new MultilingualString("Keskusta");
        helsinkiHub.location = Points.create2D(24.95220318379312, 60.16094413640474, region.area.getCrsId());
        helsinkiHubId = hubDao.insertHub(helsinkiHub);

        final Hub helsinkiHub2 = new Hub();
        helsinkiHub2.name = new MultilingualString("Keskusta 2");
        helsinkiHub2.location = Points.create2D(24.95220318379312, 60.16094413640474, region.area.getCrsId());
        helsinkiHub2Id = hubDao.insertHub(helsinkiHub2);

        final Hub keravaHub = new Hub();
        keravaHub.name = new MultilingualString("Kerava");
        keravaHub.location = Points.create2D(25.106506567382755, 60.40474204568207, region.area.getCrsId());
        keravaHubId = hubDao.insertHub(keravaHub);
    }

    @Test
    public void getRegionsWithHubsReturnsHubIdsForCorrectRegions() {
        final Collection<RegionWithHubs> allRegions = regionDao.regionsWithHubs();
        assertThat(allRegions).hasSameSizeAs(regionDao.getRegions());

        final List<RegionWithHubs> regionsWithHubs = allRegions.stream()
                .filter(rwh -> !rwh.hubIds.isEmpty())
                .collect(toList());

        assertThat(regionsWithHubs).hasSize(2);
        assertThat(getRegionWithName(regionsWithHubs, "Helsinki").hubIds)
                .containsOnly(helsinkiHubId, helsinkiHub2Id);

        assertThat(getRegionWithName(regionsWithHubs, "Kerava").hubIds)
                .containsExactly(keravaHubId);
    }

    private static <T extends Region> T getRegionWithName(Collection<T> regions, String name) {
        return regions.stream()
                .filter(r -> r.name.fi.equals(name))
                .findFirst()
                .get();
    }
}