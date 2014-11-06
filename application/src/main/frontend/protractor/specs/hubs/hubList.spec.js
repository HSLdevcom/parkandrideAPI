'use strict';

var _ = require('lodash');

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var devApi = require('../devApi')();

describe('hub list', function () {
    var indexPage = po.indexPage({});
    var hubListPage = po.hubListPage({});
    var hubEditPage = po.hubEditPage({});
    var facilityEditPage = po.facilityEditPage({});

    it('is the default view', function () {
        indexPage.get();
        expect(hubListPage.isDisplayed()).toBe(true);
    });

    describe('navigation', function () {
        beforeEach(function () {
            hubListPage.get();
        });

        it('to create facility', function () {
            hubListPage.toFacilityCreateView();
            expect(facilityEditPage.isDisplayed()).toBe(true);
        });

        it('to create hub', function () {
            hubListPage.toHubCreateView();
            expect(hubEditPage.isDisplayed()).toBe(true);
        });
    });

    describe('with hubs and facilities', function () {
        beforeEach(function () {
            var idGen = 100;
            function rename(proto) {
                return function(name) {
                    var o  = proto.copy();
                    o.name = name;
                    o.id = idGen++;
                    return o;
                }
            }

            var hproto = fixtures.hubsFixture.westend;
            var fproto = fixtures.facilitiesFixture.dummies.facFull;

            var hnames = [ "guX", "NORF"];
            var fnames = [ "foo", "Bar", "b@z"];

            var h = _.map(hnames, rename(hproto));
            _.forEach(h, function(hub) {
                hub.facilities = _.map(fnames, rename(fproto));
                hub.facilityIds = _.map(hub.facilities, function (f) { return f.id; });
            });

            var f = _.map(fnames, rename(fproto));

            devApi.resetAll(_.union(f, h[0].facilities, h[1].facilities), h);
            hubListPage.get();
        });

        it('facilities without hubs are followed by facilities grouped into hubs', function () {
            expect(hubListPage.getHubAndFacilityNames()).toEqual([
                "b@z", "Bar", "foo",
                "guX", "b@z", "Bar", "foo",
                "NORF", "b@z", "Bar", "foo"
            ]);
        });
    });
});
