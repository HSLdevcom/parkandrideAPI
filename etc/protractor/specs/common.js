"use strict";

module.exports.capacityTypeOrder = ["Liityntäpysäköinti", "Polkupyörä", "Henkilöauto", "Invapaikka", "Moottoripyörä", "Sähköauto"];

var caps_dont_matter = ["aaa", "Aba", "aca"];
var v_w = ["bvb", "bwa", "b_not_needed_but_we_need_9_inputs_in_hub_edit_spec"];
var å_ä_ö = ["cåc", "cäa", "cöb"]; // NOTE: on osx, postgres orders this incorrectly
module.exports.facilityNameOrder = caps_dont_matter.concat(v_w).concat(å_ä_ö);
