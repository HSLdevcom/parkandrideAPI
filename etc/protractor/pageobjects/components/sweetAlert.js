"use strict";

module.exports = function (spec) {
    spec = spec || {};

    var _ = require('lodash');
    var that = require('../base')(spec);

    spec.view = $('.sweet-alert');
    spec.confirmButton = spec.view.$('.confirm');
    spec.cancelButton = spec.view.$('.cancel');

    that.confirm = function() {
        spec.ensureIsPresent(spec.confirmButton).click();
    };

    that.cancel = function() {
        spec.ensureIsPresent(spec.cancelButton).click();
    };

    return that;
};