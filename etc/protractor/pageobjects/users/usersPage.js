"use strict";

module.exports = function (spec) {
    var that = require('../base')(spec);

    spec.view = $('.wdUsersView');
    spec.rows = $$('.userRow');
    spec.createUserButton = $$('.wdCreate').first();

    spec.row = function(idx) {
        return spec.rows.get(idx);
    };

    spec.getUsername = function(row) {
        return spec.row(row).$$('td').get(0).getText();
    };

    spec.getOperator = function(row) {
        return spec.row(row).$$('td').get(1).getText();
    };

    spec.getRole = function(row) {
        return spec.row(row).$$('td').get(2).getText();
    };

    that.userModal = require('./userModal')({});

    that.get = function () {
        browser.get('/#/users');
    };

    that.getUsers = function() {
        return spec.rows.then(function(rows) {
            var results = [];
            for (var i=0; i < rows.length; i++) {

                var username = spec.getUsername(i);
                var operator = spec.getOperator(i);
                var role = spec.getRole(i);

                results.push(protractor.promise.all([username, operator, role]).then(function(resolved) {
                    return { username: resolved[0], operator: resolved[1], role: resolved[2] };
                }));
            }
            return protractor.promise.all(results);
        });
    };

    that.toCreateUser = function() {
        spec.createUserButton.click();
    };

    return that;
};