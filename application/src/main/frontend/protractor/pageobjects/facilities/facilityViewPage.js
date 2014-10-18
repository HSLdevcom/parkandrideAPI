'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacityAssert = require('./capacityAssert')();

    spec.view = $('.wdFacilityView');
    spec.name = $('.wdName');
    spec.aliases = $('.wdAliases');
    spec.toListButton = element.all(by.linkUiSref('facility-list')).first();
    spec.capacityTypes = element.all(by.css(".wdCapacityType"));

    that.getName = function () {
        return spec.name.getText();
    };

    that.assertAliases = function (aliases) {
        expect(spec.aliases.getText()).toEqual((aliases || []).join(', '));
    };

    that.assertCapacities = function (capacities) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                expect($('.wd' + capacityType + prop).getText()).toEqual("" + capacity[prop]);
            }
        }
    };

    that.assertCapacityOrder = function (expectedTypeOrder) {
        capacityAssert.assertInOrderIfDisplayed(spec.capacityTypes, expectedTypeOrder);
    };

    that.toListView = function () {
        return spec.toListButton.click();
    };

    return that;
};
