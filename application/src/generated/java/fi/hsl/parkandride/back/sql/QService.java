package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QService is a Querydsl query type for QService
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QService extends RelationalPathSpatial<QService> {

    private static final long serialVersionUID = -1411325302;

    public static final QService service = new QService("SERVICE");

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QService> constraintA1 = createPrimaryKey(name);

    public final com.querydsl.sql.ForeignKey<QFacilityService> _facilityServiceServiceFk = createInvForeignKey(name, "SERVICE");

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

    public QService(PathMetadata metadata) {
        super(QService.class, metadata, "PUBLIC", "SERVICE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

