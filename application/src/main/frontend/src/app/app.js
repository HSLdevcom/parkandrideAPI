(function() {
    var m = angular.module('parkandride', [
        'ui.bootstrap',

        'templates-app',
        'templates-common',

        'ui.router',
        'ui.select',
        'ngSanitize',

        'filters',
        'featureToggle',

        'parkandride.contacts',
        'parkandride.operators',
        'parkandride.hubList',
        'parkandride.dev',
        'parkandride.auth'
    ]);

    m.constant('FEATURES_URL', 'internal/features');

    m.value("schema", { capacityTypes:[], usages: [], dayTypes: [] });

    m.value("EVENTS", {
        validationErrors: "validation-errors-updated",
        showErrorsCheckValidity: "show-errors-check-validity"
    });

    m.config(function myAppConfig($stateProvider, $urlRouterProvider, $httpProvider) {
        $urlRouterProvider.otherwise('/hubs');

        $stateProvider.state('root', {
            abstract: true,
            template: '<div ui-view="main"></div>',
            resolve: {
                features: function(FeatureResource) {
                    return FeatureResource.getFeatures();
                },
                capacityTypes: function(schema, FacilityResource) {
                    return FacilityResource.getCapacityTypes().then(function(types) {
                        schema.capacityTypes = types;
                        return types;
                    });
                },
                usages: function(schema, FacilityResource) {
                    return FacilityResource.getUsages().then(function(types) {
                        schema.usages = types;
                        return types;
                    });
                },
                dayTypes: function(schema, FacilityResource) {
                    return FacilityResource.getDayTypes().then(function(types) {
                        schema.dayTypes = types;
                        return types;
                    });
                }
            },
            controller: function($scope, features) {
                $scope.features = features;
            }
        });

        $httpProvider.interceptors.push(function($q, $translate, $rootScope, Session, $injector, EVENTS) {
            return {
                responseError: function(rejection) {
                    if (rejection.status == 401) {
                        return $injector.get('loginPrompt')().then(function() {
                            return $injector.get('$http')(rejection.config);
                        });
                    } else if (rejection.status == 400 && rejection.data.violations) {
                        if (!rejection.config.skipDefaultViolationsHandling) {
                            $rootScope.$broadcast(EVENTS.validationErrors, rejection.data.violations);
                        }
                        return $q.reject(rejection);
                    } else {
                        swal({
                            title: $translate.instant('error.' + rejection.status + '.title'),
                            text: rejection.data.message,
                            confirmButtonText: $translate.instant('error.buttonText')
                        });
                        return $q.reject(rejection);
                    }
                },
                response: function(response) {
                    $rootScope.$broadcast(EVENTS.validationErrors, []);
                    return response;
                },
                request: function(config) {
                    $rootScope.$broadcast(EVENTS.validationErrors, []);
                    var user = Session.get();
                    if (user && user.token) {
                        config.headers.Authorization = "Bearer " + user.token;
                    }
                    return config;
                }
            };
        });
    });

    m.controller('AppCtrl', function AppCtrl($rootScope, $location, loginPrompt, Session, EVENTS, Permission, permit) {
        $rootScope.permit = permit;

        // Permission constants
        for (var permission in Permission) {
            $rootScope[permission] = permission;
        }

        $rootScope.common = {};
        $rootScope.$on(EVENTS.validationErrors, function(event, violations) {
                $rootScope.common.violations = _.map(violations, function(violation) {
                    violation.path = violation.path.replace(/\[\d+\]/, "");
                    return violation;
                });
            });

        this.hasValidationErrors = function() {
            return !_.isEmpty($rootScope.violations);
        };

        $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
            if (angular.isDefined(toState.data.pageTitle)) {
                $rootScope.pageTitle = toState.data.pageTitle + ' | parkandride';
            }
        });

        this.openLoginPrompt = function() {
            loginPrompt().then(function() {}, function() {});
        };

        this.logout = function() {
            Session.remove();
        };

        this.isUserLoggedIn = function() {
            return Session.get() != null;
        };

        this.validateAndSubmit = function(form, submitFn) {
            $rootScope.$broadcast(EVENTS.showErrorsCheckValidity);
            if (form.$valid) {
                submitFn();
            } else {
                $rootScope.$broadcast(EVENTS.validationErrors, [
                    {
                        path: "",
                        type: "BasicRequirements"
                    }
                ]);
            }
        };
    });
})();
