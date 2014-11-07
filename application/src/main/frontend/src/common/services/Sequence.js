(function() {
    var m = angular.module('parkandride.Sequence', []);

    var seq=0;
    m.value('Sequence', {
        nextval: function() {
            return ++seq;
        }
    });
})();
