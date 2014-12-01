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
 * QService is a Querydsl query type for QService
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QService extends RelationalPathSpatial<QService> {

    private static final long serialVersionUID = -1411325302;

    public static final QService service = new QService("SERVICE");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nameEn = createString("nameEn");

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final com.mysema.query.sql.PrimaryKey<QService> constraintA = createPrimaryKey(id);

    public final com.mysema.query.sql.ForeignKey<QFacilityService> _facilityServiceServiceIdFk = createInvForeignKey(id, "SERVICE_ID");

    public QService(String variable) {
        super(QService.class, forVariable(variable), "PUBLIC", "SERVICE");
        addMetadata();
    }

    public QService(String variable, String schema, String table) {
        super(QService.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QService(Path<? extends QService> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "SERVICE");
        addMetadata();
    }

    public QService(PathMetadata<?> metadata) {
        super(QService.class, metadata, "PUBLIC", "SERVICE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

