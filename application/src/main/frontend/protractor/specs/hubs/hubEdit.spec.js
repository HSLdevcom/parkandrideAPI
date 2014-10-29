'use strict';

var po = require('../../pageobjects/pageobjects.js');
var fixtures = require('../../fixtures/fixtures');
var devApi = require('../devApi')();

describe('edit hub view', function () {
    var hubEditPage = po.hubEditPage({});
    var hubViewPage = po.hubViewPage({});

    describe('hub with 2 facilities', function () {
        var hubWithTwoFacilities = fixtures.hubsFixture.westend;

        beforeEach(function () {
            devApi.resetAll(hubWithTwoFacilities.facilities, [hubWithTwoFacilities]);
        });

        it('facility can be removed from hub', function () {
            hubEditPage.get(hubWithTwoFacilities.id);
            expect(hubEditPage.facilitiesTable.getSize()).toEqual(2);

            hubEditPage.toggleFacility(hubWithTwoFacilities.facilities[1]);

            // SMELL: the below makes test pass in travis env, find a better way to do this
            browser.sleep(2000);
            expect(hubEditPage.facilitiesTable.getSize()).toEqual(1);

            hubEditPage.save();
            expect(hubViewPage.isDisplayed()).toBe(true);
            expect(hubViewPage.facilitiesTable.getSize()).toEqual(1);
        });
    });
});
