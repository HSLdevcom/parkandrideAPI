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
        devApi.resetAll({ facilities: [f], contacts: [fixtures.facilitiesFixture.contact]});
        viewPage.get(f.id);
        expect(viewPage.isDisplayed()).toBe(true);
        return f;
    }

    describe('navigation', function () {
        beforeEach(function () {
            f = toView(facFull);
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
            expect(viewPage.getServices()).toEqual("Katettu, Valaistus");

            expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
            expect(viewPage.getPaymentMethods()).toEqual("Kolikko, Seteli");
        });
    });

    it('view port', function() {
        f = {
            "id":1,"name":{"fi":"test","sv":"test","en":"tes"},
            "aliases":[],"capacities":{},"serviceIds":[],
            "contacts":{
                "emergency": 1,
                "operator": 1
            },
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
            f.serviceIds = [];
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

        describe('with auth required', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                f.paymentInfo.parkAndRideAuthRequired = true;
                toView(f);
            });

            it('payment info is displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
            });
        });

        describe('with payment methods', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                f.paymentInfo.paymentMethodIds = [ 1 ];
                toView(f);
            });

            it('payment info is displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
            });
        });

        describe('with details', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                f.paymentInfo.info = { fi: "payment info", sv: "payment info", en: "payment info" };
                toView(f);
            });

            it('payment info is displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
            });
        });

        describe('with url', function () {
            beforeEach(function () {
                f = facFull.copy();
                f.paymentInfo = {};
                f.paymentInfo.url = { fi: "url", sv: "url", en: "url" };
                toView(f);
            });

            it('payment info is displayed', function () {
                expect(viewPage.isPaymentInfoDisplayed()).toBe(true);
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
            f.capacities = {};
            toView(f);
        });

        it('capacities are not displayed', function () {
            expect(viewPage.capacitiesTable.isDisplayed()).toBe(false);
        });
    });
});
