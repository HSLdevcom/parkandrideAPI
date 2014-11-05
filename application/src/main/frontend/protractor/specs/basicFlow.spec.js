'use strict';

var _ = require('lodash');

var po = require('../pageobjects/pageobjects');
var fixtures = require('../fixtures/fixtures');
var arrayAssert = require('./arrayAssert')();
var devApi = require('./devApi')();
var common = require('./common');

describe('Basic flow', function() {
    var menu = po.menu({});
    var indexPage = po.indexPage({});

    var facilityEditPage = po.facilityEditPage({});
    var facilityViewPage = po.facilityViewPage({});

    var hubListPage = po.hubListPage({});
    var hubEditPage = po.hubEditPage({});
    var hubViewPage = po.hubViewPage({});

    function newHubName() {
        return 'Test Hub ' + new Date().getTime();
    }

    var facility1 = fixtures.facilitiesFixture.dummies.facFull;
    var facility2 = fixtures.facilitiesFixture.dummies.facCar;

    var totalCapacities = _.reduce([facility1, facility2], function (acc, facility) { return acc.incCapacity(facility); });

    var hubName = "Test Hub 1";

    it('should reset all', function() {
        devApi.resetAll();
    });

    it('Go to facility create', function() {
        indexPage.get();
        expect(hubListPage.isDisplayed()).toBe(true);
        hubListPage.toFacilityCreateView();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });

    it('Create facility 1', function () {
        facility1.name = "Test Facility B";
        facilityEditPage.setName(facility1.name);
        facilityEditPage.drawLocation(facility1.locationInput.offset, facility1.locationInput.w, facility1.locationInput.h);
        facilityEditPage.addAlias(facility1.aliases[0]);
        facilityEditPage.addAlias(facility1.aliases[1]);
        facilityEditPage.setCapacities(facility1.capacities);
        arrayAssert.assertInOrder(facilityEditPage.getCapacityTypes(), common.capacityTypeOrder);

        facilityEditPage.save();
        expect(facilityViewPage.isDisplayed()).toBe(true);
        expect(facilityViewPage.getName()).toBe(facility1.name);
        expect(facilityViewPage.getAliases()).toEqual(facility1.aliases);
        arrayAssert.assertInOrder(facilityViewPage.capacitiesTable.getTypes(), common.capacityTypeOrder);
        expect(facilityViewPage.capacitiesTable.getCapacities(_.keys(facility1.capacities))).toEqual(facility1.capacities);
    });

    it('Return to list and go to facility create', function() {
        facilityViewPage.toListView();
        expect(hubListPage.isDisplayed()).toBe(true);
        arrayAssert.assertInOrder(hubListPage.getCapacityTypes(1), common.capacityTypeOrder);

        hubListPage.toFacilityCreateView();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });

    it('Create facility 2', function() {
        facility2.name = "Test Facility a";
        facilityEditPage.setName(facility2.name);

        facilityEditPage.drawLocation(facility2.locationInput.offset, facility2.locationInput.w, facility2.locationInput.h);
        facilityEditPage.addAlias(facility2.aliases[0]);
        facilityEditPage.setCapacities(facility2.capacities);

        facilityEditPage.save();
        expect(facilityViewPage.isDisplayed()).toBe(true);
        expect(facilityViewPage.getName()).toBe(facility2.name);
        expect(facilityViewPage.getAliases()).toEqual(facility2.aliases);
        arrayAssert.assertInOrder(facilityViewPage.capacitiesTable.getTypes(), common.capacityTypeOrder, { allowSkip: true });
        expect(facilityViewPage.capacitiesTable.getCapacities(_.keys(facility2.capacities))).toEqual(facility2.capacities);
    });

    it('Go to create hub via hub list', function() {
        menu.toHubs();
        expect(hubListPage.isDisplayed()).toBe(true);
        expect(hubListPage.getHubAndFacilityNames()).toEqual([facility2.name, facility1.name]);

        hubListPage.toHubCreateView();
        expect(hubEditPage.isDisplayed()).toBe(true);
    });

    it('Create hub', function() {
        hubEditPage.setName(hubName);
        expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(false);

        hubEditPage.toggleFacility(facility1);
        hubEditPage.toggleFacility(facility2);
        hubEditPage.setLocation({x: 165, y: 165});
        // NOTE: It takes some time until toggleFacility is reflected facilitiesTable - asserting getFacilityNames directly after toggle fails.
        expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(true);
        browser.debugger();
        expect(hubEditPage.facilitiesTable.getFacilityNames()).toEqual([facility2.name, facility1.name]);

        hubEditPage.save();
        expect(hubViewPage.isDisplayed()).toBe(true);
        expect(hubViewPage.getName()).toBe(hubName);
        arrayAssert.assertInOrder(hubViewPage.capacitiesTable.getTypes(), common.capacityTypeOrder);
        expect(hubViewPage.capacitiesTable.getCapacities(_.keys(totalCapacities.capacities))).toEqual(totalCapacities.capacities);
        expect(hubViewPage.facilitiesTable.isDisplayed()).toBe(true);
        expect(hubViewPage.facilitiesTable.getFacilityNames()).toEqual([facility2.name, facility1.name]);
    });

    it('List facilities grouped by hubs', function() {
        menu.toHubs();
        expect(hubListPage.isDisplayed()).toBe(true);
        expect(hubListPage.getHubAndFacilityNames()).toEqual([hubName, facility2.name, facility1.name]);
    });
});
