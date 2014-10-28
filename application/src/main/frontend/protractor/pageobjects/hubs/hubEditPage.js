'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var clickSleepMs = 200;

    spec.view = $('.wdHubEditView');
    spec.name = element(by.model('editCtrl.hub.name'));
    spec.map = $('.hub-map .ol-viewport');
    spec.saveButton = element.all(by.css('.wdSave')).first();
    spec.form = $('form');

    that.facilitiesTable = require('../facilitiesTable')({});

    that.setName = function (name) {
        return spec.name.sendKeys(name);
    };

    that.setLocation = function (pos) {
        spec.ptor.actions()
            .mouseMove(spec.map, {x: pos.x, y: pos.y}).click().click()
            .perform();
        spec.ptor.sleep(clickSleepMs);
    };

    that.toggleFacility = function (f) {
        var offsetX = f.border.offset.x + f.border.w / 2;
        var offsetY = f.border.offset.y + f.border.h / 2;
        spec.ptor.actions()
            .mouseMove(spec.map, {x: offsetX, y: offsetY}).click()
            .perform();
        spec.ptor.sleep(clickSleepMs);
    };

    that.save = function () {
        spec.saveButton.click();
    };

    return that;
};
