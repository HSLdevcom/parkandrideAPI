"use strict";

module.exports = function (spec) {
    var that = require('./base')(spec);

    var _ = require('lodash');
    spec.view = $('.wdPredictionsTable');
    spec.predictionRows = element.all(by.css('.wdPredictionsTable .wdPredictionRow'));
    spec.capacityTypes = element.all(by.css(".wdPredictionsTable .wdCapacityType"));
    spec.usages = element.all(by.css(".wdPredictionsTable .wdUsage"));

    that.getRows = function() {
        return spec.predictionRows;
    };
    that.getTypes = function() {
        return spec.capacityTypes.filter(function(e) {
            return e.isDisplayed();
        }).getText();
    };
    that.getUsages = function() {
        return spec.usages.filter(function(e) {
            return e.isDisplayed();
        }).getText();
    };

    that.getSize = function() {
        return that.getRows().count();
    };

    return that;
};