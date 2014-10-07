'use strict';

var Pages = require('../../pages/pages.js');

describe('Facility create view', function() {
    var facilityEditPage = new Pages.FacilityEditPage();

    beforeEach(function () {
        facilityEditPage.get();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });

    it ('fields are initially empty', function() {
        expect(facilityEditPage.getName()).toEqual('');
    });
});