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
            _.assign(facilitiesFixture.westend2, { locationInput: { offset: { x: 380, y: 135 }, w: 30, h: 40 } })
        ]
    });
    self.westend.facilityIds = _.map(self.westend.facilities, function(f) { return f.id; });

    self.contact = facilitiesFixture.contact;

    self.all = [
        self.westend
    ];

    return self;
};