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
 * QDayType is a Querydsl query type for QDayType
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QDayType extends RelationalPathSpatial<QDayType> {

    private static final long serialVersionUID = -1948027701;

    public static final QDayType dayType = new QDayType("DAY_TYPE");

    public final StringPath name = createString("name");

    public final com.mysema.query.sql.PrimaryKey<QDayType> constraintA = createPrimaryKey(name);

    public final com.mysema.query.sql.ForeignKey<QPricing> _pricingDayTypeFk = createInvForeignKey(name, "DAY_TYPE");

    public QDayType(String variable) {
        super(QDayType.class, forVariable(variable), "PUBLIC", "DAY_TYPE");
        addMetadata();
    }

    public QDayType(String variable, String schema, String table) {
        super(QDayType.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QDayType(Path<? extends QDayType> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "DAY_TYPE");
        addMetadata();
    }

    public QDayType(PathMetadata<?> metadata) {
        super(QDayType.class, metadata, "PUBLIC", "DAY_TYPE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

