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

    var h;

    function assertFacilityNamesInAnyOrder(facilitiesTable, expected) {
        expect(facilitiesTable.isDisplayed()).toBe(true);
        arrayAssert.assertInAnyOrder(facilitiesTable.getFacilityNames(), expected);
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
        devApi.resetAll(h.facilities, [h]);
        viewPage.get(h.id);
        expect(viewPage.isDisplayed()).toBe(true);
        return h;
    }

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

        // TODO verify facility link
        // TODO verify facility types
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

    // TODO facility name order tests
});
