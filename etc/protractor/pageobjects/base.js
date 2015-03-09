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
        var value = element.getAttribute("value");
        return value || element.getText();
    };

    spec.sendKeys = function(element, input) {
        element.clear().sendKeys("" + input);
    };

    spec.isDisplayed = function(element) {
        return element.then(
            function(elem) { return elem.isDisplayed(); },
            function() { return false; }
        );
    };

    // workaround to ensure element is present before invoking actions on it, this might be useful for elements residing inside non-angular components e.g.
    // sweetAlert.
    spec.ensureIsPresent = function(element) {
        browser.wait(function() {
            return element.then(function(elem) { return elem.isPresent(); });
        });
        return element;
    };

    spec.parent = function(element) {
        return element.element(by.xpath('..'));
    };

    var that = {};

    function capitaliseFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }
    function definedLocalizedAccessors(fieldBaseName, lang) {
        var name = fieldBaseName + lang;
        var capitalisedName = capitaliseFirstLetter(fieldBaseName);

        spec[name] = spec.context ? spec.context.element(by.name(name)) : element(by.name(name));

        that["get" + capitalisedName + lang] = function() {
            return spec.getValue(spec[name]);
        };
        that["set" + capitalisedName + lang] = function(value) {
            spec.sendKeys(spec[name], value);
        };
    }
    spec.defineMultilingualAccessors = function(fieldBaseName) {
        definedLocalizedAccessors(fieldBaseName, "Fi");
        definedLocalizedAccessors(fieldBaseName, "Sv");
        definedLocalizedAccessors(fieldBaseName, "En");

        var capitalisedName = capitaliseFirstLetter(fieldBaseName);
        that["set" + capitalisedName] = function(value) {
            if (typeof value === 'string') {
                value = [value, value, value];
            }
            that["set" + capitalisedName + "Fi"](value[0]);
            that["set" + capitalisedName + "Sv"](value[1]);
            that["set" + capitalisedName + "En"](value[2]);
        };
        that["get" + capitalisedName] = function(value) {
            return protractor.promise.all([
                that["get" + capitalisedName + "Fi"](),
                that["get" + capitalisedName + "Sv"](),
                that["get" + capitalisedName + "En"]()
            ]);
        };
    };

    spec.getMultilingualValues = function(parentElement) {
        return protractor.promise.all([
            parentElement.element(by.css(".lang-fi")).getText(),
            parentElement.element(by.css(".lang-sv")).getText(),
            parentElement.element(by.css(".lang-en")).getText()
            ]);
    };

    spec.select = function(element, name) {
        element.element(by.css('.ui-select-toggle')).click();
        element = browser.driver.switchTo().activeElement();
        element.sendKeys(name);
        element.sendKeys(protractor.Key.ENTER);
    };

    that.isDisplayed = function() {
        return spec.isDisplayed(spec.view);
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

    that.waitUntilAbsent = function() {
        expect(spec.view.waitAbsent()).toBe(true);
    };

    return that;
};