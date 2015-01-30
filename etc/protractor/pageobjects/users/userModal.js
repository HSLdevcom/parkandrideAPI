"use strict";

module.exports = function (spec) {
    var that = require('../base')(spec);

    spec.view = $('#userModal');
    spec.username = $('input[name="username"]');
    spec.password = $('input[name="password"]');
    spec.role = $('div[name="role"]');
    spec.operator = $('div[name="operator"]');
    spec.ok = $("#wdUserModalOk");
    spec.cancel = $("#wdUserModalCancel");

    that.canSetUsername = function() {
        return spec.isDisplayed(spec.username);
    };

    that.setUsername = function(input) {
        spec.username.sendKeys(input);
    };

    that.canSetPassword = function () {
        return spec.isDisplayed(spec.password);
    };

    that.setPassword = function (input) {
        spec.password.sendKeys(input);
    };

    that.getRoles = function() {
        spec.role.click();
        return spec.role.$$('.ui-select-choices-row a').getText();
    };

    that.canSetRole = function() {
        return spec.isDisplayed(spec.role);
    };

    that.setRole = function(input) {
        spec.select(spec.role, input);
    };

    that.getOperators = function() {
        spec.operator.click();
        return spec.operator.$$('.ui-select-choices-row a').getText();
    };



    that.canSetOperator = function() {
        return spec.isDisplayed(spec.operator);
    };

    that.save = function() {
        spec.ok.click();
    };

    that.cancel = function() {
        spec.cancel.click();
    };

    return that;
};