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
    var portView = viewPage.portView;
    var editPage = po.facilityEditPage({});
    var listPage = po.hubListPage({});

    var f;

    function toView(f) {
        devApi.resetAll({ facilities: [f], contacts: [fixtures.facilitiesFixture.contact], operators: [fixtures.facilitiesFixture.operator]});
        viewPage.get(f.id);
        expect(viewPage.isDisplayed()).toBe(true);
        return f;
    }

    describe('navigation', function () {
        beforeEach(function () {
            f = toView(facFull);
        });

        it('to hub list', function () {
            viewPage.toListView();
            expect(listPage.isDisplayed()).toBe(true);
        });

        it('to edit view', function () {
            devApi.loginAs('ADMIN');
            viewPage.get(f.id);
            viewPage.toEditView();
            expect(editPage.isDisplayed()).toBe(true);
        });
    });

    describe('with full data', function () {
        beforeEach(function () {
            f = toView(facFull);
        });

        it('displays full data', function () {
            expect(viewPage.getName()).toBe(f.name);

            expect(viewPage.isAliasesDisplayed()).toBe(true);
            expect(viewPage.getAliases()).toEqual(f.aliases);

            arrayAssert.assertInOrder(viewPage.capacitiesTable.getTypes(), common.capacityTypeOrder);
            expect(viewPage.capacitiesTable.getCapacities(_.keys(f.capacities))).toEqual(f.capacities);

            expect(viewPage.isServicesDisplayed()).toBe(true);
            expect(viewPage.getServices()).toEqual("Valaistus, Katettu");

            expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
            expect(viewPage.getPaymentMethods()).toEqual("Kolikko, Seteli");

            expect(viewPage.getOpeningHours()).toEqual({
                "Arkipäivä": "00 - 24",
                "Lauantai": "08 - 18",
                "Sunnuntai": "Kiinni"
            });

            expect(viewPage.getPricing()).toEqual([
                {capacityType: "Henkilöauto", usage: "Liityntä", maxCapacity: "10",
                    dayType: "Arkipäivä", is24h: "✓", from: "", until: "",
                    isFree: "✓", priceFi: "", priceSv: "", priceEn: ""},
                {capacityType: "Invapaikka", usage: "Kaupallinen", maxCapacity: "40",
                    dayType: "Lauantai", is24h: "", from: "08", until: "18",
                    isFree: "", priceFi: "price fi", priceSv: "price sv", priceEn: "price en"},
                {"capacityType":"Sähköauto","usage":"Kaupallinen","maxCapacity":"60",
                    "dayType":"Lauantai", is24h: "", from:"08","until":"18",
                    isFree: "", "priceFi":"price fi","priceSv":"price sv","priceEn":"price en"},
                {"capacityType":"Moottoripyörä","usage":"Liityntä","maxCapacity":"50",
                    dayType: "Arkipäivä", is24h: "✓", from: "", until: "",
                    isFree: "✓", priceFi: "", priceSv: "", priceEn: ""},
                {"capacityType":"Polkupyörä","usage":"Liityntä","maxCapacity":"20",
                    "dayType":"Arkipäivä", is24h: "✓", "from":"","until":"",
                    isFree: "✓", priceFi: "", priceSv: "", priceEn: ""},
                {"capacityType":"Polkupyörä, lukittu tila","usage":"Kaupallinen","maxCapacity":"30",
                    "dayType":"Lauantai", is24h: "", from:"08","until":"18",
                    isFree: "", "priceFi":"price fi","priceSv":"price sv","priceEn":"price en"}
            ]);

            expect(viewPage.getUnavailableCapacities()).toEqual([
                {capacityType: "Henkilöauto", usage: "Liityntä", capacity: "1"},
                {capacityType: "Invapaikka", usage: "Kaupallinen", capacity: "0"},
                {capacityType: "Sähköauto", usage: "Kaupallinen", capacity: "0"},
                {capacityType: "Moottoripyörä", usage: "Liityntä", capacity: "0"},
                {capacityType: "Polkupyörä", usage: "Liityntä", capacity: "0"},
                {capacityType: "Polkupyörä, lukittu tila", usage: "Kaupallinen", capacity: "0"},
            ]);
        });
    });

    it('view port', function() {
        f = {
            "id":1,"name":{"fi":"test","sv":"test","en":"tes"},
            "aliases":[],"capacities":{},"services":[],
            "operatorId": 1,
            "status": "IN_OPERATION",
            "contacts":{
                "emergency": 1,
                "operator": 1
            },
            "pricingMethod": "CUSTOM",
            "location":{"type":"Polygon","coordinates":[[[24.943295535227904,60.17184809821847],[24.944218215129023,60.17186410779244],[24.94464736857141,60.17192280949709],[24.94479757227624,60.17077543898606],[24.944293316981444,60.17060466413514],[24.9436925021621,60.170572643751754],[24.9434028235885,60.17073274535653],[24.943295535227904,60.17184809821847]]]},
            "ports":[{
                "location":{"type":"Point","coordinates":[24.944605,60.17197]},
                "entry":true,"exit":true,"pedestrian":true,"bicycle":true,
                "address":{"streetAddress":{"fi":"Vilhonkatu 9","sv":"Vilhelmsgatan 9","en":"Vilhonkatu 9"},
                    "postalCode":"00100","city":{"fi":"Helsinki","sv":"Helsingfors","en":"Helsinki"}},
                "info":{"fi":"Info fi","sv":"Info sv","en":"Info en"}}]
            };
        f.toPayload = function() { return f; };
        f = toView(f);
        viewPage.openPortAt(592, 109);
        expect(portView.isDisplayed()).toBe(true);
        expect(portView.isEntrySelected()).toBe(true);
        expect(portView.isExitSelected()).toBe(true);
        expect(portView.isPedestrianSelected()).toBe(true);
        expect(portView.isBicycleSelected()).toBe(true);
        expect(portView.getStreetAddress()).toEqual(["Vilhonkatu 9", "Vilhelmsgatan 9", "Vilhonkatu 9"]);
        expect(portView.getPostalCode()).toBe("00100");
        expect(portView.getCity()).toEqual(["Helsinki", "Helsingfors", "Helsinki"]);
        expect(portView.getInfo()).toEqual(["Info fi", "Info sv", "Info en"]);
        portView.ok();
        expect(portView.isDisplayed()).toBe(false);
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

    describe('without services', function () {
        beforeEach(function () {
            f = facFull.copy();
            f.services = [];
            toView(f);
        });

        it('services are not displayed', function () {
            expect(viewPage.isServicesDisplayed()).toBe(false);
        });
    });

    describe('payment info', function () {
        describe('without any info', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                toView(f);
            });

            it('payment info is not displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(false);
            });
        });

        describe('with payment methods', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                f.paymentInfo.paymentMethods = facFull.copy().paymentInfo.paymentMethods;
                toView(f);
            });

            it('payment info is displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.getPaymentMethods()).toEqual("Kolikko, Seteli");
                expect(viewPage.isPaymentInfoDetailsDisplayed()).toBe(false);
            });
        });

        describe('with details', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                f.paymentInfo.detail = facFull.copy().paymentInfo.detail;
                toView(f);
            });

            it('payment info is displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.isPaymentMethodsDisplayed()).toBe(false);
                expect(viewPage.isPaymentInfoDetailsDisplayed()).toBe(true);
                expect(viewPage.getPaymentInfoDetail()).toEqual([f.paymentInfo.detail.fi, f.paymentInfo.detail.sv, f.paymentInfo.detail.en]);
            });
        });

        describe('with url', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                f.paymentInfo.url = facFull.copy().paymentInfo.url;
                toView(f);
            });

            it('payment info is displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
                expect(viewPage.isPaymentMethodsDisplayed()).toBe(false);
                expect(viewPage.isPaymentInfoDetailsDisplayed()).toBe(true);
                expect(viewPage.getPaymentInfoUrl()).toEqual([f.paymentInfo.url.fi, f.paymentInfo.url.sv, f.paymentInfo.url.en]);
            });
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
            f.builtCapacity = {};
            toView(f);
        });

        it('capacities are not displayed', function () {
            expect(viewPage.capacitiesTable.isDisplayed()).toBe(false);
        });
    });
});
