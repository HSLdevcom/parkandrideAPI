'use strict';

module.exports = function(spec) {
    var that = require('./base')(spec);

    spec.hubs = element.all(by.linkUiSref('hub-list')).first();
    spec.contacts = element.all(by.linkUiSref('contact-list')).first();
    spec.operators = element.all(by.linkUiSref('operator-list')).first();
    spec.users = element.all(by.linkUiSref('user-list')).first();


    spec.loginLink = $("#openLoginPrompt");
    spec.logoutLink = $("#logout");


    spec.isActive = function(link) {
        return spec.hasClasses(spec.parent(link), ['active']);
    };

    that.toHubs = function () {
        return spec.hubs.click();
    };

    that.toContacts = function () {
        return spec.contacts.click();
    };

    that.toOperators = function () {
        return spec.operators.click();
    };

    that.toUsers = function () {
        return spec.users.click();
    };

    that.isHubsActive = function() {
        return spec.isActive(spec.hubs);
    };

    that.isContactsActive = function() {
        return spec.isActive(spec.contacts);
    };

    that.isOperatorsActive = function() {
        return spec.isActive(spec.operators);
    };

    that.isUsersActive = function() {
        return spec.isActive(spec.users);
    };

    that.openLoginModal = function() {
        expect(that.canLogin()).toBe(true);
        spec.loginLink.click();
    };

    that.logout = function() {
        expect(that.canLogout()).toBe(true);
        spec.logoutLink.click();
    };

    that.canLogout = function() {
        return spec.isDisplayed(spec.logoutLink);
    };

    that.canLogin = function() {
        return spec.isDisplayed(spec.loginLink);
    };

    return that;
};