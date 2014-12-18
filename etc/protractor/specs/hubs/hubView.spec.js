"use strict";

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('hub view', function () {
    var westend = fixtures.hubsFixture.westend;

    var viewPage = po.hubViewPage({});
    var editPage = po.hubEditPage({});
    var listPage = po.hubListPage({});
    var facilityViewPage = po.facilityViewPage({});

    var h;

    function assertFacilityNamesInAnyOrder(facilitiesTable, expected) {
        expect(facilitiesTable.isDisplayed()).toBe(true);
        arrayAssert.assertInAnyOrder(facilitiesTable.getNames(), expected);
    }

    function totalCapacities(facilities) {
        return _.reduce(facilities, function (acc, facility) { return acc.incCapacity(facility); });
    }

    function assertCapacities(capacitiesTable, facilities) {
        arrayAssert.assertInOrder(capacitiesTable.getTypes(), common.capacityTypeOrder, { allowSkip: true });
        var total = totalCapacities(facilities);
        expect(capacitiesTable.getCapacities(_.keys(total.capacities))).toEqual(total.capacities);
    }

    function toView(h) {
        devApi.resetAll({facilities: h.facilities, hubs: [h], contacts: [fixtures.hubsFixture.contact], operators: [fixtures.hubsFixture.operator]});
        viewPage.get(h.id);
        expect(viewPage.isDisplayed()).toBe(true);
        return h;
    }

    describe('navigation', function () {
        beforeEach(function () {
            h = toView(westend);
        });

        it('to edit view', function () {
            viewPage.toEditView();
            expect(editPage.isDisplayed()).toBe(true);
        });

        it('to hub list', function () {
            viewPage.toListView();
            expect(listPage.isDisplayed()).toBe(true);
        });
    });

    describe('with facilities and capacities', function () {
        beforeEach(function () {
            h = toView(westend);
        });

        it('facilities and total capacities are shown', function () {
            expect(viewPage.isDisplayed()).toBe(true);
            expect(viewPage.getName()).toBe(h.name);
            assertFacilityNamesInAnyOrder(viewPage.facilitiesTable, [h.facilities[0].name, h.facilities[1].name]);

            expect(viewPage.isFacilitiesTotalDisplayed()).toBe(true);
            assertCapacities(viewPage.capacitiesTable, h.facilities);
        });
    });

    describe('without facilities', function () {
        beforeEach(function () {
            h = westend.copy();
            h.setFacilities([]);
            toView(h);
        });

        it('facilities and capacities are not displayed', function () {
            expect(viewPage.getNoFacilitiesMessage()).toEqual("Alueeseen ei ole lisätty pysäköintipaikkoja");
            expect(viewPage.facilitiesTable.isDisplayed()).toBe(false);

            expect(viewPage.isFacilitiesTotalDisplayed()).toBe(false);
            expect(viewPage.capacitiesTable.isDisplayed()).toBe(false);
        });
    });

    describe('without capacities', function () {
        beforeEach(function () {
            h = westend.copy();
            _.forEach(h.facilities, function(f){ f.capacities = {}; });
            toView(h);
        });

        it('capacities are not displayed', function () {
            assertFacilityNamesInAnyOrder(viewPage.facilitiesTable, [h.facilities[0].name, h.facilities[1].name]);

            expect(viewPage.isFacilitiesTotalDisplayed()).toBe(false);
            expect(viewPage.capacitiesTable.isDisplayed()).toBe(false);
        });
    });

    describe('facility list', function () {
        var facilityNameOrder = common.facilityNameOrder;
        var facilityFactory = fixtures.facilityFactory;

        beforeEach(function () {
            var facilitiesFn  = _.partial(facilityFactory.facilitiesFromProto, fixtures.facilitiesFixture.dummies.facFull, facilityNameOrder);

            h = westend.copy();
            h.setFacilities(facilitiesFn());
            toView(h);
        });

        xit('is ordered', function () {
            expect(viewPage.facilitiesTable.getNames()).toEqual(facilityNameOrder);
        });

        it('facility name is link to facility view', function () {
            viewPage.facilitiesTable.clickName(0);
            expect(facilityViewPage.isDisplayed()).toBe(true);
        });

        xit('facility capacity types are listed in order', function () {
            arrayAssert.assertInOrder(viewPage.facilitiesTable.getCapacityTypes(0), common.capacityTypeOrder);
        });
    });
});
