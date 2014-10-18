'use strict';

module.exports = function() {
    var api = {};
    var self = {};
    var ptor = protractor.getInstance();
    var capacityAssert = require('./capacityAssert')();

    self.view = $('.wdFacilityView');
    self.name = $('.wdName');
    self.aliases = $('.wdAliases');
    self.toListButton = element.all(by.linkUiSref('facility-list')).first();
    self.capacityTypes = element.all(by.css(".wdCapacityType"));

    api.isDisplayed = function () {
        return self.view.isDisplayed();
    };

    api.getName = function () {
        return self.name.getText();
    };

    api.assertAliases = function (aliases) {
        expect(self.aliases.getText()).toEqual((aliases || []).join(', '));
    };

    api.assertCapacities = function (capacities) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                expect($('.wd' + capacityType + prop).getText()).toEqual("" + capacity[prop]);
            }
        }
    };

    api.assertCapacityOrder = function (expectedTypeOrder) {
        capacityAssert.assertInOrderIfDisplayed(self.capacityTypes, expectedTypeOrder);
    };

    api.toListView = function () {
        return self.toListButton.click();
    };

    return api;
};
