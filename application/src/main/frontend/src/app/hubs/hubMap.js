(function() {
    var m = angular.module('parkandride.hubMap', [
        'parkandride.MapService',
        'parkandride.FacilityResource'
    ]);

    function setPoint(point, layer) {
        point.transform('EPSG:4326', 'EPSG:3857');
        var feature = new ol.Feature({});
        feature.setGeometry(point);
        var source = layer.getSource();
        source.clear();
        source.addFeature(feature);
    }

    m.directive('editHubMap', function(MapService, FacilityResource) {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                hub: '=ngModel'
            },
            template: '<div class="map hub-map edit-hub-map"></div>',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                var facilitiesLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.facilityStyle
                });

                var hubLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.hubStyle
                });

                var map = MapService.createMap(element, { layers: [ facilitiesLayer, hubLayer ], readOnly: false });
                var view = map.getView();

                if (scope.hub.location) {
                    var point = new ol.format.GeoJSON().readGeometry(scope.hub.location);
                    setPoint(point, hubLayer);
                    view.setCenter(point.getCoordinates());
                    view.setZoom(14);
                } else {
                    ctrl.$setValidity("required", false);
                }

                map.on('dblclick', function(event) {
                    var point = new ol.geom.Point(event.coordinate).transform('EPSG:3857', 'EPSG:4326');
                    scope.hub.location = new ol.format.GeoJSON().writeGeometry(point);
                    scope.$apply();
                    setPoint(point, hubLayer);
                    ctrl.$setValidity("required", true);
                    return false;
                });

                var selectFeatures = new ol.interaction.Select({
                    toggleCondition: function() { return true; },
                    style: MapService.selectedFacilityStyle,
                    layers: [ facilitiesLayer ]
                });
                map.addInteraction(selectFeatures);

                var selectedFeatures = selectFeatures.getFeatures();

                FacilityResource.findFacilitiesAsFeatureCollection().then(function(geojson) {
                    var features = new ol.format.GeoJSON().readFeatures(geojson);
                    var extent = hubLayer.getSource().getExtent();

                    _.forEach(features, function (feature) {
                        feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');
                        facilitiesLayer.getSource().addFeature(feature);
                        if (_.contains(scope.hub.facilityIds, feature.getId())) {
                            selectedFeatures.push(feature);
                            extent = ol.extent.extend(extent, feature.getGeometry().getExtent());
                        }
                    });
                    if (!_.isEmpty(scope.hub.facilityIds)) {
                        view.fitExtent(extent, map.getSize());
                    }
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
            }
        };
    });

    m.directive('viewHubMap', function(MapService, FacilityResource) {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                hub: '=ngModel',
                editable: '='
            },
            template: '<div class="map hub-map"></div>',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                var facilitiesLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.selectedFacilityStyle
                });

                var width = 3;
                var hubLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.hubStyle
                });

                var map = MapService.createMap(element, { layers: [ facilitiesLayer, hubLayer ], readOnly: true });
                var view = map.getView();

                var point = new ol.format.GeoJSON().readGeometry(scope.hub.location);
                setPoint(point, hubLayer);
                view.setCenter(point.getCoordinates());
                view.setZoom(14);

                if (!_.isEmpty(scope.hub.facilityIds)) {
                    FacilityResource.findFacilitiesAsFeatureCollection({ ids: scope.hub.facilityIds }).then(function(geojson) {
                        var features = new ol.format.GeoJSON().readFeatures(geojson);
                        _.forEach(features, function (feature) {
                            feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');
                            facilitiesLayer.getSource().addFeature(feature);
                        });
                        var extent = ol.extent.extend(hubLayer.getSource().getExtent(), facilitiesLayer.getSource().getExtent());
                        if (!ol.extent.isEmpty(extent)) {
                            view.fitExtent(extent, map.getSize());
                        }
                    });
                }
            }
        };
    });
})();