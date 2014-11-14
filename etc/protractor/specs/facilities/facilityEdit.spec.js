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

        describe('capacities', function () {
            function testCapacityMustBePositive(c) {
                expect(editPage.hasNoValidationErrors()).toBe(true);
                editPage.setCapacities(c, true);
                expect(editPage.hasNoValidationErrors()).toBe(false);
            }

            it('build value must be positive', function () {
                testCapacityMustBePositive({ "CAR": {"built": -1 }});
            });

            it('unavailable value must be positive', function () {
                testCapacityMustBePositive({ "CAR": {"unavailable": -1 }});
            });
        });

        describe('ports', function () {
            it('add port', function() {
                editPage.zoom(5);
                // New port
                editPage.openPortAt(200, 200);
                expect(editPage.portEditModal.isDisplayed()).toBe(true);
                expect(editPage.portEditModal.entryIsSelected()).toBe(true);
                expect(editPage.portEditModal.exitIsSelected()).toBe(true);
                expect(editPage.portEditModal.pedestrianIsSelected()).toBe(false);

                editPage.portEditModal.toggleEntry();
                editPage.portEditModal.toggleExit();
                editPage.portEditModal.togglePedestrian();
                editPage.portEditModal.setStreetAddress("katu");
                editPage.portEditModal.setPostalCode("00100");
                editPage.portEditModal.setCity("kaupunki");
                editPage.portEditModal.setInfo("info");
                editPage.portEditModal.ok();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                // Edit port -> ok
                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.isDisplayed()).toBe(true);

                expect(editPage.portEditModal.entryIsSelected()).toBe(false);
                expect(editPage.portEditModal.exitIsSelected()).toBe(false);
                expect(editPage.portEditModal.pedestrianIsSelected()).toBe(true);

                expect(editPage.portEditModal.getStreetAddress()).toEqual(["katu", "katu", "katu"]);
                expect(editPage.portEditModal.getPostalCode()).toBe("00100");
                expect(editPage.portEditModal.getCity()).toEqual(["kaupunki", "kaupunki", "kaupunki"]);
                expect(editPage.portEditModal.getInfo()).toEqual(["info", "info", "info"]);

                editPage.portEditModal.setCity("city");
                editPage.portEditModal.ok();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                // Edit port -> cancel
                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.getCity()).toEqual(["city", "city", "city"]);
                editPage.portEditModal.setCity("kaupunki");
                editPage.portEditModal.togglePedestrian();
                editPage.portEditModal.cancel();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                // Remove port
                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.isDisplayed()).toBe(true);
                expect(editPage.portEditModal.pedestrianIsSelected()).toBe(true);
                expect(editPage.portEditModal.getCityFi()).toBe("city");
                editPage.portEditModal.remove();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.isDisplayed()).toBe(true);
                expect(editPage.portEditModal.pedestrianIsSelected()).toBe(false);
                expect(editPage.portEditModal.getCityFi()).toBe("");
            });
        });

        it('create full', function () {
            editPage.setName(facFull.name);
            editPage.drawLocation(facFull.locationInput.offset, facFull.locationInput.w, facFull.locationInput.h);

            editPage.openPortAt(200, 200);
            editPage.portEditModal.ok();
            expect(editPage.portEditModal.isDisplayed()).toBe(false);

            editPage.addAlias(facFull.aliases[0]);
            editPage.addAlias(facFull.aliases[1]);
            editPage.setCapacities(facFull.capacities);
            arrayAssert.assertInOrder(editPage.getCapacityTypes(), common.capacityTypeOrder);

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
        });

        it('create without aliases', function () {
            editPage.setName(facCar.name);
            editPage.drawLocation(facCar.locationInput.offset, facCar.locationInput.w, facCar.locationInput.h);
            editPage.setCapacities(facCar.capacities);
            arrayAssert.assertInOrder(editPage.getCapacityTypes(), common.capacityTypeOrder);

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
        });

        it('provides navigation back to hub list', function () {
            editPage.toListView();
            expect(hubListPage.isDisplayed()).toBe(true);
        });
    });
});
