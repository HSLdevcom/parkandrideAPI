'use strict';

module.exports = function(spec) {
    spec = spec || {};
    var that = require('../base')(spec);

    spec.view = $("#operatorEditModal");
    spec.ok = $("#wdOperatorOk");
    spec.cancel = $("#wdOperatorCancel");

    spec.context = spec.view;
    spec.defineMultilingualAccessors("name");

    that.save = function() {
        spec.ok.click();
        browser.waitForAngular();
    };

    that.cancel = function() {
        spec.cancel.click();
        browser.waitForAngular();
    };

    return that;
};