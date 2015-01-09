package fi.hsl.parkandride.front.geojson;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * TODO: This is a bit of a hack as swagger-springmvc's support for inheritance is still under way:
 * https://github.com/martypitt/swagger-springmvc/issues/425
 */
@ApiModel(value="Geometry",
        description = "http://geojson.org/geojson-spec.html#geometry-objects")
public class Geometry {

    // NOTE: https://github.com/martypitt/swagger-springmvc/pull/559
    @ApiModelProperty(required = true, allowableValues = "[Polygon, Point]")
    public String type;

}
