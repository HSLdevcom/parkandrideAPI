// Copyright © 2017 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function () {
    var m = angular.module('parkandride.about-en', []);

    m.config(function config($stateProvider) {
        $stateProvider.state('about-en-page', {
            parent: 'abouttab',
            url: '/about-en',
            views: {
                "main": {
                    templateUrl: 'about/about-en.tpl.html'
                }
            },
            data: {pageTitle: 'About'}
        });
    });

})();
