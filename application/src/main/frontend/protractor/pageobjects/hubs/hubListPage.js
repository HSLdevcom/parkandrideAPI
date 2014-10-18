'use strict';

module.exports = function() {
    var api = {};
    var self = {};

    self.title = element(by.cssContainingText('h2', 'Alueet'));
    self.createButton = element.all(by.linkUiSref('hub-create')).first();

    api.get = function () {
        browser.get('/#/hubs')
    };

    api.isDisplayed = function () {
        return self.title.isDisplayed();
    };

    api.toCreateView = function () {
        return self.createButton.click();
    };

    return api;
};
