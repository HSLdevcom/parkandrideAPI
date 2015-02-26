(function() {
    var m = angular.module('parkandride.MapService', []);

    var portResolutionThresholds = {
        hide: 7,
        dot: 4
    };

    var formatOptions = {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857'
    };
    var geoJsonFormat = new ol.format.GeoJSON();
    var wktFormat = new ol.format.WKT();

    m.value('MapService', {
            facilityStyle: new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(255, 99, 25, 0.4)' //HSL orange, transparent
                }),
                stroke: new ol.style.Stroke({
                    color: '#FF6319', //HSL orange
                    width: 2
                }),
                image: new ol.style.Circle({
                    radius: 7,
                    fill: new ol.style.Fill({
                        color: '#FF6319' //HSL orange
                    })
                })
            }),

            selectedFacilityStyle: new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(0, 122, 201, 0.4)' //HSL blue, transparent
                }),
                stroke: new ol.style.Stroke({
                    color: '#007AC9', //HSL blue
                    width: 2
                })
            }),

            facilityDrawStyle: function(feature, resolution) {
                switch(feature.getGeometry().getType()) {
                    case "Point": return [
                        new ol.style.Style({
                            image: new ol.style.Circle({
                                radius: 7,
                                fill: new ol.style.Fill({
                                    color: '#FF6319' //HSL orange
                                })
                            })
                        })
                    ];
                    case "LineString": return [
//                        new ol.style.Style({
//                            stroke: new ol.style.Stroke({
//                                color: '#000000',
//                                width: 5
//                            })
//                        }),
                        new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: '#FF6319',
                                width: 2
                            })
                        })
                    ];
                    case "Polygon": return [
                        new ol.style.Style({
                            fill: new ol.style.Fill({
                                color: 'rgba(255, 99, 25, 0.4)' //HSL orange, transparent
                            })
                        })
                    ];
                }
            },

            portsStyle: function(feature, resolution) {
                if (resolution >= portResolutionThresholds.hide) {
                    return [];
                }
                else if (resolution >= portResolutionThresholds.dot) {
                    return [new ol.style.Style({
                        image: new ol.style.Circle({
                            radius: 5,
                            fill: new ol.style.Fill({
                                color: '#007AC9' //HSL blue
                            })
                        })
                    })];
                } else {
                    var properties = feature.getProperties();
                    var entry = properties.entry ? "entry" : "noentry";
                    var exit = properties.exit ? "exit" : "noexit";
                    var pedestrian = properties.pedestrian ? "pedestrian" : "nopedestrian";
                    var bicycle = properties.bicycle ? "bicycle" : "nobicycle";
                    return [new ol.style.Style({
                        image: new ol.style.Icon({
                            anchor: [0.5, 1],
                            anchorXUnits: 'fraction',
                            anchorYUnits: 'fraction',
                            src: 'assets/ports/' + entry + "-" + exit + "-" + pedestrian + "-" + bicycle + ".gif"
                        })
                    })];
                }
            },
            isPortStyleIcon: function(resolution) {
                return resolution < portResolutionThresholds.dot;
            },

            hubStyle: new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 8,
                    fill: new ol.style.Fill({
                        color: '#007AC9' //HSL blue
                    }),
                    stroke: new ol.style.Stroke({
                        color: '#007AC9', //not used
                        width: 0
                    })
                })
            }),

            mapCRS: formatOptions.featureProjection,

            targetCRS: formatOptions.dataProjection,

            GeoJSON: {
                readGeometry: function(geojson) {
                    return geoJsonFormat.readGeometry(geojson, formatOptions);
                },
                writeGeometry: function(geometry) {
                    return geoJsonFormat.writeGeometryObject(geometry, formatOptions);
                },
                readFeatures: function(geojson) {
                    return geoJsonFormat.readFeatures(geojson, formatOptions);
                }
            },

            WKT: {
                readGeometry: function(wkt) {
                    return wktFormat.readGeometry(wkt, formatOptions);
                },
                writeGeometry: function(wkt) {
                    return wktFormat.writeGeometryObject(wkt, formatOptions);
                },
                readFeatures: function(wkt) {
                    return wktFormat.readFeatures(wkt, formatOptions);
                }
            },

            createMap: function(ngElement, options) {
                var layers = [];
                if (!options.noTiles) {
                    layers.push(new ol.layer.Tile({ source: new ol.source.OSM() }));
                }

                if (options.layers) {
                    layers = layers.concat(options.layers);
                }

                var interactions = new ol.Collection();
                interactions.push(new ol.interaction.KeyboardZoom());

                var controls = new ol.Collection();
                controls.push(new ol.control.Attribution({tipLabel: "Tietoa kartasta"}));
                controls.push(new ol.control.Zoom({zoomOutTipLabel: "Zoomaa", zoomInTipLabel: "Zoomaa"}));
                controls.push(new ol.control.FullScreen({tipLabel: "Koko näyttö"}));

                interactions.push(new ol.interaction.DoubleClickZoom());
                interactions.push(new ol.interaction.MouseWheelZoom());
                interactions.push(new ol.interaction.DragZoom());
                interactions.push(new ol.interaction.PinchZoom());
                interactions.push(new ol.interaction.KeyboardPan());
                interactions.push(new ol.interaction.DragPan({
                    kinetic: new ol.Kinetic(-0.005, 0.05, 100)
                }));

                return new ol.Map({
                    target: ngElement.children()[0],
                    controls: controls,
                    interactions: interactions,
                    layers: layers,
                    view: new ol.View({
                        center: ol.proj.transform([24.941025, 60.173324], 'EPSG:4326', 'EPSG:3857'),
                        zoom: 12
                    })
                });
            }
    });
})();