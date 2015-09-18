"use strict";

module.exports = function (spec) {
    var that = require('./base')(spec);

    var _ = require('lodash');
    spec.view = $('.wdPredictionsTable');
    spec.predictionRows = element.all(by.css('.wdPredictionsTable .wdPredictionRow'));
    spec.capacityTypes = element.all(by.css(".wdCapacityTable .wdCapacityType"));

    that.getRows = function() {
        return spec.predictionRows;
    };
    that.getTypes = function() {
        return spec.capacityTypes.filter(function(e) { return e.isDisplayed(); }).getText();
    };

    that.getSize = function() {
        return that.getRows().count();
    };

    that.getBuilt = function(type) {
        return $('.wdBuiltCapacity' + type);
    };

    that.getCapacity = function (type) {
        return that.getBuilt(type).then(function (elem) {
            return elem.getText()
        }).then(function (value) {
            return spec.parseInt(value);
        });
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