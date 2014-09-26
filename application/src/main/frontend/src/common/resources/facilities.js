(function() {
    var m = angular.module('parkandride.resources.facilities', []);

    m.factory('Capacity', function() {
        function Capacity(capacityType, built, unavailable) {
            this.capacityType = capacityType;
            this.built = built;
            this.unavailable = unavailable;
        }

        var capacityTypes = ['CAR', 'PARK_AND_RIDE', 'BICYCLE'];
        function checkType(type) {
            return capacityTypes.indexOf(type) !== -1;
        }
        Capacity.capacityTypes = angular.copy(capacityTypes);

        Capacity.build = function(k, v) {
            if (!checkType(k)) {
                return;
            }
            return new Capacity(
                k,
                v.built,
                v.unavailable
            );
        };

        Capacity.toData = function(capacity, container) {
            return container[capacity.capacityType] =  {
                "built" : capacity.built,
                "unavailable" : capacity.unavailable
            };
        };

        return Capacity;
    });

    m.factory('Capacities', function(Capacity) {
        function Capacities() {}

        Capacities.build = function(data) {
            return _.map(data, function(v, k) {
                return Capacity.build(k, v);
            });
        };

        Capacities.toData = function(capacities) {
            var container = {};
            _.forEach(capacities, function(c) { Capacity.toData(c, container); });
            return container;
        };

        return Capacities;
    });

    m.factory('Facility', function(Capacities){
        function Facility(name, aliases, capacities) {
            this.name = name;
            this.aliases = aliases;
            this.capacities = Capacities.build(capacities);
        }

        Facility.build = function(data) {
            return new Facility(
                data.name,
                data.aliases,
                data.capacities
            );
        };

        Facility.toData = function(facility) {
            var container = {};
            container.name = facility.name;
            container.aliases = facility.aliases;
            container.capacities = Capacities.toData(facility.capacities);
            container.border = {
                "type": "Polygon",
                "coordinates": [[
                    [60.25055, 25.010827],
                    [60.250023, 25.011867],
                    [60.250337, 25.012479],
                    [60.250886, 25.011454],
                    [60.25055, 25.010827]
                ]]
            };

            return container;
        };

        return Facility;
    });
})();