"use strict";

module.exports = function () {
    var _ = require('lodash');
    var request = require('request');
    var devApiUrl = browser.baseUrl + '/dev-api',
        facilitiesUrl = devApiUrl + '/facilities',
        hubsUrl = devApiUrl + '/hubs',
        contactsUrl = devApiUrl + '/contacts',
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

    api.resetFacilities = function(facilities, contacts) {
        api.deleteFacilities();
        if (contacts) {
            api.resetContacts(contacts);
        }
        if (facilities) {
            api.insertFacilities(facilities);
        }
    };

    api.resetHubs = function(hubs) {
        flow.execute(function() { return asPromise({ method: 'DELETE', url: hubsUrl }); });
        if (hubs) {
            flow.execute(function(){ return asPromise({ method: 'PUT', url: hubsUrl, json: true, body: asPayload(hubs) }); });
        }
    };

    api.resetContacts = function(contacts) {
        flow.execute(function() { return asPromise({ method: 'DELETE', url: contactsUrl }); });
        if (contacts) {
            flow.execute(function(){return asPromise({ method: 'PUT', url: contactsUrl, json: true, body: asPayload(contacts) })});
        }
    };

    api.loginAs = function(role, username, password) {
        browser.get('/');
        var newUser = {
            username: username || "testuser",
            password: password || "password",
            role: role
        };
        flow.execute(function() {
            asPromise({ method: 'POST', url: loginUrl, json: true, body: newUser })
            .then(function(response) {
                    var login = response.body;
                    var script =
                        "sessionStorage.authToken='"+login.token+"';\n" +
                        "sessionStorage.authUsername='"+login.username+"';\n" +
                        "sessionStorage.authRole='"+login.role+"';\n";
                    return browser.driver.executeScript(script);
            });
        });
    };

    api.resetAll = function(facilities, hubs, contacts) {
        // Contacts cannot be deleted if there's facilities refering them
        api.deleteFacilities();
        // Facilities may refer to contacts, so insert contacts first
        api.resetContacts(contacts)
        if (facilities) {
            api.insertFacilities(facilities);
        }
        api.resetHubs(hubs);
    };

    return api;
};