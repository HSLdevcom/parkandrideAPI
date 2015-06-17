// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.back.FacilityDaoTest;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.FacilityService;
import fi.hsl.parkandride.core.service.PredictionService;
import fi.hsl.parkandride.front.UrlSchema;
import fi.hsl.parkandride.itest.AbstractIntegrationTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.util.Collections;

import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.payload.FieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest({
        "server.port:0",
        "spring.jackson.serialization.indent_output:true"})
public class ApiDocumentation extends AbstractIntegrationTest {

    @Inject Dummies dummies;
    @Inject FacilityRepository facilityRepository;
    @Inject FacilityService facilityService;
    @Inject PredictionService predictionService;
    @Inject WebApplicationContext context;
    @Inject ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private DateTimeZone originalDateTimeZone;

    private String authToken;
    private long facilityId;
    private NewUser currentUser;

    @Before
    public void init() {
        devHelper.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(new RestDocumentationConfigurer())
                .build();
        facilityId = dummies.createFacility();
        authToken = loginApiUserForFacility(facilityId);
    }

    @Before
    public void setTimezone() {
        originalDateTimeZone = DateTimeZone.getDefault();
        DateTimeZone.setDefault(DateTimeZone.forID("Europe/Helsinki"));
    }

    @After
    public void restoreTimezone() {
        DateTimeZone.setDefault(originalDateTimeZone);
    }


