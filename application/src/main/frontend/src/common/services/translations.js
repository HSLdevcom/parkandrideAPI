(function() {
    var m = angular.module('parkandride.services.translations', [
        'pascalprecht.translate'
    ]);

    var translationsFI = {
        "facilities": {
            "title": "Fasiliteetit",
            "list" : {
              "name": "Nimi",
              "types": "Tyypit"
            },
            "action" : {
                "new" : "Lisää uusi fasiliteetti"
            },
            "common" : {
                "capacity" : {
                    "CAR" : "Henkilöauto",
                    "PARK_AND_RIDE": "Liityntäpysäköinti",
                    "BICYCLE" : "Polkupyörä"
                }
            }
        }
    };

    m.config(function($translateProvider){
        $translateProvider.translations('fi', translationsFI);

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