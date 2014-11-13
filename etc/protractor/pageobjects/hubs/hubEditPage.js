'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var clickSleepMs = 200;

    spec.view = $('.wdHubEditView');
    spec.nameFi = element(by.name('nameFi'));
    spec.nameSv = element(by.name('nameSv'));
    spec.nameEn = element(by.name('nameEn'));
    spec.map = $('.hub-map .ol-viewport');
    spec.saveButton = element.all(by.css('.wdSave')).first();
    spec.form = $('form');

    that.facilitiesTable = require('../facilitiesTable')({});

    that.get = function (id) {
        if (id) {
            browser.get('/#/hubs/edit/' + id);
        } else {
            browser.get('/#/hubs/create');
        }
    };

    that.setLocation = function (pos) {
        browser.actions()
            .mouseMove(spec.map, {x: pos.x, y: pos.y}).click().click()
            .perform();
        browser.sleep(clickSleepMs);
    };

    that.toggleFacility = function (f) {
        var offsetX = f.locationInput.offset.x + f.locationInput.w / 2;
        var offsetY = f.locationInput.offset.y + f.locationInput.h / 2;
        browser.actions()
            .mouseMove(spec.map, {x: offsetX, y: offsetY}).click()
            .perform();
        browser.sleep(clickSleepMs);
    };

    that.save = function () {
        spec.saveButton.click();
    };

    that.isLocationRequiredError = function() {
        return spec.isRequiredError($('edit-hub-map'));
    };

    return that;
};
