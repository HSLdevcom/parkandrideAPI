"use strict";

module.exports = function(spec) {
    spec.ptor = protractor.getInstance();

    spec.hasClass = function (element, cls) {
        return element.getAttribute('class').then(function (classes) {
            return classes.split(' ').indexOf(cls) !== -1;
        });
    };

    var that = {};

    that.isDisplayed = function() {
        return spec.view.isDisplayed();
    };

    that.isDirty = function() {
        return spec.hasClass(spec.form, "ng-dirty");
    };

    return that;
};