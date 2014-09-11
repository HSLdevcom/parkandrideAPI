package fi.hsl.parkandride.outbound;

import static org.assertj.core.api.Assertions.*;

import javax.inject.Inject;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.QueryFactory;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import fi.hsl.parkandride.config.JdbcConfiguration;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.outbound.FacilityRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JdbcConfiguration.class)
@TransactionConfiguration
public class FacilityDaoTest {

    @Inject
    PostgresQueryFactory queryFactory;

    private FacilityDao facilityDao;

    @Before
    public void initDao() {
        facilityDao = new FacilityDao(queryFactory);
    }

    @Test
    @Transactional
    public void insert_facility() {
        Facility facility = new Facility();
        facility.name = "Facility";
        facility.border = polygon("POLYGON((" +
                "2784304.4 8455636.2, " +
                "2784218.43 8455719.52, " +
                "2784269.14 8455772.95, " +
                "2784358.75 8455688.77, " +
                "2784304.4 8455636.2))");

    }

    private static Geometry polygon(String wktShape) {
        return Wkt.newWktDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wktShape);
    }
}
