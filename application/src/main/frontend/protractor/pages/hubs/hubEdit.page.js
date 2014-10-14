var HubEditPage = (function() {
    var ptor = protractor.getInstance();
    var clickSleepMs = 200;

    function HubEditPage() {
        this.view = $('.wdHubEditView');
        this.name = element(by.model('editCtrl.hub.name'));
        this.map = $('.hub-map .ol-viewport');
        this.saveButton = element.all(by.css('.wdSave')).first();
    }

    HubEditPage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    HubEditPage.prototype.setName = function(newName) {
        return this.name.sendKeys(newName);
    };

    HubEditPage.prototype.setLocation = function(pos) {
        ptor.actions()
            .mouseMove(this.map, {x: pos.x, y: pos.y}).click().click()
            .perform();
        ptor.sleep(clickSleepMs);
    };

    HubEditPage.prototype.toggleFacility = function(f) {
        var offsetX = f.border.offset.x + f.border.w/2;
        var offsetY = f.border.offset.y + f.border.h/2;
        ptor.actions()
            .mouseMove(this.map, {x: offsetX, y: offsetY}).click()
            .perform();
        ptor.sleep(clickSleepMs);
    };

    HubEditPage.prototype.save = function() {
        this.saveButton.click();
    };

    return HubEditPage;
})();

module.exports = HubEditPage;