(function() {
    var m = angular.module('parkandride.facilityContacts', [
        'parkandride.ContactResource',
        'parkandride.contacts'
    ]);

    m.factory('ContactsManager', function($q, ContactResource) {
        var operatorId;
        var loading = false;
        var api = {
            allContacts: [],
            refreshContacts: function() {
                if (!loading) {
                    if (operatorId) {
                        loading = true;
                        ContactResource.listContacts({operatorId: operatorId}).then(function(response) {
                            api.allContacts.splice(0, api.allContacts.length);
                            for (var i=0; i < response.results.length; i++) {
                                var contact = response.results[i];
                                api.allContacts.push(contact);
                            }
                            loading = false;
                        }, function(rejection) {
                            loading = false;
                            return $q.reject(rejection);
                        });
                    } else {
                        api.allContacts.splice(0, api.allContacts.length);
                    }
                }
            },
            setOperatorId: function(opid) {
                if (operatorId !== opid) {
                    operatorId = opid;
                    api.refreshContacts();
                }
            },
            add: function(contact) {
                api.allContacts.push(contact);
            }
        };
        return api;
    });

    m.directive('facilityContactEdit', function (ContactsManager, editContact) {
        return {
            restrict: 'E',
            scope: {
                facilityContacts: '=',
                operatorId: '=',
                contactType: '@',
                mandatory: '='
            },
            templateUrl: 'facilities/facilityContactEdit.tpl.html',
            transclude: false,
            link: function(scope) {
                scope.allContacts = ContactsManager.allContacts;
                scope.createContact = function(type) {
                    editContact({}, true).then(function(contact) {
                        ContactsManager.add(contact);
                        scope.facilityContacts[scope.contactType] = contact.id;
                    });
                };
                scope.contactLabel = function(contact) {
                    if (contact && contact.name) {
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

                scope.$watch("operatorId", function(newValue) {
                    ContactsManager.setOperatorId(newValue);
                });

                ContactsManager.setOperatorId(scope.operatorId);

                scope.$watchCollection("allContacts", function(newCollection, oldCollection) {
                    // Is current selection deprecated by operator change?
                    var currentSelection = scope.facilityContacts[scope.contactType];
                    if (newCollection != oldCollection && currentSelection) {
                        var currentSelectionFound = false;
                        for (var i=0; i < ContactsManager.allContacts.length; i++) {
                            var contact = ContactsManager.allContacts[i];
                            currentSelectionFound = currentSelectionFound || contact.id == currentSelection;
                        }
                        if (!currentSelectionFound) {
                            scope.facilityContacts[scope.contactType] = null;
                        }
                    }
                });
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