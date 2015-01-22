(function() {
    var m = angular.module('parkandride', [
        'ui.bootstrap',
        'parkandride.i18n',

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

    function scrollToTop() {
        window.scrollTo(0, 0);
    }

    m.constant('FEATURES_URL', 'internal/features');

    m.value("schema", {});

    m.value("EVENTS", {
        validationErrors: "validation-errors-updated",
        showErrorsCheckValidity: "show-errors-check-validity"
    });

    m.config(function myAppConfig($stateProvider, $urlRouterProvider, $httpProvider) {
        $urlRouterProvider.otherwise('/hubs');

        function registerEnumValues(schema, type, values, $translate) {
            schema[type] = { values: [] };
            _.forEach(values, function(value) {
                var enumObject = {
                    id: value,
                    label: $translate.instant(type + "." + value + ".label")
                };
                schema[type].values.push(enumObject);
                schema[type][value] = enumObject;
            });
            return schema[type];
        }

        $stateProvider.state('root', {
            abstract: true,
            template: '<div ui-view="main"></div>',
            resolve: {
                features: function(FeatureResource) {
                    return FeatureResource.getFeatures();
                },
                capacityTypes: function(schema, SchemaResource, $translate) {
                    return SchemaResource.getCapacityTypes().then(function(types) {
                        return registerEnumValues(schema, "capacityTypes", types, $translate);
                    });
                },
                usages: function(schema, SchemaResource, $translate) {
                    return SchemaResource.getUsages().then(function(types) {
                        return registerEnumValues(schema, "usages", types, $translate);
                    });
                },
                dayTypes: function(schema, SchemaResource, $translate) {
                    return SchemaResource.getDayTypes().then(function(types) {
                        return registerEnumValues(schema, "dayTypes", types, $translate);
                    });
                },
                services: function(schema, SchemaResource, $translate) {
                    return SchemaResource.getServices().then(function(types) {
                        return registerEnumValues(schema, "services", types, $translate);
                    });
                },
                paymentMethods: function(schema, SchemaResource, $translate) {
                    return SchemaResource.getPaymentMethods().then(function(types) {
                        return registerEnumValues(schema, "paymentMethods", types, $translate);
                    });
                }
            },
            controller: function($scope, features) {
                $scope.features = features;
            }
        });

        $stateProvider.state('hubstab', {
            abstract: true,
            parent: 'root',
            views: { "main": { template: '<div ui-view="main"></div>' } }
        });
        $stateProvider.state('contactstab', {
            abstract: true,
            parent: 'root',
            views: { "main": { template: '<div ui-view="main"></div>' } }
        });

        $stateProvider.state('operatorstab', {
            abstract: true,
            parent: 'root',
            views: { "main": { template: '<div ui-view="main"></div>' } }
        });

        $stateProvider.state('devtab', {
            abstract: true,
            parent: 'root',
            views: { "main": { template: '<div ui-view="main"></div>' } }
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

    m.run(function($rootScope, $state, EVENTS) {
        $rootScope.$state = $state;

        $rootScope.$on("$stateChangeSuccess", scrollToTop);
        $rootScope.$on(EVENTS.validationErrors, scrollToTop);
    });

    m.controller('AppCtrl', function AppCtrl($rootScope, $location, loginPrompt, Session, EVENTS, Permission, permit, schema) {
        $rootScope.permit = permit;

        // Permission constants
        for (var permission in Permission) {
            $rootScope[permission] = permission;
        }

        $rootScope.common = {};
        $rootScope.$on(EVENTS.validationErrors, function(event, violations) {
            $rootScope.common.violations = [];
            var duplicates = {};
            for (var i=0; i < violations.length; i++) {
                var violation = violations[i];
                violation.path = violation.path.replace(/\[\d+\]/, "");
                var violationKey = violation.path + "/" + violation.type;
                if (!duplicates[violationKey]) {
                    duplicates[violationKey] = true;
                    $rootScope.common.violations.push(violation);
                }
            }
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
