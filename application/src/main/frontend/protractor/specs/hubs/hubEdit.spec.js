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

    function assertFacilityNamesInAnyOrder(facilitiesTable, expected) {
        expect(facilitiesTable.isDisplayed()).toBe(true);
        arrayAssert.assertInAnyOrder(facilitiesTable.getFacilityNames(), expected);
    }

    function totalCapacities(facilities) {
        return _.reduce(facilities, function (acc, facility) { return acc.incCapacity(facility); });
    }

    function assertCapacities(capacitiesTable, facilities) {
        arrayAssert.assertInOrder(capacitiesTable.getTypes(), common.capacityTypeOrder);
        var total = totalCapacities(facilities);
        expect(capacitiesTable.getCapacities(_.keys(total.capacities))).toEqual(total.capacities);
    }

    xdescribe('new hub', function () {
        beforeEach(function () {
            devApi.resetAll();
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

        it('without facilities', function() {
            hubEditPage.setName("Hub name");
            hubEditPage.setLocation({x: 165, y: 165});
            expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(false);

            hubEditPage.save();
            expect(hubViewPage.isDisplayed()).toBe(true);
            expect(hubViewPage.getName()).toBe("Hub name");
            expect(hubViewPage.getNoFacilitiesMessage()).toEqual("Alueeseen ei ole lisätty pysäköintipaikkoja");
            expect(hubViewPage.facilitiesTable.isDisplayed()).toBe(false);
            expect(hubViewPage.capacitiesTable.isDisplayed()).toBe(false);
        });

        describe('with facilities', function() {
            beforeEach(function () {
                devApi.resetFacilities([facFull, facCar]);
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
                expect(hubViewPage.getName()).toBe("Hub name");
                assertCapacities(hubViewPage.capacitiesTable, [facFull, facCar]);
                assertFacilityNamesInAnyOrder(hubViewPage.facilitiesTable, [facFull.name, facCar.name]);
            });
        });
    });

    describe('hub with facilities', function () {
        var hub = fixtures.hubsFixture.westend;

        beforeEach(function () {
            var idGen = 100;
            var n = 0;
            function buildFacility(proto) {
                return function(name) {
                    var o  = proto.copyHorizontallyInDefaultZoom(n++ * 65);
                    o.name = name;
                    o.id = idGen++;
                    return o;
                }
            }
            var fproto = fixtures.facilitiesFixture.dummies.facFull;
            var fnames = [ "foo", "Bar", "b@z", "_foo_", "fåå", "bär", "föö", "fow", "fov"];
            var f = _.map(fnames, buildFacility(fproto));
            var offset = [280, 155];
            n = 0;
            _.forEach(f, function(fac){ fac.locationInput.offset = { x: offset[0] + (n++ * 65), y: offset[1] } });

            hub.location.coordinates = f[0].coordinatesFromTopLeft({ x: 30, y: 30 });
            hub.setFacilities(f);
            devApi.resetAll(hub.facilities, [hub]);
        });

        it('facility can be removed from hub, order in facility list is maintained', function () {
            var expectedOrder = [ "_foo_", "b@z", "Bar", "bär", "foo", "fov", "fow", "fåå", "föö" ];

            hubEditPage.get(hub.id);
            expect(hubEditPage.facilitiesTable.getSize()).toEqual(9);

            _.forEach(hub.facilities, function(f){
                hubEditPage.toggleFacility(f);
                expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(true);
                arrayAssert.assertInOrder(hubEditPage.facilitiesTable.getFacilityNames(), expectedOrder, { allowSkip: true });
             });

            expect(hubEditPage.facilitiesTable.isDisplayed()).toBe(true);
            expect(hubEditPage.facilitiesTable.getSize()).toEqual(0);

            hubEditPage.save();
            expect(hubViewPage.isDisplayed()).toBe(true);
            expect(hubViewPage.facilitiesTable.getSize()).toEqual(0);
        });
    });
});
