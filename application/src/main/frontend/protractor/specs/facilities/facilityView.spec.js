"use strict";

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('facility view', function () {
    var facFull = fixtures.facilitiesFixture.dummies.facFull;
    var facCar = fixtures.facilitiesFixture.dummies.facCar;

    var viewPage = po.facilityViewPage({});

    var f;

    function toView(f) {
        devApi.resetFacilities([f]);
        viewPage.get(f.id);
        expect(viewPage.isDisplayed()).toBe(true);
        return f;
    }

    describe('with aliases and capacities', function () {
        beforeEach(function () {
            f = toView(facFull);
        });

        it('displays full data', function () {
            expect(viewPage.getName()).toBe(f.name);

            expect(viewPage.isAliasesDisplayed()).toBe(true);
            expect(viewPage.getAliases()).toEqual(f.aliases);

            arrayAssert.assertInOrder(viewPage.capacitiesTable.getTypes(), common.capacityTypeOrder);
            expect(viewPage.capacitiesTable.getCapacities(_.keys(f.capacities))).toEqual(f.capacities);
        });
    });

    describe('without aliases', function () {
        beforeEach(function () {
            f = facFull.copy();
            f.aliases = [];
            toView(f);
        });

        it('aliases are not displayed', function () {
            expect(viewPage.isAliasesDisplayed()).toBe(false);
        });
    });

    describe('with one capacity', function () {
        beforeEach(function () {
            f = toView(facCar);
        });

        it('single capacity is displayed', function () {
            expect(viewPage.capacitiesTable.isDisplayed()).toBe(true);
            expect(viewPage.capacitiesTable.getSize()).toBe(1);
            expect(viewPage.capacitiesTable.getCapacities(_.keys(f.capacities))).toEqual(f.capacities);
        });
    });

    describe('without capacities', function () {
        beforeEach(function () {
            f = facFull.copy();
            f.capacities = {};
            toView(f);
        });

        it('capacities are not displayed', function () {
            expect(viewPage.capacitiesTable.isDisplayed()).toBe(false);
        });
    });
});
