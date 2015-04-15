// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

describe( 'AppCtrl', function() {
  describe( 'isCurrentUrl', function() {
    var AppCtrl, $location, $scope;

    beforeEach( module( 'parkandride' ) );

    beforeEach( inject( function( $controller, _$location_, $rootScope ) {
      $location = _$location_;
      $scope = $rootScope.$new();
      AppCtrl = $controller( 'AppCtrl', { $location: $location, $scope: $scope });
    }));

    it( 'should pass a dummy test', inject( function() {
      expect( AppCtrl ).toBeTruthy();
    }));
  });
});
