"use strict";

function hub(data) {
    var _ = require('lodash');
    var self = data || {};

    self.copy = function() {
        return hub(_.cloneDeep(self));
    };

    self.toPayload = function() {
        var payload = self.copy();
        var skipFields = ['facilities'];
        _.forEach(skipFields, function(field) { delete payload[field]; });
        return payload;
    };

    return self;
};

module.exports = hub;