'use strict';

module.exports = function(spec) {
    spec = spec || {};
    var that = require('../base')(spec);

    spec.view = $("#contactEditModal");
    spec.phone = element(by.model('contact.phone'));
    spec.email = element(by.model('contact.email'));
    spec.postalCode = element(by.model('address.postalCode'));
    spec.ok = $("#wdContactOk");
    spec.cancel = $("#wdContactCancel");

    spec.context = spec.view;
    spec.defineMultilingualAccessors("name");
    spec.defineMultilingualAccessors("streetAddress");
    spec.defineMultilingualAccessors("city");
    spec.defineMultilingualAccessors("openingHours");
    spec.defineMultilingualAccessors("info");

    that.setPhone = function(phone) {
        spec.sendKeys(spec.phone, phone);
    }
    that.getPhone = function() {
        return spec.getValue(spec.phone);
    }

    that.setEmail = function(email) {
        spec.sendKeys(spec.email, email);
    }
    that.getEmail = function() {
        return spec.getValue(spec.email);
    }

    that.setPostalCode = function(postalCode) {
        spec.sendKeys(spec.postalCode, postalCode);
    }
    that.getPostalCode = function() {
        return spec.getValue(spec.postalCode);
    }

    that.save = function() {
        spec.ok.click();
    }

    that.cancel = function() {
        spec.cancel.click();
    };

    return that;
};