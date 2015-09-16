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
 * QRegion is a Querydsl query type for QRegion
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QRegion extends RelationalPathSpatial<QRegion> {

    private static final long serialVersionUID = 895335551;

    public static final QRegion region = new QRegion("REGION");

    public final PolygonPath<org.geolatte.geom.Polygon> area = createPolygon("area", org.geolatte.geom.Polygon.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nameEn = createString("nameEn");

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final com.mysema.query.sql.PrimaryKey<QRegion> constraint8 = createPrimaryKey(id);

    public QRegion(String variable) {
        super(QRegion.class, forVariable(variable), "PUBLIC", "REGION");
        addMetadata();
    }

    public QRegion(String variable, String schema, String table) {
        super(QRegion.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QRegion(Path<? extends QRegion> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "REGION");
        addMetadata();
    }

    public QRegion(PathMetadata<?> metadata) {
        super(QRegion.class, metadata, "PUBLIC", "REGION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(area, ColumnMetadata.named("AREA").withIndex(5).ofType(Types.OTHER).withSize(2147483647).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

