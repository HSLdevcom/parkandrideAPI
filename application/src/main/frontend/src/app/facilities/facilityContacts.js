(function() {
    var m = angular.module('parkandride.facilityContacts', [
        'parkandride.ContactResource',
        'parkandride.contacts'
    ]);

    m.directive('facilityContactEdit', function (ContactResource, editContact) {
        return {
            restrict: 'E',
            scope: {
                facilityContacts: '=',
                contactType: '@',
                mandatory: '@'
            },
            templateUrl: 'facilities/facilityContactEdit.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.allContacts = [];
                ContactResource.listContacts().then(function(response) {
                    scope.allContacts = response.results;
                });
                scope.createContact = function(type) {
                    editContact({}, true).then(function(contact) {
                        scope.allContacts.push(contact);
                        scope.facilityContacts[scope.contactType] = contact.id;
                    });
                };
                scope.contactLabel = function(contact) {
                    if (contact) {
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
                    }
                    return "";
                };
            }
        };
    });

    m.directive('facilityContactView', function (editContact) {
        return {
            restrict: 'E',
            scope: {
                contact: '=',
                contactType: '@'
            },
            templateUrl: 'facilities/facilityContactView.tpl.html',
            transclude: false
        };
    });
})();