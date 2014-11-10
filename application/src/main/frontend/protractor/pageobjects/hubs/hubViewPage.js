'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')({});

    spec.view = $('.wdHubView');
    spec.name = $('.wdHubNameFi');
    spec.noFacilitiesMsg = $('.wdNoFacilitiesMsg');
    spec.facilitiesTotal = $('.wdFacilitiesTotal');

    that.facilitiesTable = require('../facilitiesTable')({});

    that.get = function(id) {
        browser.get('/#/hubs/view/' + id);
    };

    that.getName = function () {
        return spec.name.getText();
    };

    that.capacitiesTable = capacitiesTable;

    that.getNoFacilitiesMessage = function() {
        return spec.noFacilitiesMsg.getText();
    };

    that.isFacilitiesTotalDisplayed = function() {
        return spec.facilitiesTotal.isDisplayed();
    };

    return that;
};