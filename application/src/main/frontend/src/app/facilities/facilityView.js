(function() {
    var m = angular.module('parkandride.facilityView', [
        'ui.router',
        'parkandride.facilityMap',
        'parkandride.capacities',
        'parkandride.layout',
        'parkandride.address',
        'parkandride.OperatorResource',
        'parkandride.ContactResource',
        'parkandride.FacilityResource',
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
                        }
                    }
                }
            },
            data: { pageTitle: 'View Facility' }
        });
    });

    m.controller('FacilityViewCtrl', function(PricingService, schema, facility, contacts, operator) {
        var self = this;
        self.services = schema.services.values;
        self.dayTypes = schema.dayTypes.values;
        self.facility = facility;
        self.contacts = contacts;
        self.operator = operator;
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
            return facility.paymentInfo.parkAndRideAuthRequired || self.hasPaymentMethods() || self.hasPaymentInfoDetails();
        };
        self.hasPaymentMethods = function() {
            return facility.paymentInfo.paymentMethod.length > 0;
        };
        self.getPaymentMethodNames = function() {
            function hasPaymentMethod(paymentMethod) {
                return  _.contains(facility.paymentInfo.paymentMethod, paymentMethod.id);
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
