'use strict';

module.exports = function() {
    var api = {};
    var self = {};
    var ptor = protractor.getInstance();
    var clickSleepMs = 200;

    self.view = $('.wdHubEditView');
    self.name = element(by.model('editCtrl.hub.name'));
    self.map = $('.hub-map .ol-viewport');
    self.saveButton = element.all(by.css('.wdSave')).first();

    api.isDisplayed = function () {
        return self.view.isDisplayed();
    };

    api.setName = function (name) {
        return self.name.sendKeys(name);
    };

    api.setLocation = function (pos) {
        ptor.actions()
            .mouseMove(self.map, {x: pos.x, y: pos.y}).click().click()
            .perform();
        ptor.sleep(clickSleepMs);
    };

    api.toggleFacility = function (f) {
        var offsetX = f.border.offset.x + f.border.w / 2;
        var offsetY = f.border.offset.y + f.border.h / 2;
        ptor.actions()
            .mouseMove(self.map, {x: offsetX, y: offsetY}).click()
            .perform();
        ptor.sleep(clickSleepMs);
    };

    api.save = function () {
        self.saveButton.click();
    };

    return api;
};
