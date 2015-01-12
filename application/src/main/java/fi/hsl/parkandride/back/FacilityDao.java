package fi.hsl.parkandride.back;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysema.query.group.GroupBy.groupBy;
import static com.mysema.query.group.GroupBy.list;
import static com.mysema.query.group.GroupBy.map;
import static com.mysema.query.group.GroupBy.set;
import static fi.hsl.parkandride.back.GSortedSet.sortedSet;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;

import java.util.*;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mysema.query.ResultTransformer;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;

import fi.hsl.parkandride.back.sql.*;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;

public class FacilityDao implements FacilityRepository {

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    private static final QFacility qFacility = QFacility.facility;

    private static final QFacilityAlias qAlias = QFacilityAlias.facilityAlias;

    private static final QPort qPort = QPort.port;

    private static final QFacilityService qService = QFacilityService.facilityService;

    private static final QFacilityStatus qStatus = QFacilityStatus.facilityStatus;

    private static final QFacilityPaymentMethod  qPaymentMethod = QFacilityPaymentMethod.facilityPaymentMethod;

    private static final QPricing qPricing = QPricing.pricing;

    private static final MultilingualStringMapping pricingPriceMapping =
            new MultilingualStringMapping(qPricing.priceFi, qPricing.priceSv, qPricing.priceEn);


    private static final AddressMapping addressMapping = new AddressMapping(qPort);

    private static final MultilingualStringMapping portInfoMapping =
            new MultilingualStringMapping(qPort.infoFi, qPort.infoSv, qPort.infoEn);

    private static final MappingProjection<Port> portMapping = new MappingProjection<Port>(Port.class, qPort.all()) {

        @Override
        protected Port map(Tuple row) {
            Boolean entry = row.get(qPort.entry);
            if (entry == null) {
                return null;
            }
            Point location = row.get(qPort.location);
            boolean exit = row.get(qPort.exit);
            boolean pedestrian = row.get(qPort.pedestrian);
            boolean bicycle = row.get(qPort.bicycle);
            Port port = new Port(location, entry, exit, pedestrian, bicycle);
            port.address = addressMapping.map(row);
            port.info = portInfoMapping.map(row);
            return port;
        }
    };

    private static final MappingProjection<Pricing> pricingMapping = new MappingProjection<Pricing>(Pricing.class, qPricing.all()) {
        @Override
        protected Pricing map(Tuple row) {
            CapacityType capacityType = row.get(qPricing.capacityType);
            if (capacityType == null) {
                return null;
            }
            Pricing pricing = new Pricing();
            pricing.capacityType = capacityType;
            pricing.usage = row.get(qPricing.usage);
            pricing.maxCapacity = row.get(qPricing.maxCapacity);
            pricing.dayType = row.get(qPricing.dayType);
            pricing.from = row.get(qPricing.fromTime);
            pricing.until = row.get(qPricing.untilTime);
            pricing.price = pricingPriceMapping.map(row);
            return pricing;
        }
    };

    public static final ResultTransformer<Map<Long, Set<String>>> aliasesByFacilityIdMapping =
            groupBy(qAlias.facilityId).as(set(qAlias.alias));

    public static final ResultTransformer<Map<Long, List<Port>>> portsByFacilityIdMapping =
            groupBy(qPort.facilityId).as(list(portMapping));


    private static final MultilingualStringMapping paymentInfoDetailMapping =
            new MultilingualStringMapping(qFacility.paymentInfoDetailFi, qFacility.paymentInfoDetailSv, qFacility.paymentInfoDetailEn);
    private static final MultilingualStringMapping paymentInfoUrlMapping =
            new MultilingualStringMapping(qFacility.paymentInfoUrlFi, qFacility.paymentInfoUrlSv, qFacility.paymentInfoUrlEn);

