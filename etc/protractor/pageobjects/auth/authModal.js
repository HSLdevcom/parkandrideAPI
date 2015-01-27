'use strict';

module.exports = function(spec) {
    spec = spec || {};
    var that = require('../base')(spec);

    spec.view = $("#loginModal");
    spec.context = spec.view;
    spec.username = element(by.model('credentials.username'));
    spec.password = element(by.model('credentials.password'));
    spec.doLogin = $("#doLogin");
    spec.loginError = $("#loginError");
    spec.cancel = $("#loginModal .wdCancel");

    that.login = function(username, password) {
        spec.username.sendKeys(username);
        spec.password.sendKeys(password);
        spec.doLogin.click();
    };

    that.cancel = function() {
        spec.cancel.click();
    };

    that.isLoginError = function() {
        return spec.isDisplayed(spec.loginError);
    };

    that.getUsername = function() {
        return spec.getValue(spec.username);
    };

    return that;
};