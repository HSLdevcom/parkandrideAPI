'use strict';

var FacilityViewPage = (function() {
    var ptor = protractor.getInstance();

    function FacilityViewPage() {
        this.view = element(by.css('.wdFacilityView'));
        this.name = element(by.css('.wdName'));
        this.aliases = element(by.css('.wdAliases'));
    }

    FacilityViewPage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    FacilityViewPage.prototype.getName = function() {
        return this.name.getText();
    };

    FacilityViewPage.prototype.assertAliases = function(aliases) {
        expect(this.aliases.getText()).toEqual((aliases || []).join(', '));
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