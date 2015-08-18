// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.reports', [
        'ui.router',
        'parkandride.auth',
        'parkandride.OperatorResource',
        'parkandride.HubResource',
        'parkandride.RegionResource',
        'parkandride.FacilityResource',
        'parkandride.layout'
    ]);

    m.config(function($stateProvider) {
      $stateProvider.state('report-list', {
          parent: 'reportstab',
          url: '/reports',
          views: {
              "main": {
                  controller: 'ReportsCtrl as ctrl',
                  templateUrl: 'reports/reports.tpl.html'
              }
          },
          data: {pageTitle: 'Reports'}
      });
    });

    m.controller('ReportsCtrl', function($scope, $translate, $http, OperatorResource, HubResource, FacilityResource, RegionResource, schema) {
      $scope.allOperators = [];
      OperatorResource.listOperators().then(function(response) {
        $scope.allOperators = response.results;
      });
      $scope.allHubs = [];
      HubResource.listHubs().then(function(response) {
        $scope.allHubs = response;
      });
      $scope.allFacilities = [];
      FacilityResource.listFacilities().then(function(response) {
        $scope.allFacilities = response;
      });
      $scope.allRegions = [];
      RegionResource.listRegions().then(function(response) {
        $scope.allRegions = response;
      });
      $scope.capacityTypes = schema.capacityTypes.values;
      $scope.usages = schema.usages.values;
      var contentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      $scope.date = new Date();
      $scope.generate = function(type, parameters) {
        if (!parameters) {
          parameters = {};
        }
        document.documentElement.classList.add('wait');
        //report name generation
        var name = $translate.instant('reports.'+type+'.name');
        if(type == 'FacilityUsage' || type == 'MaxUtilization') {
            var date = parameters.startDate.split(".");
            name += '_'+ date[2] + ("0" + date[1]).slice(-2) + ("0" + date[0]).slice(-2);
            if (!/^\s*$/.test(parameters.endDate)) {
                date = parameters.endDate.split(".");
                name += '-'+ date[2] + ("0" + date[1]).slice(-2) + ("0" + date[0]).slice(-2);
            }
        }
        else if(type == 'HubsAndFacilities') {
            var d = new Date();
            name += '_' + d.getFullYear() + ("0" + (d.getMonth() + 1)).slice(-2) +  ("0" + d.getDate()).slice(-2);
        }
        name += ".xlsx";
        name = name.replace(/ /g,"_");

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
        document.documentElement.classList.remove('wait');
      };
    });

    m.directive('reportsNavi', function() {
      return {
          restrict: 'E',
          templateUrl: 'reports/reportsNavi.tpl.html'
      };
    });

})();
