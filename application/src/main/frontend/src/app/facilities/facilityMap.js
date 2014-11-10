(function() {
    var m = angular.module('parkandride.facilityMap', [
        'parkandride.Sequence',
        'parkandride.MapService'
    ]);

    m.controller("PortEditCtrl", function ($scope, $modalInstance, port, create) {
        $scope.port = port;
        $scope.titleKey = 'facilities.ports.' + (create ? 'create' : 'edit');
        $scope.ok = function () {
            $modalInstance.close($scope.port);
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });

    m.directive('facilityMap', function(MapService, $modal, Sequence) {
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
                var mapCRS = MapService.mapCRS;
                var targetCRS = MapService.targetCRS;

                var locationSource = new ol.source.Vector();
                var locationLayer = new ol.layer.Vector({
                    source: locationSource,
                    style: MapService.facilityStyle
                });

                var portsSource = new ol.source.Vector();
                var portsLayer = new ol.layer.Vector({
                    source: portsSource,
                    style: MapService.portsStyle
                });

                var map = MapService.createMap(element, {
                    layers: [ locationLayer, portsLayer],
                    readOnly: !editable,
                    noTiles: attrs.noTiles === "true" });

                var view = map.getView();

                if (editable) {
                    // LOCATION
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
                        var polygon = drawEvent.feature.getGeometry().clone().transform(mapCRS, targetCRS);
                        scope.polygon = new ol.format.GeoJSON().writeGeometry(polygon);
                        ctrl.$setTouched();
                        scope.$apply();
                    });
                    map.addInteraction(drawLocation);

                    // PORTS
                    var drawPortCondition = function(mapBrowserEvent) {
                        return scope.editMode == 'ports' && ol.events.condition.noModifierKeys(mapBrowserEvent);
                    };
                    var setPortAsFeature = function(port) {
                        var feature = portsSource.getFeatureById(port._id);
                        if (feature == null) {
                            var geometry = new ol.format.GeoJSON().readGeometry(port.location).transform(targetCRS, mapCRS);
                            feature = new ol.Feature(geometry);
                            feature.setId(port._id);
                            portsSource.addFeature(feature);
                        }
                        feature.setProperties(port);
                    };
                    var editPort = function(port, create) {
                        var modalInstance = $modal.open({
                            templateUrl: 'facilities/portEdit.tpl.html',
                            controller: 'PortEditCtrl',
                            resolve: {
                                port: function () {
                                    return _.clone(port);
                                },
                                create: function() {
                                    return create;
                                }
                            }
                        });
                        return modalInstance.result;
                    };
                    map.on('dblclick', function(event) {
                        if (drawPortCondition(event)) {
                            // Edit existing port
                            var create = false;
                            var port = map.forEachFeatureAtPixel(event.pixel,
                                function(feature, layer) {
                                    if (layer != null) {
                                        return feature.getProperties();
                                    }
                                },
                                undefined,
                                function(layer) {
                                    return layer === portsLayer;
                                });
                            if (!port) {
                                // Create new port
                                create = true;
                                var point = new ol.geom.Point(event.coordinate).transform(mapCRS, targetCRS);
                                port = {
                                    _id: Sequence.nextval(),
                                    location: new ol.format.GeoJSON().writeGeometry(point),
                                    entry: true,
                                    exit: true,
                                    pedestrian: false
                                };
                            }
                            editPort(port, create).then(function (port) {
                                setPortAsFeature(port);
                            });
                            return false;
                        }
                    });
                }

                if (scope.polygon) {
                    var polygon = new ol.format.GeoJSON().readGeometry(scope.polygon).transform(targetCRS, mapCRS);
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