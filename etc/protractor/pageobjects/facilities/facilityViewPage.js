'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')({});
    var portView = require('./portViewModal')();

    spec.view = $('.wdFacilityView');
    spec.name = $('.wdFacilityNameFi');
    spec.aliases = $('.wdAliases');
    spec.services = $$('.wdService');
    spec.servicesBlock = $('.wdServices');
    spec.aliasesBlock = $('.wdAliasesBlock');
    spec.map = $('.facility-map .ol-viewport');
    spec.editViewButton = $$('.wdEditViewButton').first();
    spec.toListButton = element.all(by.linkUiSref('hub-list')).first();

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
        return spec.servicesBlock.then(
            function(elem) {
                return elem.isDisplayed();
            },
            function() {
                return false;
            }
        );
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

    that.portView = portView;

    that.capacitiesTable = capacitiesTable;

    return that;
};
