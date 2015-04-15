// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.service;

import static com.google.common.collect.Lists.newArrayList;
import static fi.hsl.parkandride.core.domain.CapacityType.CAR;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.hsl.parkandride.back.FacilityDao;
import fi.hsl.parkandride.back.FacilityDaoTest;
import fi.hsl.parkandride.core.domain.*;

public class ValidationServiceTest {

    private static ValidationService validationService = new ValidationService();

    private static class MyType {
        @Min(1)
        int integer;

        @Valid
        Map<String, MyType> map = Maps.newHashMap();

        public MyType() {}

        public MyType(int integer){
            this.integer = integer;
        }
    }

    @Test
    public void min_violation() {
        try {
            validationService.validate(new MyType());
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            List<Violation> violations = e.violations;
            assertThat(violations).hasSize(1);
            Violation violation = violations.get(0);
            assertMinViolation(violation);
        }
    }

    @Test
    public void nested_violation() {
        try {
            MyType myType = new MyType(1);
            myType.map.put("KEY", new MyType());

            validationService.validate(myType);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            List<Violation> violations = e.violations;
            assertThat(violations).hasSize(1);
            Violation violation = violations.get(0);
            assertMinViolation(violation, "map.KEY.");
        }
    }

    @Test
    public void valid_facility() {
        Facility facility = FacilityDaoTest.createFacility(1l, new FacilityContacts(1l, 1l, 1l));
        validationService.validate(facility);
    }

    @Test
    public void null_elements() {
        try {
            Facility facility = FacilityDaoTest.createFacility(1l, new FacilityContacts(1l, 1l, 1l));
            facility.builtCapacity = new HashMap<>();
            facility.builtCapacity.put(CAR, null);
            facility.aliases = new HashSet<>(asList(""));
            facility.pricing = withNull(newArrayList());
            facility.services = withNull(new NullSafeSortedSet<>());
            facility.unavailableCapacities = withNull(newArrayList());
            facility.paymentInfo.paymentMethods = withNull(new NullSafeSortedSet<>());
            validationService.validate(facility);
            fail("Expected ValidationException");
        } catch (ValidationException e) {
            List<Violation> violations = e.violations;
            assertThat(violations).hasSize(6);
        }
    }

    private static <T, C extends Collection<T>> C withNull(C coll) {
        coll.add(null);
        return coll;
    }

    private void assertMinViolation(Violation violation) {
        assertMinViolation(violation, "");
    }

    private void assertMinViolation(Violation violation, String pathPrefix) {
        assertThat(violation.type).isEqualTo("Min");
        assertThat(violation.args).isEqualTo(ImmutableMap.of("value", 1l));
        assertThat(violation.path).isEqualTo(pathPrefix + "integer");
    }
}
