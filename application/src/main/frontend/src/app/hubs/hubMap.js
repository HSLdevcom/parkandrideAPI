(function() {
    var m = angular.module('parkandride.hubMap', [
        'parkandride.MapService',
        'parkandride.FacilityResource'
    ]);

    m.directive('hubMap', function(MapService, FacilityResource) {
        return {
            restrict: 'E',
            scope: {
                hub: '=ngModel',
                editable: '='
            },
            template: '<div class="map hub-map"></div>',
            transclude: false,
            link: function(scope, element, attrs) {
                var editable = attrs.editable == "true";

                var facilitiesLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: (editable ? MapService.facilityStyle : MapService.selectedFacilityStyle)
                });

                var width = 3;
                var hubLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.hubStyle
                });

                var map = MapService.createMap(element, [ facilitiesLayer, hubLayer ]);
                var view = map.getView();

                if (scope.hub.location) {
                    var point = new ol.format.GeoJSON().readGeometry(scope.hub.location);
                    setPoint(point);
                    view.setCenter(point.getCoordinates());
                    view.setZoom(14);
                }

                function setPoint(point) {
                    point.transform('EPSG:4326', 'EPSG:3857');
                    var feature = new ol.Feature({});
                    feature.setGeometry(point);
                    var source = hubLayer.getSource();
                    source.clear();
                    source.addFeature(feature);
                }

                if (editable) {
                    map.on('dblclick', function(event) {
                        var point = new ol.geom.Point(event.coordinate).transform('EPSG:3857', 'EPSG:4326');
                        scope.hub.location = new ol.format.GeoJSON().writeGeometry(point);
                        scope.$apply();
                        setPoint(point);
                        return false;
                    });

                    var selectFeatures = new ol.interaction.Select({
                        toggleCondition: goog.functions.TRUE,
                        style: MapService.selectedFacilityStyle,
                        layers: [ facilitiesLayer ]
                    });
                    map.addInteraction(selectFeatures);

                    var selectedFeatures = selectFeatures.getFeatures();

                    FacilityResource.findFacilitiesAsFeatureCollection().then(function(geojson) {
                        var features = new ol.format.GeoJSON().readFeatures(geojson);
                        _.forEach(features, function (feature) {
                            feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');
                            facilitiesLayer.getSource().addFeature(feature);
                            if (_.contains(scope.hub.facilityIds, feature.getId())) {
                                selectedFeatures.push(feature);
                            }
                        });
                        selectedFeatures.on("add", function (collectionEvent) {
                            scope.hub.facilityIds.push(collectionEvent.element.getId());
                            scope.$apply();
                            return true;
                        });
                        selectedFeatures.on("remove", function (collectionEvent) {
                            scope.hub.facilityIds = _.without(scope.hub.facilityIds, collectionEvent.element.getId());
                            scope.$apply();
                            return true;
                        });
                    });
                } else {
                    FacilityResource.findFacilitiesAsFeatureCollection({ ids: scope.hub.facilityIds }).then(function(geojson) {
                        var features = new ol.format.GeoJSON().readFeatures(geojson);
                        _.forEach(features, function (feature) {
                            feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');
                            facilitiesLayer.getSource().addFeature(feature);
                        });
                    });
                }
            }
        };
    });
})();