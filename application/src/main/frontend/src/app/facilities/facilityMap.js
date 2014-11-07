(function() {
    var m = angular.module('parkandride.facilityMap', [
        'parkandride.MapService'
    ]);

    m.directive('facilityMap', function(MapService, $modal) {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                polygon: '=ngModel',
                editMode: '=', // location | ports
                noTiles: '@'
            },
            template: '<div class="map facility-map"></div>',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                var editable = scope.editMode == 'location' || scope.editMode == 'ports';

                var locationSource = new ol.source.Vector();
                var locationLayer = new ol.layer.Vector({
                    source: locationSource,
                    style: MapService.facilityStyle
                });

                var portsSource = new ol.source.Vector();
                var portsLayer = new ol.layer.Vector({
                    source: portsSource,
                    style: MapService.facilityStyle
                });

                var map = MapService.createMap(element, {
                    layers: [ portsLayer, locationLayer],
                    readOnly: !editable,
                    noTiles: attrs.noTiles === "true" });

                var view = map.getView();

                if (editable) {
                    var drawLocationCondition = function(mapBrowserEvent) {
                        return scope.editMode == 'location' && ol.events.condition.noModifierKeys(mapBrowserEvent);
                    };
                    var drawLocation = new ol.interaction.Draw({
                        source: locationSource,
                        condition: drawLocationCondition,
                        style: MapService.facilityDrawStyle,
                        type: "Polygon"
                    });
                    drawLocation.on("drawstart", function(drawEvent) {
                        console.log("drawstart");
                        if (document.activeElement) {
                            document.activeElement.blur();
                        }
                        locationSource.clear();
                    });
                    drawLocation.on("drawend", function(drawEvent) {
                        console.log("drawend");
                        var polygon = drawEvent.feature.getGeometry().clone().transform('EPSG:3857', 'EPSG:4326');
                        scope.polygon = new ol.format.GeoJSON().writeGeometry(polygon);
                        ctrl.$setTouched();
                        scope.$apply();
                    });
                    map.addInteraction(drawLocation);


                    var drawPortCondition = function(mapBrowserEvent) {
                        return scope.editMode == 'ports' && ol.events.condition.noModifierKeys(mapBrowserEvent);
                    };
                    map.on('dblclick', function(event) {
                        if (drawPortCondition(event)) {
                            var point = new ol.geom.Point(event.coordinate).transform('EPSG:3857', 'EPSG:4326');
                            var feature = new ol.Feature({});
                            feature.setGeometry(new ol.geom.Point(event.coordinate));
                            portsSource.addFeature(feature);
                            return false;
                        }
                    });
                }

                if (scope.polygon) {
                    var polygon = new ol.format.GeoJSON().readGeometry(scope.polygon).transform('EPSG:4326', 'EPSG:3857');
                    var feature = new ol.Feature({});
                    feature.setGeometry(polygon);
                    locationSource.getFeatures().clear();
                    locationSource.addFeature(feature);

                    view.fitGeometry(
                        polygon,
                        map.getSize()
                    );
                }
            }
        };
    });

})();