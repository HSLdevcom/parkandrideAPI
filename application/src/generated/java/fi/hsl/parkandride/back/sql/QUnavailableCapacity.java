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
 * QUnavailableCapacity is a Querydsl query type for QUnavailableCapacity
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QUnavailableCapacity extends RelationalPathSpatial<QUnavailableCapacity> {

    private static final long serialVersionUID = -1124006529;

    public static final QUnavailableCapacity unavailableCapacity = new QUnavailableCapacity("UNAVAILABLE_CAPACITY");

    public final NumberPath<Integer> capacity = createNumber("capacity", Integer.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Usage> usage = createEnum("usage", fi.hsl.parkandride.core.domain.Usage.class);

    public final com.mysema.query.sql.PrimaryKey<QUnavailableCapacity> constraint4e = createPrimaryKey(capacityType, facilityId, usage);

    public final com.mysema.query.sql.ForeignKey<QCapacityType> unavailableCapacityCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public final com.mysema.query.sql.ForeignKey<QFacility> unavailableCapacityFacilityIdFk = createForeignKey(facilityId, "ID");

    public final com.mysema.query.sql.ForeignKey<QUsage> unavailableCapacityUsageFk = createForeignKey(usage, "NAME");

    public QUnavailableCapacity(String variable) {
        super(QUnavailableCapacity.class, forVariable(variable), "PUBLIC", "UNAVAILABLE_CAPACITY");
        addMetadata();
    }

    public QUnavailableCapacity(String variable, String schema, String table) {
        super(QUnavailableCapacity.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUnavailableCapacity(Path<? extends QUnavailableCapacity> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "UNAVAILABLE_CAPACITY");
        addMetadata();
    }

    public QUnavailableCapacity(PathMetadata<?> metadata) {
        super(QUnavailableCapacity.class, metadata, "PUBLIC", "UNAVAILABLE_CAPACITY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacity, ColumnMetadata.named("CAPACITY").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(usage, ColumnMetadata.named("USAGE").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

