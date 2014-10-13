'use strict';

var Pages = require('../pages/pages.js');

describe('Index page', function() {
    var indexPage = new Pages.IndexPage();
    var facilityListPage = new Pages.FacilityListPage();

    it('navigates to facilities list page', function() {
        indexPage.get();
        expect(facilityListPage.isDisplayed()).toBe(true);
    });
});
