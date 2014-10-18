'use strict';

module.exports = function(spec) {
    var that = require('./base')(spec);

    spec.facilities = element(by.linkUiSref('facility-list'));
    spec.hubs = element(by.linkUiSref('hub-list'));

    that.selectFacilities = function () {
        return spec.facilities.click();
    };

    that.toHubs = function () {
        return spec.hubs.click();
    };

    return that;
};