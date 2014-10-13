'use strict';

var FacilityViewPage = (function() {
    var ptor = protractor.getInstance();

    function FacilityViewPage() {
        this.view = element(by.css('.wdFacilityView'));
    }

    FacilityViewPage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    FacilityViewPage.prototype.assertCapacities = function(capacities) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                expect(element(by.css('.wd' + capacityType + prop)).getText()).toEqual("" + capacity[prop]);
            }
        }
    };

    return FacilityViewPage;
})();

module.exports = FacilityViewPage;