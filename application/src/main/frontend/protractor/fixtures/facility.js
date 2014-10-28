'use strict';

module.exports = function(data) {
    var _ = require('lodash');
    var self = data || {};

    self.incCapacity = function (that) {
        var copy = _.cloneDeep(self);
        for (var capacityType in that.capacities) {
            var c1 = copy.capacities[capacityType] || {"built": 0, "unavailable": 0};
            var c2 = that.capacities[capacityType];
            for (var prop in c2) {
                c1[prop] += c2[prop];
            }
        }
        return copy;
    };

    return self;
};