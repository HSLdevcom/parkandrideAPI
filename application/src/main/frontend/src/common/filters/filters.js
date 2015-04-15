// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

angular.module('filters', [])
    .filter('joinBy', function () {
        return function (input,delimiter) {
            return (input || []).join(delimiter || ',');
        };
    });
