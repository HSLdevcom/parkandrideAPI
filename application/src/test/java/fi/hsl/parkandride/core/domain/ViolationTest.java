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
    private final PropertyPathTranslator translator = new PropertyPathTranslator();

    @Test
    public void capacityViolationIsTranslated() {
        for (CapacityType ct : CapacityType.values()) {
            assertThat(violationPath(capacityBuiltViolationForType(ct))).isEqualTo("capacities." + ct.name() + ".built");
        }
    }

    @Test
    public void nonCapacityViolationIsNotTranslated() {
        assertThat(violationPath(nameViolation(""))).isEqualTo("name");
    }

    private String violationPath(ConstraintViolation<Facility> cv) {
        return new Violation(cv).path;
    }

    private ConstraintViolation<Facility> capacityBuiltViolationForType(CapacityType t) {
        Facility f = validFacility();
        f.capacities.put(t, new Capacity(-1));

        Set<ConstraintViolation<Facility>> violations = validator.validate(f);
        assertThat(violations).hasSize(1);
        return violations.iterator().next();
    }

    private ConstraintViolation<Facility> nameViolation(String name) {
        Facility f = validFacility();
        f.name = name;

        Set<ConstraintViolation<Facility>> violations = validator.validate(f);
        assertThat(violations).hasSize(1);
        return violations.iterator().next();
    }

    private Facility validFacility() {
        Facility f = new Facility();
        f.name = "Test";
        f.border = Mockito.mock(Geometry.class);

        Set<ConstraintViolation<Facility>> violations = validator.validate(f);
        assertThat(violations).isEmpty();

        return f;
    }
}