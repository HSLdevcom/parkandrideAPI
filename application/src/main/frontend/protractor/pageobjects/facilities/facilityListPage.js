'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    spec.view = spec.title = element(by.cssContainingText('h2', 'Fasiliteetit'));
    spec.createButton = element.all(by.linkUiSref('facility-create')).first();

    that.get = function () {
        browser.get('/#/facilities');
    };

    that.getCapacityTypes = function(facilityId) {
        return element.all(by.css(".wdFacility" + facilityId + " .wdCapacityType")).filter(function(el) { return el.isDisplayed(); }).getText();
    };

    that.toCreateView = function () {
        return spec.createButton.click();
    };

    return that;
};
