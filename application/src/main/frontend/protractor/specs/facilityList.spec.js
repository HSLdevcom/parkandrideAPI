'use strict';

var Pages = require('../pages/pages.js');

describe('Facilities list view', function() {
    var facilityListPage = new Pages.FacilityListPage();
    var facilityEditPage = new Pages.FacilityEditPage();

    beforeEach(function () {
        facilityListPage.get();
    });

    it ('provides navigation to facility create view', function() {
        facilityListPage.toCreateView();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });
});