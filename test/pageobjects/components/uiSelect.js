"use strict";

module.exports = function (spec) {
    spec = spec || {};

    var _ = require('lodash');
    var that = require('../base')(spec);

    spec.isMultipleSelect = function(element) {
        return element.getAttribute('class').then(function(classes) {
            if (classes.split(' ').indexOf('ui-select-multiple') === -1) {
                throw Error('not multiple select');
            }
            return element;
        });
    };

    /** This method only applicable for single selects */
    spec.placeholder = function(element) {
        return element.$('.ui-select-match .text-muted');
    };

    /** This method only applicable for single selects */
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
        spec.isMultipleSelect(element).then(
            function revealMultiple() { element.$('input').click(); },
            function revealSingle() { element.$('.ui-select-toggle').click(); }
        );
    };

    /** This method only applicable for multiple selects */
    spec.getSelectedChoices = function(element) {
        return element.$$('.ui-select-match-item [uis-transclude-append]').then(function(selections) {
            return protractor.promise.all(selections.map(function(el) {
                return el.getText();
            }));
        });
    };

    /** This method only applicable for multiple selects on reports page */
    that.getGreyedOutSelections = function(element) {
        return element.$$('.ui-select-match-item .ui-select--not-available').then(function(selections) {
            return protractor.promise.all(selections.map(function(el) {
                return el.getText();
            }));
        });
    };

    /** This method only applicable for multiple selects */
    that.clearSelections = function(element) {
        element.$$('.ui-select-match-close').click();
    };

    that.select = function(element, value) {
        spec.revealChoices(element);
        browser.driver.switchTo().activeElement().sendKeys(value, protractor.Key.ENTER);
    };

    that.isSet = function(element) {
        var negate = function (val) { return !val; };
        var notEmpty = function (choices) { return choices.length > 0; };
        return spec.isMultipleSelect(element).then(
            function multiSelect() {
                return spec.getSelectedChoices(element).then(notEmpty);
            },
            function noMultiSelect() {
                return spec.isDisplayed(spec.placeholder(element)).then(negate);
            }
        );
    };

    that.getSetValue = function(element) {
        expect(that.isSet(element)).toBe(true);
        return spec.isMultipleSelect(element)
            .then(spec.getSelectedChoices, spec.selectedValue);
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