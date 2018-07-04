// Copyright © 2018 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

"use strict";

var _ = require('lodash');

module.exports.capacityTypeOrder = ["Henkilöauto", "Invapaikka", "Sähköauto", "Moottoripyörä", "Polkupyörä", "Polkupyörä, lukittu tila"];

var caps_dont_matter = ["aaa", "Aba", "aca"];
var v_w = ["bvb", "bwa"];
var å_ä_ö = ["cåc", "cäa", "cöb"]; // NOTE: on osx this is ordered incorrectly
var weNeed9InputsInHubEditSpec = ["ö"];
module.exports.facilityNameOrder = caps_dont_matter.concat(v_w).concat(å_ä_ö).concat(weNeed9InputsInHubEditSpec);

module.exports.isOsx = /^darwin/.test(process.platform);

beforeEach(function () {
    var customMatchers = {
        toContainInAnyOrder: function (util, customEqualityTesters) {
            return {
                compare: function (actual, expected) {
                    var actualCopy = actual.slice();
                    actualCopy.sort();
                    var expectedCopy = expected.slice();
                    expectedCopy.sort();

                    var result = {};
                    result.pass = util.equals(actualCopy, expectedCopy, customEqualityTesters);
                    if (result.pass) {
                        result.message = "Expected " + JSON.stringify(actual) + " to not contain in any order " + JSON.stringify(expected);
                    } else {
                        result.message = "Expected " + JSON.stringify(actual) + " to contain in any order " + JSON.stringify(expected);
                    }
                    return result;
                }
            }
        },

        toContainSomeInSameOrder: function (util, customEqualityTesters) {
            return {
                compare: function (actual, expected) {
                    var result = {};
                    var toSkip = _.difference(expected, actual);
                    result.pass = util.equals(actual, _.difference(expected, toSkip), customEqualityTesters);
                    if (result.pass) {
                        result.message = "Expected " + JSON.stringify(actual) + " to not contain in any order " + JSON.stringify(expected);
                    } else {
                        result.message = "Expected " + JSON.stringify(actual) + " to contain in any order " + JSON.stringify(expected);
                    }
                    return result;
                }
            }
        }
    };
    jasmine.addMatchers(customMatchers);
});
