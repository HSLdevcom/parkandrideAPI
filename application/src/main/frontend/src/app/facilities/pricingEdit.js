(function() {
    var m = angular.module('parkandride.pricingEdit', [
        'parkandride.multilingual',
        'showErrors'
    ]);

    m.directive('pricingEdit', function (schema) {
        return {
            restrict: 'A',
            scope: {
                pricing: '=',
                selected: '='
            },
            templateUrl: 'facilities/pricingEdit.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.capacityTypes = schema.capacityTypes;
                scope.usages = schema.usages;
                scope.dayTypes = schema.dayTypes;
                scope.h24 = is24h();
                scope.free = isFree();

                scope.$watch("h24", function(newValue) {
                    var h24 = is24h();
                    if (newValue && !h24) {
                        scope.pricing.from = "00";
                        scope.pricing.until = "24";
                    } else if (h24) {
                        scope.h24 = true;
                    }
                });
                scope.$watchGroup(["pricing.from", "pricing.until"], function() {
                    scope.h24 = is24h();
                });

                scope.$watch("free", function(newValue) {
                    var free = isFree();
                    if (newValue && !free) {
                        scope.pricing.price = null;
                    } else if (free) {
                        scope.free = true;
                    }
                });
                scope.$watchGroup(["pricing.price.fi", "pricing.price.sv", "pricing.price.en"], function() {
                    scope.free = isFree();
                });

                function is24h() {
                    return (/^0?0(?::00)?$/).test(scope.pricing.from) && (/^24(?::00)?$/).test(scope.pricing.until);
                }
                function isFree() {
                    return !(scope.pricing.price && (scope.pricing.price.fi || scope.pricing.price.sv || scope.pricing.price.en));
                }
            }
        };
    });
})();