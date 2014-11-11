"use strict";

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

module.exports.capacityTypeOrder = ["Liityntäpysäköinti", "Polkupyörä", "Henkilöauto", "Invapaikka", "Moottoripyörä", "Sähköauto"];
module.exports.facilityNameOrder = [ "_foo_", "b@z", "Bar", "bär", "foo", "fov", "fow", "fåå", "föö" ];
module.exports.takeScreenshot = function() { capture(); };