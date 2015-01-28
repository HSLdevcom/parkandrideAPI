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

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QFacilityStatus> constraint5 = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QFacility> _facilityStatusFk = createInvForeignKey(name, "STATUS");

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
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

