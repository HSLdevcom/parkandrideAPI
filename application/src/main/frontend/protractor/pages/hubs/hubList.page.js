var HubListPage = (function() {
    function HubListPage() {
        this.title = element(by.cssContainingText('h2', 'Alueet'));
        this.createButton = element.all(by.linkUiSref('hub-create')).first();
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