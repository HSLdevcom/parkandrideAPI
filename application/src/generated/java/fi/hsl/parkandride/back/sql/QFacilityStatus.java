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
 * QFacilityStatus is a Querydsl query type for QFacilityStatus
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityStatus extends RelationalPathSpatial<QFacilityStatus> {

    private static final long serialVersionUID = -847823104;

    public static final QFacilityStatus facilityStatus = new QFacilityStatus("FACILITY_STATUS");

    public final StringPath capacityType = createString("capacityType");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Integer> spacesAvailable = createNumber("spacesAvailable", Integer.class);

    public final StringPath status = createString("status");

    public final DateTimePath<java.sql.Timestamp> ts = createDateTime("ts", java.sql.Timestamp.class);

    public final com.mysema.query.sql.PrimaryKey<QFacilityStatus> constraint5d = createPrimaryKey(capacityType, facilityId, ts);

    public final com.mysema.query.sql.ForeignKey<QFacility> facilityStatusFacilityIdFk = createForeignKey(facilityId, "ID");

    public final com.mysema.query.sql.ForeignKey<QCapacityType> facilityStatusCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public final com.mysema.query.sql.ForeignKey<QFacilityStatusEnum> facilityStatusFacilityStatusEnumFk = createForeignKey(status, "NAME");

    public QFacilityStatus(String variable) {
        super(QFacilityStatus.class, forVariable(variable), "PUBLIC", "FACILITY_STATUS");
        addMetadata();
    }

    public QFacilityStatus(String variable, String schema, String table) {
        super(QFacilityStatus.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityStatus(Path<? extends QFacilityStatus> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_STATUS");
        addMetadata();
    }

    public QFacilityStatus(PathMetadata<?> metadata) {
        super(QFacilityStatus.class, metadata, "PUBLIC", "FACILITY_STATUS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(spacesAvailable, ColumnMetadata.named("SPACES_AVAILABLE").withIndex(4).ofType(Types.INTEGER).withSize(10));
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(5).ofType(Types.VARCHAR).withSize(64));
        addMetadata(ts, ColumnMetadata.named("TS").withIndex(3).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
    }

}

