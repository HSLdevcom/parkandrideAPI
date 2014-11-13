"use strict";

module.exports = function () {
    var _ = require('lodash');
    var api = {};

    var unwrap = function (actual, fn) {
        if (actual instanceof protractor.promise.Promise) {
            actual.then(function (a) { fn(a); });
        } else {
            fn(actual);
        }
    };

    api.assertInOrder = function(actual, expected, opts) {
        unwrap(actual, function(a) {
            opts = opts || {};

            var toSkip = [];
            if (opts.allowSkip) {
                toSkip = _.difference(expected, a);
            }

            expect(a).toEqual(_.difference(expected, toSkip));
        });
    };

    api.assertInAnyOrder = function(actual, expected) {
        unwrap(actual, function(a) { api.assertInOrder(a.sort(), expected.sort()); });
    };

    return api;
};