"use strict";

module.exports = function (spec) {
    var that = require('./base')(spec);

    var _ = require('lodash');
    spec.view = $(".wdFacilitiesTable");

    spec.rows = function() {
        return $('.wdFacilitiesTable').all(by.xpath(".//*[starts-with(@class, 'wdFacilityRow')]"));
    };

    spec.row = function(idx) {
        return spec.rows().get(idx);
    };

    that.getSize = function() {
        return spec.rows().count();
    };

    that.getNames = function() {
        return spec.rows().$$('.wdFacilityNameFi').getText();
    };
    that.clickName = function(row) {
        return spec.row(row).$('.wdFacilityNameFi a').click();
    };

    that.getCapacityTypes = function(row) {
        return spec.row(row).$$(".wdCapacityType").getText();
    };

    that.getCapacityTypesAsDisplayed = function(row) {
        return spec.row(row).$(".wdCapacities").getText();
    };

    return that;
};