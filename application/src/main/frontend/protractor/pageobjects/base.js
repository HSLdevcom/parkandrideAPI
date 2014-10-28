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

    that.getViolations = function() {
        return $$('.wdViolation').then(function(violations) {
            var result = [];
            for (var i=0; i < violations.length; i++) {
                var violation = violations[i];
                var path = violation.element(by.css('.wdViolationPath')).getText();
                var message = violation.element(by.css('.wdViolationMessage')).getText();
                result.push(protractor.promise.all([path, message]).then(function(result) {
                    return {path: result[0], message: result[1]};
                }));
            }
            return protractor.promise.all(result);
        });
    }

    return that;
};