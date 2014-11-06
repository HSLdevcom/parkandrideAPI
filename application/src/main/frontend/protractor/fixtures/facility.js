'use strict';

function facility(data) {
    var _ = require('lodash');
    var self = data || {};

    self.copy = function() {
        return facility(_.cloneDeep(self));
    };

    self.incCapacity = function (that) {
        var copy = self.copy();
        for (var capacityType in that.capacities) {
            var c1 = copy.capacities[capacityType] || {"built": 0, "unavailable": 0};
            var c2 = that.capacities[capacityType];
            for (var prop in c2) {
                c1[prop] += c2[prop];
            }
        }
        return copy;
    };

    self.toPayload = function() {
        var payload = self.copy();
        var skipFields = ['locationInput'];
        _.forEach(skipFields, function(field){ delete payload[field]; });
        return payload;
    };

    return self;
};

module.exports = facility;