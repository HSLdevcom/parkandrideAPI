package fi.hsl.parkandride.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import fi.hsl.parkandride.core.domain.Violation;

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
            assertMinViolation(violation, "map[KEY].");
        }
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
