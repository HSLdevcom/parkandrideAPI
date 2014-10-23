"use strict";

module.exports = function (spec) {
    var that = require('./base')(spec);

    var _ = require('lodash');
    spec.view = element(by.css(".wdFacilitiesTable"));

    that.getFacilityNames = function() {
        return element.all(by.css(".wdFacilitiesTable .wdFacilityName")).getText();
    }

    return that;
};