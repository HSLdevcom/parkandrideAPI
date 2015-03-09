"use strict";

module.exports = function (spec) {
    spec = spec || {};

    var _ = require('lodash');
    var that = require('../base')(spec);

    spec.placeholder = function(element) {
        return element.$('.ui-select-match .text-muted');
    };

    spec.selectedValue = function(element) {
        return element.$('.ui-select-match .ng-scope').getText();
    };

    spec.getChoices = function(element) {
        spec.revealChoices(element);
        return element.$$('.ui-select-choices-row a').getText().then(function(arr){
            return _.without(arr, '');
        });
    };

    spec.revealChoices = function(element) {
        element.$('.ui-select-toggle').click();
    };

    that.select = function(element, value) {
        spec.revealChoices(element);
        browser.driver.switchTo().activeElement().sendKeys(value, protractor.Key.ENTER);
    };

    that.isSet = function(element) {
        return spec.isDisplayed(spec.placeholder(element)).then(function(isPlaceholderDisplayed) {
            return !isPlaceholderDisplayed;
        });
    };

    that.getSetValue = function(element) {
        expect(that.isSet(element)).toBe(true);
        return spec.selectedValue(element);
    };

    that.getValues = function(element) {
        return that.isSet(element).then(function(isSet) {
            var values = [];
            if (isSet) {
                values.push(spec.selectedValue(element));
            }
            values.push(spec.getChoices(element));

            return protractor.promise.all(values).then(function(arr) {
                return _.flatten(arr);
            });
        });
    };

    return that;
};