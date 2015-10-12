package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QFacilityUtilization is a Querydsl query type for QFacilityUtilization
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFacilityUtilization extends RelationalPathSpatial<QFacilityUtilization> {

    private static final long serialVersionUID = -1445988396;

    public static final QFacilityUtilization facilityUtilization = new QFacilityUtilization("FACILITY_UTILIZATION");

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Integer> spacesAvailable = createNumber("spacesAvailable", Integer.class);

    public final DateTimePath<org.joda.time.DateTime> ts = createDateTime("ts", org.joda.time.DateTime.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Usage> usage = createEnum("usage", fi.hsl.parkandride.core.domain.Usage.class);

    public final com.querydsl.sql.ForeignKey<QFacility> facilityUtilizationFacilityIdFk = createForeignKey(facilityId, "ID");

    public final com.querydsl.sql.ForeignKey<QUsage> facilityUtilizationUsageFk = createForeignKey(usage, "NAME");

    public final com.querydsl.sql.ForeignKey<QCapacityType> facilityUtilizationCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public QFacilityUtilization(String variable) {
        super(QFacilityUtilization.class, forVariable(variable), "PUBLIC", "FACILITY_UTILIZATION");
        addMetadata();
    }

    public QFacilityUtilization(String variable, String schema, String table) {
        super(QFacilityUtilization.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityUtilization(Path<? extends QFacilityUtilization> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_UTILIZATION");
        addMetadata();
    }

    public QFacilityUtilization(PathMetadata metadata) {
        super(QFacilityUtilization.class, metadata, "PUBLIC", "FACILITY_UTILIZATION");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(spacesAvailable, ColumnMetadata.named("SPACES_AVAILABLE").withIndex(5).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(ts, ColumnMetadata.named("TS").withIndex(4).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
        addMetadata(usage, ColumnMetadata.named("USAGE").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

