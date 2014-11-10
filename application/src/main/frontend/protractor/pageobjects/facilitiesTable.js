"use strict";

module.exports = function (spec) {
    var that = require('./base')(spec);

    var _ = require('lodash');
    spec.view = element(by.css(".wdFacilitiesTable"));

    spec.rows = function() {
        return element.all(by.css(".wdFacilitiesTable .wdFacilityNameFi"));
    };

    spec.row = function(idx) {
        return spec.rows().get(idx);
    };

    that.getFacilityNames = function() {
        return spec.rows().getText();
    };

    that.getSize = function() {
        return spec.rows().count();
    };

    that.clickRow = function(row) {
        return spec.row(row).click();
    };

    return that;
};