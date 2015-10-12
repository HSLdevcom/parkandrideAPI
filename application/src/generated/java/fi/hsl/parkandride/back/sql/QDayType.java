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
 * QDayType is a Querydsl query type for QDayType
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QDayType extends RelationalPathSpatial<QDayType> {

    private static final long serialVersionUID = -1948027701;

    public static final QDayType dayType = new QDayType("DAY_TYPE");

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QDayType> constraintA = createPrimaryKey(name);

    public final com.querydsl.sql.ForeignKey<QPricing> _pricingDayTypeFk = createInvForeignKey(name, "DAY_TYPE");

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

    public QDayType(PathMetadata metadata) {
        super(QDayType.class, metadata, "PUBLIC", "DAY_TYPE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
    }

}

