'use strict';

module.exports = function() {
    var _ = require('lodash');

    var api = {};
    var self = {};

    self.view = $('.wdHubView');
    self.name = $('.wdName');

    api.isDisplayed = function () {
        return self.view.isDisplayed();
    };

    api.getName = function () {
        return self.name.getText();
    };

    api.assertCapacities = function (facilities) {
        var sum = _.reduce(facilities, function (acc, facility) {
            return acc.incCapacity(facility);
        });

        for (var capacityType in sum.capacities) {
            var capacity = sum.capacities[capacityType];
            for (var prop in capacity) {
                expect($('.wd' + capacityType + prop).getText()).toEqual("" + capacity[prop]);
            }
        }
    };

    return api;
};