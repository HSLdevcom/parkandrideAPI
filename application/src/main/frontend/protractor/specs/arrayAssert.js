"use strict";

module.exports = function () {
    var _ = require('lodash');
    var api = {};

    var assertInOrder = function(actual, expected, opts) {
        opts = opts || {};

        var toSkip = [];
        if (opts.allowSkip) {
            toSkip = _.difference(expected, actual);
        }

        expect(actual).toEqual(_.difference(expected, toSkip));
    };

    api.assertInOrder = function(actual, expected, opts) {
        if (actual instanceof protractor.promise.Promise) {
            return actual.then(function (a) { assertInOrder(a, expected, opts); });
        }
        return assertInOrder(actual, expected, opts);
    };

    return api;
};