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
 * QFacilityStatusEnum is a Querydsl query type for QFacilityStatusEnum
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityStatusEnum extends RelationalPathSpatial<QFacilityStatusEnum> {

    private static final long serialVersionUID = -1310668767;

    public static final QFacilityStatusEnum facilityStatusEnum = new QFacilityStatusEnum("FACILITY_STATUS_ENUM");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QFacilityStatusEnum> constraintC = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QFacilityStatus> _facilityStatusFacilityStatusEnumFk = createInvForeignKey(name, "STATUS");

    public QFacilityStatusEnum(String variable) {
        super(QFacilityStatusEnum.class, forVariable(variable), "PUBLIC", "FACILITY_STATUS_ENUM");
        addMetadata();
    }

    public QFacilityStatusEnum(String variable, String schema, String table) {
        super(QFacilityStatusEnum.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityStatusEnum(Path<? extends QFacilityStatusEnum> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_STATUS_ENUM");
        addMetadata();
    }

    public QFacilityStatusEnum(PathMetadata<?> metadata) {
        super(QFacilityStatusEnum.class, metadata, "PUBLIC", "FACILITY_STATUS_ENUM");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

