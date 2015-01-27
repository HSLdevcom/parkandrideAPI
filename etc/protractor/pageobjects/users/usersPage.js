"use strict";

module.exports = function () {
    var that = require('../base')(spec);

    spec.view = $('.wdUsersView');

    that.get = function () {
        browser.get('/#/users');
    };

    return that;
};