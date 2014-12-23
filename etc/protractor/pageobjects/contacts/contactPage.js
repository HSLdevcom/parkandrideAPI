'use strict';

module.exports = function(spec) {
    var that = require('../base')(spec);

    that.editModal = require('./contactEditModal')({});

    spec.view = $('.wdContactsView');
    spec.createButton = $$('.wdCreate').first();
    spec.contactRow = $$('.contactRow');

    that.get = function () {
        browser.get('/#/contacts');
    };

    that.openCreateModal = function() {
        spec.createButton.click();
    };

    that.openEditDialog = function(id) {
        $('.wdContact' + id + " .wdEdit").click();
    };

    that.getContacts = function() {
        return spec.contactRow.then(function(rows) {
            var results = [];
            for (var i=0; i < rows.length; i++) {
                results.push(
                    protractor.promise.all([
                        rows[i].element(by.css(".wdNameFi")).getText(),
                        rows[i].element(by.css(".wdPhone")).getText(),
                        rows[i].element(by.css(".wdEmail")).getText()
                    ])
                );
            }
            return protractor.promise.all(results);;
        });
    };

    return that;
};