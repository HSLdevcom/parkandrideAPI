'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    spec.view = $('.wdFacilityEditView');
    spec.nameFi = element(by.model('editCtrl.facility.name.fi'));
    spec.nameSv = element(by.model('editCtrl.facility.name.sv'));
    spec.nameEn = element(by.model('editCtrl.facility.name.en'));
    spec.map = $('.facility-map .ol-viewport');
    spec.saveButton = element.all(by.css('.wdSave')).first();
    spec.aliases = $('.wdAliases .tags');
    spec.capacityTypes = element.all(by.css(".wdCapacityType"));
    spec.form = $('form');

    that.get = function (id) {
        if (id) {
            browser.get('/#/facilities/edit/' + id);
        } else {
            browser.get('/#/facilities/create');
        }
    };

    that.drawBorder = function (topLeft, w, h) {
        browser.actions()
            .mouseMove(spec.map, topLeft).click()
            .mouseMove(spec.map, {x: topLeft.x, y: topLeft.y + h}).click()
            .mouseMove(spec.map, {x: topLeft.x + w, y: topLeft.y + h}).click()
            .mouseMove(spec.map, {x: topLeft.x + w, y: topLeft.y}).click()
            .mouseMove(spec.map, topLeft).click()
            .perform();
    };

    that.save = function () {
        spec.saveButton.click();
    };

    that.addAlias = function (alias) {
        spec.aliases.click();
        var tagsElement = browser.driver.switchTo().activeElement();
        tagsElement.sendKeys(alias);
        tagsElement.sendKeys(protractor.Key.ENTER);
    };

    that.setCapacities = function (capacities) {
        for (var capacityType in capacities) {
            var capacity = capacities[capacityType];
            for (var prop in capacity) {
                $("input[name='" + capacityType + prop + "']").sendKeys(capacity[prop]);
            }
        }
    };

    that.getCapacityTypes = function() {
      return spec.capacityTypes.filter(function(el) { return el.isDisplayed(); }).getText();
    };

    that.isFacilityRequiredError = function() {
        return spec.isRequiredError($('facility-map'));
    };

    return that;
};