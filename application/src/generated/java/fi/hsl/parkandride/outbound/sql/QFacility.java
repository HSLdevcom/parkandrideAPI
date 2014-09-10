package fi.hsl.parkandride.outbound.sql;

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
 * QFacility is a Querydsl query type for QFacility
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacility extends RelationalPathSpatial<QFacility> {

    private static final long serialVersionUID = 456386359;

    public static final QFacility facility = new QFacility("FACILITY");

    public final GeometryPath<org.geolatte.geom.Geometry> border = createGeometry("border", org.geolatte.geom.Geometry.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QFacility> constraint4 = createPrimaryKey(id);

    public QFacility(String variable) {
        super(QFacility.class, forVariable(variable), "PUBLIC", "FACILITY");
        addMetadata();
    }

    public QFacility(String variable, String schema, String table) {
        super(QFacility.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacility(Path<? extends QFacility> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY");
        addMetadata();
    }

    public QFacility(PathMetadata<?> metadata) {
        super(QFacility.class, metadata, "PUBLIC", "FACILITY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(border, ColumnMetadata.named("BORDER").withIndex(3).ofType(Types.OTHER).withSize(6).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

