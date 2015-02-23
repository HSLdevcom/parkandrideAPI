package fi.hsl.parkandride.itest;

import static com.jayway.restassured.RestAssured.when;
import static fi.hsl.parkandride.core.domain.FacilityStatus.IN_OPERATION;
import static fi.hsl.parkandride.core.domain.PricingMethod.PARK_AND_RIDE_247_FREE;
import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;

import fi.hsl.parkandride.back.ContactDao;
import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.OperatorDao;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalWrite;

public class FacilityITest extends AbstractIntegrationTest{

    @Inject
    private ContactDao contactDao;

    @Inject
    private FacilityDao facilityDao;

    @Inject
    private OperatorDao operatorDao;

    private Facility f;

    private String authToken;

    @Before
    @TransactionalWrite
    public void initFixture() {
        devHelper.deleteAll();

        Operator o = new Operator();
        o.id = 1l;
        o.name = new MultilingualString("smooth operator");

        Contact c = new Contact();
        c.id = 1L;
        c.name = new MultilingualString("minimal contact");

        Facility f = new Facility();
        f.id = 1L;
        f.status = IN_OPERATION;
        f.pricingMethod = PARK_AND_RIDE_247_FREE;
        f.name = new MultilingualString("minimal facility");
        f.operatorId = 1l;
        f.location = Spatial.fromWktPolygon("POLYGON((24.941439860329403 60.177428123791714, 24.941276245579488 60.17732941468276, " +
                "24.941718810066945 60.17714266690834, 24.941893153652913 60.17724671222794, 24.941439860329403 60.177428123791714))");
        f.contacts = new FacilityContacts(c.id, c.id);

        operatorDao.insertOperator(o, o.id);
        contactDao.insertContact(c, c.id);
        facilityDao.insertFacility(f, f.id);

        devHelper.createOrUpdateUser(new NewUser(1l, "operator", OPERATOR_API, f.operatorId, "operator"));
        String authToken = devHelper.login("operator").token;
    }

    @Test
    public void facilities_can_queried() {
        when()
            .get("api/v1/facilities")
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.JSON)
            .assertThat()
                .body("results[0].id", equalTo(1))
                .body("hasMore", is(false));
        ;
    }

    @Test
    public void facilities_can_be_searched_by_intersection() {
        when()
            .get("api/v1/facilities?geometry={geometry}", ImmutableMap.of(
                    "geometry", "POLYGON((24.94093353344757 60.17723257643158, 24.94269306256134 60.17720056250843, " +
                        "24.94179184033234 60.176650985295, 24.94093353344757 60.17723257643158))"))
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.JSON)
            .assertThat()
                .body("results[0].id", equalTo(1))
                .body("hasMore", is(false));

        when()
            .get("api/v1/facilities?geometry={geometry}", ImmutableMap.of(
                    "geometry", "LINESTRING( 24.941448517578433 60.176923107200565, 24.942339010971374 60.17740331682813)"))
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.JSON)
            .assertThat()
                .body("results", hasItems())
                .body("hasMore", is(false));
    }

    @Test
    public void facilities_can_be_searched_by_distance() {
        when()
            .get("api/v1/facilities?geometry={geometry}&maxDistance={maxDistance}", ImmutableMap.of(
                    "geometry", "LINESTRING( 24.941448517578433 60.176923107200565, 24.942339010971374 60.17740331682813)",
                    "maxDistance", "10")) // meters on Postgis or degrees on H2
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.JSON)
                .assertThat()
                .body("results[0].id", equalTo(1))
                .body("hasMore", is(false));

        when()
            .get("api/v1/facilities?geometry={geometry}&maxDistance={maxDistance}", ImmutableMap.of(
                    "geometry", "POINT(-0.1218811390209618 51.47755021995749)", // London
                    "maxDistance", "10"))
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.JSON)
            .assertThat()
                .body("results", hasItems())
                .body("hasMore", is(false));
    }

}
