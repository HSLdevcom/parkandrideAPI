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

                if (attrs.editable == 'true') {
                    var draw = new ol.interaction.Draw({
                        features: featureOverlay.getFeatures(),
                        type: "Polygon"
                    });
                    draw.on("drawstart", function(drawEvent) {
                        featureOverlay.getFeatures().clear();
                    });
                    draw.on("drawend", function(drawEvent) {
                        var geometry = drawEvent.feature.getGeometry().clone().transform('EPSG:3857', 'EPSG:4326');
                        scope.polygon = new ol.format.GeoJSON().writeGeometry(geometry);
                        scope.$apply();
                    });
                    map.addInteraction(draw);
                }

                if (scope.polygon) {
                    var geometry = new ol.format.GeoJSON().readGeometry(scope.polygon).transform('EPSG:4326', 'EPSG:3857');
                    var feature = new ol.Feature({});
                    feature.setGeometry(geometry);
                    featureOverlay.getFeatures().clear();
                    featureOverlay.addFeature(feature);
                    view.fitGeometry(
                        geometry,
                        map.getSize()
                    );
                }
            }
        };
    });
})();