    @Test
    public void jsonDefaultExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITIES);
        mockMvc.perform(withBaseUrl(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("json-default-example"));
    }

    @Test
    public void jsonSuffixExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITIES + ".json");
        mockMvc.perform(withBaseUrl(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("json-suffix-example"));
    }

    @Test
    public void geojsonSuffixExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITIES + ".geojson");
        mockMvc.perform(withBaseUrl(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(UrlSchema.GEOJSON))
                .andDo(document("geojson-suffix-example"));
    }

    @Test
    public void jsonHeaderExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITIES)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(withBaseUrl(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("json-header-example"));
    }

    @Test
    public void geojsonHeaderExample() throws Exception {
        dummies.createHub();

        MockHttpServletRequestBuilder request = get(UrlSchema.HUBS)
                .accept(UrlSchema.GEOJSON);
        mockMvc.perform(withBaseUrl(request))
                .andExpect(status().isOk())
                .andExpect(content().contentType(UrlSchema.GEOJSON))
                .andDo(document("geojson-header-example"));
    }

    @Test
    public void limitOffsetSortExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITIES)
                .param("limit", "10")
                .param("offset", "4")
                .param("sort.by", "name.fi")
                .param("sort.dir", Sort.Dir.ASC.name());
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(withBaseUrl(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("results", is(empty())))
                .andExpect(jsonPath("hasMore", is(false)))
                .andDo(document("limit-offset-sort-example"));
    }

    @Test
    public void authenticationExample() throws Exception {
        MockHttpServletRequestBuilder request = put(UrlSchema.FACILITY_UTILIZATION, facilityId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(document("authentication-example"));
    }

    @Test
    public void facilityGeojsonExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITY + ".geojson", facilityId);
        this.mockMvc.perform(withBaseUrl(request))
                .andExpect(status().isOk())
                .andDo(document("facility-geojson-example"));
    }


    @Test
    public void facilityExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITY, facilityId);
        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(document("facility-example")
                        .withResponseFields(
                                fieldWithPath("id").description("Facility ID: `/api/v1/facilities/{id}`"),
                                fieldWithPath("name").description("Localized name"),
                                fieldWithPath("location").description("Facility location, GeoJSON Polygon"),
                                fieldWithPath("operatorId").description("Facility operator ID: `/api/v1/operators/{operatorId}`"),
                                fieldWithPath("status").description("Status, one of `/api/v1/facility-statuses`"),
                                fieldWithPath("pricingMethod").description("Pricing method, one of `/api/v1/pricing-methods`"),
                                fieldWithPath("statusDescription").description("Localized description of status (OPTIONAL)"),
                                fieldWithPath("builtCapacity").description("Built capacity by CapacityType, may be split or shared by different Usage types as defined by pricing"),
                                fieldWithPath("usages").description("Read-only summary of distinct pricing rows' usages"),
                                fieldWithPath("pricing[].usage").description("Usage type, one of `/api/v1/usages`"),
                                fieldWithPath("pricing[].capacityType").description("Capacity type, one of `/api/v1/capacity-types`"),
                                fieldWithPath("pricing[].maxCapacity").description("Max capacity"),
                                fieldWithPath("pricing[].dayType").description("Day type, one of `/api/v1/day-types`"),
                                fieldWithPath("pricing[].time.from").description("Start time `hh[:mm]`"),
                                fieldWithPath("pricing[].time.until").description("End time `hh[:mm]`, exclusive"),
                                fieldWithPath("pricing[].price").description("Localized description of price (OPTIONAL)"),
                                fieldWithPath("unavailableCapacities[].capacityType").description("Unavailable capacity type"),
                                fieldWithPath("unavailableCapacities[].usage").description("Unavailable usage"),
                                fieldWithPath("unavailableCapacities[].capacity").description("Unavailable capacity"),
                                fieldWithPath("aliases").description("Alternative labels, list of strings"),
                                fieldWithPath("ports[].location").description("Port location, GeoJSON Point"),
                                fieldWithPath("ports[].entry").description("Entry for cars"),
                                fieldWithPath("ports[].exit").description("Exit for cars"),
                                fieldWithPath("ports[].pedestrian").description("Entry/exit by foot"),
                                fieldWithPath("ports[].bicycle").description("Entry/exit for bicycles"),
                                fieldWithPath("ports[].address").description("Port address (OPTIONAL)"),
                                fieldWithPath("ports[].info").description("Localized port info (OPTIONAL)"),
                                fieldWithPath("services").description("List of services provided, `/api/v1/services`"),
                                fieldWithPath("contacts.emergency").description("Emergency contact ID, `/api/v1/contacts/{contactId}`"),
                                fieldWithPath("contacts.operator").description("Operator contact ID, `/api/v1/contacts/{contactId}`"),
                                fieldWithPath("contacts.service").description("Service contact ID, `/api/v1/contacts/{contactId}`"),
                                fieldWithPath("paymentInfo.detail").description("Localized details of payment options (OPTIONAL)"),
                                fieldWithPath("paymentInfo.url").description("Localized link to payment options (OPTIONAL)"),
                                fieldWithPath("paymentInfo.paymentMethods").description("Allowed payment methods"),
                                fieldWithPath("openingHours.byDayType").description("Read-only summary of pricing rows' opening hours"),
                                fieldWithPath("openingHours.info").description("Localized info about opening hours (OPTIONAL)"),
                                fieldWithPath("openingHours.url").description("Localized link to opening hours info (OPTIONAL)")));
    }

    @Test
    public void enumerationCapacityTypesExample() throws Exception {
        mockMvc.perform(get(UrlSchema.CAPACITY_TYPES))
                .andExpect(status().isOk())
                .andDo(document("enumeration-capacity-types-example"));
    }

    @Test
    public void enumerationDayTypesExample() throws Exception {
        mockMvc.perform(get(UrlSchema.DAY_TYPES))
                .andExpect(status().isOk())
                .andDo(document("enumeration-day-types-example"));
    }

    @Test
    public void enumerationServicesExample() throws Exception {
        mockMvc.perform(get(UrlSchema.SERVICES))
                .andExpect(status().isOk())
                .andDo(document("enumeration-services-example"));
    }

    @Test
    public void enumerationPaymentMethodsExample() throws Exception {
        mockMvc.perform(get(UrlSchema.PAYMENT_METHODS))
                .andExpect(status().isOk())
                .andDo(document("enumeration-payment-methods-example"));
    }

    @Test
    public void enumerationFacilityStatusesExample() throws Exception {
        mockMvc.perform(get(UrlSchema.FACILITY_STATUSES))
                .andExpect(status().isOk())
                .andDo(document("enumeration-facility-statuses-example"));
    }

    @Test
    public void enumerationPricingMethodsExample() throws Exception {
        mockMvc.perform(get(UrlSchema.PRICING_METHODS))
                .andExpect(status().isOk())
                .andDo(document("enumeration-pricing-methods-example"));
    }

    @Test
    public void allOperatorsExample() throws Exception {
        mockMvc.perform(get(UrlSchema.OPERATORS))
                .andExpect(status().isOk())
                .andDo(document("all-operators-example"));
    }

    @Test
    public void operatorsDetailsExample() throws Exception {
        mockMvc.perform(get(UrlSchema.OPERATOR, 1))
                .andExpect(status().isOk())
                .andDo(document("operator-details-example")
                        .withResponseFields(
                                fieldWithPath("id").description("Contact ID: `/api/v1/operators/{id}`"),
                                fieldWithPath("name").description("Localized name")
                        ));
    }

    @Test
    public void allContactsExample() throws Exception {
        mockMvc.perform(get(UrlSchema.CONTACTS))
                .andExpect(status().isOk())
                .andDo(document("all-contacts-example"));
    }

    @Test
    public void findContactsByIdsExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(get(UrlSchema.CONTACTS).param("ids", "101", "102"))
                .andExpect(status().isOk())
                .andDo(document("find-contacts-by-ids-example"));
    }

    @Test
    public void findContactsByOperatorIdExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(get(UrlSchema.CONTACTS).param("operatorId", "42"))
                .andExpect(status().isOk())
                .andDo(document("find-contacts-by-operator-id-example"));
    }

    @Test
    public void contactsDetailsExample() throws Exception {
        mockMvc.perform(get(UrlSchema.CONTACT, 1))
                .andExpect(status().isOk())
                .andDo(document("contact-details-example")
                        .withResponseFields(
                                fieldWithPath("id").description("Contact ID: `/api/v1/contacts/{id}`"),
                                fieldWithPath("name").description("Localized name"),
                                fieldWithPath("operatorId").type(NUMBER).description("Contact operator ID: `/api/v1/operators/{operatorId}` (OPTIONAL)"),
                                fieldWithPath("email").description("Email address (OPTIONAL)"),
                                fieldWithPath("phone").description("Phone number (OPTIONAL)"),
                                fieldWithPath("address").description("Contact address (OPTIONAL)"),
                                fieldWithPath("address.streetAddress").description("Localized street name (OPTIONAL)"),
                                fieldWithPath("address.postalCode").description("Postal code (OPTIONAL)"),
                                fieldWithPath("address.city").description("Localized city name (OPTIONAL)"),
                                fieldWithPath("openingHours").description("Localized opening hours (OPTIONAL)"),
                                fieldWithPath("info").description("Localized information (OPTIONAL)")
                        ));
    }

    @Test
    public void findFacilitiesByStatusesExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(get(UrlSchema.FACILITIES).param("statuses", FacilityStatus.IN_OPERATION.name(), FacilityStatus.EXCEPTIONAL_SITUATION.name()))
                .andExpect(status().isOk())
                .andDo(document("find-facilities-by-statuses-example"));
    }

    @Test
    public void findFacilitiesByIdsExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(get(UrlSchema.FACILITIES).param("ids", "11", "12"))
                .andExpect(status().isOk())
                .andDo(document("find-facilities-by-ids-example"));
    }

    @Test
    public void findFacilitiesByGeometryExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        String geometry = FacilityDaoTest.OVERLAPPING_AREA.asText();
        geometry = geometry.substring(geometry.indexOf(";") + 1);
        mockMvc.perform(get(UrlSchema.FACILITIES).param("geometry", geometry))
                .andExpect(status().isOk())
                .andDo(document("find-facilities-by-geometry-example"));
    }

    @Test
    public void findFacilitiesByGeometryMaxDistanceExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        String geometry = FacilityDaoTest.OVERLAPPING_AREA.asText();
        geometry = geometry.substring(geometry.indexOf(";") + 1);
        mockMvc.perform(get(UrlSchema.FACILITIES).param("geometry", geometry).param("maxDistance", "123.45"))
                .andExpect(status().isOk())
                .andDo(document("find-facilities-by-geometry-max-distance-example"));
    }

    @Test
    public void findFacilitiesSummaryExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(get(UrlSchema.FACILITIES).param("summary", "true"))
                .andExpect(status().isOk())
                .andDo(document("find-facilities-summary-example"));
    }

    @Test
    public void utilizationExample() throws Exception {
        facilityService.registerUtilization(facilityId, Collections.singletonList(newUtilization()), currentUser);

        mockMvc.perform(get(UrlSchema.FACILITY_UTILIZATION, facilityId))
                .andExpect(status().isOk())
                .andDo(document("utilization-example")
                        .withResponseFields(
                                fieldWithPath("[]facilityId").description("The facility"),
                                fieldWithPath("[]capacityType").description("The capacity type"),
                                fieldWithPath("[]usage").description("The usage"),
                                fieldWithPath("[]timestamp").description("When this information was last updated"),
                                fieldWithPath("[]spacesAvailable").description("Number of available parking spaces for this facility, capacity type and usage combination")));
    }

    @Test
    public void utilizationUpdateExample() throws Exception {
        MockHttpServletRequestBuilder request = put(UrlSchema.FACILITY_UTILIZATION, facilityId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonList(newUtilization())));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(document("utilization-update-example"));
    }

    @Test
    public void predictionExample() throws Exception {
        facilityService.registerUtilization(facilityId, Collections.singletonList(newUtilization()), currentUser);
        predictionService.updatePredictions();

        mockMvc.perform(get(UrlSchema.FACILITY_PREDICTION_ABSOLUTE, facilityId, new DateTime().plusMinutes(15)))
                .andExpect(status().isOk())
                .andDo(document("prediction-absolute-example"));
        mockMvc.perform(get(UrlSchema.FACILITY_PREDICTION_RELATIVE, facilityId, "0015"))
                .andExpect(status().isOk())
                .andDo(document("prediction-relative-example"));
    }

    @Test
    public void allHubsExample() throws Exception {
        dummies.createHub();

        mockMvc.perform(get(UrlSchema.HUBS))
                .andExpect(status().isOk())
                .andDo(document("all-hubs-example"));
    }

    @Test
    public void hubGeojsonExample() throws Exception {
        long hubId = dummies.createHub();

        mockMvc.perform(withBaseUrl(get(UrlSchema.HUB + ".geojson", hubId)))
                .andExpect(status().isOk())
                .andDo(document("hub-geojson-example"));
    }

    @Test
    public void hubDetailsExample() throws Exception {
        long hubId = dummies.createHub();

        mockMvc.perform(get(UrlSchema.HUB, hubId))
                .andExpect(status().isOk())
                .andDo(document("hub-details-example")
                        .withResponseFields(
                                fieldWithPath("id").description("Hub ID: `/api/v1/hubs/{id}`"),
                                fieldWithPath("name").description("Localized name"),
                                fieldWithPath("location").description("Hub location, GeoJSON Point"),
                                fieldWithPath("address").description("Hub address (OPTIONAL)"),
                                fieldWithPath("address.streetAddress").description("Localized street name (OPTIONAL)"),
                                fieldWithPath("address.postalCode").description("Postal code (OPTIONAL)"),
                                fieldWithPath("address.city").description("Localized city name (OPTIONAL)"),
                                fieldWithPath("facilityIds").description("A list of facility IDs (number), `/api/v1/facilities/{id}`")
                        ));
    }

    @Test
    public void findHubsByIdsExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(get(UrlSchema.HUBS).param("ids", "11", "12"))
                .andExpect(status().isOk())
                .andDo(document("find-hubs-by-ids-example"));
    }

    @Test
    public void findHubsByFacilityIdsExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        mockMvc.perform(get(UrlSchema.HUBS).param("facilityIds", "11", "12"))
                .andExpect(status().isOk())
                .andDo(document("find-hubs-by-facility-ids-example"));
    }

    @Test
    public void findHubsByGeometryExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        String geometry = FacilityDaoTest.OVERLAPPING_AREA.asText();
        geometry = geometry.substring(geometry.indexOf(";") + 1);
        mockMvc.perform(get(UrlSchema.HUBS).param("geometry", geometry))
                .andExpect(status().isOk())
                .andDo(document("find-hubs-by-geometry-example"));
    }

    @Test
    public void findHubsByGeometryMaxDistanceExample() throws Exception {
        // XXX: not really testing whether all the parameters work
        String geometry = FacilityDaoTest.OVERLAPPING_AREA.asText();
        geometry = geometry.substring(geometry.indexOf(";") + 1);
        mockMvc.perform(get(UrlSchema.HUBS).param("geometry", geometry).param("maxDistance", "123.45"))
                .andExpect(status().isOk())
                .andDo(document("find-hubs-by-geometry-max-distance-example"));
    }


    // helpers

    private RequestBuilder withBaseUrl(final MockHttpServletRequestBuilder builder) {
        return new RequestBuilder() {
            @Override
            public MockHttpServletRequest buildRequest(ServletContext servletContext) {
                MockHttpServletRequest request = builder.buildRequest(servletContext);
                request.setServerName("p.hsl.fi");
                request.setRemotePort(80);
                return request;
            }
        } ;
    }

    public String loginApiUserForFacility(long facilityId) {
        Facility facility = facilityRepository.getFacility(facilityId);
        devHelper.deleteUsers();
        currentUser = new NewUser(1L, "dummyoperator", OPERATOR_API, facility.operatorId, "secret");
        devHelper.createOrUpdateUser(currentUser);
        return devHelper.login("dummyoperator").token;
    }

    public static Utilization newUtilization() {
        Utilization u = new Utilization();
        u.capacityType = CapacityType.CAR;
        u.usage = Usage.HSL_TRAVEL_CARD;
        u.spacesAvailable = 30;
        u.timestamp = new DateTime();
        return u;
    }
}
