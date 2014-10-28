"use strict";

module.exports = function (data) {
    var facilitiesFixture = require('./fixtures').facilitiesFixture;

    var self = data || {};

    self.westend = {
        "id": 2,
        "name": "Westend",
        "location": {
            "type": "Point",
            "coordinates": [24.804316, 60.16846]
        },
        "facilityIds": [facilitiesFixture.westend1.id, facilitiesFixture.westend2.id]
    };

    self.all = [
        self.westend
    ];

    return self;
};