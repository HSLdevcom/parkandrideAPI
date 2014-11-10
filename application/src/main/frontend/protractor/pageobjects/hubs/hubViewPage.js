'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')({});

    spec.view = $('.wdHubView');
    spec.name = $('.wdHubNameFi');
    spec.noFacilitiesMsg = $('.wdNoFacilitiesMsg');

    that.facilitiesTable = require('../facilitiesTable')({});

    that.getName = function () {
        return spec.name.getText();
    };

    that.capacitiesTable = capacitiesTable;

    that.getNoFacilitiesMessage = function() {
        return spec.noFacilitiesMsg.getText();
    };

    return that;
};