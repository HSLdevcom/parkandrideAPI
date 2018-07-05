// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.searchDemo', [
        'ui.router',
        'parkandride.MapService',
        'parkandride.layout',
        'parkandride.FacilityResource'
    ]);

    m.config(function config($stateProvider) {
        $stateProvider.state('search-demo', { // dot notation in ui-router indicates nested ui-view
            parent: 'hubstab',
            url: '/search-demo?geometry&maxDistance', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilitySearchCtrl as searchCtrl',
                    templateUrl: 'facilities/searchDemo.tpl.html',
                    resolve: {
                        results: function(FacilityResource, $translate, $stateParams) {
                            if ($stateParams.geometry) {
                                return FacilityResource.findFacilitiesAsFeatureCollection({
                                    geometry: $stateParams.geometry.replace(/\s+/g, ' '),
                                    maxDistance: $stateParams.maxDistance
                                }).then(
                                    function(geojson) {
                                        return geojson;
                                    },
                                    function(rejection) {
                                        swal({
                                            text: $translate.instant('error.' + rejection.status + '.title') + " " + rejection.data.message,
                                            width: 400,
                                            confirmButtonText: $translate.instant('error.buttonText'),
                                            confirmButtonColor: "#007AC9",
                                            closeOnConfirm: true
                                        });
                                        return null;
                                    }
                                );
                            } else {
                                return null;
                            }
                        }
                    }
                }
            },
            data: {pageTitle: 'Facility Search Demo'}
        });
    });

    m.controller('FacilitySearchCtrl', function($scope, $stateParams, $location, results) {
        var self = this;
        if (results) {
            self.geometry = $stateParams.geometry;
            self.searchGeometry = self.geometry;
        }
        self.geometry = $stateParams.geometry || "MULTIPOINT(";
        self.maxDistance = $stateParams.maxDistance ? parseFloat($stateParams.maxDistance) : null;
        self.results = results;

        self.coordinateCallback = function(x, y) {
            self.geometry += " " + x + " " + y;
            $scope.$apply();
            $scope.$broadcast("focus-on-geometry");
        };
        self.search = function() {
            $location.search({
                geometry: self.geometry,
                maxDistance: self.maxDistance
            });
        };
    });

    m.directive('focusOnDemand', function() {
        return {
            restrict: 'A',
            scope: true, // inherit scope
            link: function (scope, element, attrs) {
                scope.$on(attrs.focusOnDemand, function() {
                    element[0].focus();
                });
            }
        };
    });

    m.directive('searchMap', function(MapService) {
        return {
            restrict: 'E',
            scope: {
                results: "=",
                searchGeometry: "=",
                callback: "&callback"
            },
            template: '<div class="map hub-map edit-hub-map"></div>',
            transclude: false,
            link: function (scope, element, attrs, ctrl) {
                var mapCRS = MapService.mapCRS;
                var targetCRS = MapService.targetCRS;
                var GeoJSON = MapService.GeoJSON;
                var WKT = MapService.WKT;

                var facilitiesLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.facilityStyle
                });
                var searchLayer = new ol.layer.Vector({
                    source: new ol.source.Vector()
                });
                var map = MapService.createMap(element, {
                    layers: [ facilitiesLayer, searchLayer ],
                    readOnly: false,
                    noTiles: attrs.noTiles === "true" });

                map.on('dblclick', function(event) {
                    var point = new ol.geom.Point(event.coordinate).transform(mapCRS, targetCRS);
                    var coordinates = point.getCoordinates();
                    console.log("WGS84: " + coordinates[0] + " " + coordinates[1]);
                    console.log("Pixel: " + event.pixel[0] + " " + event.pixel[1]);
                    if (scope.callback) {
                        scope.callback()(coordinates[0], coordinates[1]);
                    }
                    return false;
                });

                if (scope.results) {
                    facilitiesLayer.getSource().clear();
                    var features = GeoJSON.readFeatures(scope.results);
                    facilitiesLayer.getSource().addFeatures(features);
                }
                if (scope.searchGeometry) {
                    searchLayer.getSource().clear();
                    var geometry = WKT.readGeometry(scope.searchGeometry);
                    searchLayer.getSource().addFeature(new ol.Feature(geometry));
                }
            }
        };
    });

    m.directive('searchDemoNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'facilities/searchDemoNavi.tpl.html'
        };
    });
})();