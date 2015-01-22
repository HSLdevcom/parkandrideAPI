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
 * QFacilityService is a Querydsl query type for QFacilityService
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityService extends RelationalPathSpatial<QFacilityService> {

    private static final long serialVersionUID = -926402201;

    public static final QFacilityService facilityService = new QFacilityService("FACILITY_SERVICE");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Service> service = createEnum("service", fi.hsl.parkandride.core.domain.Service.class);

    public final com.mysema.query.sql.PrimaryKey<QFacilityService> constraint3 = createPrimaryKey(facilityId, service);

    public final com.mysema.query.sql.ForeignKey<QService> facilityServiceServiceFk = createForeignKey(service, "NAME");

    public final com.mysema.query.sql.ForeignKey<QFacility> facilityServiceFacilityIdFk = createForeignKey(facilityId, "ID");

    public QFacilityService(String variable) {
        super(QFacilityService.class, forVariable(variable), "PUBLIC", "FACILITY_SERVICE");
        addMetadata();
    }

    public QFacilityService(String variable, String schema, String table) {
        super(QFacilityService.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityService(Path<? extends QFacilityService> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_SERVICE");
        addMetadata();
    }

    public QFacilityService(PathMetadata<?> metadata) {
        super(QFacilityService.class, metadata, "PUBLIC", "FACILITY_SERVICE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(service, ColumnMetadata.named("SERVICE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

