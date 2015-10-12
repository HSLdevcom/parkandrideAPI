package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QFacilityStatusHistory is a Querydsl query type for QFacilityStatusHistory
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFacilityStatusHistory extends RelationalPathSpatial<QFacilityStatusHistory> {

    private static final long serialVersionUID = 1932287348;

    public static final QFacilityStatusHistory facilityStatusHistory = new QFacilityStatusHistory("FACILITY_STATUS_HISTORY");

    public final DateTimePath<org.joda.time.DateTime> endTs = createDateTime("endTs", org.joda.time.DateTime.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<org.joda.time.DateTime> startTs = createDateTime("startTs", org.joda.time.DateTime.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.FacilityStatus> status = createEnum("status", fi.hsl.parkandride.core.domain.FacilityStatus.class);

    public final StringPath statusDescriptionEn = createString("statusDescriptionEn");

    public final StringPath statusDescriptionFi = createString("statusDescriptionFi");

    public final StringPath statusDescriptionSv = createString("statusDescriptionSv");

    public final com.querydsl.sql.PrimaryKey<QFacilityStatusHistory> constraint8c = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QFacility> statusHistoryFacilityIdFk = createForeignKey(facilityId, "ID");

    public QFacilityStatusHistory(String variable) {
        super(QFacilityStatusHistory.class, forVariable(variable), "PUBLIC", "FACILITY_STATUS_HISTORY");
        addMetadata();
    }

    public QFacilityStatusHistory(String variable, String schema, String table) {
        super(QFacilityStatusHistory.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityStatusHistory(Path<? extends QFacilityStatusHistory> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_STATUS_HISTORY");
        addMetadata();
    }

    public QFacilityStatusHistory(PathMetadata metadata) {
        super(QFacilityStatusHistory.class, metadata, "PUBLIC", "FACILITY_STATUS_HISTORY");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(endTs, ColumnMetadata.named("END_TS").withIndex(5).ofType(Types.TIMESTAMP).withSize(23).withDigits(10));
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(startTs, ColumnMetadata.named("START_TS").withIndex(4).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
        addMetadata(status, ColumnMetadata.named("STATUS").withIndex(3).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(statusDescriptionEn, ColumnMetadata.named("STATUS_DESCRIPTION_EN").withIndex(8).ofType(Types.VARCHAR).withSize(255));
        addMetadata(statusDescriptionFi, ColumnMetadata.named("STATUS_DESCRIPTION_FI").withIndex(6).ofType(Types.VARCHAR).withSize(255));
        addMetadata(statusDescriptionSv, ColumnMetadata.named("STATUS_DESCRIPTION_SV").withIndex(7).ofType(Types.VARCHAR).withSize(255));
    }

}

