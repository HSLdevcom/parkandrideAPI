'use strict';

var FacilityEditPage = (function() {
    function FacilityCreatePage() {
        this.view = element(by.css('.wdFacilityEditView'));
        this.name = element(by.model('editCtrl.facility.name'));
    }

    FacilityCreatePage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    FacilityCreatePage.prototype.get = function() {
        browser.get('/#/facilities/create');
    };

    FacilityCreatePage.prototype.getName = function() {
        return this.name.getAttribute('value');
    };

    return FacilityCreatePage;
})();

module.exports = FacilityEditPage;