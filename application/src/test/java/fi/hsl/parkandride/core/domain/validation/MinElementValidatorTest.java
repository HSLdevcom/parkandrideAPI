package fi.hsl.parkandride.core.domain.validation;

import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class MinElementValidatorTest {

    public static class MinType {
        @MinElement(1)
        public Map<String, Integer> values = new LinkedHashMap<>();
    }

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void report_all_invalid_values() {
        MinType t = new MinType();
        t.values.put("a", 0);
        t.values.put("b", 1);
        t.values.put("c", -1);

        Set<ConstraintViolation<MinType>> violations = validate(t);
        assertThat(violations).hasSize(2);

        Iterator<ConstraintViolation<MinType>> iter = violations.iterator();
        ConstraintViolation<MinType> c1 = iter.next();
        ConstraintViolation<MinType> c2 = iter.next();

        assertThat(c1.getPropertyPath().toString()).isEqualTo("values[a]");
        assertThat(c1.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(MinElement.class);

        assertThat(c2.getPropertyPath().toString()).isEqualTo("values[c]");
        assertThat(c2.getConstraintDescriptor().getAnnotation().annotationType()).isEqualTo(MinElement.class);
    }

    @Test
    public void ignore_null_elements() {
        MinType t = new MinType();
        t.values.put("a", null);
        assertThat(validate(t)).isEmpty();
    }

    @Test
    public void ignore_null() {
        MinType t = new MinType();
        t.values = null;
        assertThat(validate(t)).isEmpty();
    }

    private Set<ConstraintViolation<MinType>> validate(MinType t) {
        Set<ConstraintViolation<MinType>> violations = new TreeSet<>(comparing(c -> c.getPropertyPath().toString()));
        violations.addAll(validator.validate(t));
        return violations;
    }
}
