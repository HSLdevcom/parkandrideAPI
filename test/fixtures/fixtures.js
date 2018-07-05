'use strict';

module.exports.facility = require('./facility');
module.exports.facilityFactory = require('./facilityFactory')();
module.exports.hub= require('./hub.js');
module.exports.hubFactory= require('./hubFactory')();
module.exports.facilitiesFixture = require('./facilities.fixture')();
module.exports.hubsFixture = require('./hubs.fixture')();