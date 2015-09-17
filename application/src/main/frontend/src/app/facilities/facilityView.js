// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.facilityView', [
        'ui.router',
        'parkandride.facilityMap',
        'parkandride.capacities',
        'parkandride.predictions',
        'parkandride.address',
        'parkandride.OperatorResource',
        'parkandride.ContactResource',
        'parkandride.FacilityResource',
        'parkandride.HubResource',
        'parkandride.pricing',
        'parkandride.layout'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facility-view', { // dot notation in ui-router indicates nested ui-view
            parent: 'hubstab',
            url: '/facilities/view/:id', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilityViewCtrl as viewCtrl',
                    templateUrl: 'facilities/facilityView.tpl.html',
                    resolve: {
                        facility: function($stateParams, FacilityResource) {
                            return FacilityResource.getFacility($stateParams.id);
                        },
                        contacts: function(ContactResource, facility)  {
                            var contactIds = _.filter(_.values(facility.contacts));
                            if (!_.isEmpty(contactIds)) {
                                return ContactResource.listContacts({ids: contactIds}).then(function(results) {
                                    return _.indexBy(results.results, "id");
                                });
                            } else {
                                return {};
                            }
                        },
                        operator: function(OperatorResource, facility) {
                            return OperatorResource.getOperator(facility.operatorId);
                        },
                        hubs: function($stateParams, HubResource) {
                            return HubResource.listHubs({facilityIds: [$stateParams.id]});
                        },
                        predictions: function($q, $stateParams, FacilityResource, facility) {

                            var FORECAST_DISTANCES = [0,5,10,15,30,60];

                            function getPredictionForMinutesAfterNow(after) {
                                function addForecastDistance(listOfPredictions) {
                                    return listOfPredictions.map(function(prediction) {
                                        return _.merge(prediction, {forecastDistanceInMinutes: after});
                                    });
                                }
                                return FacilityResource.getPredictions($stateParams.id, after)
                                    .then(addForecastDistance);
                            }
                            // Grouping
                            function capacityTypeAndUsage(pred) { return pred.capacityType + pred.usage; }

                            return $q.all(FORECAST_DISTANCES.map(getPredictionForMinutesAfterNow))
                                .then(function(predictions) {
                                    return _.chain(predictions)
                                        .flatten()
                                        .groupBy(capacityTypeAndUsage)
                                        .map(function(row) {
                                            var reduce = row.reduce(function (row, newPrediction) {
                                                row.capacityType = newPrediction.capacityType;
                                                row.usage = newPrediction.usage;
                                                row.capacity = facility.builtCapacity[row.capacityType];
                                                row[newPrediction.forecastDistanceInMinutes] = newPrediction.spacesAvailable;
                                                return row;
                                            }, {});
                                            return reduce;
                                        })
                                        .value();
                                });
                        }
                    }
                }
            },
            data: { pageTitle: 'View Facility' }
        });
    });

    m.controller('FacilityViewCtrl', function(PricingService, schema, facility, contacts, operator, hubs, predictions) {
        var self = this;
        self.services = schema.services.values;
        self.dayTypes = schema.dayTypes.values;
        self.facility = facility;
        self.contacts = contacts;
        self.operator = operator;
        self.predictions = predictions;
        self.hubs = hubs;

        self.loadedDate = Date.now();

        self.isFree = function(pricing) {
            return PricingService.isFree(pricing);
        };
        self.is24h = function(pricing) {
            return PricingService.is24h(pricing);
        };
        self.hasOpeningHoursByDayType = function() {
            return !_.isEmpty(self.facility.openingHours.byDayType);
        };
        self.hasOpeningHoursInfo= function() {
            return !_.isEmpty(self.facility.openingHours.info) || !_.isEmpty(self.facility.openingHours.url);
        };
        self.hasCapacities = function() {
          return !_.isEmpty(facility.builtCapacity);
        };
        self.hasServices = function() {
            return self.facility.services.length > 0;
        };
        self.getServiceNames = function() {
            return _.map(self.facility.services, function(service) {
                return schema.services[service].label;
            });
        };
        self.isRepeatingValue = function(collection, i, properties) {
            if (!_.isArray(properties)) {
                properties = [properties];
            }
            if (i === 0) {
                return false;
            }
            return _.reduce(
                properties,
                function(and, property) {
                    return and && collection[i - 1][property] === collection[i][property];
                },
                true);
        };

        self.hasPaymentInfo = function() {
            return self.hasPaymentMethods() || self.hasPaymentInfoDetails();
        };
        self.hasPaymentMethods = function() {
            return facility.paymentInfo.paymentMethods.length > 0;
        };

        self.getPaymentMethodNames = function() {
            function hasPaymentMethod(paymentMethod) {
                return  _.contains(facility.paymentInfo.paymentMethods, paymentMethod.id);
            }

            return _.map(_.filter(schema.paymentMethods.values, hasPaymentMethod), function(paymentMethod) {
                return paymentMethod.label;
            });
        };
        self.hasPaymentInfoDetails = function() {
            return facility.paymentInfo.detail || facility.paymentInfo.url;
        };
    });

    m.directive('facilityViewNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/facilityViewNavi.tpl.html'
        };
    });

//    m.directive('myLink', function(){
//        return {
//            restrict: 'E',
//            transclude: true,
//            scope: {},
//            template: '<a ng-transclude></a>',
//            compile: function(element, attrs) {
//                element.find("a").attr("ui-sref", attrs.uiSref);
//            }
//        };
//    });
})();
