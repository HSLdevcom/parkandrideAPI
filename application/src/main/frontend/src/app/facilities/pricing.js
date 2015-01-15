(function() {
    var m = angular.module('parkandride.pricing', [
        'parkandride.multilingual',
        'showErrors'
    ]);

    m.value('PricingService', {
        is24h: function(pricing) {
            return (/^0?0(?::00)?$/).test(pricing.from) && (/^24(?::00)?$/).test(pricing.until);
        },
        isFree: function(pricing) {
            return !(pricing.price && (pricing.price.fi || pricing.price.sv || pricing.price.en));
        }
    });

    m.directive('pricingView', function (schema, $translate, PricingService) {
        return {
            restrict: 'A',
            scope: {
                pricing: '='
            },
            templateUrl: 'facilities/pricingView.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.is24h = function() {
                    return PricingService.is24h(scope.pricing);
                };
                scope.isFree = function() {
                    var free = PricingService.isFree(scope.pricing);
                    console.log("isFree: " + free);
                    return free;
                };
            }
        };
    });

    m.directive('pricingEdit', function (schema, $translate, PricingService) {

        function translatedEnumValues(prefix, values) {
            return _.map(values, function(v) {
                return {
                    id: v,
                    label: $translate.instant(prefix + "." + v + ".label")
                };
            });
        }

        return {
            restrict: 'A',
            scope: {
                pricing: '=',
                selections: '='
            },
            templateUrl: 'facilities/pricingEdit.tpl.html',
            transclude: false,
            link: function(scope) {
                var pricingId = "_" + scope.pricing._id;

                scope.capacityTypes = translatedEnumValues("capacity-types", schema.capacityTypes);
                scope.usages = translatedEnumValues("usages", schema.usages);
                scope.dayTypes = translatedEnumValues("day-types", schema.dayTypes);
                scope.h24 = is24h();
                scope.free = isFree();
                scope.rowSelected = scope.selections[pricingId];

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
                scope.$watch("rowSelected", function(value) {
                    if (scope.rowSelected != scope.selections[pricingId]) {
                        scope.selections[pricingId] = scope.rowSelected;
                        if (scope.rowSelected) {
                            scope.selections.count++;
                        } else {
                            scope.selections.count--;
                        }
                    }
                });
                scope.$watch("selections." + pricingId , function() {
                    scope.rowSelected = scope.selections[pricingId];
                });

                function is24h() {
                    return PricingService.is24h(scope.pricing);
                }
                function isFree() {
                    return PricingService.isFree(scope.pricing);
                }
            }
        };
    });
})();