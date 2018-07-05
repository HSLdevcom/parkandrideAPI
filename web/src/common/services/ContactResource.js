// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.ContactResource', []);

    m.factory('ContactResource', function($http) {
        var api = {};

        api.listContacts = function(search) {
            return $http.get("api/v1/contacts", {
                params: search
            }).then(function(response) {
                return response.data;
            });
        };

        api.save = function(data) {
            var config = {"skipDefaultViolationsHandling": true};
            if (data.id) {
                return $http.put('api/v1/contacts/' + data.id, data, config).then(function(response) {
                    return response.data;
                });
            } else {
                return $http.post('api/v1/contacts', data, config).then(function(response) {
                    return response.data;
                });
            }
        };

        return api;
    });

})();