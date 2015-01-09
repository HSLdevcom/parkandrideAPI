package fi.hsl.parkandride.front.swagger;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * TODO: This is a bit of a hack as swagger-springmvc's support for inheritance is still under way:
 * https://github.com/martypitt/swagger-springmvc/issues/425
 */
@ApiModel(value = "Point",
        description = "http://geojson.org/geojson-spec.html#point")
public class Point { // TODO: extends Geometry {

    @ApiModelProperty(required = true, allowableValues = "[Point]")
    public String type;

    @ApiModelProperty(required = true)
    public double[] coordinates;

}
