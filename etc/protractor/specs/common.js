"use strict";

module.exports.capacityTypeOrder = ["Liityntäpysäköinti", "Polkupyörä", "Henkilöauto", "Invapaikka", "Moottoripyörä", "Sähköauto"];

var caps_dont_matter = ["aaa", "Aba", "aca"];
var v_w = ["bvb", "bwa"];
var å_ä_ö = ["cåc", "cäa", "cöb"]; // NOTE: on osx this is ordered incorrectly
var weNeed9InputsInHubEditSpec = ["ö"];
module.exports.facilityNameOrder = caps_dont_matter.concat(v_w).concat(å_ä_ö).concat(weNeed9InputsInHubEditSpec);

module.exports.isOsx = /^darwin/.test(process.platform);


var fs = require('fs');

function spec() {
    return jasmine.getEnv().currentSpec;
}

function capture() {
    var name = spec().description.split(' ').join('_');
    browser.takeScreenshot().then(function(png){
        var stream = fs.createWriteStream('screenshots/' + name + '.png');
        stream.write(new Buffer(png, 'base64'));
        stream.end();
    });
}

afterEach(function () {
    if (!spec().results().passed()) {
        capture();
    }
});

module.exports.takeScreenshot = function() { capture(); };
