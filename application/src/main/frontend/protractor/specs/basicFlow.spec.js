'use strict';

var Pages = require('../pages/pages.js');

describe('Create Facility', function() {
    var facilityEditPage = new Pages.FacilityEditPage();
    var facilityViewPage = new Pages.FacilityViewPage();

    var testName = 'Test Facility ' + new Date().getTime();
    var capacities = {"BICYCLE":{"built":20,"unavailable":2},"CAR":{"built":10,"unavailable":1},"DISABLED":{"built":40,"unavailable":4},
        "ELECTRIC_CAR":{"built":60,"unavailable":6},"MOTORCYCLE":{"built":50,"unavailable":5},"PARK_AND_RIDE":{"built":30,"unavailable":3}};

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

    it ('should add aliases', function() {
        facilityEditPage.addAlias("Test alias");
        facilityEditPage.addAlias("Another alias");
    });

    it ('should define all capacities', function() {
        facilityEditPage.setCapacities(capacities);
    });

    it ('draw facility border', function() {
        facilityEditPage.drawBorder({x:60, y:60}, 60, 60);
    });

    it ('saves facility', function() {
        facilityEditPage.save();
        expect(facilityViewPage.isDisplayed()).toBe(true);
        facilityViewPage.assertCapacities(capacities);
    });

});