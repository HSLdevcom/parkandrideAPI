// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

"use strict";

var components = require('../components/components');
var _ = require('lodash');

module.exports = function (spec) {
    var that = require('../base')(spec);
    var uiSelect = components.uiSelect({});
    var datepicker = components.datepicker({});

    spec.view           = $('.wdReportsView');

    spec.heading        = $('.wdReportHeading');

    // Inputs
    spec.startDate      = $('.wdStartDate');
    spec.endDate        = $('.wdEndDate');
    spec.interval       = $('.wdInterval select');
    spec.operators      = $('.wdOperators .ui-select-multiple');
    spec.fixedOperator  = $('.wdOperators .wdFixedOperator');
    spec.usages         = $('.wdUsages .ui-select-multiple');
    spec.capacities     = $('.wdCapacityTypes .ui-select-multiple');
    spec.regions        = $('.wdRegions .ui-select-multiple');
    spec.hubs           = $('.wdHubs .ui-select-multiple');
    spec.facilities     = $('.wdFacilities .ui-select-multiple');

    spec.submitButton   = $('.wdReportsView button[type=submit]');

    that.get = function () {
        browser.get('/#/reports');
        return this;
    };

    that.generateReport = function() {
        spec.submitButton.click();
        return this;
    };

    that.checkOperatorFixedTo = function(fixedOperator) {
        expect(spec.fixedOperator.isDisplayed()).toBe(true);
        expect(spec.fixedOperator.getText()).toEqual(fixedOperator);
        expect(spec.operators.isDisplayed()).toBe(false);
        return this;
    };

    that.checkOperatorNotFixed = function() {
        expect(spec.fixedOperator.isDisplayed()).toBe(false);
        expect(spec.operators.isDisplayed()).toBe(true);
        return this;
    };

    function createUiSelect(elem, extend) {
        return _.assign(extend || {}, {
            getValue: function() {
                return uiSelect.getSetValue(elem);
            },
            checkEmpty: function() {
                expect(uiSelect.isSet(elem)).toBe(false);
            },
            getChoices: function() {
                return uiSelect.getValues(elem);
            },
            select: function(val) {
                uiSelect.select(elem, val);
            },
            getGreyedOutSelections: function() {
                return uiSelect.getGreyedOutSelections(elem);
            },
            clearSelections: function() {
                uiSelect.clearSelections(elem);
            }
        });
    }
    function createDateField(elem, extend) {
        return _.assign(extend || {}, {
            getValue: function() {
                return elem.getAttribute('value');
            },
            open: function() {
                elem.click();
                return this;
            },
            clickPreviousMonth: function() {
                datepicker.clickPreviousMonth(elem);
                return this;
            },
            clickNextMonth: function() {
                datepicker.clickNextMonth(elem);
                return this;
            },
            selectDate: function(date) {
                datepicker.clickDate(elem, date);
                return this;
            },
            verifyOpen: function() {
                datepicker.verifyOpen(elem);
                return this;
            },
            verifyClosed: function() {
                datepicker.verifyClosed(elem);
                return this;
            }
        });
    }

    that.interval = {
        getValue: function() {
            return spec.interval.$(':checked').getText();
        }
    };
    that.startDate  = createDateField(spec.startDate);
    that.endDate    = createDateField(spec.endDate);

    that.operators  = createUiSelect(spec.operators);
    that.capacities = createUiSelect(spec.capacities);
    that.usages     = createUiSelect(spec.usages);
    that.regions    = createUiSelect(spec.regions);
    that.hubs       = createUiSelect(spec.hubs);
    that.facilities = createUiSelect(spec.facilities);

    return that;
};