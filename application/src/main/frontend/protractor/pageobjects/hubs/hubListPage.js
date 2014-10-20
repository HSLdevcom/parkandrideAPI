'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    spec.view = spec.title = element(by.cssContainingText('h2', 'Alueet'));
    spec.createButton = element.all(by.linkUiSref('hub-create')).first();

    that.get = function () {
        browser.get('/#/hubs')
    };

    that.toCreateView = function () {
        return spec.createButton.click();
    };

    return that;
};
