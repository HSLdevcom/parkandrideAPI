'use strict';

var po = require('../../pageobjects/pageobjects.js');

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
});
