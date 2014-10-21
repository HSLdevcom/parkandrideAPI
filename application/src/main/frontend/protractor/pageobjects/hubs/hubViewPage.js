'use strict';

module.exports = function(spec) {
    var _ = require('lodash');

    var that = require('../base')(spec);

    spec.view = $('.wdHubView');
    spec.name = $('.wdName');
    spec.capacityTypes = element.all(by.css(".wdCapacityType"));

    that.getName = function () {
        return spec.name.getText();
    };

    that.assertCapacities = function (facilities) {
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

    that.getCapacityTypes = function() {
        return spec.capacityTypes.filter(function(el) { return el.isDisplayed(); }).getText();
    };

    return that;
};