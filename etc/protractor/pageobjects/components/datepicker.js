// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

"use strict";

module.exports = function (spec) {
    spec = spec || {};

    var _ = require('lodash');
    var that = require('../base')(spec);

    spec.headingText = function(elem) {
        return spec.datepickerPopup(elem).then(function(elem) {
            return elem.element(by.css('[role=heading]')).getText();
        });
    };

    spec.datepickerPopup = function (elem) {
        var deferred = protractor.promise.defer();
        elem.element(by.xpath('following-sibling::*[1]')).then(function (elem) {
            elem.getTagName().then(function (tagName) {
                if ('ul' === tagName.toLowerCase()) {
                    deferred.fulfill(elem);
                } else {
                    deferred.reject(new Error('Next sibling was not an UL element, instead found: ' + tagName))
                }
            });
        }, function () {
            deferred.reject(new Error('No sibling element found'));
        });
        return deferred.promise;
    };

    function returns(val) { return function() { return val; }}

    that.isOpen = function (elem) {
        return spec.datepickerPopup(elem).then(returns(true), returns(false));
    };

    that.verifyOpen = function(elem) {
        expect(that.isOpen(elem)).toEqualBecause(true, 'datepicker should be open');
        return this;
    };

    that.verifyClosed = function(elem) {
        expect(that.isOpen(elem)).toEqualBecause(true, 'datepicker should be closed');
        return this;
    };

    that.clickDate = function(elem, date) {
        spec.datepickerPopup(elem).then(function(popup) {
            // :not(.text-muted) prevents selecting from the previous month
            popup.element(by.cssContainingText('td > button > span:not(.text-muted)', date))
                .click();
        });
    };


    // CHANGING MONTHS
    spec.changeMonthByClick = function(elem, selector) {
        var deferred = protractor.promise.defer();

        // We must wait for the heading to change to ensure
        // that the datepicker has responded correctly
        spec.datepickerPopup(elem).then(function (popup) {
            var previousHeading;
            spec.headingText(elem).then(function(v) { previousHeading = v; });

            popup.element(selector).click();

            browser.wait(function() {
                // Wait until the heading has changed
                return spec.headingText(elem).then(function(text) {
                    return text !== previousHeading;
                });
            });
            deferred.fulfill();
        });
        return deferred.promise;
    };

    that.clickPreviousMonth = function(elem) {
        return spec.changeMonthByClick(elem, by.css('.glyphicon-chevron-left'));
    };

    that.clickNextMonth = function(elem) {
        return spec.changeMonthByClick(elem, by.css('.glyphicon-chevron-right'));
    };

    return that;
};