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
        'parkandride.layout'
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

    m.controller('ReportsCtrl', function ($scope, $translate, $http, allOperators, allHubs, allFacilities, allRegions, schema) {
        $scope.allOperators = allOperators;
        $scope.allHubs = allHubs;
        $scope.allFacilities = allFacilities;
        $scope.allRegions = allRegions;
        $scope.capacityTypes = schema.capacityTypes.values;
        $scope.usages = schema.usages.values;
        var contentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
        var date = new Date();

        function to_date(daynumber) {
            return daynumber + '.' + (date.getMonth() + 1) + '.' + date.getFullYear();
        }

        $scope.reportType = 'FacilityUsage';
        $scope.report = {
            startDate: to_date(1),
            endDate: to_date(date.getDate()),
            interval: 60,
            capacityTypes: ['CAR']
        };
        $scope.generate = function (type, parameters) {
            if (!parameters) {
                parameters = {};
            }
            //report name generation
            var name = $translate.instant('reports.' + type + '.name');
            if (type == 'FacilityUsage' || type == 'MaxUtilization') {
                var date = parameters.startDate.split(".");
                name += '_' + date[2] + ("0" + date[1]).slice(-2) + ("0" + date[0]).slice(-2);
                if (!/^\s*$/.test(parameters.endDate)) {
                    date = parameters.endDate.split(".");
                    name += '-' + date[2] + ("0" + date[1]).slice(-2) + ("0" + date[0]).slice(-2);
                }
            }
            else if (type == 'HubsAndFacilities') {
                var d = new Date();
                name += '_' + d.getFullYear() + ("0" + (d.getMonth() + 1)).slice(-2) + ("0" + d.getDate()).slice(-2);
            }
            name += ".xlsx";
            name = name.replace(/ /g, "_");

            $http({
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
            });
        };
    });

    m.directive('reportsNavi', function () {
        return {
            restrict: 'E',
            templateUrl: 'reports/reportsNavi.tpl.html'
        };
    });

})();
