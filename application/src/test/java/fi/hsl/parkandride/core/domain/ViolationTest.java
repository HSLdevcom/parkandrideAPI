package fi.hsl.parkandride.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.mockito.Mockito;

public class ViolationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void capacityViolation_is_translated() {
        for (CapacityType ct : CapacityType.values()) {
            assertThat(path(capacityViolation(ct))).isEqualTo("capacities." + ct.name() + ".built");
        }
    }

    @Test
    public void nonCapacityViolation_is_not_translated() {
        assertThat(path(nameViolation())).isEqualTo("name.fi");
    }

    private String path(ConstraintViolation<Facility> cv) {
        return new Violation(cv).path;
    }

    private ConstraintViolation<Facility> capacityViolation(CapacityType t) {
        Facility f = validFacility();
        f.capacities.put(t, new Capacity(-1));
        return toFacilityConstraintViolation(f);
    }

    private ConstraintViolation<Facility> nameViolation() {
        Facility f = validFacility();
        f.name = new MultilingualString("", "Test", "Test");
        return toFacilityConstraintViolation(f);
    }

    private ConstraintViolation<Facility> toFacilityConstraintViolation(Facility f) {
        Set<ConstraintViolation<Facility>> violations = validator.validate(f);
        assertThat(violations).hasSize(1);
        return violations.iterator().next();
    }

    private Facility validFacility() {
        Facility f = new Facility();
        f.name = new MultilingualString("Test", "Test", "Test");
        f.location = Mockito.mock(Geometry.class);

        Set<ConstraintViolation<Facility>> violations = validator.validate(f);
        assertThat(violations).isEmpty();

        return f;
    }
}