"use strict";

module.exports = function () {
    var spec = {};
    var that = {};

    var _ = require('lodash');
    spec.capacityTypes = element.all(by.css(".wdCapacityType"));

    spec.getTypeProperty = function(type, property) {
        return $('.wd' + type + property).getText();
    };

    spec.parseInt = function(value) {
       if (value) {
          return parseInt(value, 10);
       }
       return 0;
    };

    that.getTypes = function() {
        return spec.capacityTypes.filter(function(e) { return e.isDisplayed(); }).getText();
    };

    that.getBuilt = function(type) {
        return spec.getTypeProperty(type, 'built');
    };

    that.getUnavailable = function(type) {
        return spec.getTypeProperty(type, 'unavailable');
    };

    that.getCapacity = function (type, result) {
        result = result || {};
        return that.getBuilt(type)
            .then(function (value) {
                result.built = spec.parseInt(value);
                return that.getUnavailable(type);
            })
            .then(function (value) {
                result.unavailable = spec.parseInt(value);
                return result;
            });
    };

    that.getCapacities = function(types) {
        var capacityPromises = [];
        for (var i = 0; i < types.length; i++) {
            capacityPromises.push(that.getCapacity(types[i], { type: types[i]}));
        }
        return protractor.promise.all(capacityPromises).then(function(capacities) {
            return _.reduce(capacities, function(result, capacity) {
                var copy = _.clone(capacity);
                delete copy.type;
                result[capacity.type] = copy;
                return result;
            }, {});
        });
    };

    return that;
};