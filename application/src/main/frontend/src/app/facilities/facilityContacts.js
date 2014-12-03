(function() {
    var m = angular.module('parkandride.facilityContacts', [
        'parkandride.contacts'
    ]);

    m.directive('facilityContactEdit', function (editContact) {
        return {
            restrict: 'E',
            scope: {
                allContacts: '=',
                facilityContacts: '=',
                contactType: '@',
                mandatory: '@'
            },
            templateUrl: 'facilities/facilityContactEdit.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.createContact = function(type) {
                    editContact({}, true).then(function(contact) {
                        scope.allContacts[contact.id] = contact;
                        scope.facilityContacts[scope.contactType] = contact.id;
                    });
                };
                scope.contactLabel = function(contact) {
                    var label = contact.name.fi + " (";
                    if (contact.phone) {
                        label += contact.phone;
                        if (contact.email) {
                            label += " / " + contact.email;
                        }
                    } else {
                        label += contact.email;
                    }
                    return label + ")";
                };
            }
        };
    });

})();