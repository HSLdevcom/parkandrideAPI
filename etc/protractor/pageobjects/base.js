"use strict";

module.exports = function(spec) {
    var _ = require('lodash');
    var validationErrorClass = "validation-error";

    spec.hasClasses = function(element, expected) {
        return element.getAttribute('class').then(function (classAttr) {
            var actual = classAttr.split(' ');
            return _.every(expected, function(cls) { return _.contains(actual, cls); } );
        });
    };

    spec.hasNoClasses = function(element, expected) {
        return element.getAttribute('class').then(function (classAttr) {
            var actual = classAttr.split(' ');
            return _.every(expected, function(cls) { return !_.contains(actual, cls); } );
        });
    };

    spec.isRequiredError = function(element) {
        return spec.hasClasses(element, [validationErrorClass, 'ng-invalid-required']);
    };

    spec.getValue = function(element) {
        return element.getAttribute("value");
    };

    spec.sendKeys = function(element, input) {
        element.clear().sendKeys(input);
    };

    var that = {};

    function capitaliseFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }
    function definedLocalizedAccessors(fieldBaseName, lang) {
        var name = fieldBaseName + lang;
        var caplitalizedName = capitaliseFirstLetter(fieldBaseName);

        spec[name] = spec.context ? spec.context.element(by.name(name)) : element(by.name(name));

        that["get" + caplitalizedName + lang] = function() {
            return spec.getValue(spec[name]);
        };
        that["set" + caplitalizedName + lang] = function(value) {
            spec.sendKeys(spec[name], value);
        };
    }
    spec.defineMultilingualAccessors = function(fieldBaseName) {
        definedLocalizedAccessors(fieldBaseName, "Fi");
        definedLocalizedAccessors(fieldBaseName, "Sv");
        definedLocalizedAccessors(fieldBaseName, "En");

        var caplitalizedName = capitaliseFirstLetter(fieldBaseName);
        that["set" + caplitalizedName] = function(value) {
            that["set" + caplitalizedName + "Fi"](value);
            that["set" + caplitalizedName + "Sv"](value);
            that["set" + caplitalizedName + "En"](value);
        }
    }

    that.isDisplayed = function() {
        return spec.view.then(
            function(elem) {
                return elem.isDisplayed();
            },
            function() {
                return false;
            }
        );
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

    that.hasNoValidationErrors = function() {
        return $('.' + validationErrorClass).isPresent().then(function(isValidationErrorPresent) {
            return !isValidationErrorPresent;
        });
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