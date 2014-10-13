var HubEditPage = (function() {
    function HubEditPage() {
        this.view = element(by.css('.wdHubEditView'));
    }

    HubEditPage.prototype.isDisplayed = function() {
        return this.view.isDisplayed();
    };

    return HubEditPage;
})();

module.exports = HubEditPage;