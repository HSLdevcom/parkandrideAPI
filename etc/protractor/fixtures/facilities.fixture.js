"use strict";

module.exports = function () {
    var self = {};
    var facility = require('./fixtures').facility;

    var ids = {};
    ids._1 = 1;
    ids._2 = 2;
    ids._3 = 3;
    ids._4 = 4;

    var paymentMethods = {
        coins: { id: 1 }, 
        notes: { id: 2 }
    };

    var dummies = {
        facFull: facility({
            "id": ids._3,
            "name": "Dummy full",
            "location": {
                "bbox": [24.77640173950195, 60.18169023118941, 24.797001104736328, 60.191931693737104],
                "type": "Polygon",
                "coordinates": [
                    [
                        [24.77640173950195, 60.191931693737104],
                        [24.77640173950195, 60.18169023118941],
                        [24.797001104736328, 60.18169023118941],
                        [24.797001104736328, 60.191931693737104],
                        [24.77640173950195, 60.191931693737104]
                    ]
                ]
            },
            locationInput: {
                offset: {x: 90, y: 90},
                w: 60,
                h: 60
            },
            aliases: ["alias with spaces", "facFull"],
            capacities: {
                "CAR": {"built": 10, "unavailable": 1},
                "BICYCLE": {"built": 20, "unavailable": 2},
                "PARK_AND_RIDE": {"built": 30, "unavailable": 3},
                "DISABLED": {"built": 40, "unavailable": 4},
                "MOTORCYCLE": {"built": 50, "unavailable": 5},
                "ELECTRIC_CAR": {"built": 60, "unavailable": 6}
            },
            contacts: {
                emergency: 1,
                operator: 1
            },
            serviceIds: [4, 5],
            paymentInfo: {
                parkAndRideAuthRequired: true,
                paymentMethodIds: [ paymentMethods.coins.id, paymentMethods.notes.id ],
                detail: { fi: "Lisätietoja", sv: "Tilläggsinformation", en: "Additional info"},
                url: { fi: "http://www.x-park.fi/hinnasto", sv: "http://www.x-park.fi/prislista", en: "http://www.x-park.fi/pricing" }
            }
        }),
        facCar: facility({
            "id": ids._4,
            "name": "Dummy CAR",
            "location": {
                "bbox": [24.807300787353515, 60.166322046355866, 24.82790015258789, 60.176568301796806],
                "type": "Polygon",
                "coordinates": [
                    [
                        [24.807300787353515, 60.176568301796806],
                        [24.807300787353515, 60.166322046355866],
                        [24.82790015258789, 60.166322046355866],
                        [24.82790015258789, 60.176568301796806],
                        [24.807300787353515, 60.176568301796806]
                    ]
                ]
            },
            locationInput: {
                offset: {x: 180, y: 180},
                w: 60,
                h: 60
            },
            aliases: ["facCar"],
            contacts: {
                emergency: 1,
                operator: 1
            },
            capacities: {
                "CAR": {"built": 10, "unavailable": 1}
            }
        })
    };

    self.westend1 = facility({
        "id": ids._1,
        "name": "Westend CAR",
        "location": {
            "bbox": [24.807768741075638, 60.16837631366566, 24.80811206382954, 60.16868052638392],
            "type": "Polygon",
            "coordinates": [
                [
                    [24.807768741075638, 60.16866985230115],
                    [24.80781165641989, 60.16837631366566],
                    [24.80811206382954, 60.16839232493162],
                    [24.808069148485306, 60.16868052638392],
                    [24.807768741075638, 60.16866985230115]
                ]
            ]
        },
        "aliases": ["Westend", "Westis"],
        contacts: {
            emergency: 1,
            operator: 1
        },
        "capacities": {
            "CAR": {
                "built": 100,
                "unavailable": 0
            }
        }
    });

    self.westend2 = facility({
        "id": ids._2,
        "name": "Westend BICYCLE",
        "location": {
            "bbox": [24.805209586446352, 60.16861541831023, 24.805365154569223, 60.16873283322467],
            "type": "Polygon",
            "coordinates": [
                [
                    [24.805209586446352, 60.168727496192155],
                    [24.805247137372564, 60.16861541831023],
                    [24.805365154569223, 60.16861808683568],
                    [24.805316874806948, 60.16873283322467],
                    [24.805209586446352, 60.168727496192155]
                ]
            ]
        },
        "aliases": ["Westis"],
        contacts: {
            emergency: 1,
            operator: 1
        },
        "capacities": {
            "BICYCLE": {
                "built": 50,
                "unavailable": 0
            }
        }
    });

    self.contact = {
        id: 1,
        name: { fi: "hsl fi", sv: "hsl sv", en: "hsl en" },
        phone: "09 47664444",
        toPayload: function() {
            return self.contact;
        }
    };

    self.all = [
        self.westend1,
        self.westend2
    ];

    self.dummies = dummies;

    self.paymentInfo = {};
    self.paymentInfo.paymentMethods = {};
    self.paymentInfo.paymentMethods.coins = { id: 1 };
    self.paymentInfo.paymentMethods.notes = { id: 2 };

    return self;
};