'use strict';

var Pages = require('../pages/pages.js');

describe('Basic flow', function() {
    var facilityListPage = new Pages.FacilityListPage();
    var facilityEditPage = new Pages.FacilityEditPage();
    var facilityViewPage = new Pages.FacilityViewPage();

    function newFacilityName() {
        return 'Test Facility ' + new Date().getTime();
    }

    var facility1 = {
        name: newFacilityName(),
        capacities: {
            "CAR": {"built": 10, "unavailable": 1},
            "BICYCLE": {"built": 20, "unavailable": 2},
            "PARK_AND_RIDE": {"built": 30, "unavailable": 3},
            "DISABLED": {"built": 40, "unavailable": 4},
            "MOTORCYCLE": {"built": 50, "unavailable": 5},
            "ELECTRIC_CAR": {"built": 60, "unavailable": 6}
        },
        aliases: ["fac1", "facility1"]
    };

    var facility2 = {
        name: newFacilityName(),
        capacities: {
            "CAR": {"built": 10, "unavailable": 1}
        },
        aliases: ["fac2"]
    };

    describe('Create facilities', function () {
        it('Create facility 1', function () {
            facilityEditPage.get();
            expect(facilityEditPage.isDisplayed()).toBe(true);

            facilityEditPage.setName(facility1.name);
            expect(facilityEditPage.getName()).toEqual(facility1.name);

            facilityEditPage.drawBorder({x: 60, y: 60}, 60, 60);
            facilityEditPage.addAlias(facility1.aliases[0]);
            facilityEditPage.addAlias(facility1.aliases[1]);
            facilityEditPage.setCapacities(facility1.capacities);

            facilityEditPage.save();
            expect(facilityViewPage.isDisplayed()).toBe(true);
            expect(facilityViewPage.getName()).toBe(facility1.name);
            facilityViewPage.assertAliases(facility1.aliases);
            facilityViewPage.assertCapacities(facility1.capacities);
        });

        it('Create facility 2', function() {
            facilityEditPage.get();
            expect(facilityEditPage.isDisplayed()).toBe(true);

            facilityEditPage.setName(facility2.name);
            expect(facilityEditPage.getName()).toEqual(facility2.name);

            facilityEditPage.drawBorder({x: 150, y: 150}, 60, 60);
            facilityEditPage.addAlias(facility2.aliases[0]);
            facilityEditPage.setCapacities(facility2.capacities);

            facilityEditPage.save();
            expect(facilityViewPage.isDisplayed()).toBe(true);
            expect(facilityViewPage.getName()).toBe(facility2.name);
            facilityViewPage.assertAliases(facility2.aliases);
            facilityViewPage.assertCapacities(facility2.capacities);
        });
    });
});
