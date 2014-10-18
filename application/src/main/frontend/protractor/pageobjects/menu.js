'use strict';

module.exports = function() {
    var api = {};
    var self = {};

    self.facilities = element(by.linkUiSref('facility-list'));
    self.hubs = element(by.linkUiSref('hub-list'));

    api.selectFacilities = function () {
        return self.facilities.click();
    };

    api.toHubs = function () {
        return self.hubs.click();
    };

    return api;
};