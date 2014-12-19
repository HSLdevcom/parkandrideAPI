'use strict';

module.exports = function(spec) {
    var that = require('./base')(spec);

    spec.view = element(by.cssContainingText('h2', 'Pysäköintipaikat'));

    that.get = function () {
        browser.get('/');
    };

    return that;
};
