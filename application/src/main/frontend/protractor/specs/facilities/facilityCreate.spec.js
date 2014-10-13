'use strict';

var Pages = require('../../pages/pages.js');

describe('Facility create view', function() {
    var facilityEditPage = new Pages.FacilityEditPage();

    var testName = 'Test Facility ' + new Date().getTime();;

    it ('should show facility create page', function () {
        facilityEditPage.get();
        expect(facilityEditPage.isDisplayed()).toBe(true);
    });

    it ('fields are initially empty', function() {
        expect(facilityEditPage.getName()).toEqual('');
    });

    it ('insert name', function() {
        facilityEditPage.setName(testName);
        expect(facilityEditPage.getName()).toEqual(testName);
    });

    it ('draws facility borders', function() {
        facilityEditPage.drawBorder({x:60, y:60}, 60, 60);
    });

    it ('saves facility', function() {
        facilityEditPage.save();
        browser.debugger();
        element(by.css('.wdFacilityView')).isDisplayed();
    });

});