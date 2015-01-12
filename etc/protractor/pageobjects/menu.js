'use strict';

module.exports = function(spec) {
    var that = require('./base')(spec);

    spec.hubs = element.all(by.linkUiSref('hub-list')).first();
    spec.contacts = element.all(by.linkUiSref('contact-list')).first();
    spec.operators = element.all(by.linkUiSref('operator-list')).first();

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

    that.isHubsActive = function() {
        return spec.isActive(spec.hubs);
    };

    that.isContactsActive = function() {
        return spec.isActive(spec.contacts);
    };

    that.isOperatorsActive = function() {
        return spec.isActive(spec.operators);
    };

    return that;
};