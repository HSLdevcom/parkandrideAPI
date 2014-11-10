'use strict';

function facility(data) {
    var _ = require('lodash');
    var self = data || {};

    self.copy = function() {
        return facility(_.cloneDeep(self));
    };

    self.copyHorizontallyInDefaultZoom = function(pixels) {
        var copy = self.copy();
        copy.locationInput.offset.x += pixels;
        var coords = copy.location.coordinates[0];
        var xpixelfactor = (coords[2][0] - coords[0][0]) / copy.locationInput.w;
        var xdelta = pixels * xpixelfactor;
        var bbox = copy.location.bbox;
        bbox[0] += xdelta;
        bbox[2] += xdelta;
        _.forEach(coords, function(coord) { coord[0] +=  xdelta; });
        return copy;
    };

    self.coordinatesFromTopLeft = function(offset) {
        var coords = self.location.coordinates[0];
        var xpixelfactor = (coords[2][0] - coords[0][0]) / self.locationInput.w;
        var ypixelfactor = (coords[1][1] - coords[0][1]) / self.locationInput.h;
        var xdelta = offset.x * xpixelfactor;
        var ydelta = offset.y * ypixelfactor;
        var bbox = self.location.bbox;
        return [ bbox[0] + xdelta, bbox[1] + ydelta ];
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