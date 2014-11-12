'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    spec.view = spec.title = element(by.cssContainingText('h2', 'Pysäköintipaikat'));
    spec.createFacilityButton = element.all(by.linkUiSref('facility-create')).first();
    spec.createHubButton = element.all(by.linkUiSref('hub-create')).first();
    spec.hubAndFacilityNames = element.all(by.css(".wdNameFi"));

    that.get = function () {
        browser.get('/#/hubs')
    };

    that.toFacilityCreateView = function () {
        return spec.createFacilityButton.click();
    };

    that.toHubCreateView = function () {
        return spec.createHubButton.click();
    };

    that.getHubAndFacilityNames = function() {
        return spec.hubAndFacilityNames.getText();
    };

    that.getCapacityTypes = function(facilityId) {
        return $$(".wdFacility" + facilityId + " .wdCapacityType").filter(function(el) { return el.isDisplayed(); }).getText();
    };

    that.clickFacilityName = function(facilityId) {
        $(".wdFacility" + facilityId + " .wdNameFi a").click();
    };

    that.clickHubName = function(hubId) {
        $(".wdHub" + hubId + " .wdNameFi a").click();
    };

    return that;
};
