var HubListPage = (function() {
    function HubListPage() {
        this.title = element(by.cssContainingText('h2', 'Alueet'));
        this.createButton = element(by.linkUiSref('hub-create'));
    }

    HubListPage.prototype.get = function() {
        browser.get('/#/hubs')
    };

    HubListPage.prototype.isDisplayed = function() {
        return this.title.isDisplayed();
    };

    HubListPage.prototype.toCreateView = function() {
        return this.createButton.click();
    };

    return HubListPage;
})();

module.exports = HubListPage;