package fi.hsl.parkandride;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fi.hsl.parkandride.adapter.rest.domain.MultiLingualString;
import fi.hsl.parkandride.adapter.rest.domain.ParkingArea;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ParkingAreaTest {
    @Value("${local.server.port}")
    private int port;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void parking_area_can_be_added_and_queried() throws InterruptedException {
        when()
            .get("/parking-areas")
        .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(ContentType.JSON)
            .assertThat().body(is("[]"))
        ;

        given()
            .log().all()
            .header("Content-Type", "application/json;charset=UTF-8")
            .request().body(ruoholahti())
        .when()
            .post("/parking-areas")
        .then()
            .statusCode(HttpStatus.CREATED.value())
        ;
    }

    public static ParkingArea ruoholahti() {
        MultiLingualString name = new MultiLingualString();
        name.fi = "P-Ruoholahti, Helsinki";
        name.sv = "P-Gräsviken, Helsingfors";
        name.en = "P-Ruoholahti, Helsinki";
        ParkingArea parkingArea = new ParkingArea();
        parkingArea.setParkingAreaName(name);
        return parkingArea;
    };
}
