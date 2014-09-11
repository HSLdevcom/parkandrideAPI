package fi.hsl.parkandride.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mysema.query.sql.postgres.PostgresQueryFactory;

import fi.hsl.parkandride.config.JdbcConfiguration;
import fi.hsl.parkandride.core.domain.Facility;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JdbcConfiguration.class)
public class FacilityDaoTest {

    @Inject
    PostgresQueryFactory queryFactory;

    private FacilityDao facilityDao;

    @Before
    public void initDao() {
        facilityDao = new FacilityDao(queryFactory);
    }

    @Test
    public void insert_facility() {
        Facility facility = new Facility();
        facility.name = "Facility";
        facility.border = polygon("POLYGON((" +
                "2784304.4 8455636.2, " +
                "2784218.43 8455719.52, " +
                "2784269.14 8455772.95, " +
                "2784358.75 8455688.77, " +
                "2784304.4 8455636.2))");
        long id = facilityDao.insertFacility(facility);
        assertThat(id).isGreaterThan(0);
    }

    private static Polygon polygon(String wktShape) {
        return (Polygon) Wkt.newWktDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wktShape);
    }

}
