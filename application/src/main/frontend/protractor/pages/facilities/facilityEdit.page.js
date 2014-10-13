'use strict';

var FacilityEditPage = (function() {
    var ptor = protractor.getInstance();

    function FacilityCreatePage() {
        this.view = element(by.css('.wdFacilityEditView'));
        this.name = element(by.model('editCtrl.facility.name'));
        this.map = element(by.css('.facility-map'));
        this.saveButton = element(by.css('.wdSave'));
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

    FacilityCreatePage.prototype.setName = function(newName) {
        return this.name.sendKeys(newName);
    };

    FacilityCreatePage.prototype.drawBorder = function(topLeft, w, h) {
        ptor.actions()
            .mouseMove(this.map, topLeft).click()
            .mouseMove(this.map, {x: topLeft.x, y: topLeft.y + h}).click()
            .mouseMove(this.map, {x: topLeft.x + w, y: topLeft.y + h}).click()
            .mouseMove(this.map, {x: topLeft.x + w, y: topLeft.y}).click()
            .mouseMove(this.map, topLeft).click()
            .perform();
    };

    FacilityCreatePage.prototype.save = function() {
        this.saveButton.click();
    };

    return FacilityCreatePage;
})  ();

module.exports = FacilityEditPage;