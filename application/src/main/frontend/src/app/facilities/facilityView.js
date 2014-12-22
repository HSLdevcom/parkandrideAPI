(function() {
    var m = angular.module('parkandride.facilityView', [
        'ui.router',
        'parkandride.facilityMap',
        'parkandride.capacities',
        'parkandride.layout',
        'parkandride.address',
        'parkandride.ContactResource',
        'parkandride.FacilityResource',
        'parkandride.ServiceResource',
        'parkandride.PaymentMethodResource',
        'parkandride.layout'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('facility-view', { // dot notation in ui-router indicates nested ui-view
            parent: 'root',
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
                        }
                    }
                }
            },
            data: { pageTitle: 'View Facility' }
        });
    });

    m.controller('FacilityViewCtrl', function(facility, services, paymentMethods, contacts) {
        this.facility = facility;
        this.services = services;
        this.contacts = contacts;
        this.hasCapacities = function() {
          return _.keys(facility.capacities).length !== 0;
        };
        this.hasServices = function() {
            return services.length > 0;
        };
        this.getServiceNames = function() {
            return _.map(services, function(service) {
                return service.name.fi;
            });
        };
        this.getPaymentMethodNames = function() {
            function hasPaymentMethod(paymentMethod) {
                return  _.contains(facility.paymentInfo.paymentMethodIds, paymentMethod.id);
            }

            return _.map(_.filter(paymentMethods, hasPaymentMethod), function(paymentMethod) {
                return paymentMethod.name.fi;
            });
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
