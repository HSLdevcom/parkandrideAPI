package fi.hsl.parkandride.rest.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fi.hsl.parkandride.application.event.parkingarea.CreateParkingAreaEvent;
import fi.hsl.parkandride.application.service.ParkingAreaService;
import fi.hsl.parkandride.rest.controller.fixture.RestDataFixture;
import fi.hsl.parkandride.rest.controller.fixture.RestEventFixture;

public class CreateParkingAreaTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ParkingAreaCommandController controller;

    @Mock
    private ParkingAreaService service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setMessageConverters(new MappingJackson2HttpMessageConverter()).build();

        when(service.createParkingArea(any(CreateParkingAreaEvent.class))).thenReturn(RestEventFixture.parkingAreaCreated(42L));
    }

    @Test
    public void http_status_is_created() throws Exception {
        mockMvc.perform(
                post("/parking-areas")
                        .content(RestDataFixture.defaultParkingAreaJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void response_renders_as_json() throws Exception {
        mockMvc.perform(
                post("/parking-areas")
                        .content(RestDataFixture.defaultParkingAreaJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.parkingAreaId").value("42"));
    }

    @Test
    public void location_header_is_present_in_response() throws Exception {
        mockMvc.perform(
                post("/parking-areas")
                        .content(RestDataFixture.defaultParkingAreaJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", Matchers.endsWith("/parking-areas/42")));
    }
}
