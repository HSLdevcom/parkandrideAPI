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
 * QContactType is a Querydsl query type for QContactType
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QContactType extends RelationalPathSpatial<QContactType> {

    private static final long serialVersionUID = -1446952753;

    public static final QContactType contactType = new QContactType("CONTACT_TYPE");

    public final EnumPath<fi.hsl.parkandride.core.domain.ContactType> name = createEnum("name", fi.hsl.parkandride.core.domain.ContactType.class);

    public final com.mysema.query.sql.PrimaryKey<QContactType> constraintD = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QFacilityContact> _facilityContactTypeFk = createInvForeignKey(name, "CONTACT_TYPE");

    public QContactType(String variable) {
        super(QContactType.class, forVariable(variable), "PUBLIC", "CONTACT_TYPE");
        addMetadata();
    }

    public QContactType(String variable, String schema, String table) {
        super(QContactType.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QContactType(Path<? extends QContactType> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "CONTACT_TYPE");
        addMetadata();
    }

    public QContactType(PathMetadata<?> metadata) {
        super(QContactType.class, metadata, "PUBLIC", "CONTACT_TYPE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

