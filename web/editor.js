
function EditorCtrl($scope, $http) {
	
	$scope.onLoad = function(editor) {
		$scope.editor = editor;

		editor.setShowPrintMargin(false);
	};

	$scope.onChange = function (editor) {
		$scope.changed = true;
	};
	
	$scope.save = function save() {
		var value = $scope.editor.getValue();
		var length = utf8ByteCount(value);
		
		return $http({url: '/plugin'+$scope.selectedPath, method: 'PUT', params: {length: length}, data: value})
			.success(function(data) {
				if (data == "Saved OK")
					$scope.changed = false;
				else
					console.log('not saved', data);
			});
	};
	

	if (typeof String.prototype.endsWith !== 'function') {
		String.prototype.endsWith = function(suffix) {
			return this.indexOf(suffix, this.length - suffix.length) !== -1;
		};
	}

	$scope.openFile = function(path) {
		console.log('openFile', path);

		$scope.selectedPath = path;

		$http({url: '/plugin' + path, method: 'GET'})
				.success(function(data) {
					$scope.editor.getSession().setValue(data, -1);
					$scope.changed = false;
				});
	};
}



function FileCtrl($scope, $http) {
	$scope.isFolder = function() {
		return $scope.name[$scope.name.length-1] == '/';
	};

	$scope.toggle = function() {
		if ($scope.nameList === null) {

			$scope.nameList = [];

			var p = $http({url: '/plugin' + $scope.path, method: 'GET'});
			p.success(function(data) {
				$scope.nameList = _.filter(data.split('\n'));

				$scope.open = true;
			});

			return p;
		}

		$scope.open = !$scope.open;
	};

	$scope.open = false;
	$scope.nameList = null;


	if ($scope.name === undefined && $scope.path === undefined) {
		$scope.name = '/';
		$scope.path = '/';
		$scope.toggle().success(function() {
			if (_.contains($scope.nameList, 'config.mdscript')) {
				$scope.openFile('/config.mdscript');
			}
		});
	}
	else {
		$scope.path = $scope.path + $scope.name;
	}
}
