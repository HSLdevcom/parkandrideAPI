(function() {
    var m = angular.module('parkandride.PaymentMethodResource', []);

    m.factory('PaymentMethodResource', function($http) {
        var api = {};

        api.listPaymentMethods = function() {
            return $http.get("api/v1/payment-methods").then(function(response) { return response.data; });
        };

        return api;
    });
})();