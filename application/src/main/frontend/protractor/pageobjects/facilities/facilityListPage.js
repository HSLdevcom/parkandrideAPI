'use strict';


module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacityAssert = require('./capacityAssert')();

    spec.view = spec.title = element(by.cssContainingText('h2', 'Fasiliteetit'));
    spec.createButton = element.all(by.linkUiSref('facility-create')).first();

    that.get = function () {
        browser.get('/#/facilities');
    };

    that.assertCapacityOrder = function (expectedTypeOrder, facilityId) {
        capacityAssert.assertInOrderIfDisplayed(
            element.all(by.css(".wdFacility" + facilityId + " .wdCapacityType")),
            expectedTypeOrder);
    };

    that.toCreateView = function () {
        return spec.createButton.click();
    };

    return that;
};
