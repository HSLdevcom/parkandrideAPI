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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
                .andDo(print())
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
                                fieldWithPath("pricing").description("TODO, deep"),
                                fieldWithPath("unavailableCapacities").description("TODO, deep"),
                                fieldWithPath("aliases").description("TODO"),
                                fieldWithPath("ports").description("TODO, deep"),
                                fieldWithPath("services").description("TODO"),
                                fieldWithPath("contacts").description("TODO, deep"),
                                fieldWithPath("paymentInfo").description("TODO, deep"),
                                fieldWithPath("openingHours").description("TODO, deep")));
    }


    // helpers

    public String loginApiUserForFacility(long facilityId) {
        Facility facility = facilityRepository.getFacility(facilityId);
        devHelper.deleteUsers();
        devHelper.createOrUpdateUser(new NewUser(1L, "dummyoperator", OPERATOR_API, facility.operatorId, "secret"));
        return devHelper.login("dummyoperator").token;
    }
}
