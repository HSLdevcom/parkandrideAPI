package fi.hsl.parkandride.core.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.PointCollection;

public class CoordinatesValidator implements ConstraintValidator<Coordinates, Geometry> {

    @Override
    public void initialize(Coordinates constraintAnnotation) {}

    @Override
    public boolean isValid(Geometry geometry, ConstraintValidatorContext context) {
        return isValid(geometry);
    }
    public boolean isValid(Geometry geometry) {
            if (geometry == null) {
            return true;
        }
        PointCollection points = geometry.getPoints();
        for (int i = 0; i < points.size(); i++) {
            if (isNotBetween(19, points.getX(i), 32) || isNotBetween(59.5, points.getY(i), 70.5)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNotBetween(double low, double value, double hi) {
        return value < low || hi < value;
    }
}
