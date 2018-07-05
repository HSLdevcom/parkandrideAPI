'use strict';

module.exports = function(spec) {
    spec = spec || {};
    var that = require('../base')(spec);

    spec.view = $('#portEditModal');
    spec.entry = $('.portEntry');
    spec.exit = $('.portExit');
    spec.pedestrian = $('.portPedestrian');
    spec.bicycle = $('.portBicycle');
    spec.ok = element(by.id('wdPortOk'));
    spec.streetAddress = $('.streetAddress');
    spec.postalCode = $('.postalCode');
    spec.city = $('.city');
    spec.info = $('.portInfo');

    spec.context = spec.view;

    that.isEntrySelected = function() {
        return spec.isDisplayed(spec.entry);
    };
    that.isExitSelected = function() {
        return spec.isDisplayed(spec.exit);
    };
    that.isPedestrianSelected = function() {
        return spec.isDisplayed(spec.pedestrian);
    };
    that.isBicycleSelected = function() {
        return spec.isDisplayed(spec.bicycle);
    };
    that.getStreetAddress = function() {
        return spec.getMultilingualValues(spec.streetAddress);
    };
    that.getPostalCode = function() {
        return spec.postalCode.getText();
    };
    that.getCity = function() {
        return spec.getMultilingualValues(spec.city);
    };
    that.getInfo = function() {
        return spec.getMultilingualValues(spec.info);
    };

    that.ok = function() {
        spec.ok.click();
    };

    return that;
};