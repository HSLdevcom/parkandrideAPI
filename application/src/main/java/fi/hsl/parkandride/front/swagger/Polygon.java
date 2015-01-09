package fi.hsl.parkandride.front.swagger;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * TODO: This is a bit of a hack as swagger-springmvc's support for inheritance is still under way:
 * https://github.com/martypitt/swagger-springmvc/issues/425
 */
@ApiModel(value = "Polygon",
        description = "http://geojson.org/geojson-spec.html#polygon")
public class Polygon { // TODO: extends Geometry {

    @ApiModelProperty(required = true, allowableValues = "[Polygon]")
    public String type;

    // TODO: Requires Swagger 2.0: https://github.com/martypitt/swagger-springmvc/milestones/2.0
//    @ApiModelProperty(required = true, dataType = "Array[Array[Array[number]]]")
//    public double[][][] coordinates;

}
