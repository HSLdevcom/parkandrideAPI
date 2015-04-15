// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

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

    @ApiModelProperty(required = true,
            value="Coordinates as defined by GeoJSON Point")
    public double[] coordinates;

}
