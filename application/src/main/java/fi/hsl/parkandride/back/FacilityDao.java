// Copyright © 2015 HSL

package fi.hsl.parkandride.back;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysema.query.ResultTransformer;
import com.mysema.query.Tuple;
import com.mysema.query.dml.StoreClause;
import com.mysema.query.sql.SQLExpressions;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.sql.postgres.PostgresQuery;
import com.mysema.query.sql.postgres.PostgresQueryFactory;
import com.mysema.query.types.ConstantImpl;
import com.mysema.query.types.MappingProjection;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.NumberPath;
import fi.hsl.parkandride.back.sql.*;
import fi.hsl.parkandride.core.back.FacilityRepository;
import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.service.TransactionalRead;
import fi.hsl.parkandride.core.service.TransactionalWrite;
import fi.hsl.parkandride.core.service.ValidationException;
import org.geolatte.geom.Point;
import org.joda.time.DateTime;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mysema.query.group.GroupBy.*;
import static com.mysema.query.spatial.GeometryExpressions.dwithin;
import static fi.hsl.parkandride.back.GSortedSet.sortedSet;
import static fi.hsl.parkandride.core.domain.CapacityType.*;
import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;
import static fi.hsl.parkandride.core.domain.Sort.Dir.DESC;
import static fi.hsl.parkandride.core.domain.Usage.*;

public class FacilityDao implements FacilityRepository {

    private static final Sort DEFAULT_SORT = new Sort("name.fi", ASC);

    private static final QFacility qFacility = QFacility.facility;

    private static final QFacilityAlias qAlias = QFacilityAlias.facilityAlias;

    private static final QPort qPort = QPort.port;

    private static final QFacilityService qService = QFacilityService.facilityService;

    private static final QFacilityUtilization qUtilization = QFacilityUtilization.facilityUtilization;

    private static final MappingProjection<Utilization> utilizationMapping = new MappingProjection<Utilization>(Utilization.class, qUtilization.all()) {
        @Override
        protected Utilization map(Tuple row) {
            Utilization u = new Utilization();
            u.facilityId = row.get(qUtilization.facilityId);
            u.capacityType = row.get(qUtilization.capacityType);
            u.usage = row.get(qUtilization.usage);
            u.timestamp = row.get(qUtilization.ts);
            u.spacesAvailable = row.get(qUtilization.spacesAvailable);
            return u;
        }
    };

    private static final QFacilityPaymentMethod qPaymentMethod = QFacilityPaymentMethod.facilityPaymentMethod;

    private static final QUnavailableCapacity qUnavailableCapacity = QUnavailableCapacity.unavailableCapacity;

    private static final MultilingualStringMapping statusDescriptionMapping =
            new MultilingualStringMapping(qFacility.statusDescriptionFi, qFacility.statusDescriptionSv, qFacility.statusDescriptionEn);

    private static final MultilingualStringMapping openingHoursInfoMapping =
            new MultilingualStringMapping(qFacility.openingHoursInfoFi, qFacility.openingHoursInfoSv, qFacility.openingHoursInfoEn);

