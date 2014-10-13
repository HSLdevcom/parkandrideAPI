var HubEditPage = (function() {
    var ptor = protractor.getInstance();

    function HubEditPage() {
        this.view = element(by.css('.wdHubEditView'));
        this.name = element(by.model('editCtrl.hub.name'));
        this.map = element(by.css('.hub-map .ol-viewport'));
        this.saveButton = element(by.css('.wdSave'));
    }

    HubEditPage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    HubEditPage.prototype.setName = function(newName) {
        return this.name.sendKeys(newName);
    };

    HubEditPage.prototype.setLocation = function(pos) {
        ptor.actions()
            .mouseMove(this.map, {x: pos.x, y: pos.y}).doubleClick()
            .perform();
    };

    HubEditPage.prototype.toggleFacility = function(f) {
        var offsetX = f.border.offset.x + f.border.w/2;
        var offsetY = f.border.offset.y + f.border.h/2;
        ptor.actions()
            .mouseMove(this.map, {x: offsetX, y: offsetY}).click()
            .perform();
    };

    HubEditPage.prototype.save = function() {
        this.saveButton.click();
    };

    return HubEditPage;
})();

module.exports = HubEditPage;