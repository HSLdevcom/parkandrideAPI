// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.date', [
        'ui.bootstrap.datepicker'
    ]);

    m.constant('dateInputConfig', {
        format: 'd.M.yyyy'
    });

    m.config(function(datepickerConfig, datepickerPopupConfig, dateInputConfig) {
        _.extend(datepickerConfig, {
            //focusOnOpen: true,
            startingDay: 1, // Monday
            minMode: 'day',
            maxMode: 'day'
        });

        _.extend(datepickerPopupConfig, {
            showButtonBar: false,
            datepickerPopup: dateInputConfig.format
        });
    });

    /**
     * Formats and parses dates
     */
    m.directive('dateInput', function(dateFilter, dateInputConfig, dateParser) {
        return {
            require: 'ngModel',
            priority: 2,
            link: function(scope, elem, attr, ngModel) {
                function parseDate(val) {
                    return dateParser.parse(val, dateInputConfig.format);
                }
                // Replace the formatters and parsers of datepicker-popup for good
                ngModel.$formatters = [function(val) {
                    return dateFilter(val, dateInputConfig.format);
                }];
                ngModel.$parsers = [function(val) {
                    return parseDate(val);
                }];

                ngModel.$validators.date = function(modelValue, viewValue) {
                    return !!parseDate(viewValue);
                };
            }
        };
    });
})();
