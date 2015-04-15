// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.i18n', [
        'pascalprecht.translate'
    ]);

    m.config(function($translateProvider){
        $translateProvider.useStaticFilesLoader({
            prefix: 'assets/translations-',
            suffix: '.json'
        });
        $translateProvider.preferredLanguage('fi');
        $translateProvider.useMissingTranslationHandler("missingTranslation");
        $translateProvider.usePostCompiling(true);
    });

    m.factory('missingTranslation', function($log) {
        return function(translationId) {
            $log.warn("Missing translation for '" + translationId + "'");
        };
    });

})();