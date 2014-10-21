(function() {
    var m = angular.module('parkandride.layout', []);

    function findElements(clone, tag) {
        tag = tag.toUpperCase();
        var results = [];
        for (var i=0; i < clone.length; i++) {
            if (clone[i].tagName === tag) {
                results.push(clone[i]);
            }
        }
        return results;
    }
    m.directive('mainLayout', function (MapService, schema, $compile) {
        return {
            restrict: 'E',
            templateUrl: 'layout/mainLayout.tpl.html',
            transclude: true,
            compile: function(element, attributes) {
                return {
                    post: function (scope, element, attributes, controller, transcludeFn) {
                        transcludeFn(scope, function(clone) {
                            var heading = findElements(clone, 'headline');
                            var actions = findElements(clone, 'actions');
                            var body = findElements(clone, 'content');
                            element.find('headline').replaceWith(heading[0]);
                            element.find('actions-top').replaceWith(actions[0]);
                            element.find('content').replaceWith(body[0]);
                            element.find('actions-bottom').replaceWith(actions[1]);
                        });
                    }
                };
            }
        };
    });
})();
