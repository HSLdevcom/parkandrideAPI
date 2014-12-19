'use strict';

module.exports = function(spec) {
    spec = spec || {};
    var that = require('../base')(spec);

    spec.view = $("#loginModal");
    spec.context = spec.view;
    spec.openLoginButton = $("#openLoginPrompt");
    spec.logout = $("#logout");
    spec.username = element(by.model('credentials.username'));
    spec.password = element(by.model('credentials.password'));
    spec.doLogin = $("#doLogin");
    spec.loginError = $("#loginError");

    that.openLoginModal = function() {
        spec.openLoginButton.click();
    };

    that.login = function(username, password) {
        spec.username.sendKeys(username);
        spec.password.sendKeys(password);
        spec.doLogin.click();
    };

    that.logout = function() {
        spec.logout.click();
    };

    that.isLoginError = function() {
        return spec.isDisplayed(spec.loginError);
    };

    that.getUsername = function() {
        return spec.getValue(spec.username);
    };

    that.isLogoutDisplayed = function() {
        return spec.isDisplayed(spec.logout);
    };

    that.isLoginDisplayed = function() {
        return spec.isDisplayed(spec.openLoginButton);
    };

    return that;
};