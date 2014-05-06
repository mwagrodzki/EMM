function IndexCtrl($scope, $http) {
    $scope.getSelectionCombo = function(){
        return $scope.outlook + "," + $scope.temperature + "," + $scope.humidity + "," + $scope.windy;
    }
}