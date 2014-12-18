"use strict";

module.exports = function () {
    var _ = require('lodash');
    var request = require('request');
    var devApiUrl = browser.baseUrl + '/dev-api',
        facilitiesUrl = devApiUrl + '/facilities',
        hubsUrl = devApiUrl + '/hubs',
        contactsUrl = devApiUrl + '/contacts',
        operatorsUrl = devApiUrl + '/operators',
        loginUrl = devApiUrl + "/login";
    var flow = protractor.promise.controlFlow();

    var api = {};

    function asPromise(options) {
        var defer = protractor.promise.defer();
        request(options, function(error, message) {
            if (error || message.statusCode >= 400) {
                console.error("backend error: ", message.body);
                defer.reject({
                    error : error,
                    message : message
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

    api.deleteFacilities = function() {
        flow.execute(function() { return asPromise({ method: 'DELETE', url: facilitiesUrl }); });
    };

    api.insertFacilities = function(facilities) {
        flow.execute(function() { return asPromise({ method: 'PUT', url: facilitiesUrl, json: true, body: asPayload(facilities) }); });
    };

    api.deleteHubs = function() {
        flow.execute(function() { return asPromise({ method: 'DELETE', url: hubsUrl }); });
    };

    api.insertHubs = function(hubs) {
        if (hubs) {
            flow.execute(function(){ return asPromise({ method: 'PUT', url: hubsUrl, json: true, body: asPayload(hubs) }); });
        }
    };

    api.deleteContacts = function() {
        flow.execute(function() { return asPromise({ method: 'DELETE', url: contactsUrl }); });
    };

    api.insertContacts = function(contacts) {
        if (contacts) {
            flow.execute(function(){return asPromise({ method: 'PUT', url: contactsUrl, json: true, body: asPayload(contacts) })});
        }
    };

    api.deleteOperators = function() {
        flow.execute(function() { return asPromise({ method: 'DELETE', url: operatorsUrl }); });
    };

    api.insertOperators= function(operators) {
        if (operators) {
            flow.execute(function(){return asPromise({ method: 'PUT', url: operatorsUrl, json: true, body: asPayload(operators) })});
        }
    };

    api.createLogin = function(role, username, password) {
        var newUser = {
            username: username || "testuser",
            password: password || "password",
            role: role
        };
        return flow.execute(function() { return asPromise({ method: 'POST', url: loginUrl, json: true, body: newUser }) });
    };

    api.loginAs = function(role, username, password) {
        function storeAuthToSessionStorage(response) {
            browser.get('/');
            var login = response.body;
            var script =
                "sessionStorage.authToken='"+login.token+"';\n" +
                "sessionStorage.authUsername='"+login.username+"';\n" +
                "sessionStorage.authRole='"+login.role+"';\n;";
            return browser.executeScript(script);
        }
        api.createLogin(role, username, password).then(storeAuthToSessionStorage);
    };

    api.resetAll = function(data) {
        data = data || {};

        api.deleteHubs();
        api.deleteFacilities();
        api.deleteContacts();
        api.deleteOperators();

        api.insertOperators(data.operators);
        api.insertContacts(data.contacts);
        api.insertFacilities(data.facilities);
        api.insertHubs(data.hubs);
    };

    return api;
};