'use strict';

module.exports = function(spec) {
    spec = spec || {};
    var that = require('../base')(spec);

    that.operatorEditModal = require('../operators/operatorEditModal')({});

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

    spec.operator = spec.view.element(by.name('operator'));
    spec.createOperator = spec.view.element(by.css('.operator .createOperator'));
    spec.selectedOperator = spec.view.element(by.css('.operator .ui-select-match'));

    that.setPhone = function(phone) {
        spec.sendKeys(spec.phone, phone);
    };

    that.getPhone = function() {
        return spec.getValue(spec.phone);
    };

    that.setEmail = function(email) {
        spec.sendKeys(spec.email, email);
    };

    that.getEmail = function() {
        return spec.getValue(spec.email);
    };

    that.setPostalCode = function(postalCode) {
        spec.sendKeys(spec.postalCode, postalCode);
    };

    that.getPostalCode = function() {
        return spec.getValue(spec.postalCode);
    };

    that.save = function() {
        spec.ok.click();
        browser.waitForAngular();
    };

    that.cancel = function() {
        spec.cancel.click();
        browser.waitForAngular();
    };

    that.createOperator = function(name) {
        spec.createOperator.click();
        that.operatorEditModal.setName(name);
        that.operatorEditModal.save();
    };

    that.selectOperator = function(name) {
        spec.operator.element(by.css('.ui-select-match')).click();
        var operatorElement = browser.driver.switchTo().activeElement();
        operatorElement.sendKeys(name);
        operatorElement.sendKeys(protractor.Key.ENTER);
    };

    that.getOperator = function() {
        return spec.selectedOperator.getText();
    };

    return that;
};