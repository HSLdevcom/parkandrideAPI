"use strict";

module.exports = function (spec) {
    spec = spec || {};

    var _ = require('lodash');
    var that = require('../base')(spec);

    spec.view = $('.sweet-alert');
    spec.confirmButton = spec.view.$('.confirm');
    spec.cancelButton = spec.view.$('.cancel');

    that.confirm = function() {
        // TODO angularize this component as it causes test failures due to not being included in angular
        // wait cycle, e.g. clicking of confirm button does not seems to trigger the related action
        expect(spec.isDisplayed(spec.confirmButton)).toBe(true);
        browser.sleep(100);

        spec.confirmButton.click();
    };

    that.cancel = function() {
        spec.cancelButton.click();
    };

    that.confirmButton = spec.confirmButton;
    that.cancelButton = spec.cancelButton;

    return that;
};