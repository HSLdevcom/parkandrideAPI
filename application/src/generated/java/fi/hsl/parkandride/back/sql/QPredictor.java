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
 * QPredictor is a Querydsl query type for QPredictor
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPredictor extends RelationalPathSpatial<QPredictor> {

    private static final long serialVersionUID = -227775727;

    public static final QPredictor predictor = new QPredictor("PREDICTOR");

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath internalState = createString("internalState");

    public final DateTimePath<org.joda.time.DateTime> latestUtilization = createDateTime("latestUtilization", org.joda.time.DateTime.class);

    public final BooleanPath moreUtilizations = createBoolean("moreUtilizations");

    public final StringPath type = createString("type");

    public final EnumPath<fi.hsl.parkandride.core.domain.Usage> usage = createEnum("usage", fi.hsl.parkandride.core.domain.Usage.class);

    public final com.mysema.query.sql.PrimaryKey<QPredictor> constraint55 = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QCapacityType> predictorCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public final com.mysema.query.sql.ForeignKey<QFacility> predictorFacilityIdFk = createForeignKey(facilityId, "ID");

    public final com.mysema.query.sql.ForeignKey<QUsage> predictorUsageFk = createForeignKey(usage, "NAME");

    public QPredictor(String variable) {
        super(QPredictor.class, forVariable(variable), "PUBLIC", "PREDICTOR");
        addMetadata();
    }

    public QPredictor(String variable, String schema, String table) {
        super(QPredictor.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPredictor(Path<? extends QPredictor> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PREDICTOR");
        addMetadata();
    }

    public QPredictor(PathMetadata<?> metadata) {
        super(QPredictor.class, metadata, "PUBLIC", "PREDICTOR");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(4).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(internalState, ColumnMetadata.named("INTERNAL_STATE").withIndex(8).ofType(Types.CLOB).withSize(2147483647).notNull());
        addMetadata(latestUtilization, ColumnMetadata.named("LATEST_UTILIZATION").withIndex(6).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
        addMetadata(moreUtilizations, ColumnMetadata.named("MORE_UTILIZATIONS").withIndex(7).ofType(Types.BOOLEAN).withSize(1).notNull());
        addMetadata(type, ColumnMetadata.named("TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(usage, ColumnMetadata.named("USAGE").withIndex(5).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

