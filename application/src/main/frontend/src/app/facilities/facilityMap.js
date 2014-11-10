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

    var CancelControl = function(opt_options) {

        var options = opt_options || {};

        var tip = document.createElement('span');
        tip.setAttribute('role', 'tooltip');
        tip.innerHTML = "Peruuta";

        var button = document.createElement('button');
        button.className = 'map-cancel ol-unselectable ol-has-tooltip';
        button.appendChild(tip);
        button.appendChild(document.createTextNode("X"));

        var handleCancel = function(e) {
            // prevent #rotate-north anchor from getting appended to the url
            e.preventDefault();
            options.callback(e);
        };

        button.addEventListener('click', handleCancel, false);
        button.addEventListener('touchstart', handleCancel, false);

        var element = document.createElement('div');
        element.className = 'map-cancel ol-unselectable ol-control';
        element.appendChild(button);

        ol.control.Control.call(this, {
            element: element,
            target: options.target
        });

    };
    ol.inherits(CancelControl, ol.control.Control);


    m.directive('facilityMap', function(MapService, $modal, Sequence) {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                facility: '=ngModel',
                editMode: '=', // location | ports
                noTiles: '@'
            },
            template: '<div class="map facility-map"></div>',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                var facility = scope.facility;
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

                var setLocation = function(location) {
                    var polygon = new ol.format.GeoJSON().readGeometry(location).transform(targetCRS, mapCRS);
                    var feature = new ol.Feature(polygon);
                    locationSource.clear();
                    locationSource.addFeature(feature);
                };

                var setPortAsFeature = function(port) {
                    var feature = portsSource.getFeatureById(port._id);
                    if (feature == null) {
                        // New port
                        var geometry = new ol.format.GeoJSON().readGeometry(port.location).transform(targetCRS, mapCRS);
                        feature = new ol.Feature(geometry);
                        feature.setId(port._id);
                        portsSource.addFeature(feature);
                    }
                    feature.setProperties(port);
                };

                if (editable) {
                    var changeMode = false;

                    // LOCATION
                    var drawLocationCondition = function(mapBrowserEvent) {
                        return scope.editMode == 'location' && ol.events.condition.noModifierKeys(mapBrowserEvent);
                    };
                    var drawLocation = new ol.interaction.Draw({
                        condition: drawLocationCondition,
                        style: MapService.facilityDrawStyle,
                        type: "Polygon"
                    });
                    var cancelControl = new CancelControl({
                        callback: function() {
                            // setMap(null) aborts drawing as abortDrawing_ -function is hidden
                            drawLocation.setMap(null);
                            drawLocation.setMap(map);
                            locationLayer.setOpacity(1);
                            map.removeControl(cancelControl);
                        }
                    });
                    drawLocation.on("drawstart", function(drawEvent) {
                        if (document.activeElement) {
                            document.activeElement.blur();
                        }
                        locationLayer.setOpacity(0);
                        map.addControl(cancelControl);
                    });
                    drawLocation.on("drawend", function(drawEvent) {
                        var polygon = drawEvent.feature.getGeometry().clone().transform(mapCRS, targetCRS);
                        facility.location = new ol.format.GeoJSON().writeGeometry(polygon);
                        setLocation(facility.location);
                        changeMode = true;
                        scope.editMode = 'ports';
                        locationLayer.setOpacity(1);
                        map.removeControl(cancelControl);
                        ctrl.$setTouched();
                        scope.$apply();
                    });
                    map.addInteraction(drawLocation);

                    // PORTS
                    var drawPortCondition = function(mapBrowserEvent) {
                        var change = changeMode;
                        changeMode = false;
                        return !change && scope.editMode == 'ports' && ol.events.condition.noModifierKeys(mapBrowserEvent);
                    };
                    var findPortIndex = function(portId) {
                        return _.findIndex(facility.ports, function(p) {
                            return p._id == portId;
                        });
                    };
                    var findPortAtPixel = function(pixel) {
                        var portId = map.forEachFeatureAtPixel(pixel,
                            function(feature, layer) {
                                if (layer != null) {
                                    return feature.getId();
                                }
                            },
                            undefined,
                            function(layer) {
                                return layer === portsLayer;
                            });
                        if (portId) {
                            return facility.ports[findPortIndex(portId)];
                        }
                        return null;
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
                            var port = findPortAtPixel(event.pixel);
                            if (!port) {
                                // Create new port
                                create = true;
                                var point = new ol.geom.Point(event.coordinate).transform(mapCRS, targetCRS);
                                port = {
                                    location: new ol.format.GeoJSON().writeGeometry(point),
                                    entry: true,
                                    exit: true,
                                    pedestrian: false
                                };
                            }
                            editPort(port, create).then(function (port) {
                                setPortAsFeature(port);
                                if (port._id) {
                                    facility.ports[findPortIndex(port._id)] = port;
                                } else {
                                    port._id = Sequence.nextval();
                                    facility.ports.push(port);
                                }
                            });
                            return false;
                        }
                    });
                }

                if (facility.location) {
                    setLocation(facility.location);

                    for (var i=0; i < facility.ports.length; i++) {
                        setPortAsFeature(facility.ports[i]);
                    }

                    var extent = ol.extent.extend(portsSource.getExtent(), locationSource.getExtent());
                    if (!ol.extent.isEmpty(extent)) {
                        view.fitExtent(extent, map.getSize());
                    }
                }
            }
        };
    });

})();