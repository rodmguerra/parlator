var parlatorApp = angular.module('parlatorApp', []);

parlatorApp.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown", function (event) {
            if ((event.keyCode == 10 || event.keyCode == 13) && event.ctrlKey) {
                scope.$apply(function (){
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

parlatorApp.directive('elastic', [
    '$timeout',
    function($timeout) {
        return {
            restrict: 'A',
            link: function($scope, element) {
                $scope.initialHeight = $scope.initialHeight || element[0].style.height;
                var resize = function() {
                    element[0].style.height = $scope.initialHeight;
                    var newHeight = (element[0].scrollHeight);
                    element[0].style.height = "" + (newHeight + 2) + "px";
                    var decrease = document.body.scrollHeight - document.body.clientHeight;
                    if(decrease > 0) {
                        element[0].style.height = "" + (newHeight - decrease)  + "px";
                    }
                };
                element.on("input change", resize);
                document.body.onresize = resize;
                $timeout(resize, 0);
            }
        };
    }
]);

parlatorApp.factory('audio', function ($document, $http) {
    var audioElement = $document[0].createElement('audio'); // <-- Magic trick here
    return {
        audioElement: audioElement,
        lastUrl: null,

        play: function (filename) {
            if(this.lastUrl === filename && !audioElement.error) {
                audioElement.play();
            } else {
                audioElement.src = filename;
                audioElement.play();
                this.lastUrl = filename;
            }
        }
    }
});

parlatorApp.controller('parlatorController', function ($scope, $sce, audio, $http, $timeout) {
    $scope.myError = false;
    $scope.isTalk = false;

    $scope.showError = function (errorMessage) {
        $timeout(function(){
            $scope.myError = true;
            $scope.errorMessage = errorMessage;
            document.body.dispatchEvent(new Event('resize'));
        }, 0);
    }

    $scope.hideError = function () {
        $timeout(function(){
            $scope.myError = false;
        }, 0);
    };

    $scope.clear = function () {
        $scope.hideError();
        $timeout(function(){
            $scope.isTalk = false;
            document.body.dispatchEvent(new Event('resize'));
        }, 0);
    };

    var talkParams = function (voice, text) {
        return "voice=" + encodeURIComponent(JSON.stringify(voice)) + "&text=" + encodeURIComponent(text);
    };

    $scope.talk = function () {
        if($scope.text != null && $scope.text !== "") {
            $scope.hideError();
            $scope.talkUrl = "parla?" + talkParams($scope.selectedVoice, $scope.text);
            audio.play($scope.talkUrl);
        }
    };

    audio.audioElement.onerror = function (e) {
        var input = document.getElementById("text");
        var url = "errorMessage?" + talkParams($scope.selectedVoice, $scope.text);
        $http.get(url).success(function (data) {
            $scope.showError(data);
        });
    };

    audio.audioElement.onloadeddata = function (e) {
        $timeout(function(){
            $scope.isTalk=true;
            document.body.dispatchEvent(new Event('resize'));
        }, 0);
    };

    audio.audioElement.onplaying = audio.audioElement.onloadeddata;

    $scope.initVoices = function () {
        $http.get("voices?recommended=true").success(function (data) {
            $scope.voices = data;
            $scope.selectedVoice = $scope.voices[0];
        });
    }
});
         