    private static final MappingProjection<Facility> facilityMapping = new MappingProjection<Facility>(Facility.class, qFacility.all()) {
        private final MultilingualStringMapping nameMapping = new MultilingualStringMapping(qFacility.nameFi, qFacility.nameSv, qFacility.nameEn);
        @Override
        protected Facility map(Tuple row) {
            Long id = row.get(qFacility.id);
            if (id == null) {
                return null;
            }
            Facility facility = new Facility();
            facility.id = id;
            facility.location = row.get(qFacility.location);
            facility.name = nameMapping.map(row);
            facility.operatorId = row.get(qFacility.operatorId);
            facility.contacts = new FacilityContacts(
                    row.get(qFacility.emergencyContactId),
                    row.get(qFacility.operatorContactId),
                    row.get(qFacility.serviceContactId)
            );
            facility.paymentInfo.parkAndRideAuthRequired = row.get(qFacility.parkAndRideAuthRequired);
            facility.paymentInfo.detail = paymentInfoDetailMapping.map(row);
            facility.paymentInfo.url = paymentInfoUrlMapping.map(row);

            return facility;
        }
    };

    public static final String FACILITY_ID_SEQ = "facility_id_seq";

    private static final SimpleExpression<Long> nextFacilityId = SQLExpressions.nextval(FACILITY_ID_SEQ);

    private final PostgresQueryFactory queryFactory;

