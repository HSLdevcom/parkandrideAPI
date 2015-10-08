// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('httpInterceptor', []);

    m.constant('httpInterceptorHeader', {
        name: 'X-HSL-Source',
        value: '#webkäli'
    });

    m.config(function($httpProvider, httpInterceptorHeader) {
        $httpProvider.interceptors.push(function() {
           return {
               request: function(config) {
                   config.headers[httpInterceptorHeader.name] = httpInterceptorHeader.value;
                   return config;
               }
           };
        });
    });
})();
