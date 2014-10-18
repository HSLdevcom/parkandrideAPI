'use strict';

module.exports = function() {
    var api = {};
    var self = {};

    self.title = element(by.cssContainingText('h2', 'Fasiliteetit'));
    self.createButton = element.all(by.linkUiSref('facility-create')).first();

    api.get = function () {
        browser.get('/#/facilities');
    };

    api.isDisplayed = function () {
        return self.title.isDisplayed();
    };

    api.assertCapacityOrder = function (expectedTypeOrder, facilityId) {
        var capacityTypes = element.all(by.css(".wdFacility" + facilityId + " .wdCapacityType"));
        for (var i = 0; i < expectedTypeOrder.length; i++) {
            if (expectedTypeOrder[i]) {
                expect(capacityTypes.get(i).isDisplayed()).toBe(true);
                expect(capacityTypes.get(i).getText()).toBe(expectedTypeOrder[i]);
            } else {
                expect(capacityTypes.get(i).isDisplayed()).toBe(false);
            }
        }
    };

    api.toCreateView = function () {
        return self.createButton.click();
    };

    return api;
};
