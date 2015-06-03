// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function () {
    var m = angular.module('parkandride.about', []);

    m.config(function config($stateProvider) {
        $stateProvider.state('about-page', {
            parent: 'abouttab',
            url: '/about',
            views: {
                "main": {
                    templateUrl: 'about/about.tpl.html'
                }
            },
            data: {pageTitle: 'About'}
        });
    });

})();
