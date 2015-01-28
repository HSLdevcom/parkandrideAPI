"use strict";

module.exports = function (spec) {
    var that = require('../base')(spec);

    spec.view = $('#userModal');

    return that;
};