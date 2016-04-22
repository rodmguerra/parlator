var parlatorApp = angular.module('parlatorApp', []);

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

    $scope.showError = function (errorMessage) {
        $timeout(function(){
            $scope.myError = true;
            $scope.errorMessage = errorMessage;
        }, 0);
    }

    $scope.hideError = function () {
        $timeout(function(){
            $scope.myError = false;
        }, 0);
    };

    var talkParams = function (voiceName, voiceLanguage, text) {
        return "voiceName=" + voiceName + "&voiceLanguage=" + voiceLanguage + "&text=" + encodeURIComponent(text);
    };

    $scope.talk = function () {
        if($scope.text != null && $scope.text !== "") {
            $scope.hideError();
            var url = "parla?" + talkParams($scope.selectedVoice.name, $scope.selectedVoice.language, $scope.text);
            audio.play(url);
        }
    };

    audio.audioElement.onerror = function (e) {
        var input = document.getElementById("text");
        var url = "errorMessage?" + talkParams($scope.selectedVoice.name, $scope.selectedVoice.language, $scope.text);
        $http.get(url).success(function (data) {
            $scope.showError(data);
        });
    };

    $scope.initVoices = function () {
        $http.get("voices?recommended=true").success(function (data) {
            $scope.voices = data;
            $scope.selectedVoice = $scope.voices[0];
        });
    }
});
             