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
 * QFacilityContact is a Querydsl query type for QFacilityContact
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QFacilityContact extends RelationalPathSpatial<QFacilityContact> {

    private static final long serialVersionUID = -1959029038;

    public static final QFacilityContact facilityContact = new QFacilityContact("FACILITY_CONTACT");

    public final NumberPath<Long> contactId = createNumber("contactId", Long.class);

    public final EnumPath<fi.hsl.parkandride.core.domain.ContactType> contactType = createEnum("contactType", fi.hsl.parkandride.core.domain.ContactType.class);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final com.mysema.query.sql.PrimaryKey<QFacilityContact> constraintE = createPrimaryKey(contactId, contactType, facilityId);

    public final com.mysema.query.sql.ForeignKey<QFacility> facilityContactFacilityIdFk = createForeignKey(facilityId, "ID");

    public final com.mysema.query.sql.ForeignKey<QContact> facilityContactContactIdFk = createForeignKey(contactId, "ID");

    public final com.mysema.query.sql.ForeignKey<QContactType> facilityContactTypeFk = createForeignKey(contactType, "NAME");

    public QFacilityContact(String variable) {
        super(QFacilityContact.class, forVariable(variable), "PUBLIC", "FACILITY_CONTACT");
        addMetadata();
    }

    public QFacilityContact(String variable, String schema, String table) {
        super(QFacilityContact.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFacilityContact(Path<? extends QFacilityContact> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "FACILITY_CONTACT");
        addMetadata();
    }

    public QFacilityContact(PathMetadata<?> metadata) {
        super(QFacilityContact.class, metadata, "PUBLIC", "FACILITY_CONTACT");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(contactId, ColumnMetadata.named("CONTACT_ID").withIndex(3).ofType(Types.BIGINT).withSize(19).notNull());
        addMetadata(contactType, ColumnMetadata.named("CONTACT_TYPE").withIndex(2).ofType(Types.VARCHAR).withSize(64).notNull());
        addMetadata(facilityId, ColumnMetadata.named("FACILITY_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}

