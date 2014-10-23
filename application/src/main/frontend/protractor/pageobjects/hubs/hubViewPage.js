'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')();

    spec.view = $('.wdHubView');
    spec.name = $('.wdHubName');

    that.facilitiesTable = require('../facilitiesTable')({});

    that.getName = function () {
        return spec.name.getText();
    };

    that.capacitiesTable = capacitiesTable;

    return that;
};