// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.docs;

import fi.hsl.parkandride.back.Dummies;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.Facility;
import fi.hsl.parkandride.core.domain.NewUser;
import fi.hsl.parkandride.front.UrlSchema;
import fi.hsl.parkandride.itest.AbstractIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.config.RestDocumentationConfigurer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static fi.hsl.parkandride.core.domain.Role.OPERATOR_API;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest("spring.jackson.serialization.indent_output:true")
public class ApiDocumentation extends AbstractIntegrationTest {

    @Inject Dummies dummies;
    @Inject FacilityRepository facilityRepository;
    @Inject WebApplicationContext context;

    private MockMvc mockMvc;

    private String authToken;
    private long facilityId;

    @Before
    public void init() {
        devHelper.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(new RestDocumentationConfigurer())
                .build();
        facilityId = dummies.createFacility();
        authToken = loginApiUserForFacility(facilityId);
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
    public void facilityExample() throws Exception {
        MockHttpServletRequestBuilder request = get(UrlSchema.FACILITY, facilityId);
        this.mockMvc.perform(request)
                //.andDo(print())
                .andExpect(status().isOk())
                .andDo(document("facility-example")
                        .withResponseFields(
                                fieldWithPath("id").description("TODO"),
                                fieldWithPath("name").description("TODO"),
                                fieldWithPath("location").description("TODO"),
                                fieldWithPath("operatorId").description("TODO"),
                                fieldWithPath("status").description("TODO"),
                                fieldWithPath("pricingMethod").description("TODO"),
                                fieldWithPath("statusDescription").description("TODO"),
                                fieldWithPath("builtCapacity").description("Built capacity by CapacityType, may be split or shared by different Usage types as defined by pricing"),
                                fieldWithPath("usages").description("Read-only summary of distinct pricing rows' usages"),
                                fieldWithPath("pricing[].usage").description("TODO"),
                                fieldWithPath("pricing[].capacityType").description("TODO"),
                                fieldWithPath("pricing[].maxCapacity").description("TODO"),
                                fieldWithPath("pricing[].dayType").description("TODO"),
                                fieldWithPath("pricing[].time.from").description("TODO"),
                                fieldWithPath("pricing[].time.until").description("TODO"),
                                fieldWithPath("pricing[].price").description("TODO"),
                                fieldWithPath("unavailableCapacities[].capacityType").description("TODO"),
                                fieldWithPath("unavailableCapacities[].usage").description("TODO"),
                                fieldWithPath("unavailableCapacities[].capacity").description("TODO"),
                                fieldWithPath("aliases").description("TODO"),
                                fieldWithPath("ports[].location").description("TODO"),
                                fieldWithPath("ports[].entry").description("TODO"),
                                fieldWithPath("ports[].exit").description("TODO"),
                                fieldWithPath("ports[].pedestrian").description("TODO"),
                                fieldWithPath("ports[].bicycle").description("TODO"),
                                fieldWithPath("ports[].address").description("TODO"),
                                fieldWithPath("ports[].info").description("TODO"),
                                fieldWithPath("services").description("TODO"),
                                fieldWithPath("contacts.emergency").description("Emergency contact ID"),
                                fieldWithPath("contacts.operator").description("Operator contact ID"),
                                fieldWithPath("contacts.service").description("Service contact ID"),
                                fieldWithPath("paymentInfo.detail").description("TODO"),
                                fieldWithPath("paymentInfo.url").description("TODO"),
                                fieldWithPath("paymentInfo.paymentMethods").description("TODO"),
                                fieldWithPath("openingHours.byDayType").description("Read-only summary of pricing rows' opening hours"),
                                fieldWithPath("openingHours.info").description("TODO"),
                                fieldWithPath("openingHours.url").description("TODO")));
    }


    // helpers

    public String loginApiUserForFacility(long facilityId) {
        Facility facility = facilityRepository.getFacility(facilityId);
        devHelper.deleteUsers();
        devHelper.createOrUpdateUser(new NewUser(1L, "dummyoperator", OPERATOR_API, facility.operatorId, "secret"));
        return devHelper.login("dummyoperator").token;
    }
}
