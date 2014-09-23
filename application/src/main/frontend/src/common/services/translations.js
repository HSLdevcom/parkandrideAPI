(function() {
    var m = angular.module('parkandride.services.translations', [
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