(function() {
    var m = angular.module('parkandride.hubMap', [
        'parkandride.MapService',
        'parkandride.FacilityResource'
    ]);

    m.factory("HubMapCommon", function(MapService) {
        return {
            setPoint: function (point, layer) {
                point.transform(MapService.targetCRS, MapService.mapCRS);
                var feature = new ol.Feature(point);
                var source = layer.getSource();
                source.clear();
                source.addFeature(feature);
            }
        };
    });

    m.directive('editHubMap', function(MapService, HubMapCommon, FacilityResource) {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                hub: '=ngModel',
                facilities: '=',
                noTiles: '@'
            },
            template: '<div class="map hub-map edit-hub-map"></div>',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                var mapCRS = MapService.mapCRS;
                var targetCRS = MapService.targetCRS;

                var facilitiesLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.facilityStyle
                });

                var hubLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.hubStyle
                });

                var map = MapService.createMap(element, {
                    layers: [ facilitiesLayer, hubLayer ],
                    readOnly: false,
                    noTiles: attrs.noTiles === "true" });

                var view = map.getView();

                if (scope.hub.location) {
                    var point = new ol.format.GeoJSON().readGeometry(scope.hub.location);
                    HubMapCommon.setPoint(point, hubLayer);
                    view.setCenter(point.getCoordinates());
                    view.setZoom(14);
                } else {
                    ctrl.$setValidity("required", false);
                }

                map.on('dblclick', function(event) {
                    var point = new ol.geom.Point(event.coordinate).transform(mapCRS, targetCRS);
                    scope.hub.location = new ol.format.GeoJSON().writeGeometry(point);
                    ctrl.$setValidity("required", true);
                    ctrl.$setTouched();
                    scope.$apply();
                    HubMapCommon.setPoint(point, hubLayer);
                    return false;
                });

                // https://github.com/openlayers/ol3/pull/2936
                var selectFeatures = new ol.interaction.Select({
                    toggleCondition: function() { return true; },
                    style: MapService.selectedFacilityStyle,
                    layers: [ facilitiesLayer ]
                });
                map.addInteraction(selectFeatures);
                var selectedFeatures = selectFeatures.getFeatures();

                function addFeatureAsFacility(feature) {
                    var facility = feature.getProperties();
                    facility.id = feature.getId();
                    var indx = _.sortedIndex(scope.facilities, facility, function(f) { return f._index; });
                    scope.facilities.splice(indx, 0, facility);
                }

                function removeFacility(facilityId) {
                    var index = _.findIndex(scope.facilities, function(facility) { return facility.id === facilityId; });
                    if (index >= 0) {
                        scope.facilities.splice(index, 1);
                    }
                }

                function addFeatureListener(collectionEvent) {
                    var feature = collectionEvent.element;
                    scope.hub.facilityIds.push(feature.getId());
                    addFeatureAsFacility(feature);
                    scope.$apply();
                    return true;
                }

                function removeFeatureListener(collectionEvent) {
                    var facilityId = collectionEvent.element.getId();
                    scope.hub.facilityIds = _.without(scope.hub.facilityIds, facilityId);
                    removeFacility(facilityId);
                    scope.$apply();
                    return true;
                }

                FacilityResource.findFacilitiesAsFeatureCollection().then(function(geojson) {
                    var features = new ol.format.GeoJSON().readFeatures(geojson);
                    var extent = hubLayer.getSource().getExtent();

                    _.forEach(features, function (feature) {
                        feature.getGeometry().transform(targetCRS, mapCRS);
                        facilitiesLayer.getSource().addFeature(feature);
                        if (_.contains(scope.hub.facilityIds, feature.getId())) {
                            selectedFeatures.push(feature);
                            addFeatureAsFacility(feature);
                            extent = ol.extent.extend(extent, feature.getGeometry().getExtent());
                        }
                    });
                    if (!_.isEmpty(scope.hub.facilityIds)) {
                        view.fitExtent(extent, map.getSize());
                    }
                    selectedFeatures.on("add", addFeatureListener);
                    selectedFeatures.on("remove", removeFeatureListener);
                });
            }
        };
    });

    m.directive('viewHubMap', function($log, MapService, HubMapCommon, FacilityResource) {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                hub: '=ngModel',
                editable: '@',
                noTiles: '@'
            },
            template: '<div class="map hub-map"></div>',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                var mapCRS = MapService.mapCRS;
                var targetCRS = MapService.targetCRS;

                var facilitiesLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.selectedFacilityStyle
                });

                var width = 3;
                var hubLayer = new ol.layer.Vector({
                    source: new ol.source.Vector(),
                    style: MapService.hubStyle
                });

                var map = MapService.createMap(element, { layers: [ facilitiesLayer, hubLayer ], readOnly: true, noTiles: attrs.noTiles === "true"});
                var view = map.getView();

                var point = new ol.format.GeoJSON().readGeometry(scope.hub.location);
                HubMapCommon.setPoint(point, hubLayer);
                view.setCenter(point.getCoordinates());
                view.setZoom(14);

                if (!_.isEmpty(scope.hub.facilityIds)) {
                    FacilityResource.findFacilitiesAsFeatureCollection({ ids: scope.hub.facilityIds }).then(function(geojson) {
                        var features = new ol.format.GeoJSON().readFeatures(geojson);
                        _.forEach(features, function (feature) {
                            feature.getGeometry().transform(targetCRS, mapCRS);
                            facilitiesLayer.getSource().addFeature(feature);
                        });
                        var extent = ol.extent.extend(hubLayer.getSource().getExtent(), facilitiesLayer.getSource().getExtent());
                        if (!ol.extent.isEmpty(extent)) {
                            view.fitExtent(extent, map.getSize());
                        }
                    });
                }
                map.on('dblclick', function(event) {
                    $log.debug("{x: "+ event.pixel[0] + ", y: " + event.pixel[1] + "}");
                    return true;
                });
            }
        };
    });
})();
