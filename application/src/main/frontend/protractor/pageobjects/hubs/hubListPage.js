'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    spec.view = spec.title = element(by.cssContainingText('h2', 'Alueet'));
    spec.createFacilityButton = element.all(by.linkUiSref('facility-create')).first();
    spec.createHubButton = element.all(by.linkUiSref('hub-create')).first();
    spec.hubAndFacilityNames = element.all(by.css(".wdName"));

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
    }

    that.getCapacityTypes = function(facilityId) {
        return element.all(by.css(".wdFacility" + facilityId + " .wdCapacityType")).filter(function(el) { return el.isDisplayed(); }).getText();
    };

    return that;
};
