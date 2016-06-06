angular.module('dependencyApp.controllers', [
]).controller('ItemListController', function ($scope, $state, $resource, $interval, $modal, ngUrlBind) {


  var Project = $resource('/project/:token', {token: '@token'}, {
    // Let's make the `query()` method cancellable
    create: {method: 'post', isArray: true, cancellable: true},
    pull: {method: 'put' }
  });
  $scope.items = [];
  $scope.projectRequest = {  };
  $scope.url = '';

  ngUrlBind($scope, 'projectRequest');
  var stop;
  function reloadProject(){
    var newItems = Project.query({token: $scope.projectRequest.token});
    newItems.$promise.then(function(){
      for (var i = 0; i < newItems.length; i++) {
        if (!$scope.items[i]) {
          $scope.items.push(newItems[i]);
        }
        _.merge($scope.items[i], newItems[i]);
      }

    });
  }

  $scope.createProject = function() {
    $interval.cancel(stop);
    $scope.items.length = 0;
    var sendRequest = {
      token: $scope.projectRequest.token,
      company: $scope.projectRequest.company,
      excludes: _.compact($scope.projectRequest.excludes.replace(/ /g, '').split(',')),
      includes: _.compact($scope.projectRequest.includes.replace(/ /g, '').split(','))
    };
    var createdItems = Project.create(sendRequest);
    createdItems.$promise.then(function(){
        stop = $interval(reloadProject, 1000);
    });
  }

  $scope.debug = function (name, item) {
    $scope.item=item;
    $scope.projectName=name;
    $scope.companyName=$scope.projectRequest.company;
    $scope.url = '';
    $scope.loading = false;
    $scope.loadingIndex = 0;
    var modalInstance = $modal.open({
        templateUrl: 'debug.html',
        scope: $scope
    });
  };

  $scope.createPullRequest = function(projectName, module){
    $scope.url = "Working ..."
    $scope.loading = true;
    $scope.loadingIndex++;
    var newLibs = [];
    for (var i = 0; i < module.libraries.length; i++) {
      if (module.libraries[i].selected) {
        newLibs.push({ newVersion: module.libraries[i].selectedVersion, library: module.libraries[i] });
      }
    }

    var sendRequest = {
      token: $scope.projectRequest.token,
      company: $scope.projectRequest.company,
      projectName: projectName,
      moduleName: module.name,
      upgrades: newLibs,
      moduleType: module.type
    };
    var pullResponse = Project.pull(sendRequest);
    pullResponse.$promise.then(function(){
      $scope.url = pullResponse.pullRequest;
      $scope.loading = false;
    });

  }

  if ($scope.projectRequest.token && $scope.projectRequest.company) {
    $interval.cancel(stop);
    stop = $interval(reloadProject, 1000);
  }
});