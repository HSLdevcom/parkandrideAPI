(function() {
    var m = angular.module('parkandride.facilityMap', [
        'parkandride.Sequence',
        'parkandride.MapService',
        'parkandride.FacilityResource'
    ]);

    m.controller("PortModalCtrl", function ($scope, $modalInstance, EVENTS, port, mode) {
        $scope.port = port;
        $scope.titleKey = 'facilities.ports.' + mode;

        $scope.hasAddress = function() {
            return port.address && port.address.streetAddress || port.address.postalCode || port.address.city;
        };

        $scope.ok = function (form) {
            $scope.$broadcast(EVENTS.showErrorsCheckValidity);
            if (!form || form.$valid) {
                $modalInstance.close($scope.port);
            }
        };
        $scope.remove = function () {
            $scope.port.entry = false;
            $scope.port.exit = false;
            $scope.port.pedestrian = false;
            $scope.port.bicycle = false;
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


    m.directive('facilityMap', function(MapService, $modal, Sequence, FacilityResource) {
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
                var openPort = function(port, mode) {
                    var modalInstance = $modal.open({
                        templateUrl: (mode === 'view' ? 'facilities/portView.tpl.html' : 'facilities/portEdit.tpl.html'),
                        controller: 'PortModalCtrl',
                        resolve: {
                            port: function () {
                                return _.cloneDeep(port);
                            },
                            mode: function() {
                                return mode;
                            }
                        },
                        backdrop: 'static'
                    });
                    return modalInstance.result;
                };

                var adjustResolution = function(view) {
                    // if ports are shown as icon, increment resolution to prevent port icon from overflowing
                    var resolution = view.getResolution();
                    if (MapService.isPortStyleIcon(resolution)) {
                        view.setResolution(resolution + 1);
                    }
                };

                if (editable) {
                    var portsDisabled = false;

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

                        locationLayer.setOpacity(1);
                        map.removeControl(cancelControl);
                        ctrl.$setValidity("required", true);
                        ctrl.$setTouched();
                        scope.$apply();

                        // Disable port editing for 1 ms to prevent dblclick/drawend adding a new port
                        portsDisabled = true;
                        setTimeout(function() { portsDisabled = false; }, 1);
                    });
                    map.addInteraction(drawLocation);

                    // PORTS
                    var drawPortCondition = function(mapBrowserEvent) {
                        return !portsDisabled && scope.editMode == 'ports' && ol.events.condition.noModifierKeys(mapBrowserEvent);
                    };
                    map.on('dblclick', function(event) {
                        if (drawPortCondition(event)) {
                            // Edit existing port
                            var mode = "edit";
                            var port = findPortAtPixel(event.pixel);
                            if (!port) {
                                // Create new port
                                mode = "create";
                                var point = new ol.geom.Point(event.coordinate).transform(mapCRS, targetCRS);
                                port = FacilityResource.newPort(new ol.format.GeoJSON().writeGeometry(point));
                            }
                            openPort(port, mode).then(function (port) {
                                if (port._id) {
                                    var portIndex = findPortIndex(port._id);
                                    if (port.entry || port.exit || port.pedestrian || port.bicycle) {
                                        facility.ports[portIndex] = port;
                                        setPortAsFeature(port);
                                    } else {
                                        facility.ports.splice(portIndex, 1);
                                        portsSource.removeFeature(portsSource.getFeatureById(port._id));
                                    }
                                } else {
                                    port._id = Sequence.nextval();
                                    facility.ports.push(port);
                                    setPortAsFeature(port);
                                }
                            });
                            return false;
                        }
                    });
                    // FIXME: Moving ports around by dragging won't work until
                    // https://github.com/openlayers/ol3/issues/2940 is fixed
//                    var selectInteraction = new ol.interaction.Select({
//                        layers: [ portsLayer ]
//                    });
//                    map.addInteraction(selectInteraction);
//                    map.addInteraction(new ol.interaction.Modify({
//                        features: selectInteraction.getFeatures()
//                    }));
                }
                // view mode
                else {
                    var viewPort;
                    map.on('click', function(event) {
                        var port = findPortAtPixel(event.pixel);
                        if (port) {
                            viewPort = true;
                            event.preventDefault(); // prevent zoom
                            openPort(port, "view");
                        } else {
                            viewPort = false;
                        }
                    });
                    map.on('dblclick', function(event) {
                        if (viewPort) {
                            event.preventDefault();
                        }
                    });
                }

                if (facility.location) {
                    setLocation(facility.location);

                    for (var i=0; i < facility.ports.length; i++) {
                        setPortAsFeature(facility.ports[i]);
                    }

                    var extent = ol.extent.extend(portsSource.getExtent(), locationSource.getExtent());
                    view.fitExtent(extent, map.getSize());
                    adjustResolution(view);
                } else {
                    ctrl.$setValidity("required", false);
                }
            }
        };
    });

})();