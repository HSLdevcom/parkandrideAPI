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
 * QLock is a Querydsl query type for QLock
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QLock extends RelationalPathSpatial<QLock> {

    private static final long serialVersionUID = 1614168438;

    public static final QLock lock = new QLock("LOCK");

    public final StringPath name = createString("name");

    public final StringPath owner = createString("owner");

    public final DateTimePath<org.joda.time.DateTime> validUntil = createDateTime("validUntil", org.joda.time.DateTime.class);

    public final com.querydsl.sql.PrimaryKey<QLock> constraint23 = createPrimaryKey(name);

    public QLock(String variable) {
        super(QLock.class, forVariable(variable), "PUBLIC", "LOCK");
        addMetadata();
    }

    public QLock(String variable, String schema, String table) {
        super(QLock.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QLock(Path<? extends QLock> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "LOCK");
        addMetadata();
    }

    public QLock(PathMetadata metadata) {
        super(QLock.class, metadata, "PUBLIC", "LOCK");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(name, ColumnMetadata.named("NAME").withIndex(1).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(owner, ColumnMetadata.named("OWNER").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(validUntil, ColumnMetadata.named("VALID_UNTIL").withIndex(3).ofType(Types.TIMESTAMP).withSize(23).withDigits(10).notNull());
    }

}

