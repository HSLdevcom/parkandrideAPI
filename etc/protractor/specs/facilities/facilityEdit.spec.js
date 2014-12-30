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

    it('should login as admin', function() {
        devApi.loginAs('ADMIN');
    });

    describe('new facility', function () {
        beforeEach(function () {
            editPage.get();
            devApi.resetAll({ contacts: [fixtures.facilitiesFixture.contact] });
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
            expect(editPage.getViolations()).toEqual([{ path: "Pysäköintipaikka", message: "tarkista pakolliset tiedot ja syötteiden muoto" }]);
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
                editPage.selectEmergencyContact("hsl");
                editPage.selectOperatorContact("hsl");
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
                expect(editPage.portEditModal.isEntrySelected()).toBe(true);
                expect(editPage.portEditModal.isExitSelected()).toBe(true);
                expect(editPage.portEditModal.isPedestrianSelected()).toBe(false);
                expect(editPage.portEditModal.isBicycleSelected()).toBe(false);

                editPage.portEditModal.toggleEntry();
                editPage.portEditModal.toggleExit();
                editPage.portEditModal.togglePedestrian();
                editPage.portEditModal.toggleBicycle();
                editPage.portEditModal.setStreetAddress(["katu", "gata", "street"]);
                editPage.portEditModal.setPostalCode("00100");
                editPage.portEditModal.setCity(["kaupunki", "stad", "city"]);
                editPage.portEditModal.setInfo("info");
                editPage.portEditModal.ok();
                editPage.portEditModal.waitUntilAbsent();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                // Edit port -> ok
                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.isDisplayed()).toBe(true);

                expect(editPage.portEditModal.isEntrySelected()).toBe(false);
                expect(editPage.portEditModal.isExitSelected()).toBe(false);
                expect(editPage.portEditModal.isPedestrianSelected()).toBe(true);
                expect(editPage.portEditModal.isBicycleSelected()).toBe(true);

                expect(editPage.portEditModal.getStreetAddress()).toEqual(["katu", "gata", "street"]);
                expect(editPage.portEditModal.getPostalCode()).toBe("00100");
                expect(editPage.portEditModal.getCity()).toEqual(["kaupunki", "stad", "city"]);
                expect(editPage.portEditModal.getInfo()).toEqual(["info", "info", "info"]);

                editPage.portEditModal.setCity("city");
                editPage.portEditModal.ok();
                editPage.portEditModal.waitUntilAbsent();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                // Edit port -> cancel
                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.getCity()).toEqual(["city", "city", "city"]);
                editPage.portEditModal.setCity("kaupunki");
                editPage.portEditModal.togglePedestrian();
                editPage.portEditModal.toggleBicycle();
                editPage.portEditModal.cancel();
                editPage.portEditModal.waitUntilAbsent();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                // Remove port
                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.isDisplayed()).toBe(true);
                expect(editPage.portEditModal.isPedestrianSelected()).toBe(true);
                expect(editPage.portEditModal.isBicycleSelected()).toBe(true);
                expect(editPage.portEditModal.getCityFi()).toBe("city");
                editPage.portEditModal.remove();
                editPage.portEditModal.waitUntilAbsent();
                expect(editPage.portEditModal.isDisplayed()).toBe(false);

                // New port with defaults
                editPage.openPortAt(200, 198);
                expect(editPage.portEditModal.isDisplayed()).toBe(true);
                expect(editPage.portEditModal.isPedestrianSelected()).toBe(false);
                expect(editPage.portEditModal.isBicycleSelected()).toBe(false);
                expect(editPage.portEditModal.getCityFi()).toBe("");
            });
        });

        it('add and remove services', function () {
            expect(editPage.isServiceSelected("Valaistus")).toBe(false);
            expect(editPage.isServiceSelected("Katettu")).toBe(false);
            editPage.selectService("Valaistus");
            editPage.selectService("Katettu");
            expect(editPage.isServiceSelected("Valaistus")).toBe(true);
            expect(editPage.isServiceSelected("Katettu")).toBe(true);
            editPage.removeService("Valaistus");
            editPage.removeService("Katettu");
            expect(editPage.isServiceSelected("Valaistus")).toBe(false);
            expect(editPage.isServiceSelected("Katettu")).toBe(false);
        });

        it('should manage contacts', function() {
            // Create emergency contact on the fly
            editPage.createContact({name: "new contact", phone: "(09) 4766 4444", email: "hsl@hsl.fi"});
            expect(editPage.getEmergencyContact()).toBe("new contact (09 47664444 / hsl@hsl.fi)");

            // Reload and expect that new contact is still available
            editPage.get();

            editPage.selectEmergencyContact("new contact");
            expect(editPage.getEmergencyContact()).toBe("new contact (09 47664444 / hsl@hsl.fi)");

            // Clear emergency contact
            editPage.clearEmergencyContact();
            expect(editPage.getEmergencyContact()).toBe("Valitse kontakti...");
        });

        it('create and edit full', function () {
            editPage.setName(facFull.name);
            editPage.drawLocation(facFull.locationInput.offset, facFull.locationInput.w, facFull.locationInput.h);

            editPage.openPortAt(200, 200);
            editPage.portEditModal.ok();
            expect(editPage.portEditModal.isDisplayed()).toBe(false);

            editPage.addAlias(facFull.aliases[0]);
            editPage.addAlias(facFull.aliases[1]);
            editPage.setCapacities(facFull.capacities);
            arrayAssert.assertInOrder(editPage.getCapacityTypes(), common.capacityTypeOrder);

            editPage.selectService("Valaistus");
            editPage.selectService("Katettu");

            editPage.selectEmergencyContact("hsl");
            editPage.selectOperatorContact("hsl");
            editPage.selectServiceContact("hsl");

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);

            // Back to edit...
            viewPage.toEditView();
            expect(editPage.isDisplayed()).toBe(true);
            expect(editPage.getEmergencyContact()).toBe("hsl fi (09 47664444)");
            expect(editPage.getOperatorContact()).toBe("hsl fi (09 47664444)");
            expect(editPage.getServiceContact()).toBe("hsl fi (09 47664444)");
            // TODO: other expectations & modifications

            editPage.clearServiceContact();
            expect(editPage.getServiceContact()).toBe("Valitse kontakti...");

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
        });

        it('create without aliases', function () {
            editPage.setName(facCar.name);
            editPage.drawLocation(facCar.locationInput.offset, facCar.locationInput.w, facCar.locationInput.h);
            editPage.setCapacities(facCar.capacities);
            arrayAssert.assertInOrder(editPage.getCapacityTypes(), common.capacityTypeOrder);

            editPage.selectEmergencyContact("hsl");
            editPage.selectOperatorContact("hsl");

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
        });

        it('provides navigation back to hub list', function () {
            editPage.toListView();
            expect(hubListPage.isDisplayed()).toBe(true);
        });
    });

    describe('payment info', function() {
        var f = fixtures.facilitiesFixture.dummies.facFull.copy();

        beforeEach(function() {
            devApi.resetAll({ facilities: [f], contacts: [fixtures.facilitiesFixture.contact] });
        });

        describe('create', function(){
            beforeEach(function() {
                editPage.get();
                editPage.setName(facFull.name);
                editPage.drawLocation(facFull.locationInput.offset, facFull.locationInput.w, facFull.locationInput.h);

                editPage.selectEmergencyContact("hsl");
                editPage.selectOperatorContact("hsl");
                editPage.selectServiceContact("hsl");
            });

            it('with auth', function() {
                editPage.setParkAndRideAuthRequired(true);

                editPage.save();
                expect(viewPage.isDisplayed()).toBe(true);

                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.isParkAndRideAuthRequired()).toBe(true);
                expect(viewPage.isPaymentMethodsDisplayed()).toBe(false);
                expect(viewPage.isPaymentInfoDetailsDisplayed()).toBe(false);

            });

            it('with methods', function() {
                editPage.selectPaymentMethod("Kolikko");
                editPage.selectPaymentMethod("Seteli");

                editPage.save();
                expect(viewPage.isDisplayed()).toBe(true);

                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.isParkAndRideAuthRequired()).toBe(false);
                expect(viewPage.getPaymentMethods()).toEqual("Kolikko, Seteli");
                expect(viewPage.isPaymentInfoDetailsDisplayed()).toBe(false);

            });

            it('with details', function() {
                editPage.setPaymentInfoDetailFi('fooFi');
                editPage.setPaymentInfoDetailSv('fooSv');
                editPage.setPaymentInfoDetailEn('fooEn');
                editPage.setPaymentInfoUrlFi('barFi');
                editPage.setPaymentInfoUrlSv('barSv');
                editPage.setPaymentInfoUrlEn('barEn');

                editPage.save();
                expect(viewPage.isDisplayed()).toBe(true);

                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.isParkAndRideAuthRequired()).toBe(false);
                expect(viewPage.isPaymentMethodsDisplayed()).toBe(false);
                expect(viewPage.getPaymentInfoDetail()).toEqual(["fooFi", "fooSv", "fooEn"]);
                expect(viewPage.getPaymentInfoUrl()).toEqual(["barFi", "barSv", "barEn"]);
            });
        });

        describe('edit', function(){
            beforeEach(function () {
                editPage.get(f.id);
            });

            it('initial state', function() {
                expect(editPage.isParkAndRideAuthRequired()).toBe(true);
                expect(editPage.isPaymentMethodSelected("Kolikko")).toBe(true);
                expect(editPage.isPaymentMethodSelected("Seteli")).toBe(true);
                expect(editPage.getPaymentInfoDetail()).toEqual([f.paymentInfo.detail.fi, f.paymentInfo.detail.sv, f.paymentInfo.detail.en]);
                expect(editPage.getPaymentInfoUrl()).toEqual([f.paymentInfo.url.fi, f.paymentInfo.url.sv, f.paymentInfo.url.en]);
            });

            it('modify and save', function() {
                editPage.setParkAndRideAuthRequired(false);
                editPage.removePaymentMethod("Kolikko");
                editPage.setPaymentInfoDetailSv("foo");
                editPage.setPaymentInfoUrlEn("bar");

                editPage.save();
                expect(viewPage.isDisplayed()).toBe(true);

                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.isParkAndRideAuthRequired()).toBe(false);
                expect(viewPage.getPaymentMethods()).toEqual("Seteli");
                expect(viewPage.getPaymentInfoDetail()).toEqual([f.paymentInfo.detail.fi, "foo", f.paymentInfo.detail.en]);
                expect(viewPage.getPaymentInfoUrl()).toEqual([f.paymentInfo.url.fi, f.paymentInfo.url.sv, "bar"]);
            });
        });
    });
});
