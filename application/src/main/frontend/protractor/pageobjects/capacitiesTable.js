"use strict";

module.exports = function () {
    var spec = {};
    var that = {};

    var _ = require('lodash');
    spec.capacityTypes = element.all(by.css(".wdCapacityTable .wdCapacityType"));

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

    that.getCapacity = function (type) {
        var p1 = that.getBuilt(type).then(function (value) { return spec.parseInt(value); });
        var p2 = that.getUnavailable(type).then(function (value) { return spec.parseInt(value); });
        return protractor.promise.all([p1, p2]).then(function (value) { return  {built: value[0], unavailable: value[1]} });
    };

    that.getCapacities = function(types) {
        function getCapacityWithType(type) {
            return that.getCapacity(type).then(function (value) {
                var result = {};
                result[type] = value;
                return result;
            });
        }

        var capacityPromises = [];
        for (var i = 0; i < types.length; i++) {
            capacityPromises.push(getCapacityWithType(types[i]));
        }
        return protractor.promise.all(capacityPromises).then(function(capacitiesWithType){
            return _.reduce(capacitiesWithType, function(acc, capacityWithType){
                return _.merge(acc, capacityWithType);
            });
        });
    };

    return that;
};