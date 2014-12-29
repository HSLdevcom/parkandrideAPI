'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var arrayAssert = require('../arrayAssert')();
var devApi = require('../devApi')();
var common = require('../common');

describe('edit hub view', function () {
    var hubEditPage = po.hubEditPage({});
    var hubViewPage = po.hubViewPage({});

    var facFull = fixtures.facilitiesFixture.dummies.facFull;
    var facCar = fixtures.facilitiesFixture.dummies.facCar;
    var contact = fixtures.facilitiesFixture.contact;
    var operator = fixtures.facilitiesFixture.operator;

    var facilityFactory = fixtures.facilityFactory;

    function assertFacilityNamesInAnyOrder(facilitiesTable, expected) {
        expect(facilitiesTable.isDisplayed()).toBe(true);
        arrayAssert.assertInAnyOrder(facilitiesTable.getNames(), expected);
    }

    describe('new hub', function () {
        beforeEach(function () {
            devApi.resetAll();
            devApi.loginAs('ADMIN');
            hubEditPage.get();
        });

        it('initially no errors exist', function () {
            expect(hubEditPage.hasNoValidationErrors()).toBe(true);
        });

        it('required error is shown only for edited fields', function () {
            hubEditPage.setNameFi("foo");
            hubEditPage.setNameFi("");
            hubEditPage.setNameEn("bar"); // to focus out from name fi, TODO refactor
            expect(hubEditPage.isNameFiRequiredError()).toBe(true);
            expect(hubEditPage.isNameSvRequiredError()).toBe(false);
            expect(hubEditPage.isNameEnRequiredError()).toBe(false);
            expect(hubEditPage.isLocationRequiredError()).toBe(false);
        });

        it('required errors are shown for all required fields if user submits empty form without editing', function() {
            hubEditPage.save();
            expect(hubEditPage.getViolations()).toEqual([{ path: "Alue", message: "tarkista pakolliset tiedot ja syötteiden muoto" }]);
            expect(hubEditPage.isNameFiRequiredError()).toBe(true);
            expect(hubEditPage.isNameSvRequiredError()).toBe(true);
            expect(hubEditPage.isNameEnRequiredError()).toBe(true);
            expect(hubEditPage.isLocationRequiredError()).toBe(true);
        });

        describe('name', function () {
            it('is required in all languages', function () {
                hubEditPage.setName("Hub name");
                hubEditPage.setNameFi("");
                expect(hubEditPage.isNameFiRequiredError()).toBe(true);
                expect(hubEditPage.isNameSvRequiredError()).toBe(false);
                expect(hubEditPage.isNameEnRequiredError()).toBe(false);

                hubEditPage.setName("Hub name");
                hubEditPage.setNameSv("");
                expect(hubEditPage.isNameFiRequiredError()).toBe(false);
                expect(hubEditPage.isNameSvRequiredError()).toBe(true);
                expect(hubEditPage.isNameEnRequiredError()).toBe(false);

                hubEditPage.setName("Hub name");
                hubEditPage.setNameEn("");
                expect(hubEditPage.isNameFiRequiredError()).toBe(false);
                expect(hubEditPage.isNameSvRequiredError()).toBe(false);
                expect(hubEditPage.isNameEnRequiredError()).toBe(true);
            });

            it('max length is 255', function () {
                var max = new Array(255+1).join("x");
                var tooLong = max + "y";
                hubEditPage.setName(tooLong);
                expect(hubEditPage.getNameFi()).toEqual(max);
                expect(hubEditPage.getNameSv()).toEqual(max);
                expect(hubEditPage.getNameEn()).toEqual(max);
            });
        });

        describe('location', function () {
            it('is required, error is cleared after location is selected', function () {
                hubEditPage.setName("Hub name");
                hubEditPage.save();
                expect(hubEditPage.isLocationRequiredError()).toBe(true);

                hubEditPage.setLocation({ x: 165, y: 165 });
                expect(hubEditPage.hasNoValidationErrors()).toBe(true);
            });
        });

        it('with address and without facilities', function() {
            hubEditPage.setName("Hub name");
            hubEditPage.setStreetAddress(["katu", "gata", "street"]);
            hubEditPage.setPostalCode("00100");
            hubEditPage.setCity(["kaupunki", "stad", "city"]);
            hubEditPage.setLocation({x: 165, y: 165});
            expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(false);

            hubEditPage.save();
            expect(hubViewPage.isDisplayed()).toBe(true);
            hubViewPage.toEditView();
            expect(hubEditPage.getName()).toEqual(["Hub name", "Hub name", "Hub name"]);
            expect(hubEditPage.getStreetAddress()).toEqual(["katu", "gata", "street"]);
            expect(hubEditPage.getPostalCode()).toBe("00100");
            expect(hubEditPage.getCity()).toEqual(["kaupunki", "stad", "city"]);
        });

        describe('with facilities', function() {
            beforeEach(function () {
                devApi.resetAll({facilities: [facFull, facCar], contacts: [contact], operators: [operator]});
                devApi.loginAs('ADMIN');
                hubEditPage.get();
            });

            it('create', function () {
                hubEditPage.setName("Hub name");
                expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(false);

                hubEditPage.toggleFacility(facFull);
                hubEditPage.toggleFacility(facCar);
                hubEditPage.setLocation({x: 165, y: 165});
                assertFacilityNamesInAnyOrder(hubEditPage.facilitiesTable, [facFull.name, facCar.name]);

                hubEditPage.save();
                expect(hubViewPage.isDisplayed()).toBe(true);
            });
        });
    });

    if (!common.isOsx) {
        describe('hub with facilities', function () {
            var hub;
            var facilityNameOrder = common.facilityNameOrder;

            beforeEach(function () {
                hub = fixtures.hubsFixture.westend.copy();
                var fproto = facFull;
                var xdelta = fproto.locationInput.w + 5;
                var n = 0;
                var facilityCreator = function () {
                    return fproto.copyHorizontallyInDefaultZoom(n++ * xdelta);
                };
                var facilities = facilityFactory.facilitiesFromCreator(facilityCreator, facilityNameOrder);

                var f1LeftTop = [280, 155];
                _.forEach(facilities, function (f, idx) {
                    f.locationInput.offset = { x: f1LeftTop[0] + idx * xdelta, y: f1LeftTop[1] }
                });

                hub.location.coordinates = facilities[0].coordinatesFromTopLeft({ x: 30, y: 30 });
                hub.setFacilities(facilities);
                devApi.resetAll({facilities: hub.facilities, hubs: [hub], contacts: [contact], operators: [operator]});
                devApi.loginAs('ADMIN');
            });

            it('facility can be removed from hub', function () {
                hubEditPage.get(hub.id);
                expect(hubEditPage.facilitiesTable.getSize()).toEqual(9);

                hubEditPage.toggleFacility(hub.facilities[0]);
                expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(true);
                expect(hubEditPage.facilitiesTable.getSize()).toEqual(8);

                hubEditPage.save();
                expect(hubViewPage.isDisplayed()).toBe(true);
                expect(hubViewPage.facilitiesTable.getSize()).toBe(8);
            });

            it('when removing facilities, facility table is updated and order is maintained', function () {
                hubEditPage.get(hub.id);
                expect(hubEditPage.facilitiesTable.getSize()).toEqual(9);

                _.forEach(hub.facilities, function (f, idx) {
                    hubEditPage.toggleFacility(f);
                    if (idx < hub.facilities.length - 1) {
                        expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(true);
                        arrayAssert.assertInOrder(hubEditPage.facilitiesTable.getNames(), facilityNameOrder, { allowSkip: true });
                    }
                });
            });
        });
    }
});
