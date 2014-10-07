'use strict';

var FacilityListPage = require('../pages/facilities/facilityList.page.js');
var FacilityEditPage = require('../pages/facilities/facilityEdit.page.js');

describe('Facilities list view', function() {
    var facilityListPage = new FacilityListPage();
    var facilityEditPage = new FacilityEditPage();

    beforeEach(function () {
        facilityListPage.get();
    });

    it ('provides navigation to facility create view', function() {
        facilityListPage.toCreateView();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });
});