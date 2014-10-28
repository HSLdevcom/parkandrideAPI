"use strict";

module.exports = function (data) {
    var _ = require('lodash');
    var facilitiesFixture = require('./fixtures').facilitiesFixture;
    var hub = require('./fixtures').hub;

    var self = data || {};

    var ids = {};
    ids._1 = 1;

    self.westend = hub({
        "id": ids._1,
        "name": "Westend",
        "location": {
            "type": "Point",
            "coordinates": [24.804316, 60.16846]
        },
        "facilities": [
            facilitiesFixture.westend1,
            _.assign(facilitiesFixture.westend2, { borderInput: { offset: { x: 419, y: 232 }, w: 34, h: 59 } })
        ]
    });
    self.westend.facilityIds = _.map(self.westend.facilities, function(f) { return f.id; });

    self.all = [
        self.westend
    ];

    return self;
};