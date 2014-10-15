(function() {
    var m = angular.module('parkandride.MapService', []);

    m.value('MapService', {
        facilityStyle: new ol.style.Style({
            fill: new ol.style.Fill({
                color: 'rgba(255, 255, 255, 0.5)'
            }),
            stroke: new ol.style.Stroke({
                color: '#FF6319',
                width: 2
            }),
            image: new ol.style.Circle({
                radius: 7,
                fill: new ol.style.Fill({
                    color: '#ffcc33'
                })
            })
        }),

        selectedFacilityStyle: new ol.style.Style({
            fill: new ol.style.Fill({
                color: [255, 255, 255, 0.5] // transparent white
            }),
            stroke: new ol.style.Stroke({
                color: "#007AC9",
                width: 3
            })
        }),

        hubStyle: new ol.style.Style({
            image: new ol.style.Circle({
                radius: 8,
                fill: new ol.style.Fill({
                    color: [255, 255, 255, 1] // white
                }),
                stroke: new ol.style.Stroke({
                    color: [0, 0, 0, 1], // blue
                    width: 3
                })
            })
        }),

        createMap: function(ngElement, extraLayers) {
            var layers = ([
                    new ol.layer.Tile({
                        source: new ol.source.OSM()
                    })
                ])
                .concat(extraLayers);

            return new ol.Map({
                target: ngElement.children()[0],
                controls: ol.control.defaults().extend([
                    new ol.control.FullScreen()
                ]),
                layers: layers,
                view: new ol.View({
                    center: ol.proj.transform([24.941025, 60.173324], 'EPSG:4326', 'EPSG:3857'),
                    zoom: 12
                })
            });
        }
    });
})();