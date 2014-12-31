'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')({});
    var portView = require('./portViewModal')();

    spec.view = $('.wdFacilityView');
    spec.name = $('.wdFacilityNameFi');
    spec.aliases = $('.wdAliases');
    spec.services = $('.wdService');
    spec.servicesBlock = $('.wdServices');
    spec.aliasesBlock = $('.wdAliasesBlock');
    spec.map = $('.facility-map .ol-viewport');
    spec.editViewButton = $$('.wdEditViewButton').first();
    spec.toListButton = element.all(by.linkUiSref('hub-list')).first();

    spec.paymentInfo = $('.wdPaymentInfo');
    spec.parkAndRideAuthRequired = $('.wdPaymentInfo .wdParkAndRideAuthRequired');
    spec.paymentMethods = $('.wdPaymentInfo .wdPaymentMethodNames');
    spec.paymentInfoDetails = $('.wdPaymentInfo .wdDetails');
    spec.paymentInfoDetail = $('.wdPaymentInfo .wdDetail');
    spec.paymentInfoUrl = $('.wdPaymentInfo .wdUrl');

    that.get = function (id) {
        browser.get('/#/facilities/view/' + id);
    };

    that.getName = function () {
        return spec.name.getText();
    };

    that.getAliases = function () {
        return spec.aliases.getText().then(function(joined){
            return joined.split(", ");
        });
    };

    that.getServices = function() {
        return spec.services.getText();
    };

    that.isAliasesDisplayed = function () {
        return spec.aliasesBlock.isDisplayed();
    };

    that.isServicesDisplayed = function () {
        return spec.isDisplayed(spec.servicesBlock);
    };

    that.openPortAt = function(x, y) {
        browser.actions().mouseMove(spec.map, {x: x, y: y}).click().perform();
    };

    that.toListView = function () {
        spec.toListButton.click();
    };

    that.toEditView = function () {
        spec.editViewButton.click();
    };


    that.isPaymentInfoDisplayed = function () {
        return spec.isDisplayed(spec.paymentInfo);
    };

    that.isParkAndRideAuthRequired = function() {
        return spec.isDisplayed(spec.parkAndRideAuthRequired);
    };

    that.isPaymentMethodsDisplayed = function() {
        return spec.isDisplayed(spec.paymentMethods);
    };

    that.getPaymentMethods = function() {
        return spec.paymentMethods.getText();
    };

    that.isPaymentInfoDetailsDisplayed = function() {
        return spec.isDisplayed(spec.paymentInfoDetails);
    };

    that.getPaymentInfoDetail = function() {
        return spec.getMultilingualValues(spec.paymentInfoDetail);
    };

    that.getPaymentInfoUrl = function() {
        return spec.getMultilingualValues(spec.paymentInfoUrl);
    };

    that.portView = portView;

    that.capacitiesTable = capacitiesTable;

    return that;
};
