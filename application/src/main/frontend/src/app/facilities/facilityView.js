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
        'parkandride.ServiceResource',
        'parkandride.PaymentMethodResource',
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
                        services: function(ServiceResource, facility) {
                            if (!_.isEmpty(facility.serviceIds)) {
                                return ServiceResource.listServices({ids: facility.serviceIds}).then(function(results) {
                                    return results.results;
                                });
                            } else {
                                return [];
                            }
                        },
                        paymentMethods: function(PaymentMethodResource) {
                            return PaymentMethodResource.listPaymentMethods().then(function(results) {
                                return results.results;
                            });
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

    m.controller('FacilityViewCtrl', function(PricingService, facility, services, contacts, operator, paymentMethods) {
        var self = this;
        self.facility = facility;
        self.services = services;
        self.contacts = contacts;
        self.operator = operator;
        self.isFree = function(pricing) {
            return PricingService.isFree(pricing);
        };
        self.is24h = function(pricing) {
            return PricingService.is24h(pricing);
        };
        self.hasCapacities = function() {
          return !_.isEmpty(facility.builtCapacity);
        };
        self.hasServices = function() {
            return services.length > 0;
        };
        self.getServiceNames = function() {
            return _.map(services, function(service) {
                return service.name.fi;
            });
        };
        self.showUnavailableCapacityType = function(i) {
            var ucs = self.facility.unavailableCapacities;
            return i === 0 || ucs[i - 1].capacityType != ucs[i].capacityType;
        };

        self.hasPaymentInfo = function() {
            return facility.paymentInfo.parkAndRideAuthRequired || self.hasPaymentMethods() || self.hasPaymentInfoDetails();
        };
        self.hasPaymentMethods = function() {
            return facility.paymentInfo.paymentMethodIds.length > 0;
        };
        self.getPaymentMethodNames = function() {
            function hasPaymentMethod(paymentMethod) {
                return  _.contains(facility.paymentInfo.paymentMethodIds, paymentMethod.id);
            }

            return _.map(_.filter(paymentMethods, hasPaymentMethod), function(paymentMethod) {
                return paymentMethod.name.fi;
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
