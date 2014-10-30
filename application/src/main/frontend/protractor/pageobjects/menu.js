'use strict';

module.exports = function(spec) {
    var that = require('./base')(spec);

    spec.facilities = element.all(by.linkUiSref('root.facility-list')).first();
    spec.hubs = element.all(by.linkUiSref('root.hub-list')).first();

    that.toHubs = function () {
        return spec.hubs.click();
    };

    return that;
};