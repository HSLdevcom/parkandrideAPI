'use strict';

module.exports = function(spec) {
    var that = require('./base')(spec);

    that.get = function () {
        browser.get('/');
    };

    return that;
};
