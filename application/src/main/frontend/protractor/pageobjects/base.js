"use strict";

module.exports = function(spec) {
    spec.ptor = protractor.getInstance();

    spec.hasClass = function (element, cls) {
        return element.getAttribute('class').then(function (classes) {
            return classes.split(' ').indexOf(cls) !== -1;
        });
    };

    spec.isRequiredError = function(element) {
        return spec.hasClass(element, "ng-invalid-required");
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
    };

    that.getName = function () {
        spec.nameFi.getAttribute('value');
    };

    that.setNameFi = function (name) {
        spec.nameFi.clear();
        spec.nameFi.sendKeys(name);
    };

    that.setNameSv = function (name) {
        spec.nameSv.clear();
        spec.nameSv.sendKeys(name);
    };

    that.setNameEn = function (name) {
        spec.nameEn.clear();
        spec.nameEn.sendKeys(name);
    };

    that.setName = function (name) {
        that.setNameFi(name);
        that.setNameSv(name);
        that.setNameEn(name);
    };

    that.isNameFiRequiredError = function () {
        return spec.isRequiredError(spec.nameFi);
    };

    that.isNameSvRequiredError = function () {
        return spec.isRequiredError(spec.nameSv);
    };

    that.isNameEnRequiredError = function () {
        return spec.isRequiredError(spec.nameEn);
    };

    return that;
};