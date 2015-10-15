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
 * QAppUser is a Querydsl query type for QAppUser
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QAppUser extends RelationalPathSpatial<QAppUser> {

    private static final long serialVersionUID = 105577825;

    public static final QAppUser appUser = new QAppUser("APP_USER");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<org.joda.time.DateTime> minTokenTimestamp = createDateTime("minTokenTimestamp", org.joda.time.DateTime.class);

    public final NumberPath<Long> operatorId = createNumber("operatorId", Long.class);

    public final StringPath password = createString("password");

    public final DateTimePath<org.joda.time.DateTime> passwordUpdatedTimestamp = createDateTime("passwordUpdatedTimestamp", org.joda.time.DateTime.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.Role> role = createEnum("role", fi.hsl.parkandride.core.domain.Role.class);

    public final StringPath username = createString("username");

    public final com.querydsl.sql.PrimaryKey<QAppUser> constraint7 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QOperator> userOperatorIdFk = createForeignKey(operatorId, "ID");

    public QAppUser(String variable) {
        super(QAppUser.class, forVariable(variable), "PUBLIC", "APP_USER");
        addMetadata();
    }

    public QAppUser(String variable, String schema, String table) {
        super(QAppUser.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QAppUser(Path<? extends QAppUser> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "APP_USER");
        addMetadata();
    }

    public QAppUser(PathMetadata metadata) {
        super(QAppUser.class, metadata, "PUBLIC", "APP_USER");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(minTokenTimestamp, ColumnMetadata.named("MIN_TOKEN_TIMESTAMP").withIndex(5).ofType(Types.TIMESTAMP).withSize(23).withDigits(10));
        addMetadata(operatorId, ColumnMetadata.named("OPERATOR_ID").withIndex(4).ofType(Types.BIGINT).withSize(19));
        addMetadata(password, ColumnMetadata.named("PASSWORD").withIndex(6).ofType(Types.VARCHAR).withSize(128));
        addMetadata(passwordUpdatedTimestamp, ColumnMetadata.named("PASSWORD_UPDATED_TIMESTAMP").withIndex(7).ofType(Types.TIMESTAMP).withSize(23).withDigits(10));
        addMetadata(role, ColumnMetadata.named("ROLE").withIndex(3).ofType(Types.VARCHAR).withSize(32).notNull());
        addMetadata(username, ColumnMetadata.named("USERNAME").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