    public FacilityDao(PostgresQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @TransactionalWrite
    @Override
    public long insertFacility(Facility facility) {
        return insertFacility(facility, queryFactory.query().singleResult(nextFacilityId));
    }

    @TransactionalWrite
    public long insertFacility(Facility facility, long facilityId) {
        SQLInsertClause insert = insertFacility();
        insert.set(qFacility.id, facilityId);
        populate(facility, insert);
        insert.execute();

        insertAliases(facilityId, facility.aliases);
        insertPorts(facilityId, facility.ports);
        updateServices(facilityId, facility.serviceIds);
        updatePaymentMethods(facilityId, facility.paymentInfo.paymentMethodIds);
        insertPricing(facilityId, facility.pricing);

        return facilityId;
    }

    @TransactionalWrite
    @Override
    public void updateFacility(long facilityId, Facility facility) {
        updateFacility(facilityId, facility, getFacility(facility.id, true));
    }

    @TransactionalWrite
    @Override
    public void updateFacility(long facilityId, Facility newFacility, Facility oldFacility) {
        checkNotNull(newFacility, "facility");
        SQLUpdateClause update = updateFacility().where(qFacility.id.eq(facilityId));
        populate(newFacility, update);
        if (update.execute() != 1) {
            throw new FacilityNotFoundException(facilityId);
        }

        updateAliases(facilityId, newFacility.aliases, oldFacility.aliases);
        updatePorts(facilityId, newFacility.ports, oldFacility.ports);

        if (!Objects.equals(newFacility.serviceIds, oldFacility.serviceIds)) {
            updateServices(facilityId, newFacility.serviceIds);
        }

        if (!Objects.equals(newFacility.paymentInfo.paymentMethodIds, oldFacility.paymentInfo.paymentMethodIds)) {
            updatePaymentMethods(facilityId, newFacility.paymentInfo.paymentMethodIds);
        }

        if (!Objects.equals(newFacility.pricing, oldFacility.pricing)) {
            deletePricing(facilityId);
            insertPricing(facilityId, newFacility.pricing);
        }
    }

    @TransactionalRead
    @Override
    public Facility getFacility(long id) {
        return getFacility(id, false);
    }

    @TransactionalWrite
    @Override
    public Facility getFacilityForUpdate(long facilityId) {
        return getFacility(facilityId, true);
    }

    private Facility getFacility(long facilityId, boolean forUpdate) {
        PostgresQuery qry = fromFacility().where(qFacility.id.eq(facilityId));
        if (forUpdate) {
            qry.forUpdate();
        }
        Facility facility = qry.singleResult(facilityMapping);
        if (facility == null) {
            throw new FacilityNotFoundException(facilityId);
        }
        ImmutableMap<Long, Facility> facilityMap = ImmutableMap.of(facilityId, facility);
        fetchAliases(facilityMap);
        fetchPorts(facilityMap);
        fetchServices(facilityMap);
        fetchPaymentMethods(facilityMap);
        fetchPricing(facilityMap);
        return facility;
    }

    @TransactionalRead
    @Override
    public SearchResults<Facility> findFacilities(PageableSpatialSearch search) {
        PostgresQuery qry = fromFacility();
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        buildWhere(search, qry);
        orderBy(search.sort, qry);

        Map<Long, Facility> facilities = qry.map(qFacility.id, facilityMapping);
        fetchAliases(facilities);
        fetchPorts(facilities);
        fetchServices(facilities);
        fetchPaymentMethods(facilities);
        fetchPricing(facilities);

        return SearchResults.of(new ArrayList<>(facilities.values()), search.limit);
    }

    @TransactionalWrite
    @Override
    public void insertStatuses(long facilityId, List<FacilityStatus> statuses) {
        SQLInsertClause insertBatch = queryFactory.insert(qStatus);
        statuses.forEach((status) -> {
            insertBatch.set(qStatus.facilityId, facilityId);
            insertBatch.set(qStatus.capacityType, status.capacityType);
            insertBatch.set(qStatus.status, status.status);
            insertBatch.set(qStatus.spacesAvailable, status.spacesAvailable);
            insertBatch.set(qStatus.ts, status.timestamp);
            insertBatch.addBatch();
        });
        insertBatch.execute();
    }

    @TransactionalRead
    @Override
    public List<FacilityStatus> getStatuses(long facilityId) {
        return queryFactory.from(qStatus)
                .where(qStatus.facilityId.eq(facilityId))
                .list(new MappingProjection<FacilityStatus>(FacilityStatus.class, qStatus.all()) {
                    @Override
                    protected FacilityStatus map(Tuple row) {
                        FacilityStatus status = new FacilityStatus();
                        status.capacityType = row.get(qStatus.capacityType);
                        status.timestamp = row.get(qStatus.ts);
                        status.spacesAvailable = row.get(qStatus.spacesAvailable);
                        status.status = row.get(qStatus.status);
                        return status;
                    }
                });
    }

    private void updateAliases(long facilityId, Set<String> newAliases, Set<String> oldAliases) {
        Set<String> toBeRemoved = new HashSet<>(oldAliases);
        Set<String> addedAliases = Sets.newHashSet();
        if (newAliases != null) {
            for (String newAlias : newAliases) {
                if (!toBeRemoved.remove(newAlias)) {
                    addedAliases.add(newAlias);
                }
            }
        }
        insertAliases(facilityId, addedAliases);
        deleteAliases(facilityId, toBeRemoved);
    }

    private void deleteAliases(long facilityId, Set<String> aliases) {
        if (!aliases.isEmpty()) {
            queryFactory.delete(qAlias)
                    .where(qAlias.facilityId.eq(facilityId), qAlias.alias.in(aliases))
                    .execute();
        }
    }

    private void updatePorts(long facilityId, List<Port> newPorts, List<Port> oldPorts) {
        newPorts = firstNonNull(newPorts, new ArrayList<Port>());
        oldPorts = firstNonNull(oldPorts, new ArrayList<Port>());

        Map<Integer, Port> addedPorts = new HashMap<>();
        Map<Integer, Port> updatedPorts = new HashMap<>();

        for (int i=0; i < newPorts.size(); i++) {
            Port newPort = newPorts.get(i);
            Port oldPort = i < oldPorts.size() ? oldPorts.get(i) : null;
            if (oldPort == null) {
                addedPorts.put(i, newPort);
            } else if (!newPort.equals(oldPort)) {
                updatedPorts.put(i, newPort);
            }
        }

        insertPorts(facilityId, addedPorts);
        updatePorts(facilityId, updatedPorts);
        if (oldPorts.size() > newPorts.size()) {
            deletePorts(facilityId, newPorts.size());
        }
    }

    private void updatePorts(long facilityId, Map<Integer, Port> updatedPorts) {
        if (updatedPorts != null && !updatedPorts.isEmpty()) {
            SQLUpdateClause update = queryFactory.update(qPort);
            for (Map.Entry<Integer, Port> entry : updatedPorts.entrySet()) {
                Integer portIndex = entry.getKey();
                populate(facilityId, portIndex, entry.getValue(), update);
                update.where(qPort.facilityId.eq(facilityId), qPort.portIndex.eq(portIndex));
                update.addBatch();
            }
            update.execute();
        }
    }

    private void insertPorts(long facilityId, List<Port> ports) {
        if (ports != null && !ports.isEmpty()) {
            Map<Integer, Port> addedPorts = new HashMap<Integer, Port>();
            for (int i = 0; i < ports.size(); i++) {
                addedPorts.put(i, ports.get(i));
            }
            insertPorts(facilityId, addedPorts);
        }
    }

    private void insertPorts(long facilityId, Map<Integer, Port> addedPorts) {
        if (addedPorts != null && !addedPorts.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qPort);
            for (Map.Entry<Integer, Port> entry : addedPorts.entrySet()) {
                populate(facilityId, entry.getKey(), entry.getValue(), insert);
                insert.addBatch();
            }
            insert.execute();
        }
    }

