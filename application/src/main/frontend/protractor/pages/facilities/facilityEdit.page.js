'use strict';

module.exports = function() {
    var api = {};
    var self = {};
    var ptor = protractor.getInstance();

    self.view = $('.wdFacilityEditView');
    self.name = element(by.model('editCtrl.facility.name'));
    self.map = $('.facility-map .ol-viewport');
    self.saveButton = element.all(by.css('.wdSave')).first();
    self.aliases = $('.wdAliases .tags');
    self.capacityTypes = element.all(by.css(".wdCapacityType"));

    api.isDisplayed = function () {
        return self.view.isDisplayed();
    };

    api.get = function (id) {
        if (id) {
            browser.get('/#/facilities/edit/' + id);
        } else {
            browser.get('/#/facilities/create');
        }
    };

    api.getName = function () {
        return self.name.getAttribute('value');
    };

    api.setName = function (name) {
        return self.name.sendKeys(name);
    };

    api.drawBorder = function (topLeft, w, h) {
        ptor.actions()
            .mouseMove(self.map, topLeft).click()
            .mouseMove(self.map, {x: topLeft.x, y: topLeft.y + h}).click()
            .mouseMove(self.map, {x: topLeft.x + w, y: topLeft.y + h}).click()
            .mouseMove(self.map, {x: topLeft.x + w, y: topLeft.y}).click()
            .mouseMove(self.map, topLeft).click()
            .perform();
    };

    api.save = function () {
        self.saveButton.click();
    };

    api.addAlias = function (alias) {
        self.aliases.click();
        var tagsElement = browser.driver.switchTo().activeElement();
        tagsElement.sendKeys(alias);
        tagsElement.sendKeys(protractor.Key.ENTER);
    };

    api.setCapacities = function (capacities) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                element(by.css('.wd' + capacityType + prop)).sendKeys(capacity[prop]);
            }
        }
    };

    api.assertCapacityOrder = function (expectedTypeOrder) {
        expect(self.capacityTypes.count()).toBe(expectedTypeOrder.length);
        for (var i = 0; i < expectedTypeOrder.length; i++) {
            expect(self.capacityTypes.get(i).isDisplayed()).toBe(true);
            expect(self.capacityTypes.get(i).getText()).toBe(expectedTypeOrder[i]);
        }
    };

    return api;
};