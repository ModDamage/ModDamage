var app = angular.module('app', ['ngRoute', 'ngResource', 'ui.bootstrap', 'ui.ace']);
app.filter('log', function() {
	return function(value, prefix) {
		console.log('log', prefix, value);
	}
})

app.factory('MDStats', function($resource) {
	return $resource('/api/stats', {}, {
		get: {
			method: 'GET',
			cache: false,
			responseType: 'json',
		}
	});
})

app.factory('MDClass', function($resource) {
	return $resource('/api/class/:className', {}, {
		get: {
			method: 'GET',
			cache: true,
			responseType: 'json',
		}
	});
})

app.factory('MDEvents', function($resource) {
	return $resource('/api/events', {}, {
		get: {
			method: 'GET',
			cache: true,
			isArray: true,
			responseType: 'json',
		}
	});
})

app.factory('MDRoutines', function($resource) {
	return $resource('/api/routines', {}, {
		get: {
			method: 'GET',
			cache: true,
			isArray: true,
			responseType: 'json',
		}
	});
})

app.factory('MDProperties', function($resource) {
	return $resource('/api/properties', {}, {
		get: {
			method: 'GET',
			cache: true,
			isArray: true,
			responseType: 'json',
		}
	});
})

app.factory('MDTransformers', function($resource) {
	return $resource('/api/transformers', {}, {
		get: {
			method: 'GET',
			cache: true,
			isArray: true,
			responseType: 'json',
		}
	});
})


app.directive('mdClass', function() {
	return {
		restrict: 'EA',
		scope: {
			cls: '=mdClass',
		},
		template: '<span class="md-class" ng-if="cls"><a href="#/browse/{{cls.name}}" class="simple" ng-bind="cls.simpleName"></a> <a href="#/class/{{cls.name}}" class="classicon"><span class="glyphicon glyphicon-question-sign"></span></a></span>',
	};
});


app.config(function($routeProvider, $locationProvider) {
	$routeProvider
		.when('/stats', {
			templateUrl: '/static/views/stats.html',
			controller: StatsCtrl
		})
		.when('/editor', {
			templateUrl: '/static/views/editor.html',
			controller: EditorCtrl
		})
		.when('/events', {
			templateUrl: '/static/views/events.html',
			controller: EventsCtrl
		})
		.when('/routines', {
			templateUrl: '/static/views/routines.html',
			controller: RoutinesCtrl
		})
		.when('/properties', {
			templateUrl: '/static/views/properties.html',
			controller: PropertiesCtrl
		})
		.when('/transformers', {
			templateUrl: '/static/views/transformers.html',
			controller: TransformersCtrl
		})
		.when('/class/:className', {
			templateUrl: '/static/views/class.html',
			controller: ClassCtrl
		})
		.when('/browse/:className', {
			templateUrl: '/static/views/browse.html',
			controller: BrowseCtrl
		})
		.when('/support', {
		  templateUrl: '/static/views/support.html',
	    })
		.otherwise({
			redirectTo: '/stats',
		})
	// $routeProvider.when('/Book/:bookId/ch/:chapterId', {
	//   templateUrl: 'chapter.html',
	//   controller: ChapterCntl
	// });

	// configure html5 to get links working on jsfiddle
	//$locationProvider.html5Mode(true);
});

function NavCtrl($scope, $location) {
	$scope.isAnyActive = function () {
		var i = 0;
		while (i < arguments.length) {
			if ($scope.isActive(arguments[i]) )
			{
				return true;
			} else {
				i++;
			}
		}
	}
		
	$scope.isActive = function(path) {
		return $location.path() == path;
	}
}


function StatsCtrl($scope, MDStats) {
	$scope.stats = MDStats.get();
}

function EventsCtrl($scope, MDEvents) {
	$scope.categories = MDEvents.get();
	$scope.selected = {};

	$scope.select = function(categoryName) {
		if ($scope.selected.category == categoryName)
			$scope.selected.category = '';
		else
			$scope.selected.category = categoryName;
	}
}


function RoutinesCtrl($scope, MDRoutines) {
	$scope.routines = MDRoutines.get();
}



function PropertiesCtrl($scope, MDProperties) {
	$scope.properties = MDProperties.get();
}



function TransformersCtrl($scope, MDTransformers) {
	$scope.transformers = MDTransformers.get();
}




function ClassCtrl($scope, $routeParams, MDClass) {
	$scope.classInfo = MDClass.get({className: $routeParams.className});
}

function BrowseCtrl($scope, $routeParams, MDClass, MDProperties, MDTransformers) {
	var o = $scope.o = { count: 0, countNeeded: 3 };

	$scope.classInfo = MDClass.get({className: $routeParams.className}, waitForIt);
	$scope.transformers = MDTransformers.get(function() {
		$scope.transformersByWant = _.groupBy($scope.transformers, function(t) { return t.wants? t.wants.name : ''; });
		waitForIt();
	});
	$scope.properties = MDProperties.get(function() {
		$scope.propertiesByWant = _.groupBy($scope.properties, function(p) { return p.wants? p.wants.name : ''; });
		waitForIt();
	});

	function getClass(cls, done) {
		if (cls.name === undefined) debugger;
		// console.log('getClass', cls.name);

		// o.countNeeded += 1;
		return MDClass.get({className: cls.name}, function(newCls) { newCls.old = cls; if (done) done(newCls); });
	}
	// function gotClass(cls) {
	// 	console.log('gotClass', cls.name);

	// 	if (cls.superclass)
	// 		getClass(cls.superclass);

	// 	for (var i = 0; i < cls.interfaces.length; i++) {
	// 		getClass(cls.interfaces[i]);
	// 	}

	// 	// waitForIt();
	// }

	function waitForIt() {
		o.count += 1;
		if (o.count == o.countNeeded)
			loadBrowse();
	}
	$scope.propertySets = [];

	function loadBrowse() {

		// $scope.propertySets.push({
		// 	properties: $scope.propertiesByWant[$scope.classInfo.name],
		// });

		function addAllCastProperties(cls, addSelf, trans) {
			if (addSelf && $scope.propertiesByWant[cls.name])
				$scope.propertySets.push({
					cast: cls.old,
					transform: trans,
					properties: $scope.propertiesByWant[cls.name],
				});

			_.forEach(cls.interfaces, function(iface) {
				getClass(iface, function(ncls) { addAllCastProperties(ncls, true, trans); });
			});
			if (cls.superclass) getClass(cls.superclass, function(ncls) { addAllCastProperties(ncls, true, trans); });
		}

		addAllCastProperties($scope.classInfo, true);

		_.forEach($scope.transformersByWant[$scope.classInfo.name], function(trans) {

			addAllCastProperties(trans.provides, true, trans);

		});


		_.forIn($scope.propertiesByWant, function(props, want) {
			if (want == '' || want == $scope.classInfo.name) return;

			getClass({name: want}, function(wantcls) {
				var addedProps = false;

				function testMe(cls) {
					if (addedProps) return;

					// console.log('testMe', cls.name);

					if (cls.name == $scope.classInfo.name) {
						$scope.propertySets.push({
							downcast: wantcls,
							properties: $scope.propertiesByWant[wantcls.name],
						});
						addedProps = true;
						return;
					}


					_.forEach(cls.interfaces, function(iface) {
						getClass(iface, function(ncls) { if (cls.name == ncls.name) debugger; testMe(ncls); });
					});
					if (cls.superclass) getClass(cls.superclass, function(ncls) { if (cls.name == ncls.name) debugger; testMe(ncls); });
				}

				testMe(wantcls);
			});
		})
	}
}