    private void populate(long facilityId, Integer index, Port port, StoreClause store) {
        store.set(qPort.facilityId, facilityId)
                .set(qPort.portIndex, index)
                .set(qPort.location, port.location)
                .set(qPort.entry, port.entry)
                .set(qPort.exit, port.exit)
                .set(qPort.bicycle, port.bicycle)
                .set(qPort.pedestrian, port.pedestrian);
        addressMapping.populate(port.address, store);
        portInfoMapping.populate(port.info, store);
    }

    private void deletePorts(long facilityId, int fromIndex) {
        queryFactory.delete(qPort).where(qPort.facilityId.eq(facilityId), qPort.portIndex.goe(fromIndex)).execute();
    }

    private void insertPricing(long facilityId, SortedSet<Pricing> pricing) {
        if (pricing != null && !pricing.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qPricing);
            for (Pricing price : pricing) {
                insert.set(qPricing.facilityId, facilityId)
                        .set(qPricing.capacityType, price.capacityType)
                        .set(qPricing.usage, price.usage)
                        .set(qPricing.maxCapacity, price.maxCapacity)
                        .set(qPricing.dayType, price.dayType)
                        .set(qPricing.fromTime, price.from)
                        .set(qPricing.untilTime, price.until);
                pricingPriceMapping.populate(price.price, insert);
                insert.addBatch();
            }
            insert.execute();
        }
    }

    private void deletePricing(long facilityId) {
        queryFactory.delete(qPricing).where(qPricing.facilityId.eq(facilityId)).execute();
    }

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.by, DEFAULT_SORT.by)) {
            case "name.fi": sortField = qFacility.nameFi.toUpperCase(); break;
            case "name.sv": sortField = qFacility.nameSv.toUpperCase(); break;
            case "name.en": sortField = qFacility.nameEn.toUpperCase(); break;
            default: throw invalidSortBy();
        }
        if (DESC.equals(sort.dir)) {
            qry.orderBy(sortField.desc(), qFacility.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qFacility.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }

    private void buildWhere(SpatialSearch search, PostgresQuery qry) {
        if (search.intersecting != null) {
            qry.where(qFacility.location.intersects(search.intersecting));
        }

        if (search.ids != null && !search.ids.isEmpty()) {
            qry.where(qFacility.id.in(search.ids));
        }
    }

    private void insertAliases(long facilityId, Collection<String> aliases) {
        if (aliases != null && !aliases.isEmpty()) {
            SQLInsertClause insertBatch = queryFactory.insert(qAlias);
            for (String alias : aliases) {
                insertBatch.set(qAlias.facilityId, facilityId);
                insertBatch.set(qAlias.alias, alias);
                insertBatch.addBatch();
            }
            insertBatch.execute();
        }
    }

    private void updateServices(long facilityId, Set<Long> serviceIds) {
        queryFactory.delete(qService).where(qService.facilityId.eq(facilityId)).execute();

        if (serviceIds != null && !serviceIds.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qService);
            for (Long serviceId : serviceIds) {
                insert.set(qService.facilityId, facilityId).set(qService.serviceId, serviceId).addBatch();
            }
            insert.execute();
        }
    }

    private void updatePaymentMethods(long facilityId, Set<Long> paymentMethodIds) {
        queryFactory.delete(qPaymentMethod).where(qPaymentMethod.facilityId.eq(facilityId)).execute();

        if (paymentMethodIds != null && !paymentMethodIds.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qPaymentMethod);
            for (Long serviceId : paymentMethodIds) {
                insert.set(qPaymentMethod.facilityId, facilityId).set(qPaymentMethod.paymentMethodId, serviceId).addBatch();
            }
            insert.execute();
        }
    }

    private void fetchPorts(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, List<Port>> ports = findPorts(facilitiesById.keySet());

            for (Map.Entry<Long, List<Port>> entry : ports.entrySet()) {
                facilitiesById.get(entry.getKey()).ports = entry.getValue();
            }
        }
    }

    private void fetchAliases(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, Set<String>> aliasesByFacilityId = findAliases(facilitiesById.keySet());

            for (Map.Entry<Long, Set<String>> entry : aliasesByFacilityId.entrySet()) {
                facilitiesById.get(entry.getKey()).aliases = new TreeSet<>(entry.getValue());
            }
        }
    }

    private void fetchServices(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, Set<Long>> services = findServices(facilitiesById.keySet());

            for (Map.Entry<Long, Set<Long>> entry : services.entrySet()) {
                facilitiesById.get(entry.getKey()).serviceIds = entry.getValue();
            }
        }
    }

    private void fetchPaymentMethods(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, Set<Long>> paymentMethods = findPaymentMethods(facilitiesById.keySet());

            for (Map.Entry<Long, Set<Long>> entry : paymentMethods.entrySet()) {
                facilitiesById.get(entry.getKey()).paymentInfo.paymentMethodIds = entry.getValue();
            }
        }
    }

    private void fetchPricing(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, SortedSet<Pricing>> pricing = findPricing(facilitiesById.keySet());

            for (Map.Entry<Long, SortedSet<Pricing>> entry : pricing.entrySet()) {
                facilitiesById.get(entry.getKey()).pricing = entry.getValue();
            }
        }
    }

    private Map<Long, SortedSet<Pricing>> findPricing(Set<Long> facilityIds) {
        return queryFactory.from(qPricing)
                .where(qPricing.facilityId.in(facilityIds))
                .transform(groupBy(qPricing.facilityId).as(sortedSet(pricingMapping)));
    }

    private Map<Long, Set<Long>> findServices(Set<Long> facilityIds) {
        return queryFactory.from(qService)
                .where(qService.facilityId.in(facilityIds))
                .transform(groupBy(qService.facilityId).as(set(qService.serviceId)));
    }

    private Map<Long, Set<Long>> findPaymentMethods(Set<Long> facilityIds) {
        return queryFactory.from(qPaymentMethod)
                .where(qPaymentMethod.facilityId.in(facilityIds))
                .transform(groupBy(qPaymentMethod.facilityId).as(set(qPaymentMethod.paymentMethodId)));
    }

    private Map<Long, Set<String>> findAliases(Set<Long> facilityIds) {
        return queryFactory.from(qAlias)
                .where(qAlias.facilityId.in(facilityIds))
                .transform(aliasesByFacilityIdMapping);
    }

    private Map<Long, List<Port>> findPorts(Set<Long> facilitiesById) {
        return queryFactory.from(qPort)
                .where(qPort.facilityId.in(facilitiesById))
                .transform(portsByFacilityIdMapping);
    }

    private void populate(Facility facility, StoreClause store) {
        store.set(qFacility.nameFi, facility.name.fi);
        store.set(qFacility.nameSv, facility.name.sv);
        store.set(qFacility.nameEn, facility.name.en);
        store.set(qFacility.location, facility.location);
        store.set(qFacility.operatorId, facility.operatorId);

        FacilityContacts contacts = facility.contacts;
        store.set(qFacility.emergencyContactId, contacts.emergency);
        store.set(qFacility.operatorContactId, contacts.operator);
        store.set(qFacility.serviceContactId, contacts.service);

        FacilityPaymentInfo paymentInfo = facility.paymentInfo;
        store.set(qFacility.parkAndRideAuthRequired, paymentInfo.parkAndRideAuthRequired);
        paymentInfoDetailMapping.populate(paymentInfo.detail, store);
        paymentInfoUrlMapping.populate(paymentInfo.url, store);
    }

    private SQLInsertClause insertFacility() {
        return queryFactory.insert(qFacility);
    }

    private SQLUpdateClause updateFacility() {
        return queryFactory.update(qFacility);
    }

    private PostgresQuery fromFacility() {
        return queryFactory.from(qFacility);
    }

}
