'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')({});
    var portView = require('./portViewModal')();
    var _ = require('lodash');

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
    spec.paymentInfoDetails = $('.wdPaymentInfo .wdPaymentInfoDetails');
    spec.paymentInfoDetail = $('.wdPaymentInfo .wdPaymentInfoDetail');
    spec.paymentInfoUrl = $('.wdPaymentInfo .wdPaymentInfoUrl');

    spec.openingHourRows = $$('#opening-hours tr');

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

    that.getOpeningHours = function() {
        return spec.openingHourRows.then(function(rows) {
            return protractor.promise.all(
                _.map(rows, function(row) {
                    return row.all(by.css("td")).then(function(columns) {
                        return protractor.promise.all([ columns[0].getText(), columns[1].getText() ]);
                    });
                }))
                .then(function(openingHourRows) {
                    return _.reduce(openingHourRows, function(acc, row) {
                        acc[row[0]] = row[1];
                        return acc;
                    }, {});
                });
        });
    };

    that.portView = portView;

    that.capacitiesTable = capacitiesTable;

    return that;
};
