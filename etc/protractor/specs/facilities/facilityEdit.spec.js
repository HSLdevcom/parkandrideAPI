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
            devApi.resetAll({ contacts: [fixtures.facilitiesFixture.contact], operators: [fixtures.facilitiesFixture.operator] });
            devApi.loginAs('ADMIN');
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
            expect(editPage.getViolations()).toEqual([{ path: "Pysäköintipaikka", message: "tarkista pakolliset tiedot ja syötteiden muoto" }]);
            expect(editPage.isNameFiRequiredError()).toBe(true);
            expect(editPage.isNameSvRequiredError()).toBe(true);
            expect(editPage.isNameEnRequiredError()).toBe(true);
            expect(editPage.isFacilityRequiredError()).toBe(true);
        });

        it('should create and select an operator', function() {
            editPage.createOperator("smooth operator");
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
                editPage.selectOperator("smooth");
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
                testCapacityMustBePositive({ "CAR": -1 });
            });
        });

        it('should have IN_OPERATION as default status', function() {
            expect(editPage.getStatus()).toBe("Toiminnassa")
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

            // Reload and expect that new contact is still available after operator is selected
            editPage.get();
            editPage.selectOperator("smooth operator");
            editPage.selectEmergencyContact("new contact");
            expect(editPage.getEmergencyContact()).toBe("new contact (09 47664444 / hsl@hsl.fi)");
        });

        it('create and edit full', function () {
            editPage.setName(facFull.name);
            editPage.selectOperator("smooth operator");
            editPage.selectStatus('Poikkeus');
            editPage.setStatusDescription("status description");
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
            expect(editPage.getStatus()).toBe("Poikkeustilanne");
            expect(editPage.getStatusDescription()).toEqual(["status description", "status description", "status description"]);
            expect(editPage.getEmergencyContact()).toBe("hsl fi (09 47664444)");
            expect(editPage.getOperatorContact()).toBe("hsl fi (09 47664444)");
            expect(editPage.getServiceContact()).toBe("hsl fi (09 47664444)");
            // TODO: other expectations & modifications

            editPage.clearServiceContact();
            expect(editPage.getServiceContact()).toBe("Valitse yhteystieto...");

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
        });

        it('create without aliases', function () {
            editPage.setName(facCar.name);
            editPage.selectOperator("smooth");
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

    describe('pricing flow', function() {

        it('should create 24/7 free pricing', function() {
            devApi.resetAll({ facilities: [], contacts: [fixtures.facilitiesFixture.contact], operators: [fixtures.facilitiesFixture.operator] });
            devApi.loginAs('ADMIN');

            editPage.get();
            editPage.setName("test");
            editPage.selectOperator("smooth");
            editPage.drawLocation(facFull.locationInput.offset, facFull.locationInput.w, facFull.locationInput.h);

            editPage.selectEmergencyContact("hsl");
            editPage.selectOperatorContact("hsl");
            editPage.selectServiceContact("hsl");
            editPage.setCapacities({ CAR: 10 }, true);
            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
            viewPage.toEditView();
            editPage.setPricingMethod("CUSTOM");
            editPage.getPricing().then(function(actualRows) {
                expect(actualRows.length).toEqual(3);
                for (var i=0; i < actualRows.length; i++) {
                    expect(actualRows[i].capacityType).toEqual("Henkilöauto");
                    expect(actualRows[i].usage).toEqual("Liityntä");
                    expect(actualRows[i].maxCapacity).toEqual("10");
                    expect(actualRows[i].is24h).toBeTruthy();
                    expect(actualRows[i].isFree).toBeTruthy();
                }
                expect(actualRows[0].dayType).toEqual("Arkipäivä");
                expect(actualRows[1].dayType).toEqual("Lauantai");
                expect(actualRows[2].dayType).toEqual("Sunnuntai");
            });
        });

        it('should modify custom pricing', function () {
            var changes = {from:"8", until:"18", priceFi:"price fi", priceSv:"price sv", priceEn:"price en"};

            editPage.setPricing(0, changes);
            editPage.setPricing(1, changes);
            editPage.setPricing(2, changes);

            editPage.save();
            expect(viewPage.isDisplayed()).toBe(true);
            viewPage.toEditView();
            editPage.getPricing().then(function(actualRows) {
                expect(actualRows.length).toEqual(3);
                for (var i=0; i < actualRows.length; i++) {
                    expect(actualRows[i].capacityType).toEqual("Henkilöauto");
                    expect(actualRows[i].usage).toEqual("Liityntä");
                    expect(actualRows[i].maxCapacity).toEqual("10");
                    expect(actualRows[i].is24h).toBeFalsy();
                    expect(actualRows[i].from).toEqual("08");
                    expect(actualRows[i].until).toEqual("18");
                    expect(actualRows[i].isFree).toBeFalsy();
                    expect(actualRows[i].priceFi).toEqual("price fi");
                    expect(actualRows[i].priceSv).toEqual("price sv");
                    expect(actualRows[i].priceEn).toEqual("price en");
                }
            });
        });

        it('should select all', function() {
            editPage.pricingSelectAll();
            editPage.getPricing().then(function(actualRows) {
                expect(actualRows.length).toEqual(3);
                for (var i = 0; i < actualRows.length; i++) {
                    expect(actualRows[i].selected).toBeTruthy();
                }
            });
        });

        it('should unselect all', function() {
            editPage.pricingSelectAll();
            editPage.getPricing().then(function(actualRows) {
                expect(actualRows.length).toEqual(3);
                for (var i = 0; i < actualRows.length; i++) {
                    expect(actualRows[i].selected).toBeFalsy();
                }
            });
        });

        it('should remove last row', function() {
            editPage.togglePricingRow(2);
            editPage.pricingRemoveRows();
            expect(editPage.getPricingCount()).toBe(2);
        });

        it('should remove all rows', function() {
            editPage.pricingSelectAll();
            editPage.pricingRemoveRows();
            expect(editPage.getPricingCount()).toBe(0);
        });

        it('should add a new row', function() {
            editPage.pricingAddRow();
            editPage.setPricing(0,
                {capacityType: "Henkilöauto", usage: "Liityntä", maxCapacity: "10",
                    dayType: "Aatto", from: "8", until: "18",
                    priceFi: "price fi", priceSv: "price sv", priceEn: "price en"});

            editPage.getPricing().then(function(actualRows) {
                var row = actualRows[0];
                expect(row.is24h).toBeFalsy();
                expect(row.from).toBe("8");
                expect(row.until).toBe("18");
                expect(row.isFree).toBeFalsy();
                expect(row.priceFi).toBe("price fi");
                expect(row.priceSv).toBe("price sv");
                expect(row.priceEn).toBe("price en");
            });
        });

        it('should clear price', function() {
            editPage.setPricing(0, { isFree:true });

            editPage.getPricing().then(function(actualRows) {
                var row = actualRows[0];
                expect(row.isFree).toBeTruthy();
                expect(row.priceFi).toBe("");
                expect(row.priceSv).toBe("");
                expect(row.priceEn).toBe("");
            });
        });

        it('should set 24h', function() {
            editPage.setPricing(0, { is24h:true });

            editPage.getPricing().then(function(actualRows) {
                var row = actualRows[0];
                expect(row.is24h).toBeTruthy();
                expect(row.from).toBe("00");
                expect(row.until).toBe("24");
            });
        });
    });

    describe('unavailable capacity', function() {
        var facility;

        beforeEach(function () {
            facility = facFull.copy();
        });

        it('should show no unavailable capacity rows', function() {
            facility.pricing = [];
            facility.unavailableCapacities = [];
            devApi.resetAll({ facilities: [facility], contacts: [fixtures.facilitiesFixture.contact], operators: [fixtures.facilitiesFixture.operator] });
            devApi.loginAs('ADMIN');

            editPage.get(facility.id);
            browser.debugger();
            expect(editPage.getUnavailableCapacitiesCount()).toBe(0);
        });

        it('should show no unavailable capacity rows', function() {
            facility.pricing =
                [{"capacityType":"CAR","usage":"PARK_AND_RIDE","maxCapacity":10,"dayType":"BUSINESS_DAY","time":{"from":"00","until":"24"},"price":null},
                    {"capacityType":"CAR","usage":"COMMERCIAL","maxCapacity":10,"dayType":"BUSINESS_DAY","time":{"from":"00","until":"24"},"price":null},
                    {"capacityType":"DISABLED","usage":"PARK_AND_RIDE","maxCapacity":1,"dayType":"BUSINESS_DAY","time":{"from":"00","until":"24"},"price":null}];
            facility.unavailableCapacities =
                [{"capacityType":"CAR","usage":"PARK_AND_RIDE","capacity":3},
                    {"capacityType":"CAR","usage":"COMMERCIAL","capacity":2},
                    {"capacityType":"DISABLED","usage":"PARK_AND_RIDE","capacity":1}];

            devApi.resetAll({ facilities: [facility], contacts: [fixtures.facilitiesFixture.contact], operators: [fixtures.facilitiesFixture.operator] });
            devApi.loginAs('ADMIN');

            editPage.get(facility.id);
            browser.debugger();
            editPage.getUnavailableCapacities().then(function(ucs) {
                expect(ucs.length).toBe(3);
                expect(ucs[0].capacityType).toBe("Henkilöauto");
                expect(ucs[0].usage).toBe("Liityntä");
                expect(ucs[0].capacity).toBe("3");

                expect(ucs[1].capacityType).toBe("");
                expect(ucs[1].usage).toBe("Kaupallinen");
                expect(ucs[1].capacity).toBe("2");

                expect(ucs[2].capacityType).toBe("Invapaikka");
                expect(ucs[2].usage).toBe("Liityntä");
                expect(ucs[2].capacity).toBe("1");
            });
        });
    });

    describe('payment info', function() {
        describe('create', function(){
            beforeEach(function() {
                devApi.resetAll({ facilities: [], contacts: [fixtures.facilitiesFixture.contact], operators: [fixtures.facilitiesFixture.operator] });
                devApi.loginAs('ADMIN');

                editPage.get();
                editPage.setName(facFull.name);
                editPage.selectOperator("smooth");
                editPage.drawLocation(facFull.locationInput.offset, facFull.locationInput.w, facFull.locationInput.h);

                editPage.selectEmergencyContact("hsl");
                editPage.selectOperatorContact("hsl");
                editPage.selectServiceContact("hsl");
            });

            it('with methods', function() {
                editPage.selectPaymentMethod("Kolikko");
                editPage.selectPaymentMethod("Seteli");

                editPage.save();
                expect(viewPage.isDisplayed()).toBe(true);

                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.getPaymentMethods()).toEqual("Kolikko, Seteli");
                expect(viewPage.isPaymentInfoDetailsDisplayed()).toBe(false);

            });

            it('with details', function() {
                editPage.setPaymentInfoDetailFi('fooFi');
                editPage.setPaymentInfoDetailSv('fooSv');
                editPage.setPaymentInfoDetailEn('fooEn');
                editPage.setPaymentInfoUrlFi('http://www.hsl.fi');
                editPage.setPaymentInfoUrlSv('http://www.hsl.fi');
                editPage.setPaymentInfoUrlEn('http://www.hsl.fi');

                editPage.save();
                expect(viewPage.isDisplayed()).toBe(true);

                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.isPaymentMethodsDisplayed()).toBe(false);
                expect(viewPage.getPaymentInfoDetail()).toEqual(["fooFi", "fooSv", "fooEn"]);
                expect(viewPage.getPaymentInfoUrl()).toEqual(["http://www.hsl.fi", "http://www.hsl.fi", "http://www.hsl.fi"]);
            });
        });

        describe('edit', function(){
            var f = fixtures.facilitiesFixture.dummies.facFull.copy();
            beforeEach(function () {
                devApi.resetAll({ facilities: [f], contacts: [fixtures.facilitiesFixture.contact], operators: [fixtures.facilitiesFixture.operator] });
                devApi.loginAs('ADMIN');
                editPage.get(f.id);
            });

            it('initial state', function() {
                expect(editPage.isPaymentMethodSelected("Kolikko")).toBe(true);
                expect(editPage.isPaymentMethodSelected("Seteli")).toBe(true);
                expect(editPage.getPaymentInfoDetail()).toEqual([f.paymentInfo.detail.fi, f.paymentInfo.detail.sv, f.paymentInfo.detail.en]);
                expect(editPage.getPaymentInfoUrl()).toEqual([f.paymentInfo.url.fi, f.paymentInfo.url.sv, f.paymentInfo.url.en]);
            });

            it('modify and save', function() {
                editPage.removePaymentMethod("Kolikko");
                editPage.setPaymentInfoDetailSv("foo");
                editPage.setPaymentInfoUrlEn("http://www.hsl.fi");

                editPage.save();
                expect(viewPage.isDisplayed()).toBe(true);

                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.getPaymentMethods()).toEqual("Seteli");
                expect(viewPage.getPaymentInfoDetail()).toEqual([f.paymentInfo.detail.fi, "foo", f.paymentInfo.detail.en]);
                expect(viewPage.getPaymentInfoUrl()).toEqual([f.paymentInfo.url.fi, f.paymentInfo.url.sv, "http://www.hsl.fi"]);
            });
        });
    });
});
