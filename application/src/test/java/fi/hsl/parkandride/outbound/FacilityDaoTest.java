package fi.hsl.parkandride.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableSortedSet;

import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.outbound.FacilityRepository;
import fi.hsl.parkandride.core.outbound.FacilitySearch;
import fi.hsl.parkandride.outbound.sql.QFacility;
import fi.hsl.parkandride.outbound.sql.QFacilityAlias;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class FacilityDaoTest {

    public static final String NAME = "Facility";

    private static final Polygon BORDER = polygon("POLYGON((" +
            "60.25055 25.010827, " +
            "60.250023 25.011867, " +
            "60.250337 25.012479, " +
            "60.250886 25.011454, " +
            "60.25055 25.010827))");

    public static final Polygon OVERLAPPING_AREA = polygon("POLYGON((" +
            "60.251343 25.011942, " +
            "60.250454 25.011717, " +
            "60.250848 25.013487, " +
            "60.251343 25.011942))");

    public static final Polygon NON_OVERLAPPING_AREA = polygon("POLYGON((" +
            "60.251343 25.011942, " +
            "60.250816 25.012211, " +
            "60.250848 25.013487, " +
            "60.251343 25.011942))");
    public static final ImmutableSortedSet<String> ALIASES = ImmutableSortedSet.of("alias", "blias");

    @Inject TestHelper testHelper;

    @Inject
    FacilityRepository facilityDao;

//    @Before
//    public void initDao() {
//        facilityDao = new FacilityDao(queryFactory);
//    }

    @After
    public void cleanup() {
        testHelper.clear(QFacilityAlias.facilityAlias, QFacility.facility);
    }

    @Test
    public void cru() {
        Facility facility = createFacility();

        // Insert
        long id = facilityDao.insertFacility(facility);
        assertThat(id).isGreaterThan(0);
        assertThat(facility.id).isEqualTo(id);

        // Find by id
        facility = facilityDao.getFacility(id);
        assertThat(facility).isNotNull();
        assertThat(facility.name).isEqualTo(NAME);
        assertThat(facility.aliases).isEqualTo(ALIASES);

        // Update
        facility.name = "changed name";
        facilityDao.updateFacility(facility);
        assertThat(facilityDao.getFacility(id).name).isEqualTo("changed name");

        // Find by geometry
        List<Facility> facilities = findByGeometry(OVERLAPPING_AREA);
        assertThat(facilities).hasSize(1);
        assertThat(facilities.get(0).aliases).isEqualTo(ALIASES);

        // Not found by geometry
        assertThat(findByGeometry(NON_OVERLAPPING_AREA)).isEmpty();
    }

    private List<Facility> findByGeometry(Polygon geometry) {
        FacilitySearch search = new FacilitySearch();
        search.within = geometry;
        return facilityDao.findFacilities(search);
    }

    private Facility createFacility() {
        Facility facility = new Facility();
        facility.id = 0l;
        facility.name = NAME;
        facility.border = BORDER;
        facility.aliases = ALIASES;
        return facility;
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_throws_an_exception_if_not_found() {
        facilityDao.getFacility(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throws_an_exception_if_not_found() {
        facilityDao.updateFacility(createFacility());
    }

    private static Polygon polygon(String wktShape) {
        return (Polygon) Wkt.newWktDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wktShape);
    }

}
