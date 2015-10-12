package fi.hsl.parkandride.back.sql;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.spatial.RelationalPathSpatial;

import javax.annotation.Generated;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;



/**
 * QOperator is a Querydsl query type for QOperator
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QOperator extends RelationalPathSpatial<QOperator> {

    private static final long serialVersionUID = 1613793135;

    public static final QOperator operator = new QOperator("OPERATOR");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nameEn = createString("nameEn");

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final com.querydsl.sql.PrimaryKey<QOperator> constraint1 = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QFacility> _facilityOperatorIdFk = createInvForeignKey(id, "OPERATOR_ID");

    public final com.querydsl.sql.ForeignKey<QAppUser> _userOperatorIdFk = createInvForeignKey(id, "OPERATOR_ID");

    public final com.querydsl.sql.ForeignKey<QContact> _contactOperatorIdFk = createInvForeignKey(id, "OPERATOR_ID");

    public QOperator(String variable) {
        super(QOperator.class, forVariable(variable), "PUBLIC", "OPERATOR");
        addMetadata();
    }

    public QOperator(String variable, String schema, String table) {
        super(QOperator.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QOperator(Path<? extends QOperator> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "OPERATOR");
        addMetadata();
    }

    public QOperator(PathMetadata metadata) {
        super(QOperator.class, metadata, "PUBLIC", "OPERATOR");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

