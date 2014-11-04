(function() {
    var m = angular.module('parkandride.facilityMap', [
        'parkandride.MapService'
    ]);

    m.directive('facilityMap', function(MapService) {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                polygon: '=ngModel',
                editable: '@',
                noTiles: '@'
            },
            template: '<div class="map facility-map"></div>',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                var editable = attrs.editable === "true";

                var map = MapService.createMap(element, { readOnly: !editable, noTiles: attrs.noTiles === "true" });
                var view = map.getView();

                var featureOverlay = new ol.FeatureOverlay({
                    style: MapService.facilityStyle
                });
                featureOverlay.setMap(map);

                if (editable) {
                    var draw = new ol.interaction.Draw({
                        features: featureOverlay.getFeatures(),
                        style: MapService.facilityDrawStyle,
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
                        ctrl.$setTouched();
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

})();