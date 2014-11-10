"use strict";

module.exports = function () {
    var _ = require('lodash');
    var idGen = 1000;

    var fromName = function(creatorFn, name) {
        var o  = creatorFn();
        o.name = name;
        o.id = idGen++;
        return o;
    };

    var self = {};

    self.facilitiesFromProto = function(proto, names) {
        return self.facilitiesFromCreator(function() { return proto.copy(); }, names);
    };

    self.facilitiesFromCreator = function(creatorFn, names) {
        var namesShuffled = _.shuffle(names);
        return _.map(namesShuffled, _.partial(fromName, creatorFn));
    };

    return self;
};