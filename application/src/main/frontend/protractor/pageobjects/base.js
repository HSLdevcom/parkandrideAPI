"use strict";

module.exports = function(spec) {
    var _ = require('lodash');

    spec.hasClass = function (element, cls) {
        return element.getAttribute('class').then(function (classes) {
            return classes.split(' ').indexOf(cls) !== -1;
        });
    };

    spec.hasClasses = function(element, classes) {
        var promises = [];
        _.forEach(classes, function(cls) { promises.push(spec.hasClass(element, cls)); } );
        return protractor.promise.all(promises).then(function (results) {
            return _.every(results, function(r) { return r === true; });
        });
    };

    spec.isRequiredError = function(element) {
        return spec.hasClasses(element, ["ng-invalid-required", "formdirty"]);
    };

    spec.getValue = function(element) {
        return element.getAttribute("value");
    };

    spec.sendKeys = function(element, input) {
        element.clear().sendKeys(input);
    };

    var that = {};

    that.isDisplayed = function() {
        return spec.view.isDisplayed();
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

    that.getNameFi = function () {
        return spec.getValue(spec.nameFi);
    };

    that.getNameSv = function () {
        return spec.getValue(spec.nameSv);
    };

    that.getNameEn = function () {
        return spec.getValue(spec.nameEn);
    };

    that.setNameFi = function (name) {
        spec.sendKeys(spec.nameFi, name);
    };

    that.setNameSv = function (name) {
        spec.sendKeys(spec.nameSv, name);
    };

    that.setNameEn = function (name) {
        spec.sendKeys(spec.nameEn, name);
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