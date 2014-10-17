'use strict';

var FacilityViewPage = (function() {
    var ptor = protractor.getInstance();

    function FacilityViewPage() {
        this.view = $('.wdFacilityView');
        this.name = $('.wdName');
        this.aliases = $('.wdAliases');
        this.toListButton = element.all(by.linkUiSref('facility-list')).first();
        this.capacityTypes = element.all(by.css(".wdCapacityType"));
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
                expect($('.wd' + capacityType + prop).getText()).toEqual("" + capacity[prop]);
            }
        }
    };

    FacilityViewPage.prototype.assertCapacityOrder = function(expectedTypeOrder) {
        for (var i=0; i < expectedTypeOrder.length; i++) {
            if (expectedTypeOrder[i]) {
                expect(this.capacityTypes.get(i).isDisplayed()).toBe(true);
                expect(this.capacityTypes.get(i).getText()).toBe(expectedTypeOrder[i]);
            } else {
                expect(this.capacityTypes.get(i).isDisplayed()).toBe(false);
            }
        }
    };

    FacilityViewPage.prototype.toListView = function() {
      return this.toListButton.click();
    };

    return FacilityViewPage;
})();

module.exports = FacilityViewPage;