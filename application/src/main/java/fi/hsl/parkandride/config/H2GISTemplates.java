package fi.hsl.parkandride.config;

import com.mysema.query.sql.H2Templates;
import com.mysema.query.sql.spatial.SpatialTemplatesSupport;

import fi.hsl.parkandride.outbound.H2GeometryWktType;

public class H2GISTemplates extends H2Templates {

    public H2GISTemplates() {
        this('\\', false);
    }

    public H2GISTemplates(boolean quote) {
        this('\\', quote);
    }

    public H2GISTemplates(char escape, boolean quote) {
        super(escape, quote);
        addCustomType(H2GeometryWktType.DEFAULT);
        add(SpatialTemplatesSupport.getSpatialOps(true));
//        add(SpatialOps.DISTANCE_SPHERE, "ST_Distance_Sphere({0}, {1})");
//        add(SpatialOps.DISTANCE_SPHEROID, "ST_Distance_Spheroid({0}, {1})");
    }
}
