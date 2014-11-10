'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);
    var capacitiesTable = require('../capacitiesTable')({});

    spec.view = $('.wdFacilityView');
    spec.name = $('.wdFacilityNameFi');
    spec.aliases = $('.wdAliases');
    spec.toListButton = element.all(by.linkUiSref('hub-list')).first();

    that.getName = function () {
        return spec.name.getText();
    };

    that.getAliases = function () {
        return spec.aliases.getText().then(function(joined){
            return joined.split(", ");
        });
    };

    that.toListView = function () {
        return spec.toListButton.click();
    };

    that.capacitiesTable = capacitiesTable;

    return that;
};