    private static final MultilingualUrlMapping openingHoursUrlMapping =
            new MultilingualUrlMapping(qFacility.openingHoursUrlFi, qFacility.openingHoursUrlSv, qFacility.openingHoursUrlEn);

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
            pricing.time = new TimeDuration(row.get(qPricing.fromTime), row.get(qPricing.untilTime));
            pricing.price = pricingPriceMapping.map(row);
            return pricing;
        }
    };

    private static final MappingProjection<UnavailableCapacity> unavailableCapacityMapping =
            new MappingProjection<UnavailableCapacity>(UnavailableCapacity.class, qPricing.capacityType, qPricing.usage, qUnavailableCapacity.capacity) {
                @Override
                protected UnavailableCapacity map(Tuple row) {
                    CapacityType capacityType = row.get(qPricing.capacityType);
                    if (capacityType == null) {
                        return null;
                    }
                    UnavailableCapacity unavailableCapacity = new UnavailableCapacity();
                    unavailableCapacity.capacityType = capacityType;
                    unavailableCapacity.usage = row.get(qPricing.usage);
                    Integer capacity = row.get(qUnavailableCapacity.capacity);
                    unavailableCapacity.capacity = (capacity != null ? capacity : 0);
                    return unavailableCapacity;
                }
            };

    public static final ResultTransformer<Map<Long, Set<String>>> aliasesByFacilityIdMapping =
            groupBy(qAlias.facilityId).as(set(qAlias.alias));

    public static final ResultTransformer<Map<Long, List<Port>>> portsByFacilityIdMapping =
            groupBy(qPort.facilityId).as(list(portMapping));


    private static final MultilingualStringMapping paymentInfoDetailMapping =
            new MultilingualStringMapping(qFacility.paymentInfoDetailFi, qFacility.paymentInfoDetailSv, qFacility.paymentInfoDetailEn);

    private static final MultilingualUrlMapping paymentInfoUrlMapping =
            new MultilingualUrlMapping(qFacility.paymentInfoUrlFi, qFacility.paymentInfoUrlSv, qFacility.paymentInfoUrlEn);

    private static final MultilingualStringMapping nameMapping = new MultilingualStringMapping(qFacility.nameFi, qFacility.nameSv, qFacility.nameEn);

    private static final MappingProjection<FacilityInfo> facilityInfoMapping = new MappingProjection<FacilityInfo>(FacilityInfo.class, qFacility.all()) {
        @Override
        protected FacilityInfo map(Tuple row) {
            return mapFacility(row, new FacilityInfo());
        }
    };

    private static <T extends FacilityInfo> T mapFacility(Tuple row, T facility) {
        Long id = row.get(qFacility.id);
        if (id == null) {
            return null;
        }
        facility.id = id;
        facility.location = row.get(qFacility.location);
        facility.name = nameMapping.map(row);
        facility.operatorId = row.get(qFacility.operatorId);
        facility.status = row.get(qFacility.status);
        facility.statusDescription = statusDescriptionMapping.map(row);
        facility.pricingMethod = row.get(qFacility.pricingMethod);

        if (row.get(qFacility.usageParkAndRide)) {
            facility.usages.add(PARK_AND_RIDE);
        }
        if (row.get(qFacility.usageHsl)) {
            facility.usages.add(HSL_TRAVEL_CARD);
        }
        if (row.get(qFacility.usageCommercial)) {
            facility.usages.add(COMMERCIAL);
        }

        mapCapacity(facility.builtCapacity, CAR, row.get(qFacility.capacityCar));
        mapCapacity(facility.builtCapacity, DISABLED, row.get(qFacility.capacityDisabled));
        mapCapacity(facility.builtCapacity, ELECTRIC_CAR, row.get(qFacility.capacityElectricCar));
        mapCapacity(facility.builtCapacity, MOTORCYCLE, row.get(qFacility.capacityMotorcycle));
        mapCapacity(facility.builtCapacity, BICYCLE, row.get(qFacility.capacityBicycle));
        mapCapacity(facility.builtCapacity, BICYCLE_SECURE_SPACE, row.get(qFacility.capacityBicycleSecureSpace));

        return facility;
    }

    private static final MappingProjection<Facility> facilityMapping = new MappingProjection<Facility>(Facility.class, qFacility.all()) {
        @Override
        protected Facility map(Tuple row) {
            Facility facility = mapFacility(row, new Facility());
            facility.contacts = new FacilityContacts(
                    row.get(qFacility.emergencyContactId),
                    row.get(qFacility.operatorContactId),
                    row.get(qFacility.serviceContactId)
            );
            facility.paymentInfo.detail = paymentInfoDetailMapping.map(row);
            facility.paymentInfo.url = paymentInfoUrlMapping.map(row);

            facility.openingHours.info = openingHoursInfoMapping.map(row);
            facility.openingHours.url = openingHoursUrlMapping.map(row);

            return facility;
        }
    };

    private static void mapCapacity(Map<CapacityType, Integer> capacities, CapacityType type, Integer capacity) {
        if (capacity != null && capacity > 0) {
            capacities.put(type, capacity);
        }
    }

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
        checkNotNull(facility, "facility");
        facility.normalize();

        SQLInsertClause insert = insertFacility();
        insert.set(qFacility.id, facilityId);
        populate(facility, insert);
        insert.execute();

        insertAliases(facilityId, facility.aliases);
        insertPorts(facilityId, facility.ports);
        updateServices(facilityId, facility.services);
        updatePaymentMethods(facilityId, facility.paymentInfo.paymentMethods);
        insertPricing(facilityId, facility.pricingMethod.getPricing(facility));
        insertUnavailableCapacity(facilityId, facility.unavailableCapacities);

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
        newFacility.normalize();

        SQLUpdateClause update = updateFacility().where(qFacility.id.eq(facilityId));
        populate(newFacility, update);
        if (update.execute() != 1) {
            throw new FacilityNotFoundException(facilityId);
        }

        updateAliases(facilityId, newFacility.aliases, oldFacility.aliases);
        updatePorts(facilityId, newFacility.ports, oldFacility.ports);

        if (!Objects.equals(newFacility.services, oldFacility.services)) {
            updateServices(facilityId, newFacility.services);
        }

        if (!Objects.equals(newFacility.paymentInfo.paymentMethods, oldFacility.paymentInfo.paymentMethods)) {
            updatePaymentMethods(facilityId, newFacility.paymentInfo.paymentMethods);
        }

        if (!Objects.equals(newFacility.pricing, oldFacility.pricing)) {
            deletePricing(facilityId);
            insertPricing(facilityId, newFacility.pricing);
        }

        if (!Objects.equals(newFacility.unavailableCapacities, oldFacility.unavailableCapacities)) {
            deleteUnavailableCapacity(facilityId);
            insertUnavailableCapacity(facilityId, newFacility.unavailableCapacities);
        }
    }

    @TransactionalRead
    @Override
    public Facility getFacility(long facilityId) {
        return getFacility(facilityId, false);
    }

    @TransactionalRead
    @Override
    public FacilityInfo getFacilityInfo(long facilityId) {
        FacilityInfo facility = fromFacility().where(qFacility.id.eq(facilityId)).singleResult(facilityInfoMapping);
        if (facility == null) {
            throw new FacilityNotFoundException(facilityId);
        }
        return facility;
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
        fetchUnavailableCapacity(facilityMap);

        facility.initialize();
        return facility;
    }

    @TransactionalRead
    @Override
    public SearchResults<FacilityInfo> findFacilities(PageableFacilitySearch search) {
        PostgresQuery qry = fromFacility();
        qry.limit(search.limit + 1);
        qry.offset(search.offset);

        buildWhere(search, qry);
        orderBy(search.sort, qry);

        Map<Long, FacilityInfo> facilities = qry.map(qFacility.id, facilityInfoMapping);
        return SearchResults.of(facilities.values(), search.limit);
    }

    @TransactionalRead
    @Override
    public FacilitySummary summarizeFacilities(FacilitySearch search) {
        PostgresQuery qry = fromFacility();

        buildWhere(search, qry);

        Tuple result = qry.singleResult(
                qFacility.id.count(),
                qFacility.capacityCar.sum(),
                qFacility.capacityDisabled.sum(),
                qFacility.capacityElectricCar.sum(),
                qFacility.capacityMotorcycle.sum(),
                qFacility.capacityBicycle.sum(),
                qFacility.capacityBicycleSecureSpace.sum());

        // FIXME: what if (result == null) ?!?
        assert result != null;

        Map<CapacityType, Integer> capacities = Maps.newHashMap();
        mapCapacity(capacities, CAR, result.get(qFacility.capacityCar.sum()));
        mapCapacity(capacities, DISABLED, result.get(qFacility.capacityDisabled.sum()));
        mapCapacity(capacities, ELECTRIC_CAR, result.get(qFacility.capacityElectricCar.sum()));
        mapCapacity(capacities, MOTORCYCLE, result.get(qFacility.capacityMotorcycle.sum()));
        mapCapacity(capacities, BICYCLE, result.get(qFacility.capacityBicycle.sum()));
        mapCapacity(capacities, BICYCLE_SECURE_SPACE, result.get(qFacility.capacityBicycleSecureSpace.sum()));

        return new FacilitySummary(result.get(qFacility.id.count()), capacities);
    }

    @TransactionalWrite
    @Override
    public void insertUtilizations(List<Utilization> utilizations) {
        SQLInsertClause insertBatch = queryFactory.insert(qUtilization);
        utilizations.forEach(u -> {
            insertBatch.set(qUtilization.facilityId, u.facilityId);
            insertBatch.set(qUtilization.capacityType, u.capacityType);
            insertBatch.set(qUtilization.usage, u.usage);
            insertBatch.set(qUtilization.spacesAvailable, u.spacesAvailable);
            insertBatch.set(qUtilization.ts, u.timestamp);
            insertBatch.addBatch();
        });
        insertBatch.execute();
    }

    @TransactionalRead
    @Override
    public Set<Utilization> findLatestUtilization(long facilityId) {
        // TODO: do with a single query
        List<Tuple> utilizationKeyCombinations = queryFactory.from(qUtilization)
                .where(qUtilization.facilityId.eq(facilityId))
                .distinct()
                .list(qUtilization.capacityType, qUtilization.usage);
        return utilizationKeyCombinations.stream()
                .map(utilizationKey -> queryFactory.from(qUtilization)
                        .where(qUtilization.facilityId.eq(facilityId),
                                qUtilization.capacityType.eq(utilizationKey.get(qUtilization.capacityType)),
                                qUtilization.usage.eq(utilizationKey.get(qUtilization.usage)))
                        .orderBy(qUtilization.ts.desc())
                        .singleResult(utilizationMapping))
                .collect(Collectors.toSet());
    }

    @TransactionalRead
    @Override
    public List<Utilization> findUtilizationsBetween(UtilizationKey utilizationKey, DateTime start, DateTime end) {
        // TODO: limit the amount of results per query or return a lazy iterator (must also update UtilizationHistory)
        return queryFactory.from(qUtilization)
                .where(qUtilization.facilityId.eq(utilizationKey.facilityId),
                        qUtilization.capacityType.eq(utilizationKey.capacityType),
                        qUtilization.usage.eq(utilizationKey.usage),
                        qUtilization.ts.between(start, end))
                .orderBy(qUtilization.ts.asc())
                .list(utilizationMapping);
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

        for (int i = 0; i < newPorts.size(); i++) {
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
            for (Entry<Integer, Port> entry : updatedPorts.entrySet()) {
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
            Map<Integer, Port> addedPorts = new HashMap<>();
            for (int i = 0; i < ports.size(); i++) {
                addedPorts.put(i, ports.get(i));
            }
            insertPorts(facilityId, addedPorts);
        }
    }

    private void insertPorts(long facilityId, Map<Integer, Port> addedPorts) {
        if (addedPorts != null && !addedPorts.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qPort);
            for (Entry<Integer, Port> entry : addedPorts.entrySet()) {
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

    private void insertPricing(long facilityId, List<Pricing> pricing) {
        if (pricing != null && !pricing.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qPricing);
            for (Pricing price : pricing) {
                insert.set(qPricing.facilityId, facilityId)
                        .set(qPricing.capacityType, price.capacityType)
                        .set(qPricing.usage, price.usage)
                        .set(qPricing.maxCapacity, price.maxCapacity)
                        .set(qPricing.dayType, price.dayType)
                        .set(qPricing.fromTime, price.time.from)
                        .set(qPricing.untilTime, price.time.until);
                pricingPriceMapping.populate(price.price, insert);
                insert.addBatch();
            }
            insert.execute();
        }
    }

    private void deletePricing(long facilityId) {
        queryFactory.delete(qPricing).where(qPricing.facilityId.eq(facilityId)).execute();
    }

    private void insertUnavailableCapacity(long facilityId, List<UnavailableCapacity> unavailableCapacities) {
        if (unavailableCapacities != null && !unavailableCapacities.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qUnavailableCapacity);
            for (UnavailableCapacity unavailableCapacity : unavailableCapacities) {
                insert.set(qUnavailableCapacity.facilityId, facilityId)
                        .set(qUnavailableCapacity.capacityType, unavailableCapacity.capacityType)
                        .set(qUnavailableCapacity.usage, unavailableCapacity.usage)
                        .set(qUnavailableCapacity.capacity, unavailableCapacity.capacity);
                insert.addBatch();
            }
            insert.execute();
        }
    }

    private void deleteUnavailableCapacity(long facilityId) {
        queryFactory.delete(qUnavailableCapacity).where(qUnavailableCapacity.facilityId.eq(facilityId)).execute();
    }

    private void orderBy(Sort sort, PostgresQuery qry) {
        sort = firstNonNull(sort, DEFAULT_SORT);
        ComparableExpression<String> sortField;
        switch (firstNonNull(sort.getBy(), DEFAULT_SORT.getBy())) {
            case "name.fi":
                sortField = qFacility.nameFi.lower();
                break;
            case "name.sv":
                sortField = qFacility.nameSv.lower();
                break;
            case "name.en":
                sortField = qFacility.nameEn.lower();
                break;
            default:
                throw invalidSortBy();
        }
        if (DESC.equals(sort.getDir())) {
            qry.orderBy(sortField.desc(), qFacility.id.desc());
        } else {
            qry.orderBy(sortField.asc(), qFacility.id.asc());
        }
    }

    private ValidationException invalidSortBy() {
        return new ValidationException(new Violation("SortBy", "sort.by", "Expected one of 'name.fi', 'name.sv' or 'name.en'"));
    }

    private void buildWhere(FacilitySearch search, PostgresQuery qry) {
        if (search.getStatuses() != null && !search.getStatuses().isEmpty()) {
            qry.where(qFacility.status.in(search.getStatuses()));
        }

        if (search.getIds() != null && !search.getIds().isEmpty()) {
            qry.where(qFacility.id.in(search.getIds()));
        }

        if (search.getGeometry() != null) {
            if (search.getMaxDistance() != null && search.getMaxDistance() > 0) {
                qry.where(dwithin(qFacility.location, ConstantImpl.create(search.getGeometry()), search.getMaxDistance()));
            } else {
                qry.where(qFacility.location.intersects(search.getGeometry()));
            }
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

    private void updateServices(long facilityId, Set<Service> services) {
        queryFactory.delete(qService).where(qService.facilityId.eq(facilityId)).execute();

        if (services != null && !services.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qService);
            for (Service service : services) {
                insert.set(qService.facilityId, facilityId).set(qService.service, service).addBatch();
            }
            insert.execute();
        }
    }

    private void updatePaymentMethods(long facilityId, Set<PaymentMethod> paymentMethods) {
        queryFactory.delete(qPaymentMethod).where(qPaymentMethod.facilityId.eq(facilityId)).execute();

        if (paymentMethods != null && !paymentMethods.isEmpty()) {
            SQLInsertClause insert = queryFactory.insert(qPaymentMethod);
            for (PaymentMethod paymentMethod : paymentMethods) {
                insert.set(qPaymentMethod.facilityId, facilityId).set(qPaymentMethod.paymentMethod, paymentMethod).addBatch();
            }
            insert.execute();
        }
    }

    private void fetchPorts(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, List<Port>> ports = findPorts(facilitiesById.keySet());

            for (Entry<Long, List<Port>> entry : ports.entrySet()) {
                facilitiesById.get(entry.getKey()).ports = entry.getValue();
            }
        }
    }

    private void fetchAliases(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, Set<String>> aliasesByFacilityId = findAliases(facilitiesById.keySet());

            for (Entry<Long, Set<String>> entry : aliasesByFacilityId.entrySet()) {
                facilitiesById.get(entry.getKey()).aliases = new TreeSet<>(entry.getValue());
            }
        }
    }

    private void fetchServices(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, NullSafeSortedSet<Service>> services = findServices(facilitiesById.keySet());

            for (Entry<Long, NullSafeSortedSet<Service>> entry : services.entrySet()) {
                facilitiesById.get(entry.getKey()).services = entry.getValue();
            }
        }
    }

    private void fetchPaymentMethods(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, NullSafeSortedSet<PaymentMethod>> paymentMethods = findPaymentMethods(facilitiesById.keySet());

            for (Entry<Long, NullSafeSortedSet<PaymentMethod>> entry : paymentMethods.entrySet()) {
                facilitiesById.get(entry.getKey()).paymentInfo.paymentMethods = entry.getValue();
            }
        }
    }

    private void fetchPricing(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, List<Pricing>> pricing = findPricing(facilitiesById.keySet());

            for (Entry<Long, List<Pricing>> entry : pricing.entrySet()) {
                Facility facility = facilitiesById.get(entry.getKey());
                facility.pricing = entry.getValue();
            }
        }
    }

    private void fetchUnavailableCapacity(Map<Long, Facility> facilitiesById) {
        if (!facilitiesById.isEmpty()) {
            Map<Long, List<UnavailableCapacity>> pricing = findUnavailableCapacity(facilitiesById.keySet());

            for (Entry<Long, List<UnavailableCapacity>> entry : pricing.entrySet()) {
                Facility facility = facilitiesById.get(entry.getKey());
                facility.unavailableCapacities = entry.getValue();
            }
        }
    }

    private Map<Long, List<Pricing>> findPricing(Set<Long> facilityIds) {
        return queryFactory.from(qPricing)
                .where(qPricing.facilityId.in(facilityIds))
                .transform(groupBy(qPricing.facilityId).as(list(pricingMapping)));
    }

    private Map<Long, List<UnavailableCapacity>> findUnavailableCapacity(Set<Long> facilityIds) {
        return queryFactory.from(qPricing)
                .leftJoin(qUnavailableCapacity).on(
                        qPricing.facilityId.eq(qUnavailableCapacity.facilityId),
                        qPricing.capacityType.eq(qUnavailableCapacity.capacityType),
                        qPricing.usage.eq(qUnavailableCapacity.usage))
                .distinct()
                .where(qPricing.facilityId.in(facilityIds))
                .transform(groupBy(qPricing.facilityId).as(list(unavailableCapacityMapping)));
    }

    private Map<Long, NullSafeSortedSet<Service>> findServices(Set<Long> facilityIds) {
        return queryFactory.from(qService)
                .where(qService.facilityId.in(facilityIds))
                .transform(groupBy(qService.facilityId).as(sortedSet(qService.service)));
    }

    private Map<Long, NullSafeSortedSet<PaymentMethod>> findPaymentMethods(Set<Long> facilityIds) {
        return queryFactory.from(qPaymentMethod)
                .where(qPaymentMethod.facilityId.in(facilityIds))
                .transform(groupBy(qPaymentMethod.facilityId).as(sortedSet(qPaymentMethod.paymentMethod)));
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
        nameMapping.populate(facility.name, store);
        store.set(qFacility.location, facility.location);
        store.set(qFacility.operatorId, facility.operatorId);
        store.set(qFacility.status, facility.status);
        store.set(qFacility.pricingMethod, facility.pricingMethod);
        statusDescriptionMapping.populate(facility.statusDescription, store);

        FacilityContacts contacts = facility.contacts != null ? facility.contacts : new FacilityContacts();
        store.set(qFacility.emergencyContactId, contacts.emergency);
        store.set(qFacility.operatorContactId, contacts.operator);
        store.set(qFacility.serviceContactId, contacts.service);

        openingHoursInfoMapping.populate(facility.openingHours.info, store);
        openingHoursUrlMapping.populate(facility.openingHours.url, store);

        Set<Usage> usages = facility.analyzeUsages();
        store.set(qFacility.usageParkAndRide, usages.contains(PARK_AND_RIDE));
        store.set(qFacility.usageHsl, usages.contains(HSL_TRAVEL_CARD));
        store.set(qFacility.usageCommercial, usages.contains(COMMERCIAL));

        Map<CapacityType, Integer> builtCapacity = facility.builtCapacity != null ? facility.builtCapacity : ImmutableMap.of();
        populateCapacity(qFacility.capacityCar, builtCapacity.get(CAR), store);
        populateCapacity(qFacility.capacityDisabled, builtCapacity.get(DISABLED), store);
        populateCapacity(qFacility.capacityElectricCar, builtCapacity.get(ELECTRIC_CAR), store);
        populateCapacity(qFacility.capacityMotorcycle, builtCapacity.get(MOTORCYCLE), store);
        populateCapacity(qFacility.capacityBicycle, builtCapacity.get(BICYCLE), store);
        populateCapacity(qFacility.capacityBicycleSecureSpace, builtCapacity.get(BICYCLE_SECURE_SPACE), store);

        FacilityPaymentInfo paymentInfo = facility.paymentInfo;
        paymentInfoDetailMapping.populate(paymentInfo.detail, store);
        paymentInfoUrlMapping.populate(paymentInfo.url, store);
    }

    private void populateCapacity(NumberPath<Integer> path, Integer value, StoreClause store) {
        if (value == null || value < 1) {
            store.setNull(path);
        } else {
            store.set(path, value);
        }
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
