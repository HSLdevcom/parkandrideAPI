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
            url: '/search-demo', // TODO set facilities base path on upper level and say here /create ?
            views: {
                "main": {
                    controller: 'FacilitySearchCtrl as searchCtrl',
                    templateUrl: 'facilities/searchDemo.tpl.html',
                    resolve: {
                    }
                }
            },
            data: {pageTitle: 'Facility Search Demo'}
        });
    });

    m.controller('FacilitySearchCtrl', function($scope, FacilityResource) {
        var self = this;
        self.geometry = "MULTIPOINT(";
        self.maxDistance = null;
        self.results = null;
        self.searchGeometry = null;
        self.coordinateCallback = function(x, y) {
            self.geometry += " " + x + " " + y;
            $scope.$apply();
            $scope.$broadcast("focus-on-geometry");
        };
        self.search = function() {
            self.searchGeometry = self.geometry;
            FacilityResource.findFacilitiesAsFeatureCollection({
                geometry: self.geometry,
                maxDistance: self.maxDistance
            }).then(function(geojson) {
                self.results = geojson;
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
                    console.log("" + coordinates[0] + " " + coordinates[1]);
                    if (scope.callback) {
                        scope.callback()(coordinates[0], coordinates[1]);
                    }
                    return false;
                });

                scope.watch("results", function(newVal, oldVal, results) { // WTF?!?
                    facilitiesLayer.getSource().clear();
                    if (results) {
                        var features = new ol.format.GeoJSON().readFeatures(results);

                        _.forEach(features, function (feature) {
                            feature.getGeometry().transform(targetCRS, mapCRS);
                            facilitiesLayer.getSource().addFeature(feature);
                        });
                    }
                });
                scope.watch("searchGeometry", function(newVal, oldVal, wkt) {
                    searchLayer.getSource().clear();
                    if (wkt && _.endsWith(wkt, ')')) {
                        var geometry = new ol.format.WKT().readGeometry(wkt);
                        geometry.transform(targetCRS, mapCRS);
                        searchLayer.getSource().addFeature(new ol.Feature(geometry));
                    }
                });
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