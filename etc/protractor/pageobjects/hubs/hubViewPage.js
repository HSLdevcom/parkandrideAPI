'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')({});

    spec.view = $('.wdHubView');
    spec.name = $('.wdHubNameFi');
    spec.noFacilitiesMsg = $('.wdNoFacilitiesMsg');
    spec.facilitiesTotal = $('.wdFacilitiesTotal');
    spec.editViewButton = $$('.wdEditViewButton').first();
    spec.hubListButton = element.all(by.linkUiSref('hub-list')).first();

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

    that.toEditView = function() {
        spec.editViewButton.click();
    };

    that.toListView = function() {
        spec.hubListButton.click();
    };

    return that;
};