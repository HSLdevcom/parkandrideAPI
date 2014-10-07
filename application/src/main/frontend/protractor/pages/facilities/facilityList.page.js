'use string';

var FacilityListPage = (function() {
    function FacilityListPage() {
        this.title = element(by.cssContainingText('h2', 'Fasiliteetit'));
    }

    FacilityListPage.prototype.get = function() {
        browser.get('/#/facilities')
    };

    FacilityListPage.prototype.isDisplayed = function(puup) {
        return this.title.isDisplayed();
    };

    return FacilityListPage;
})();

module.exports = FacilityListPage;