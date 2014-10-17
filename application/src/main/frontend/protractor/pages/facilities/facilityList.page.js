'use strict';

var FacilityListPage = (function() {
    function FacilityListPage() {
        this.title = element(by.cssContainingText('h2', 'Fasiliteetit'));
        this.createButton = element.all(by.linkUiSref('facility-create')).first();
    }

    FacilityListPage.prototype.get = function() {
        browser.get('/#/facilities');
    };

    FacilityListPage.prototype.isDisplayed = function() {
        return this.title.isDisplayed();
    };

    FacilityListPage.prototype.assertCapacityOrder = function(expectedTypeOrder, facilityId) {
        var capacityTypes = element.all(by.css(".wdFacility" + facilityId + " .wdCapacityType"));
        for (var i=0; i < expectedTypeOrder.length; i++) {
            if (expectedTypeOrder[i]) {
                expect(capacityTypes.get(i).isDisplayed()).toBe(true);
                expect(capacityTypes.get(i).getText()).toBe(expectedTypeOrder[i]);
            } else {
                expect(capacityTypes.get(i).isDisplayed()).toBe(false);
            }
        }
    };

    FacilityListPage.prototype.toCreateView = function() {
        return this.createButton.click();
    };

    return FacilityListPage;
})();

module.exports = FacilityListPage;