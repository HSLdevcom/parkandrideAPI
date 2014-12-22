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
 * QPaymentMethod is a Querydsl query type for QPaymentMethod
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QPaymentMethod extends RelationalPathSpatial<QPaymentMethod> {

    private static final long serialVersionUID = 1642789532;

    public static final QPaymentMethod paymentMethod = new QPaymentMethod("PAYMENT_METHOD");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nameEn = createString("nameEn");

    public final StringPath nameFi = createString("nameFi");

    public final StringPath nameSv = createString("nameSv");

    public final com.mysema.query.sql.PrimaryKey<QPaymentMethod> constraintD = createPrimaryKey(id);

    public QPaymentMethod(String variable) {
        super(QPaymentMethod.class, forVariable(variable), "PUBLIC", "PAYMENT_METHOD");
        addMetadata();
    }

    public QPaymentMethod(String variable, String schema, String table) {
        super(QPaymentMethod.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPaymentMethod(Path<? extends QPaymentMethod> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PAYMENT_METHOD");
        addMetadata();
    }

    public QPaymentMethod(PathMetadata<?> metadata) {
        super(QPaymentMethod.class, metadata, "PUBLIC", "PAYMENT_METHOD");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(nameEn, ColumnMetadata.named("NAME_EN").withIndex(4).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameFi, ColumnMetadata.named("NAME_FI").withIndex(2).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(nameSv, ColumnMetadata.named("NAME_SV").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
    }

}

