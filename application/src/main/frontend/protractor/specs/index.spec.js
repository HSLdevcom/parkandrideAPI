'use strict';

var IndexPage = require('../pages/index.page.js');
var FacilityListPage = require('../pages/facilities/facilityList.page.js');

describe('Index page', function() {
    var indexPage = new IndexPage();
    var facilityListPage = new FacilityListPage();

    it('navigates to facilities list page', function() {
        indexPage.get();
        expect(facilityListPage.isDisplayed()).toBe(true);
    });
});
