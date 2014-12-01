(function() {
    var m = angular.module('parkandride.contacts', [
        'ui.router',
        'parkandride.ContactResource',
        'parkandride.layout',
        'parkandride.multilingual',
        'parkandride.util',
        'parkandride.address',
        'showErrors'
    ]);


    m.controller("ContactEditCtrl", function ($scope, $modalInstance, ContactResource, contact, create) {
        $scope.contact = contact;
        $scope.titleKey = 'contacts.action.' + (create ? 'new' : 'edit');
        $scope.ok = function () {
            ContactResource.save(contact).then(
                function(id) {
                    $scope.contact.id = id;
                    $modalInstance.close($scope.contact);
                },
                function(rejection) {
                    if (rejection.status == 400 && rejection.data.violations) {
                        $scope.violations = rejection.data.violations;
                    }
                }
            );
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });

    m.factory("editContact", function($modal, ContactResource) {
        return function(contact, create) {
            var modalInstance = $modal.open({
                templateUrl: 'contacts/contactEdit.tpl.html',
                controller: 'ContactEditCtrl',
                resolve: {
                    contact: function () {
                        return _.cloneDeep(contact);
                    },
                    create: function() {
                        return create;
                    }
                }
            });
            return modalInstance.result;
        };
    });

    m.config(function($stateProvider) {
        $stateProvider.state('contact-list', {
            parent: 'root',
            url: '/contacts',
            views: {
                "main": {
                    controller: 'ContactListCtrl as listCtrl',
                    templateUrl: 'contacts/contactList.tpl.html'
                }
            },
            data: { pageTitle: 'Contacts' },
            resolve: {
                contacts: function(ContactResource) {
                    return ContactResource.listContacts();
                }
            }
        });
    });

    m.controller('ContactListCtrl', function($scope, ContactResource, editContact, contacts) {
        var self = this;
        self.contacts = contacts.results;
        $scope.common.translationPrefix = "contacts";

        self.create = function() {
            editContact({}, true).then(function() {
                return ContactResource.listContacts();
            }).then(function(contacts) {
                self.contacts = contacts.results;
            });
        };
        self.edit = function(contact) {
            editContact(contact, false).then(function() {
                return ContactResource.listContacts();
            }).then(function(contacts) {
                self.contacts = contacts.results;
            });
        };
    });

    m.directive('contactListNavi', function() {
        return {
            restrict: 'E',
            templateUrl: 'contacts/contactListNavi.tpl.html'
        };
    });

})();
