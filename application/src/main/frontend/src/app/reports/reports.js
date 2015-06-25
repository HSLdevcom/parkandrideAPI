// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.reports', [
        'ui.router',
        'parkandride.auth',
        'parkandride.OperatorResource',
        'parkandride.HubResource',
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

    m.controller('ReportsCtrl', function($scope, $http, OperatorResource, HubResource, FacilityResource, schema) {
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
      $scope.capacityTypes = schema.capacityTypes.values;
      $scope.usages = schema.usages.values;
      var contentType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      $scope.date = new Date();
      $scope.generate = function(name, parameters) {
        if (!parameters) {
          parameters = {};
        }
        $http({
          url: 'api/v1/reports/' + name,
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

    m.directive('reportsNavi', function() {
      return {
          restrict: 'E',
          templateUrl: 'reports/reportsNavi.tpl.html'
      };
    });

})();
