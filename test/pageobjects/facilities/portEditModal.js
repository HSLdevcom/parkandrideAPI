'use strict';

module.exports = function(spec) {
    spec = spec || {};
    var that = require('../base')(spec);

    spec.view = $('#portEditModal');
    spec.entry = element(by.model('port.entry'));
    spec.exit = element(by.model('port.exit'));
    spec.pedestrian = element(by.model('port.pedestrian'));
    spec.bicycle = element(by.model('port.bicycle'));
    spec.postalCode = spec.view.element(by.name('postalCode'));
    spec.cancel = element(by.id('wdPortCancel'));
    spec.remove = element(by.id('wdPortRemove'));
    spec.ok = element(by.id('wdPortOk'));

    spec.context = spec.view;
    spec.defineMultilingualAccessors("streetAddress");
    spec.defineMultilingualAccessors("city");
    spec.defineMultilingualAccessors("info");

    that.toggleEntry = function() {
        spec.entry.click();
    };
    that.toggleExit = function() {
        spec.exit.click();
    };
    that.togglePedestrian = function() {
        spec.pedestrian.click();
    };
    that.toggleBicycle= function() {
        spec.bicycle.click();
    };

    that.getPostalCode = function() {
        return spec.getValue(spec.postalCode);
    }
    that.setPostalCode = function(value) {
        spec.postalCode.sendKeys(value);
    };

    that.isEntrySelected = function() {
        return spec.entry.isSelected().then(function(selected) {
            return !!selected;
        });
    };
    that.isExitSelected = function() {
        return spec.exit.isSelected().then(function(selected) {
            return !!selected;
        });
    };
    that.isPedestrianSelected = function() {
        return spec.pedestrian.isSelected().then(function(selected) {
            return !!selected;
        });
    };
    that.isBicycleSelected = function() {
        return spec.bicycle.isSelected().then(function(selected) {
            return !!selected;
        });
    };

    that.ok = function() {
        spec.ok.click();
    };

    that.cancel = function() {
        spec.cancel.click();
    };

    that.remove = function() {
        spec.remove.click();
    };

    return that;
};