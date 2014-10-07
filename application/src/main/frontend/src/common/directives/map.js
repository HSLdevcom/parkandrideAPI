(function() {
    var m = angular.module('parkandride.directives.map', []);

    m.directive('polygon', function() {
        return {
            restrict: 'E',
            scope: {
                polygon: '=ngModel',
                editable: '='
            },
            template: '<div class="polygon-map"></div>',
            transclude: false,
            link: function(scope, element, attrs) {

                var view = new ol.View({
                    center: ol.proj.transform([24.941025, 60.173324], 'EPSG:4326', 'EPSG:3857'),
                    zoom: 12
                });

                var map = new ol.Map({
                    target: element.children()[0],
                    controls: ol.control.defaults().extend([
                        new ol.control.FullScreen()
                    ]),
                    layers: [
                        new ol.layer.Tile({
                            source: new ol.source.OSM()
                        })
                    ],
                    view: view
                });

                var featureOverlay = new ol.FeatureOverlay({
                    style: new ol.style.Style({
                        fill: new ol.style.Fill({
                            color: 'rgba(255, 255, 255, 0.5)'
                        }),
                        stroke: new ol.style.Stroke({
                            color: '#ffcc33',
                            width: 2
                        }),
                        image: new ol.style.Circle({
                            radius: 7,
                            fill: new ol.style.Fill({
                                color: '#ffcc33'
                            })
                        })
                    })
                });
                featureOverlay.setMap(map);

                if (attrs.editable == "true") {
                    var draw = new ol.interaction.Draw({
                        features: featureOverlay.getFeatures(),
                        type: "Polygon"
                    });
                    draw.on("drawstart", function(drawEvent) {
                        if (document.activeElement) {
                            document.activeElement.blur();
                        }
                        featureOverlay.getFeatures().clear();
                    });
                    draw.on("drawend", function(drawEvent) {
                        var polygon = drawEvent.feature.getGeometry().clone().transform('EPSG:3857', 'EPSG:4326');
                        scope.polygon = new ol.format.GeoJSON().writeGeometry(polygon);
                        scope.$apply();
                    });
                    map.addInteraction(draw);
                }

                if (scope.polygon) {
                    var polygon = new ol.format.GeoJSON().readGeometry(scope.polygon).transform('EPSG:4326', 'EPSG:3857');
                    var feature = new ol.Feature({});
                    feature.setGeometry(polygon);
                    featureOverlay.getFeatures().clear();
                    featureOverlay.addFeature(feature);

                    view.fitGeometry(
                        polygon,
                        map.getSize()
                    );
                }
            }
        };
    });

    m.directive('hubMap', function() {
        return {
            restrict: 'E',
            scope: {
                point: '=ngModel',
                editable: '=',
                findFeaturesFn: '&findFeaturesFn'
            },
            template: '<div class="hub-map"></div>',
            transclude: false,
            link: function(scope, element, attrs) {

                var view = new ol.View({
                    center: ol.proj.transform([24.941025, 60.173324], 'EPSG:4326', 'EPSG:3857'),
                    zoom: 12
                });

                var map = new ol.Map({
                    target: element.children()[0],
                    controls: ol.control.defaults().extend([
                        new ol.control.FullScreen()
                    ]),
                    layers: [
                        new ol.layer.Tile({
                            source: new ol.source.OSM()
                        })
                    ],
                    view: view
                });

                var featureOverlay = new ol.FeatureOverlay({
                    style: new ol.style.Style({
                        fill: new ol.style.Fill({
                            color: 'rgba(255, 255, 255, 0.5)'
                        }),
                        stroke: new ol.style.Stroke({
                            color: '#ffcc33',
                            width: 2
                        }),
                        image: new ol.style.Circle({
                            radius: 7,
                            fill: new ol.style.Fill({
                                color: '#ffcc33'
                            })
                        })
                    })
                });
                featureOverlay.setMap(map);

                if (attrs.editable == "true") {
                    map.on('dblclick', function(event) {
                        var point = new ol.geom.Point(event.coordinate).transform('EPSG:3857', 'EPSG:4326');
                        scope.point = new ol.format.GeoJSON().writeGeometry(point);
                        scope.$apply();
                        setPoint(point);
                        return false;
                    });
                }

                if (scope.point) {
                    var point = new ol.format.GeoJSON().readGeometry(scope.point);
                    setPoint(point);
                }

                function setPoint(point) {
                    point.transform('EPSG:4326', 'EPSG:3857');
                    var feature = new ol.Feature({});
                    feature.setGeometry(point);
                    featureOverlay.getFeatures().clear();
                    featureOverlay.addFeature(feature);
                    view.setCenter(point.getCoordinates());
                    view.setZoom(14);
                }

                /*
                if (scope.findFeaturesFn) {
                    _.forEach(scope.findFeaturesFn(), function(f) {
                        featureOverlay.addFeature(f);
                    });
                }
                */
            }
        };
    });
})();