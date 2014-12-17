(function() {
    var m = angular.module('parkandride.tags', []);
    var KEYS = {
        backspace: 8,
        tab: 9,
        enter: 13,
        escape: 27
    };

    m.directive('tags', function () {
        return {
            restrict: 'E',
            require: 'ngModel',
            scope: {
                tags: '=ngModel',
                placeholder: '@'
            },
            templateUrl: 'directives/tags.tpl.html',
            transclude: false,
            link: function(scope, element, attrs, ctrl) {
                scope.newTag = "";

                function addNewTag(e) {
                    var tag = scope.newTag.trim();
                    if (tag.length > 0) {
                        var i = _.indexOf(scope.tags, scope.newTag);
                        if (i < 0) {
                            scope.tags.push(scope.newTag);
                        }
                        scope.newTag = "";
                        scope.$apply();
                    }
                    e.preventDefault();
                }

                var input = element.find('input');
                input
                    .on('keydown', function(e) {
                        var key = e.keyCode;

                        var add = key === KEYS.enter;
                        var remove = key === KEYS.backspace && scope.newTag.length === 0;
                        var cancel = key === KEYS.escape;

                        if (add) {
                            addNewTag(e);
                        }
                        else if (remove) {
                            scope.tags.pop();
                            scope.$apply();
                            e.preventDefault();
                        }
                        else if (cancel) {
                            scope.newTag = "";
                            scope.$apply();
                            e.preventDefault();
                        }
                    })
                    .on('blur', function(e) {
                        addNewTag(e);
                    });

                scope.removeTag = function(tag) {
                    var i = _.indexOf(scope.tags, tag);
                    if (i >= 0) {
                        scope.tags.splice(i, 1);
                    }
                };

                element.find('div').on('click', function() {
                    input[0].focus();
                });
            }
        };
    });
})();
