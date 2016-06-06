angular.module('dependencyApp',
    ['ui.router',
        'ngResource',
        'ngUrlBind',
        'dependencyApp.controllers',
        'dependencyApp.services',
        'dependencyApp.filters',
        'dependencyApp.directives',
        'ui.bootstrap',
//        'angular-loading-bar'
        ]);

angular.module('dependencyApp')
//    .config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
//      cfpLoadingBarProvider.includeSpinner = false;
//    }])
    .config(function ($stateProvider) {
      $stateProvider.state('items', {
          url: '/items',
          templateUrl: 'partials/items.html',
          controller: 'ItemListController'
      });
    })
.run(function ($state) {
  $state.go('items');
});