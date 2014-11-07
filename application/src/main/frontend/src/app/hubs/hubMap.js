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
                hub: '=ngModel',
                facilities: '=',
                noTiles: '@'
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

                var map = MapService.createMap(element, {
                    layers: [ facilitiesLayer, hubLayer ],
                    readOnly: false,
                    noTiles: attrs.noTiles === "true" });

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
                    ctrl.$setValidity("required", true);
                    ctrl.$setTouched();
                    scope.$apply();
                    setPoint(point, hubLayer);
                    return false;
                });

                var selectFeatures = new ol.interaction.Select({
                    toggleCondition: function() { return true; },
                    style: MapService.selectedFacilityStyle,
                    layers: [ facilitiesLayer ]
                });
                map.addInteraction(selectFeatures);

                function addFeatureAsFacility(feature) {
                    var facility = feature.getProperties();
                    facility.id = feature.getId();
                    var indx = _.sortedIndex(scope.facilities, facility, function(f) { return f.name.fi.toUpperCase(); });
                    scope.facilities.splice(indx, 0, facility);
                }

                var selectedFeatures = selectFeatures.getFeatures();

                FacilityResource.findFacilitiesAsFeatureCollection().then(function(geojson) {
                    var features = new ol.format.GeoJSON().readFeatures(geojson);
                    var extent = hubLayer.getSource().getExtent();

                    _.forEach(features, function (feature) {
                        feature.getGeometry().transform('EPSG:4326', 'EPSG:3857');
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
                    selectedFeatures.on("add", function (collectionEvent) {
                        var feature = collectionEvent.element;
                        scope.hub.facilityIds.push(feature.getId());
                        addFeatureAsFacility(feature);
                        scope.$apply();
                        return true;
                    });
                    selectedFeatures.on("remove", function (collectionEvent) {
                        var facilityId = collectionEvent.element.getId();
                        scope.hub.facilityIds = _.without(scope.hub.facilityIds, facilityId);
                        var index = _.findIndex(scope.facilities, function(facility) { return facility.id === facilityId; });
                        if (index >= 0) {
                            scope.facilities.splice(index, 1);
                        }
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
                editable: '@',
                noTiles: '@'
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

                var map = MapService.createMap(element, { layers: [ facilitiesLayer, hubLayer ], readOnly: true, noTiles: attrs.noTiles === "true"});
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
                if (console.log) {
                    map.on('dblclick', function(event) {
                        console.log("{x: "+ event.pixel[0] + ", y: " + event.pixel[1] + "}");
                        return true;
                    });
                }
            }
        };
    });
})();