"use strict";

module.exports = function (data) {
    var _ = require('lodash');
    var self = data || {};

    self.toPayload = function() {
        var payload = _.cloneDeep(self);
        var skipFields = ['facilities'];
        _.forEach(skipFields, function(field) { delete payload[field]; });
        return payload;
    };

    return self;
};