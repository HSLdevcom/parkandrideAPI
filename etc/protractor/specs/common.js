"use strict";

module.exports.capacityTypeOrder = ["Henkilöauto", "Invapaikka", "Sähköauto", "Moottoripyörä", "Polkupyörä"];

var caps_dont_matter = ["aaa", "Aba", "aca"];
var v_w = ["bvb", "bwa"];
var å_ä_ö = ["cåc", "cäa", "cöb"]; // NOTE: on osx this is ordered incorrectly
var weNeed9InputsInHubEditSpec = ["ö"];
module.exports.facilityNameOrder = caps_dont_matter.concat(v_w).concat(å_ä_ö).concat(weNeed9InputsInHubEditSpec);

module.exports.isOsx = /^darwin/.test(process.platform);
