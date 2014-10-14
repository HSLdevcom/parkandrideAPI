'use strict';

var FacilityListPage = (function() {
    function FacilityListPage() {
        this.title = element(by.cssContainingText('h2', 'Fasiliteetit'));
        this.createButton = element.all(by.linkUiSref('facility-create')).first();
    }

    FacilityListPage.prototype.get = function() {
        browser.get('/#/facilities')
    };

    FacilityListPage.prototype.isDisplayed = function() {
        return this.title.isDisplayed();
    };

    FacilityListPage.prototype.toCreateView = function() {
        return this.createButton.click();
    };

    return FacilityListPage;
})();

module.exports = FacilityListPage;