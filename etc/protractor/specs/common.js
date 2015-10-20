"use strict";

module.exports.capacityTypeOrder = ["Henkilöauto", "Invapaikka", "Sähköauto", "Moottoripyörä", "Polkupyörä", "Polkupyörä, lukittu tila"];

var caps_dont_matter = ["aaa", "Aba", "aca"];
var v_w = ["bvb", "bwa"];
var å_ä_ö = ["cåc", "cäa", "cöb"]; // NOTE: on osx this is ordered incorrectly
var weNeed9InputsInHubEditSpec = ["ö"];
module.exports.facilityNameOrder = caps_dont_matter.concat(v_w).concat(å_ä_ö).concat(weNeed9InputsInHubEditSpec);

module.exports.isOsx = /^darwin/.test(process.platform);


beforeEach(function () {
    var matchers = {
        toEqualBecause: function (value, message) {
            this.message = function () {
                return "Expected '" + this.actual + "' to equal '" + value + "' because " + message;
            };
            return this.actual === value;
        }
    };
    this.addMatchers(matchers);
});
