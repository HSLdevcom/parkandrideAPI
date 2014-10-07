'use strict';

var FacilityEditPage = (function() {
    function FacilityCreatePage() {
        this.view = element(by.css('.wdFacilityEditView'));
    }

    FacilityCreatePage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    return FacilityCreatePage;
})();

module.exports = FacilityEditPage;