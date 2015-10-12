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
 * QFacilityStatus is a Querydsl query type for QFacilityStatus
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFacilityStatus extends RelationalPathSpatial<QFacilityStatus> {

    private static final long serialVersionUID = -847823104;

    public static final QFacilityStatus facilityStatus = new QFacilityStatus("FACILITY_STATUS");

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QFacilityStatus> constraint5 = createPrimaryKey(name);

    public final com.querydsl.sql.ForeignKey<QFacility> _facilityStatusFk = createInvForeignKey(name, "STATUS");

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

    public QFacilityStatus(PathMetadata metadata) {
        super(QFacilityStatus.class, metadata, "PUBLIC", "FACILITY_STATUS");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

