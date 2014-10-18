'use strict';


module.exports = function() {
    var api = {};
    var self = {};
    var capacityAssert = require('./capacityAssert')();

    self.title = element(by.cssContainingText('h2', 'Fasiliteetit'));
    self.createButton = element.all(by.linkUiSref('facility-create')).first();

    api.get = function () {
        browser.get('/#/facilities');
    };

    api.isDisplayed = function () {
        return self.title.isDisplayed();
    };

    api.assertCapacityOrder = function (expectedTypeOrder, facilityId) {
        capacityAssert.assertInOrderIfDisplayed(
            element.all(by.css(".wdFacility" + facilityId + " .wdCapacityType")),
            expectedTypeOrder);
    };

    api.toCreateView = function () {
        return self.createButton.click();
    };

    return api;
};
