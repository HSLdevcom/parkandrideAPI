"use strict";

module.exports = function (spec) {
    spec = spec || {};

    var _ = require('lodash');
    var that = require('../base')(spec);

    spec.view = $('.sweet-alert');
    //spec.confirmButton = spec.view.$('button.confirm');
    spec.confirmButton = $('.sweet-alert button.confirm');
    spec.cancelButton = spec.view.$('button.cancel');

    that.confirm = function() {
        spec.confirmButton.click();
    };

    that.cancel = function() {
        spec.cancelButton.click();
    };

    return that;
};