'use strict';

var _ = require('lodash');

var po = require('../pageobjects/pageobjects');
var fixtures = require('../fixtures/fixtures');
var arrayAssert = require('./arrayAssert')();
var devApi = require('./devApi')();

describe('Basic flow', function() {
    var menu = po.menu({});
    var indexPage = po.indexPage({});

    var facilityEditPage = po.facilityEditPage({});
    var facilityViewPage = po.facilityViewPage({});

    var hubListPage = po.hubListPage({});
    var hubEditPage = po.hubEditPage({});
    var hubViewPage = po.hubViewPage({});

    function newFacilityName() {
        return 'Test Facility ' + new Date().getTime();
    }

    function newHubName() {
        return 'Test Hub ' + new Date().getTime();
    }

    var facility1 = fixtures.facility({
        capacities: {
            "CAR": {"built": 10, "unavailable": 1},
            "BICYCLE": {"built": 20, "unavailable": 2},
            "PARK_AND_RIDE": {"built": 30, "unavailable": 3},
            "DISABLED": {"built": 40, "unavailable": 4},
            "MOTORCYCLE": {"built": 50, "unavailable": 5},
            "ELECTRIC_CAR": {"built": 60, "unavailable": 6}
        },
        aliases: ["alias with spaces", "facility1"],
        borderInput: {
            offset: {x: 90, y: 90},
            w: 60,
            h: 60
        }
    });

    var facility2 = fixtures.facility({
        capacities: {
            "CAR": {"built": 10, "unavailable": 1}
        },
        aliases: ["fac2"],
        borderInput: {
            offset: {x: 180, y: 180},
            w: 60,
            h: 60
        }
    });
    var totalCapacities = _.reduce([facility1, facility2], function (acc, facility) { return acc.incCapacity(facility); });

    var tooLongValue = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    var hubName = newHubName();

    var capacityTypeOrder = ["Liityntäpysäköinti", "Polkupyörä", "Henkilöauto", "Invapaikka", "Moottoripyörä", "Sähköauto"];

    it('should reset all', function() {
        devApi.resetAll();
    });

    it('Go to facility create', function() {
        indexPage.get();
        expect(hubListPage.isDisplayed()).toBe(true);
        hubListPage.toFacilityCreateView();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });

    it('Try to create invalid facility', function () {
        expect(facilityEditPage.isDirty()).toBe(false);
        facilityEditPage.save();
        expect(facilityEditPage.isDisplayed()).toBe(true);
        expect(facilityEditPage.isDirty()).toBe(true);

        facility1.name = newFacilityName();
        facilityEditPage.drawBorder(facility1.borderInput.offset, facility1.borderInput.w, facility1.borderInput.h);
        facilityEditPage.setNameFi(tooLongValue);
        facilityEditPage.setNameSv(facility1.name);
        facilityEditPage.setNameEn(facility1.name);

        facilityEditPage.save();

        expect(facilityEditPage.getViolations()).toEqual([{path:"Nimi (fi)", message:"saa olla korkeintaan 255 merkkiä pitkä"}]);
    });

    it('Create facility 1', function () {
        facilityEditPage.setNameFi(facility1.name);
        facilityEditPage.addAlias(facility1.aliases[0]);
        facilityEditPage.addAlias(facility1.aliases[1]);
        facilityEditPage.setCapacities(facility1.capacities);
        arrayAssert.assertInOrder(facilityEditPage.getCapacityTypes(), capacityTypeOrder);

        facilityEditPage.save();
        expect(facilityViewPage.isDisplayed()).toBe(true);
        expect(facilityViewPage.getName()).toBe(facility1.name);
        expect(facilityViewPage.getAliases()).toEqual(facility1.aliases);
        arrayAssert.assertInOrder(facilityViewPage.capacitiesTable.getTypes(), capacityTypeOrder);
        expect(facilityViewPage.capacitiesTable.getCapacities(_.keys(facility1.capacities))).toEqual(facility1.capacities);
    });

    it('Return to list and go to facility create', function() {
        facilityViewPage.toListView();
        expect(hubListPage.isDisplayed()).toBe(true);
        arrayAssert.assertInOrder(hubListPage.getCapacityTypes(1), capacityTypeOrder);

        hubListPage.toFacilityCreateView();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });

    it('Create facility 2', function() {
        facility2.name = newFacilityName();
        facilityEditPage.setName(facility2.name);

        facilityEditPage.drawBorder(facility2.borderInput.offset, facility2.borderInput.w, facility2.borderInput.h);
        facilityEditPage.addAlias(facility2.aliases[0]);
        facilityEditPage.setCapacities(facility2.capacities);

        facilityEditPage.save();
        expect(facilityViewPage.isDisplayed()).toBe(true);
        expect(facilityViewPage.getName()).toBe(facility2.name);
        expect(facilityViewPage.getAliases()).toEqual(facility2.aliases);
        arrayAssert.assertInOrder(facilityViewPage.capacitiesTable.getTypes(), capacityTypeOrder, { allowSkip: true });
        expect(facilityViewPage.capacitiesTable.getCapacities(_.keys(facility2.capacities))).toEqual(facility2.capacities);
    });

    it('Go to create hub via hub list', function() {
        menu.toHubs();
        expect(hubListPage.isDisplayed()).toBe(true);
        expect(hubListPage.getHubAndFacilityNames()).toEqual([facility1.name, facility2.name]);

        hubListPage.toHubCreateView();
        expect(hubEditPage.isDisplayed()).toBe(true);
    });

    it('Try to create invalid hub', function () {
        expect(hubEditPage.isDirty()).toBe(false);
        hubEditPage.save();
        expect(hubEditPage.isDisplayed()).toBe(true);
        expect(hubEditPage.isDirty()).toBe(true);

        // Too long name
        hubEditPage.setNameFi(tooLongValue);
        hubEditPage.setNameSv(hubName);
        hubEditPage.setNameEn(hubName);
        hubEditPage.setLocation({x: 165, y: 165});
        hubEditPage.save();

        expect(facilityEditPage.getViolations()).toEqual([{path:"Nimi (fi)", message:"saa olla korkeintaan 255 merkkiä pitkä"}]);
    });

    it('Create hub', function() {
        hubEditPage.setName(hubName);
        expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(false);

        hubEditPage.toggleFacility(facility1);
            hubEditPage.toggleFacility(facility2);
        // NOTE: It takes some time until toggleFacility is reflected facilitiesTable - asserting getFacilityNames directly after toggle fails.

        expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(true);
        expect(hubEditPage.facilitiesTable.getFacilityNames()).toEqual([facility1.name, facility2.name]);

        hubEditPage.save();
        expect(hubViewPage.isDisplayed()).toBe(true);
        expect(hubViewPage.getName()).toBe(hubName);
        arrayAssert.assertInOrder(hubViewPage.capacitiesTable.getTypes(), capacityTypeOrder);
        expect(hubViewPage.capacitiesTable.getCapacities(_.keys(totalCapacities.capacities))).toEqual(totalCapacities.capacities);
        expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(true);
        expect(hubEditPage.facilitiesTable.getFacilityNames()).toEqual([facility1.name, facility2.name]);
    });

    it('List facilities grouped by hubs', function() {
        menu.toHubs();
        expect(hubListPage.isDisplayed()).toBe(true);
        expect(hubListPage.getHubAndFacilityNames()).toEqual([hubName, facility1.name, facility2.name]);
    });
});
