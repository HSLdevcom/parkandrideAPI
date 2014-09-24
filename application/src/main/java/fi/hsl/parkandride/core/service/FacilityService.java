package fi.hsl.parkandride.core.service;

import java.util.ArrayList;
import java.util.List;

import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;

import fi.hsl.parkandride.core.domain.*;
import fi.hsl.parkandride.core.outbound.FacilityRepository;

public class FacilityService {

    private final FacilityRepository repository;

    public FacilityService(FacilityRepository repository) {
        this.repository = repository;
    }

    @TransactionalWrite
    public Facility createFacility(Facility facility) {
        facility.id = repository.insertFacility(facility);
        return facility;
    }

    @TransactionalWrite
    public Facility updateFacility(long facilityId, Facility facility) {
        Facility oldFacility = repository.getFacilityForUpdate(facilityId);
        repository.updateFacility(facilityId, facility, oldFacility);
        return facility;
    }

    @TransactionalRead
    public Facility getFacility(long id) {
        return repository.getFacility(id);
    }

    @TransactionalRead
    public SearchResults search(PageableSpatialSearch search) {
        return repository.findFacilities(search);
    }

    // TODO: REMOVE - this method is only for demo/testing in the beginning of the project
    @TransactionalWrite
    public SearchResults generateTestData() {
        List<Facility> facilities = new ArrayList<>(64);
        // Generated from "Liityntäpysäköinnin_autopaikat_ja_pyöräparkit.xls"
        // using Excel with =CONCATENATE("facilities.add(insertTestData("""; C2;""", ";H2;"));")
        facilities.add(insertTestData("Hansatie 3", 98));
        facilities.add(insertTestData("Vantinportti 1", 20));
        facilities.add(insertTestData("Vantinportti 5 (radan eteläpuoli)", 100));
        facilities.add(insertTestData("Asemakuja 3", 52));
        facilities.add(insertTestData("Asemakuja 4", 18));
        facilities.add(insertTestData("Kamreerintie 3", 21));
        facilities.add(insertTestData("Espoonaukio 1", 29));
        facilities.add(insertTestData("Espoonaukio 2", 47));
        facilities.add(insertTestData("Kirkkojärventie 1", 100));
        facilities.add(insertTestData("Kirkkojärventie (aseman itäpuolella)", 12));
        facilities.add(insertTestData("Tuomarilantie 5", 27));
        facilities.add(insertTestData("Karapellontie 11", 12));
        facilities.add(insertTestData("Karapellontie 13", 21));
        facilities.add(insertTestData("Vanharaide 2a", 44));
        facilities.add(insertTestData("Kilonkuja 5", 34));
        facilities.add(insertTestData("Kilonportti 2", 39));
        facilities.add(insertTestData("Fonseenintie 2", 22));
        facilities.add(insertTestData("Turuntie 12", 20));
        facilities.add(insertTestData("Leppävaarankatu 3", 99));
        facilities.add(insertTestData("Leppäpolku 1", 130));
        facilities.add(insertTestData("Linnatullinkatu 2", 244));
        facilities.add(insertTestData("Ullanmäentie 2", 15));
        facilities.add(insertTestData("Luutnantintie 22", 46));
        facilities.add(insertTestData("Kaustisentie 10", 37));
        facilities.add(insertTestData("Sitratie 3", 24));
        facilities.add(insertTestData("Soittajantie 4", 20));
        facilities.add(insertTestData("Pasuunatie 1", 84));
        facilities.add(insertTestData("Kuparitie 2", 67));
        facilities.add(insertTestData("Haagan pappilantie 3", 64));
        facilities.add(insertTestData("Mäkelänkatu 70, Pyöräilystadion", 176));
        facilities.add(insertTestData("Hämeentie 105", 68));
        facilities.add(insertTestData("Haukilahdenkatu, Hämeentie 103", 35));
        facilities.add(insertTestData("Postintaival 3", 47));
        facilities.add(insertTestData("Tapulikaupungintie 7", 85));
        facilities.add(insertTestData("Maatullinaukio 10, Ajurinaukio", 7));
        facilities.add(insertTestData("Tapulikaupungintie 3", 14));
        facilities.add(insertTestData("Raidekuja 2", 62));
        facilities.add(insertTestData("Päitsipolku 3", 17));
        facilities.add(insertTestData("Jäkälätie 8", 60));
        facilities.add(insertTestData("Jäkälätie 6", 62));
        facilities.add(insertTestData("Seunalankuja 5", 11));
        facilities.add(insertTestData("Kauppakaarre 5, Malmin juna-asema", 137));
        facilities.add(insertTestData("Pikkalankatu 1", 89));
        facilities.add(insertTestData("Eskolantie 2", 54));
        facilities.add(insertTestData("Ratavallintie 4", 24));
        facilities.add(insertTestData("Oulunkylän Tori 4", 30));
        facilities.add(insertTestData("Oulunkylän Tori 6", 18));
        facilities.add(insertTestData("Torivoudintie 1", 36));
        facilities.add(insertTestData("Torivoudintie 1", 66));
        return SearchResults.of(facilities);
    }

    private Facility insertTestData(String name, int carCapacity) {
        Facility facility = new Facility();
        facility.name = name;
        facility.capacities.put(CapacityType.CAR, new Capacity(carCapacity, 0));
        facility.border = polygon("POLYGON((" +
                "25.010827 60.25055, " +
                "25.011867 60.250023, " +
                "25.012479 60.250337, " +
                "25.011454 60.250886, " +
                "25.010827 60.25055))");
        return createFacility(facility);
    }

    private static Polygon polygon(String wktShape) {
        return (Polygon) Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wktShape);
    }

}
