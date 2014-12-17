'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    that.editModal = require('./operatorEditModal')({});

    spec.view = $('.wdOperatorsView');
    spec.createButton = $$('.wdCreate').first();
    spec.operatorRow = $$('.operatorRow');

    that.get = function () {
        browser.get('/#/operators');
    };

    that.openCreateModal = function() {
        spec.createButton.click();
    };

    that.openEditDialog = function(id) {
        $('.wdOperator' + id + " .wdEdit").click();
    }

    that.getOperators = function() {
        return spec.operatorRow.then(function(rows) {
            var results = [];
            for (var i=0; i < rows.length; i++) {
                results.push(
                    protractor.promise.all([
                        rows[i].element(by.css(".wdNameFi")).getText()
                    ])
                );
            }
            return protractor.promise.all(results);;
        });
    };

    return that;
};