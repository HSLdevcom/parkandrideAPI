(function() {
    var m = angular.module('parkandride.resources.facilities', []);

    m.value('capacityTypes', ['CAR', 'PARK_AND_RIDE', 'BICYCLE']);

    m.factory('Capacities', function() {
        return {
            build: function (data) {
                return _.reduce(
                    data,
                    function(target, capacity, key) {
                        var copy = _.clone(capacity);
                        copy.capacityType = key;
                        return target;
                    },
                    []);
            },

            toData: function (capacities) {
                return _.reduce(
                    capacities,
                    function(target, capacity) {
                        target[capacity.capacityType] = _.clone(capacity);
                        delete capacity.capacityType;
                        return target;
                    },
                    {});
            }
        };
    });

    m.factory('Facility', function(Capacities){
        return {
            build: function(data) {
                var copy = _.clone(data);
                copy.capacities = Capacities.build(data.capacities);
                return copy;
            },

            toData: function(facility) {
                var copy = _.clone(facility);
                copy.capacities = Capacities.toData(facility.capacities);
                return copy;
            }
        };
    });
})();