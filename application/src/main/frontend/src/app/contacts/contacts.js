(function() {
    var m = angular.module('parkandride.contacts', [
        'ui.router',
        'parkandride.ContactResource',
        'parkandride.layout',
        'parkandride.multilingual',
        'parkandride.util',
        'parkandride.address',
        'parkandride.modalUtil',
        'showErrors'
    ]);


    m.controller("ContactEditCtrl", function ($scope, $modalInstance, ContactResource, contact, create, modalUtilFactory) {
        var vm = this;
        vm.context = "contacts";

        var modalUtil = modalUtilFactory($scope, vm.context, $modalInstance);

        $scope.contact = contact;
        $scope.titleKey = 'contacts.action.' + (create ? 'new' : 'edit');

        $scope.allOperators = [];

        $scope.ok = function (form) {
            modalUtil.validateAndSubmit(form, function() { return ContactResource.save(contact); });
        };

        $scope.cancel = function () {
            $modalInstance.dismiss();
        };
    });

    m.factory("editContact", function($modal, ContactResource, Session) {
        return function(contact, create) {
            var modalInstance = $modal.open({
                templateUrl: 'contacts/contactEdit.tpl.html',
                controller: 'ContactEditCtrl as contactEditCtrl',
                resolve: {
                    contact: function () {
                        if (!contact.operatorId) {
                            var login = Session.get();
                            contact.operatorId = login && login.operatorId;
                        }
                        return _.cloneDeep(contact);
                    },
                    create: function() {
                        return create;
                    }
                },
                backdrop: 'static'
            });
            return modalInstance.result;
        };
    });

    m.config(function($stateProvider) {
        $stateProvider.state('contact-list', {
            parent: 'contactstab',
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
