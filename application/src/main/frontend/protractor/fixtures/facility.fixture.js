'use strict';

var FacilityFixture = (function() {
    var _ = require('lodash');

    function FacilityFixture(data) {
        _.assign(this, data);
    };

    FacilityFixture.prototype.incCapacity = function(that) {
        var copy = _.cloneDeep(this);
        for (var capacityType in that.capacities) {
            var c1 = copy.capacities[capacityType] || {"built": 0, "unavailable": 0};
            var c2 = that.capacities[capacityType];
            for (var prop in c2) {
                c1[prop] += c2[prop];
            }
        }
        return copy;
    };

    return FacilityFixture;
})();

module.exports = FacilityFixture;
