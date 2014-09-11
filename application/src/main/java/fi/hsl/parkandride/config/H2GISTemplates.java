package fi.hsl.parkandride.config;

import com.mysema.query.sql.H2Templates;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.spatial.SpatialTemplatesSupport;

public class H2GISTemplates extends H2Templates {

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder() {
            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new H2GISTemplates(escape, quote);
            }
        };
    }

    public H2GISTemplates() {
        this('\\', false);
    }

    public H2GISTemplates(boolean quote) {
        this('\\', quote);
    }

    public H2GISTemplates(char escape, boolean quote) {
        super(escape, quote);
        addCustomType(H2GeometryType.DEFAULT);
        add(SpatialTemplatesSupport.getSpatialOps(true));
//        add(SpatialOps.DISTANCE_SPHERE, "ST_Distance_Sphere({0}, {1})");
//        add(SpatialOps.DISTANCE_SPHEROID, "ST_Distance_Spheroid({0}, {1})");
    }
}
