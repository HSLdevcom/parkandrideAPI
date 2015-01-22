(function() {
    var m = angular.module('parkandride.pricing', [
        'parkandride.multilingual',
        'showErrors'
    ]);

    m.value('PricingService', {
        is24h: function(pricing) {
            return pricing.time != null && (/^0?0(?::00)?$/).test(pricing.time.from) && (/^24(?::00)?$/).test(pricing.time.until);
        },
        isFree: function(pricing) {
            return !(pricing.price && (pricing.price.fi || pricing.price.sv || pricing.price.en));
        }
    });

    m.directive('pricingEdit', function (schema, $translate, PricingService) {

        return {
            restrict: 'A',
            scope: {
                pricing: '=',
                selections: '='
            },
            templateUrl: 'facilities/pricingEdit.tpl.html',
            transclude: false,
            link: function(scope) {
                var pricingId = scope.pricing._id;

                scope.capacityTypes = schema.capacityTypes.values;
                scope.usages = schema.usages.values;
                scope.dayTypes = schema.dayTypes.values;
                scope.h24 = is24h();
                scope.free = isFree();
                scope.rowSelected = scope.selections[pricingId];

                scope.$watch("h24", function(newValue) {
                    var h24 = is24h();
                    if (newValue && !h24) {
                        scope.pricing.time = {
                            from: "00",
                            until: "24"
                        };
                    } else if (h24) {
                        scope.h24 = true;
                    }
                });
                scope.$watchGroup(["pricing.time.from", "pricing.time.until"], function() {
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
                scope.$watch("selections[" + pricingId + "]", function() {
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