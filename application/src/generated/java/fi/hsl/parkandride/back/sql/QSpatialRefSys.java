package fi.hsl.parkandride.back.sql;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;
import java.sql.Types;

import com.mysema.query.sql.spatial.RelationalPathSpatial;

import com.mysema.query.spatial.path.*;



/**
 * QSpatialRefSys is a Querydsl query type for QSpatialRefSys
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QSpatialRefSys extends RelationalPathSpatial<QSpatialRefSys> {

    private static final long serialVersionUID = -983548045;

    public static final QSpatialRefSys spatialRefSys = new QSpatialRefSys("SPATIAL_REF_SYS");

    public final StringPath authName = createString("authName");

    public final StringPath authSrid = createString("authSrid");

    public final StringPath proj4text = createString("proj4text");

    public final StringPath srid = createString("srid");

    public final StringPath srtext = createString("srtext");

    public QSpatialRefSys(String variable) {
        super(QSpatialRefSys.class, forVariable(variable), "PUBLIC", "SPATIAL_REF_SYS");
        addMetadata();
    }

    public QSpatialRefSys(String variable, String schema, String table) {
        super(QSpatialRefSys.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QSpatialRefSys(Path<? extends QSpatialRefSys> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "SPATIAL_REF_SYS");
        addMetadata();
    }

    public QSpatialRefSys(PathMetadata<?> metadata) {
        super(QSpatialRefSys.class, metadata, "PUBLIC", "SPATIAL_REF_SYS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(authName, ColumnMetadata.named("AUTH_NAME").withIndex(2).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(authSrid, ColumnMetadata.named("AUTH_SRID").withIndex(3).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(proj4text, ColumnMetadata.named("PROJ4TEXT").withIndex(5).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(srid, ColumnMetadata.named("SRID").withIndex(1).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(srtext, ColumnMetadata.named("SRTEXT").withIndex(4).ofType(Types.VARCHAR).withSize(2147483647));
    }

}

