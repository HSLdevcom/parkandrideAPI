'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('edit facility view', function () {
    var editPage = po.facilityEditPage({});
    var viewPage = po.facilityViewPage({});
    var hubListPage = po.hubListPage({});

    var facFull = fixtures.facilitiesFixture.dummies.facFull;
    var facCar = fixtures.facilitiesFixture.dummies.facCar;

    describe('new facility', function () {
        beforeEach(function () {
            devApi.resetAll();
            editPage.get();
        });

        it('initially no errors exist', function () {
            expect(editPage.hasNoValidationErrors()).toBe(true);
        });

        it('required error is shown only for edited fields', function () {
            editPage.setNameFi("foo");
            editPage.setNameFi("");
            editPage.setNameEn("bar");
            expect(editPage.isNameFiRequiredError()).toBe(true);
            expect(editPage.isNameSvRequiredError()).toBe(false);
            expect(editPage.isNameEnRequiredError()).toBe(false);
            expect(editPage.isFacilityRequiredError()).toBe(false);
        });

        it('required errors are shown for all required fields if user submits empty form without editing', function() {
            editPage.save();
            expect(editPage.isNameFiRequiredError()).toBe(true);
            expect(editPage.isNameSvRequiredError()).toBe(true);
            expect(editPage.isNameEnRequiredError()).toBe(true);
            expect(editPage.isFacilityRequiredError()).toBe(true);
        });

        describe('name', function () {
            it('is required in all languages', function () {
                editPage.setName("facility name");
                editPage.setNameFi("");
                expect(editPage.isNameFiRequiredError()).toBe(true);
                expect(editPage.isNameSvRequiredError()).toBe(false);
                expect(editPage.isNameEnRequiredError()).toBe(false);

                editPage.setName("facility name");
                editPage.setNameSv("");
                expect(editPage.isNameFiRequiredError()).toBe(false);
                expect(editPage.isNameSvRequiredError()).toBe(true);
                expect(editPage.isNameEnRequiredError()).toBe(false);

                editPage.setName("facility name");
                editPage.setNameEn("");
                expect(editPage.isNameFiRequiredError()).toBe(false);
                expect(editPage.isNameSvRequiredError()).toBe(false);
                expect(editPage.isNameEnRequiredError()).toBe(true);
            });

            it('max length is 255', function () {
                var max = new Array(255+1).join("x");
                var tooLong = max + "y";
                editPage.setName(tooLong);
                expect(editPage.getNameFi()).toEqual(max);
                expect(editPage.getNameSv()).toEqual(max);
                expect(editPage.getNameEn()).toEqual(max);
            });
        });

        describe('facility', function () {
            it('is required, error is cleared after facility is set', function () {
                editPage.setName("Facility name");
                editPage.save();
                expect(editPage.isFacilityRequiredError()).toBe(true);

                editPage.drawLocation({ offset: {x: 180, y: 180}, w: 60, h: 60 });
                expect(editPage.hasNoValidationErrors()).toBe(true);
            });
        });

        it('create full', function () {
            editPage.setName(facFull.name);
            editPage.drawLocation(facFull.locationInput.offset, facFull.locationInput.w, facFull.locationInput.h);
            editPage.addAlias(facFull.aliases[0]);
            editPage.addAlias(facFull.aliases[1]);
            editPage.setCapacities(facFull.capacities);
            arrayAssert.assertInOrder(editPage.getCapacityTypes(), common.capacityTypeOrder);

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
            expect(viewPage.getName()).toBe(facFull.name);
            expect(viewPage.getAliases()).toEqual(facFull.aliases);
            arrayAssert.assertInOrder(viewPage.capacitiesTable.getTypes(), common.capacityTypeOrder);
            expect(viewPage.capacitiesTable.getCapacities(_.keys(facFull.capacities))).toEqual(facFull.capacities);
        });

        it('create without aliases', function () {
            editPage.setName(facCar.name);
            editPage.drawLocation(facCar.locationInput.offset, facCar.locationInput.w, facCar.locationInput.h);
            editPage.setCapacities(facCar.capacities);
            arrayAssert.assertInOrder(editPage.getCapacityTypes(), common.capacityTypeOrder);

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
            expect(viewPage.getName()).toBe(facCar.name);
            expect(viewPage.getAliases()).toEqual([ '' ]);
            arrayAssert.assertInOrder(viewPage.capacitiesTable.getTypes(), common.capacityTypeOrder, { allowSkip: true });
            expect(viewPage.capacitiesTable.getCapacities(_.keys(facCar.capacities))).toEqual(facCar.capacities);
        });

        it('provides navigation back to hub list', function () {
            editPage.toListView();
            expect(hubListPage.isDisplayed()).toBe(true);
        });
    });
});
