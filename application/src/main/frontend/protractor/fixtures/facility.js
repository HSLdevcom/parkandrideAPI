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

    self.moveLeftInDefaultZoom = function(pixels) {
        var copy = self.copy();
        var coords = copy.location.coordinates[0];
        var xpixelfactor = (coords[2][0] - coords[0][0]) / copy.locationInput.w;
        var xdelta = pixels * xpixelfactor;
        var bbox = copy.location.bbox;
        bbox[0] += xdelta;
        bbox[2] += xdelta;
        _.forEach(coords, function(coord) { coord[0] +=  xdelta; });
        return copy;
    };

    return self;
};

module.exports = facility;