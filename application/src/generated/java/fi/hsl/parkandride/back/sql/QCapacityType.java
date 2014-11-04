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
 * QCapacityType is a Querydsl query type for QCapacityType
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QCapacityType extends RelationalPathSpatial<QCapacityType> {

    private static final long serialVersionUID = 1619274847;

    public static final QCapacityType capacityType = new QCapacityType("CAPACITY_TYPE");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QCapacityType> constraint5 = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QCapacity> _capacityCapacityTypeFk = createInvForeignKey(name, "CAPACITY_TYPE");

    public QCapacityType(String variable) {
        super(QCapacityType.class, forVariable(variable), "PUBLIC", "CAPACITY_TYPE");
        addMetadata();
    }

    public QCapacityType(String variable, String schema, String table) {
        super(QCapacityType.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCapacityType(Path<? extends QCapacityType> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "CAPACITY_TYPE");
        addMetadata();
    }

    public QCapacityType(PathMetadata<?> metadata) {
        super(QCapacityType.class, metadata, "PUBLIC", "CAPACITY_TYPE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

