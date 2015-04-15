// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

(function() {
    var m = angular.module('parkandride.Sequence', []);

    var seq=0;
    m.value('Sequence', {
        nextval: function() {
            return ++seq;
        }
    });
})();
