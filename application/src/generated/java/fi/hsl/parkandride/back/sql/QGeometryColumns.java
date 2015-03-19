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
 * QGeometryColumns is a Querydsl query type for QGeometryColumns
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QGeometryColumns extends RelationalPathSpatial<QGeometryColumns> {

    private static final long serialVersionUID = -38037024;

    public static final QGeometryColumns geometryColumns = new QGeometryColumns("GEOMETRY_COLUMNS");

    public final NumberPath<Integer> coordDimension = createNumber("coordDimension", Integer.class);

    public final StringPath fGeometryColumn = createString("fGeometryColumn");

    public final StringPath fTableCatalog = createString("fTableCatalog");

    public final StringPath fTableName = createString("fTableName");

    public final StringPath fTableSchema = createString("fTableSchema");

    public final NumberPath<Integer> geometryType = createNumber("geometryType", Integer.class);

    public final NumberPath<Integer> srid = createNumber("srid", Integer.class);

    public final NumberPath<Integer> storageType = createNumber("storageType", Integer.class);

    public final StringPath type = createString("type");

    public QGeometryColumns(String variable) {
        super(QGeometryColumns.class, forVariable(variable), "PUBLIC", "GEOMETRY_COLUMNS");
        addMetadata();
    }

    public QGeometryColumns(String variable, String schema, String table) {
        super(QGeometryColumns.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QGeometryColumns(Path<? extends QGeometryColumns> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "GEOMETRY_COLUMNS");
        addMetadata();
    }

    public QGeometryColumns(PathMetadata<?> metadata) {
        super(QGeometryColumns.class, metadata, "PUBLIC", "GEOMETRY_COLUMNS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(coordDimension, ColumnMetadata.named("COORD_DIMENSION").withIndex(7).ofType(Types.INTEGER).withSize(2147483647));
        addMetadata(fGeometryColumn, ColumnMetadata.named("F_GEOMETRY_COLUMN").withIndex(4).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(fTableCatalog, ColumnMetadata.named("F_TABLE_CATALOG").withIndex(1).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(fTableName, ColumnMetadata.named("F_TABLE_NAME").withIndex(3).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(fTableSchema, ColumnMetadata.named("F_TABLE_SCHEMA").withIndex(2).ofType(Types.VARCHAR).withSize(2147483647));
        addMetadata(geometryType, ColumnMetadata.named("GEOMETRY_TYPE").withIndex(6).ofType(Types.INTEGER).withSize(2147483647));
        addMetadata(srid, ColumnMetadata.named("SRID").withIndex(8).ofType(Types.INTEGER).withSize(2147483647));
        addMetadata(storageType, ColumnMetadata.named("STORAGE_TYPE").withIndex(5).ofType(Types.INTEGER).withSize(10));
        addMetadata(type, ColumnMetadata.named("TYPE").withIndex(9).ofType(Types.VARCHAR).withSize(2147483647));
    }

}

