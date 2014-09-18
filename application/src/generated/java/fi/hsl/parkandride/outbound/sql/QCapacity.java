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
 * QCapacity is a Querydsl query type for QCapacity
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QCapacity extends RelationalPathSpatial<QCapacity> {

    private static final long serialVersionUID = -112554674;

    public static final QCapacity capacity = new QCapacity("CAPACITY");

    public final NumberPath<Integer> built = createNumber("built", Integer.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Integer> unavailable = createNumber("unavailable", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QCapacity> constraint2 = createPrimaryKey(capacityType, facilityId);

    public final com.mysema.query.sql.ForeignKey<QFacility> capacityFacilityIdFk = createForeignKey(facilityId, "ID");

    public final com.mysema.query.sql.ForeignKey<QCapacityType> capacityCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public QCapacity(String variable) {
        super(QCapacity.class, forVariable(variable), "PUBLIC", "CAPACITY");
        addMetadata();
    }

    public QCapacity(String variable, String schema, String table) {
        super(QCapacity.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QCapacity(Path<? extends QCapacity> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "CAPACITY");
        addMetadata();
    }

    public QCapacity(PathMetadata<?> metadata) {
        super(QCapacity.class, metadata, "PUBLIC", "CAPACITY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(built, ColumnMetadata.named("BUILT").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(unavailable, ColumnMetadata.named("UNAVAILABLE").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

