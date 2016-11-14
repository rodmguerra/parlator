
var parlatorApp = angular.module('parlatorApp', []).config( [
    '$compileProvider',
    function( $compileProvider )
    {
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|blob):/);
        // Angular before v1.2 uses $compileProvider.urlSanitizationWhitelist(...)
    }
]);

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
                    var newHeight = (element[0].scrollHeight + 2);
                    element[0].style.height = "" + newHeight + "px";
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
    $scope.downloadUrls = {};
    var ogg = {contentType: "audio/ogg; codecs=opus", extension: "ogg"};
    var flac = {contentType: "audio/flac", extension: "flac"};

    $scope.showError = function (errorMessage, errorDetail) {
        $timeout(function(){
            $scope.myError = true;
            $scope.errorMessage = errorMessage;
            $scope.errorDetail = errorDetail;
            document.body.dispatchEvent(new Event('resize'));
        }, 0);
    }

    $scope.hideError = function () {
        $timeout(function(){
            $scope.myError = false;
            $scope.errorMessage = "";
            $scope.errorDetail = "";
        }, 0);
    };

    $scope.clear = function () {
        $scope.hideError();
        $timeout(function(){
            $scope.isTalk = false;
            document.body.dispatchEvent(new Event('resize'));
        }, 0);
    };

    var talkParams = function (voice, text, mediaType) {
        return "voice=" + encodeURIComponent(JSON.stringify(voice)) + "&text=" + encodeURIComponent(text) + "&mediaType=" + encodeURIComponent(JSON.stringify(mediaType));
    };

    $scope.parla = function(voice, text, mediaType) {
        var url = "parla?" + talkParams($scope.selectedVoice, $scope.text, mediaType);
        $http({
            method: 'GET',
            url : url,
            responseType: "arraybuffer"
        }).then(
            function (response) {

                var blob = new Blob([response.data], {type: mediaType.contentType});

                var urlCreator = window.URL || window.webkitURL;
                if(!urlCreator) {
                    $scope.showError("Parlator non functiona in tu browser.");
                    return;
                }
                $scope.talkUrl = urlCreator.createObjectURL(blob);

                //replace server download url for local download url (blob url)
                $scope.downloadOptions[mediaType.extension].url = $scope.talkUrl;


                var filename = response.headers("Content-Disposition").replace(/(.*)(filename=)(.*)/g, "$3");
                $scope.downloadOptions[mediaType.extension].filename = filename;

                audio.play($scope.talkUrl);
            },

            function (response) {
                var data = response.statusText; //String.fromCharCode.apply(null, new Uint8Array(response.data));
                $scope.showError(data);
            }
        );
    }

    $scope.talk = function () {
        $scope.downloadOptions = {};
        if($scope.text != null && $scope.text !== "") {
            for (i = 0; i < $scope.mediaTypes.length; i++) {
                var mediaType = $scope.mediaTypes[i];
                $scope.downloadOptions[mediaType.extension] = {
                    url: "parla?" + talkParams($scope.selectedVoice, $scope.text, mediaType),
                    filename: ""
                };
            }
            $scope.parla($scope.selectedVoice, $scope.text, $scope.mediaTypes[$scope.mediaTypeIndex]);
        }
    };

    audio.audioElement.onerror = function (e) {
        /*
        var input = document.getElementById("text");
        var url = "errorMessage?" + talkParams($scope.selectedVoice, $scope.text, $scope.mediaType);
        $http.get(url).success(function (data) {
            $scope.showError(data);
        });
        */
        var error = e.currentTarget.error;
        var errorDetail = error.code + ".";
        var prop;
        for (prop in error) {
            if("" + error.code == "" + error[prop]) {
                if(prop != "code") {
                    errorDetail += prop + " ";
                }
            }
        }
        var errorMessage = "";
        if(error.code == 4) {
            if($scope.mediaTypeIndex < $scope.mediaTypes.length-1) {
                $scope.mediaTypeIndex ++;
                $scope.parla($scope.selectedVoice, $scope.text, $scope.mediaTypes[$scope.mediaTypeIndex]);
                return;
            } else {
                errorMessage += "Tu browser non succedeva in sonar necun ex le formatos disponibile. Ma tu pote discargar lo.";
            }
        }

        $scope.showError(errorMessage, errorDetail);

        $timeout(function(){
            $scope.isTalk = true;
            document.body.dispatchEvent(new Event('resize'));
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

    $scope.initMediaTypes = function () {
        $http.get("mediaTypes").success(function (data) {
            $scope.mediaTypes = data;
            $scope.mediaTypeIndex = 0;
            /*
            for (i = 0; i < $scope.mediaTypes.length; i++) {
                var currentMediaType = $scope.mediaTypes[i];
                if(audio.audioElement.canPlayType(currentMediaType.contentType)) {
                    $scope.mediaType = currentMediaType;
                    alert(currentMediaType.contentType);
                    break;
                }
            }

            if(!$scope.mediaType) {
                $scope.showError("Tu browser non pote sonar necun typo de audio utilisate per parlator.");
            }
            */

        });

    }
});
         