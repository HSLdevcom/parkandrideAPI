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

    self.hubsFromProto = function(proto, names) {
        var namesShuffled = _.shuffle(names);
        var creatorFn = function() { return proto.copy(); };
        return _.map(namesShuffled, _.partial(fromName, creatorFn));
    };

    return self;
};