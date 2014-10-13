'use strict';

var FacilityEditPage = (function() {
    var ptor = protractor.getInstance();

    function FacilityEditPage() {
        this.view = element(by.css('.wdFacilityEditView'));
        this.name = element(by.model('editCtrl.facility.name'));
        this.map = element(by.css('.facility-map .ol-viewport'));
        this.saveButton = element(by.css('.wdSave'));
        this.aliases = element(by.css('.wdAliases .tags'));
    }

    FacilityEditPage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    FacilityEditPage.prototype.get = function(id) {
        if (id) {
            browser.get('/#/facilities/edit/' + id);
        } else {
            browser.get('/#/facilities/create');
        }
    };

    FacilityEditPage.prototype.getName = function() {
        return this.name.getAttribute('value');
    };

    FacilityEditPage.prototype.setName = function(newName) {
        return this.name.sendKeys(newName);
    };

    FacilityEditPage.prototype.drawBorder = function(topLeft, w, h) {
        ptor.actions()
            .mouseMove(this.map, topLeft).click()
            .mouseMove(this.map, {x: topLeft.x, y: topLeft.y + h}).click()
            .mouseMove(this.map, {x: topLeft.x + w, y: topLeft.y + h}).click()
            .mouseMove(this.map, {x: topLeft.x + w, y: topLeft.y}).click()
            .mouseMove(this.map, topLeft).click()
            .perform();
    };

    FacilityEditPage.prototype.save = function() {
        this.saveButton.click();
    };

    FacilityEditPage.prototype.addAlias = function(alias) {
        this.aliases.click();
        browser.debugger();
        var tagsElement = browser.driver.switchTo().activeElement();
        tagsElement.sendKeys(alias);
        tagsElement.sendKeys(protractor.Key.ENTER);
    };

    FacilityEditPage.prototype.setCapacities = function(capacities) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                element(by.css('.wd' + capacityType + prop)).sendKeys(capacity[prop]);
            }
        }
    }

    return FacilityEditPage;
})();

module.exports = FacilityEditPage;