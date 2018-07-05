// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

"use strict";

module.exports = function () {
    var _ = require('lodash');
    var request = require('request');
    var devApiUrl = browser.baseUrl + '/dev-api',
        facilitiesUrl = devApiUrl + '/facilities',
        hubsUrl = devApiUrl + '/hubs',
        contactsUrl = devApiUrl + '/contacts',
        operatorsUrl = devApiUrl + '/operators',
        usersUrl = devApiUrl + '/users',
        loginUrl = devApiUrl + "/login",
        triggerPredictionsUrl = devApiUrl + '/prediction';
    var flow = protractor.promise.controlFlow();

    var api = {};

    function asPromise(options) {
        var defer = protractor.promise.defer();
        request(options, function(error, message) {
            if (error || message.statusCode >= 400) {
                console.error("backend error: ", error || message.body);
                defer.reject({
                    error: error,
                    message: message
                });
            } else {
                defer.fulfill(message);
            }
        });
        return defer.promise;
    }

    function asPayload(coll) {
        return _.map(coll, function(item) { return item.toPayload(); });
    }

    function execute(options) {
        return flow.execute(function () { return asPromise(options); });
    }

    api.deleteFacilities = function() {
        execute({method: 'DELETE', url: facilitiesUrl});
    };

    api.insertFacilities = function(facilities) {
        execute({method: 'PUT', url: facilitiesUrl, json: true, body: asPayload(facilities)});
    };

    api.deleteHubs = function() {
        execute({method: 'DELETE', url: hubsUrl});
    };

    api.insertHubs = function(hubs) {
        if (hubs) {
            execute({method: 'PUT', url: hubsUrl, json: true, body: asPayload(hubs)});
        }
    };

    api.deleteContacts = function() {
        execute({method: 'DELETE', url: contactsUrl});
    };

    api.insertContacts = function(contacts) {
        if (contacts) {
            execute({method: 'PUT', url: contactsUrl, json: true, body: asPayload(contacts)});
        }
    };

    api.deleteOperators = function() {
        execute({method: 'DELETE', url: operatorsUrl});
    };

    api.insertOperators= function(operators) {
        if (operators) {
                var options = {method: 'PUT', url: operatorsUrl, json: true, body: asPayload(operators)};
            execute(options);
        }
    };

    api.deleteUsers = function() {
        execute({method: 'DELETE', url: usersUrl});
    };

    api.insertUsers = function(users) {
        if (users) {
            execute({ method: 'PUT', url: usersUrl, json: true, body: asPayload(users) });
        }
    };

    api.createLogin = function(role, username, password, operatorId) {
        var newUser = {
            username: username || "testuser",
            password: password || "password",
            role: role,
            operatorId: operatorId
        };
        return execute({method: 'POST', url: loginUrl, json: true, body: newUser});
    };

    api.loginAs = function(role, username, password, operatorId) {
        function storeAuthToSessionStorage(response) {
            browser.get('/');
            var login = response.body;
            var script = "angular.element(document.body).injector().get('Session').set("+ JSON.stringify(login) + ");\n";
            //console.log(script);
            return browser.executeScript(script);
        }
        api.createLogin(role, username, password, operatorId).then(storeAuthToSessionStorage);
    };

    api.logout = function() {
        browser.executeAsyncScript(function(callback) {
            angular.element(document.body).injector().get('Session').remove();
            callback();
        });
    };

    api.fakeUtilization = function(facilityId) {
        return execute({ method: 'PUT', url: facilitiesUrl + '/' + facilityId + '/utilization'})
    };
    api.triggerPredictions = function() {
        return execute({ method: 'PUT', url: triggerPredictionsUrl });
    };

    api.resetAll = function(data) {
        data = data || {};

        api.deleteHubs();
        api.deleteFacilities();
        api.deleteContacts();
        api.deleteUsers();
        api.deleteOperators();

        api.insertOperators(data.operators);
        api.insertUsers(data.users);
        api.insertContacts(data.contacts);
        api.insertFacilities(data.facilities);
        api.insertHubs(data.hubs);
    };

    return api;
};