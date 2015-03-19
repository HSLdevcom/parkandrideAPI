// Copyright © 2015 HSL

describe('UsersCtrl', function() {
    beforeEach(module('parkandride.users'));

    var $controller;

    beforeEach(inject(function (_$controller_) {
        $controller = _$controller_;
    }));

    describe('on init', function () {
        it('enriches users with finnish operator name', function () {
            var ctrl = $controller('UsersCtrl', {
                $state: {},
                users: { results: [
                    {operatorId: 1},
                    {operatorId: 3},
                    {operatorId: 2}
                ]},
                operatorsById: {
                    1: { name: { fi: "_1"} },
                    2: { name:{ fi: "_2"} }
                }
            });

            expect(ctrl.users[0].operatorNameFi).toEqual("_1");
            expect(ctrl.users[1].operatorNameFi).toEqual("");
            expect(ctrl.users[2].operatorNameFi).toEqual("_2");
        });
    });
});