package fi.hsl.parkandride.back.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;

import com.querydsl.sql.spatial.RelationalPathSpatial;

import com.querydsl.spatial.*;



/**
 * QUnavailableCapacityHistory is a Querydsl query type for QUnavailableCapacityHistory
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QUnavailableCapacityHistory extends RelationalPathSpatial<QUnavailableCapacityHistory> {

    private static final long serialVersionUID = 1491933717;

    public static final QUnavailableCapacityHistory unavailableCapacityHistory = new QUnavailableCapacityHistory("UNAVAILABLE_CAPACITY_HISTORY");

    public final NumberPath<Integer> capacity = createNumber("capacity", Integer.class);

    public final NumberPath<Long> capacityHistoryId = createNumber("capacityHistoryId", Long.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.CapacityType> capacityType = createEnum("capacityType", fi.hsl.parkandride.core.domain.CapacityType.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Usage> usage = createEnum("usage", fi.hsl.parkandride.core.domain.Usage.class);

    public final com.querydsl.sql.PrimaryKey<QUnavailableCapacityHistory> constraint5d = createPrimaryKey(capacityHistoryId, capacityType, usage);

    public final com.querydsl.sql.ForeignKey<QFacilityCapacityHistory> unavailableCapacityHistoryIdFk = createForeignKey(capacityHistoryId, "ID");

    public final com.querydsl.sql.ForeignKey<QUsage> unavailableCapacityHistoryUsageFk = createForeignKey(usage, "NAME");

    public final com.querydsl.sql.ForeignKey<QCapacityType> unavailableCapacityHistoryCapacityTypeFk = createForeignKey(capacityType, "NAME");

    public QUnavailableCapacityHistory(String variable) {
        super(QUnavailableCapacityHistory.class, forVariable(variable), "PUBLIC", "UNAVAILABLE_CAPACITY_HISTORY");
        addMetadata();
    }

    public QUnavailableCapacityHistory(String variable, String schema, String table) {
        super(QUnavailableCapacityHistory.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUnavailableCapacityHistory(Path<? extends QUnavailableCapacityHistory> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "UNAVAILABLE_CAPACITY_HISTORY");
        addMetadata();
    }

    public QUnavailableCapacityHistory(PathMetadata metadata) {
        super(QUnavailableCapacityHistory.class, metadata, "PUBLIC", "UNAVAILABLE_CAPACITY_HISTORY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(capacity, ColumnMetadata.named("CAPACITY").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(capacityHistoryId, ColumnMetadata.named("CAPACITY_HISTORY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(capacityType, ColumnMetadata.named("CAPACITY_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(usage, ColumnMetadata.named("USAGE").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

