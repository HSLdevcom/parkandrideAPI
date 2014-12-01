"use strict";

function hub(data) {
    var _ = require('lodash');
    var self = data || {};

    self.copy = function() {
        var copy = hub(_.cloneDeep(self));
        copy.setFacilities(_.map(copy.facilities, function(facility) { return facility.copy(); }));
        return copy;
    };

    self.setFacilities = function(facilities) {
        self.facilities = facilities;
        self.facilityIds = _.map(facilities, function (f) { return f.id; })
    };

    self.toPayload = function() {
        var payload = self.copy();
        var skipFields = ['facilities'];
        _.forEach(skipFields, function(field) { delete payload[field]; });
        return payload;
    };

    return self;
};

module.exports = hub;