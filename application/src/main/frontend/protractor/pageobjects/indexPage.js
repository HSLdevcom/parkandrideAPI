'use strict';

module.exports = function() {
    var api = {};

    api.get = function () {
        browser.get('/');
    };

    return api;
};
