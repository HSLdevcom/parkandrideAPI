"use strict";

module.exports = function(spec) {
    spec.ptor = protractor.getInstance();

    var that = {};

    that.isDisplayed = function() {
        return spec.view.isDisplayed();
    };

    return that;
};