(function() {
    var m = angular.module('parkandride.resources.facilities', []);

    m.factory('Capacities', function() {
        return {
            build: function (data) {
                return _.reduce(
                    data,
                    function(target, capacity, key) {
                        var copy = _.clone(capacity);
                        copy.capacityType = key;
                        target.push(copy);
                        return target;
                    },
                    []);
            },

            toData: function (capacities) {
                return _.reduce(
                    capacities,
                    function(target, capacity) {
                        if (capacity.built > 0 && capacity.unavailable >= 0) {
                            var copy = _.clone(capacity);
                            delete copy.capacityType;
                            target[capacity.capacityType] = copy;
                        }
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