"use strict";

module.exports.capacityTypeOrder = ["Liityntäpysäköinti", "Polkupyörä", "Henkilöauto", "Invapaikka", "Moottoripyörä", "Sähköauto"];

var caps_dont_matter = ["aaa", "Aba", "aca"];
var v_w = ["bvb", "bwa", "bNotNeededButWeNeed9InputsInHubEditSpec"];
var å_ä_ö = ["cåc", "cäa", "cöb"]; // NOTE: on osx this is ordered incorrectly
module.exports.facilityNameOrder = caps_dont_matter.concat(v_w).concat(å_ä_ö);

module.exports.isOsx = /^darwin/.test(process.platform);
