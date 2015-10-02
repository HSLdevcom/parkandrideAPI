// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function () {
    var m = angular.module('parkandride.reports', [
        'ui.router',
        'parkandride.auth',
        'parkandride.OperatorResource',
        'parkandride.HubResource',
        'parkandride.RegionResource',
        'parkandride.FacilityResource',
        'parkandride.layout',
        'parkandride.date'
    ]);

    m.config(function ($stateProvider) {
        $stateProvider.state('report-list', {
            parent: 'reportstab',
            url: '/reports',
            views: {
                "main": {
                    controller: 'ReportsCtrl as ctrl',
                    templateUrl: 'reports/reports.tpl.html'
                }
            },
            resolve: {
                OperatorResource: 'OperatorResource',
                HubResource: 'HubResource',
                FacilityResource: 'FacilityResource',
                RegionResource: 'RegionResource',
                allOperators: function (OperatorResource) {
                    return OperatorResource.listOperators().then(function (response) {
                        return response.results;
                    });
                },
                allHubs: function (HubResource) {
                    return HubResource.listHubs();
                },
                allFacilities: function (FacilityResource) {
                    return FacilityResource.listFacilities();
                },
                allRegions: function (RegionResource) {
                    return RegionResource.listRegionsWithHubs();
                }

            },
            data: {pageTitle: 'Reports'}
        });
    });

    m.controller('ReportsCtrl', function ($scope, $translate, $http, allOperators, allHubs, allFacilities, allRegions, schema, Session) {
        $scope.allOperators     = allOperators;
        $scope.allRegions       = allRegions;
        $scope.allHubs          = allHubs;
        $scope.allFacilities    = allFacilities;
        $scope.capacityTypes    = schema.capacityTypes.values;
        $scope.usages           = schema.usages.values;
        var contentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';

        //
        // UTILS
        //
        $scope.currentDate = new Date();
        function to_date(daynumber) {
            return new Date($scope.currentDate.getFullYear(), $scope.currentDate.getMonth(), daynumber,0,0,0);
        }
        function date_toArray(date) {
            return [date.getDate(), date.getMonth()+1, date.getFullYear()];
        }

        //
        // REPORT DEFAULTS
        //
        $scope.datePattern = /^(((0?[1-9]|[12]\d|3[01])[\.\-\/](0?[13578]|1[02])[\.\-\/]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|[12]\d|30)[\.\-\/](0?[13456789]|1[012])[\.\-\/]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|1\d|2[0-8])[\.\-\/]0?2[\.\-\/]((1[6-9]|[2-9]\d)?\d{2}))|(29[\.\-\/]0?2[\.\-\/]((1[6-9]|[2-9]\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)))$/;
        $scope.reportType = 'FacilityUsage';
        $scope.report = {
            startDate: to_date(1),
            endDate: to_date($scope.currentDate.getDate()),
            interval: 60,
            capacityTypes: ['CAR'],
            regions: [],
            hubs: [],
            operators: []
        };

        // Limit to the logged in operator, if applicable
        $scope.$watch(function() { return Session.get(); }, function(user) {
            if (user && user.operatorId) {
                var userOperatorId = user.operatorId;
                $scope.report.operators = [userOperatorId];
                $scope.fixedOperator = _.find($scope.allOperators, 'id', userOperatorId);
            }
        });

        //
        // DATES
        //
        $scope.$watch('report.startDate', function(newStartDate) {
            if (newStartDate > $scope.report.endDate) {
                $scope.report.endDate = newStartDate;
            }
        });
        $scope.$watch('report.endDate', function(newEndDate) {
            if (newEndDate < $scope.report.startDate) {
                $scope.report.startDate = newEndDate;
            }
        });

        //
        // UI FILTERS
        //

        function isBlank(arr) {
            return !arr || arr.length === 0; 
        }

        function ifNotEmpty(arr) {
            return {
                then: function(cb) {
                    return function actualFilter(val) {
                        return isBlank(arr) ? true : cb(val);
                    };
                }
            };
        }

        function getPermittedHubs(regionIds) {
            return _.chain(regionIds)
                .map(function (id) { return _.find($scope.allRegions, 'id', id); })
                .map(function(region) { return region.hubIds; })
                .flatten()
                .map(function (hubId) { return _.find($scope.allHubs, 'id', hubId); })
                .value();
        }

        var permittedHubs = $scope.allHubs;
        var permittedFacilities = $scope.allFacilities;
        $scope.$watchCollection('[report.regions, report.hubs, report.operators]', function(arr) {
            var regionIds = arr[0] || [];
            var hubIds = arr[1] || [];
            var operatorIds = arr[2] || [];

            // Util functions
            var bySelectedHubIds = function (hub) { return hubIds.indexOf(hub.id) !== -1; };
            var bySelectedOperatorIds = function (hub) { return operatorIds.indexOf(hub.operatorId) !== -1; };
            var toFacilityIds = function (hub) { return hub.facilityIds; };
            var toFacility = function(id) { return _.find($scope.allFacilities, 'id', id); };

            // Permitted hubs by regions
            permittedHubs = regionIds.length === 0 ? $scope.allHubs : getPermittedHubs(regionIds);

            // Facilities are filtered by regions, hubs, and operators
            if ([].concat(operatorIds, hubIds, regionIds).length > 0) {
                // If any filters set
                permittedFacilities = _.chain(permittedHubs)
                    .filter(ifNotEmpty(hubIds).then(bySelectedHubIds))
                    .map(toFacilityIds)
                    .flatten()
                    .map(toFacility)
                    .filter(ifNotEmpty(operatorIds).then(bySelectedOperatorIds))
                    .value();
            } else {
                permittedFacilities = $scope.allFacilities;
            }
        });

        $scope.isPermittedHub = function(hub) {
            return permittedHubs.indexOf(hub) !== -1;
        };

        $scope.isPermittedFacility = function(facility) {
            return permittedFacilities.indexOf(facility) !== -1;
        };

        //
        // REPORT GENERATION
        //
        $scope.generate = function (type, parameters) {
            if (!parameters) {
                parameters = {};
            }
            document.documentElement.classList.add('wait');
            //report name generation
            var name = $translate.instant('reports.' + type + '.name');
            if (type == 'FacilityUsage' || type == 'MaxUtilization') {
                var date = date_toArray(parameters.startDate);
                name += '_' + date[2] + ("0" + date[1]).slice(-2) + ("0" + date[0]).slice(-2);
                if (!/^\s*$/.test(parameters.endDate)) {
                    date = date_toArray(parameters.endDate);
                    name += '-' + date[2] + ("0" + date[1]).slice(-2) + ("0" + date[0]).slice(-2);
                }
            }
            else if (type == 'HubsAndFacilities') {
                var d = new Date();
                name += '_' + d.getFullYear() + ("0" + (d.getMonth() + 1)).slice(-2) + ("0" + d.getDate()).slice(-2);
            }
            name += ".xlsx";
            name = name.replace(/ /g, "_");

            function removeWait() {
                document.documentElement.classList.remove('wait');
            }
            parameters.startDate = date_toArray(parameters.startDate).join(".");
            parameters.endDate   = date_toArray(parameters.endDate).join(".");
            return $http({
                url: 'api/v1/reports/' + type,
                method: "POST",
                data: parameters,
                headers: {
                    'Accept': contentType,
                    'Content-Type': 'application/json'
                },
                responseType: 'arraybuffer'
            }).success(function (response) {
                var blob = new Blob([response], {type: contentType});
                var objectUrl = URL.createObjectURL(blob);
                saveAs(blob, name);
            }).then(removeWait, removeWait);
        };
    });

    m.directive('reportsNavi', function () {
        return {
            restrict: 'E',
            templateUrl: 'reports/reportsNavi.tpl.html'
        };
    });

})();
