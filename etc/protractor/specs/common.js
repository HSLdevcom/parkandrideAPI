"use strict";

module.exports.capacityTypeOrder = ["Liityntäpysäköinti", "Polkupyörä", "Henkilöauto", "Invapaikka", "Moottoripyörä", "Sähköauto"];

var caps_dont_matter = ["aaa", "Aba", "aca"];
var v_w = ["bvb", "bwa", "b_not_needed_but_we_need_9_inputs_in_hub_edit_spec"];
//var å_ä_ö = ["cåc", "cäa", "cöb"]; // expected
var å_ä_ö = ["cäa", "cåc", "cöb"]; // invalid but our postgres currently orders it like this, i.e. ä is before å
module.exports.facilityNameOrder = caps_dont_matter.concat(v_w).concat(å_ä_ö);
