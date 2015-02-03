"use strict";

var components = require('../components/components');

module.exports = function (spec) {
    var that = require('../base')(spec);

    var col = {
        username: 0,
        operator: 1,
        role: 2,
        actions: 3
    };

    spec.view = $('.wdUsersView');
    spec.rows = $$('.userRow');
    spec.createUserButton = $$('.wdCreate').first();

    spec.row = function(idx) {
        return spec.rows.get(idx);
    };

    spec.getUsername = function(row) {
        return spec.row(row).$$('td').get(col.username).getText();
    };

    spec.getOperator = function(row) {
        return spec.row(row).$$('td').get(col.operator).getText();
    };

    spec.getRole = function(row) {
        return spec.row(row).$$('td').get(col.role).getText();
    };

    spec.deleteAction = function(row) {
        return spec.row(row).$$('td').get(col.actions).$('.wdDeleteUser');
    };

    that.userModal = require('./userModal')({});
    that.confirmModal = components.sweetAlert({});

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

    that.canBeDeleted = function(row) {
        return spec.isDisplayed(spec.deleteAction(row));
    };

    that.delete = function(row) {
        expect(that.canBeDeleted(row)).toBe(true);
        spec.deleteAction(row).click();
    };

    that.toCreateUser = function() {
        spec.createUserButton.click();
    };

    return that;
};