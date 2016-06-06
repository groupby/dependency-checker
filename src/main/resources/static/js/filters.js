angular.module('dependencyApp.filters', [])
.filter('errorColor', function () {
    return function (module) {
        if (module.error) {
          return 'red';
        }
        if (module.libraries.length > 15) {
          return '#FF0000';
        }
        if (module.libraries.length > 13) {
          return '#FF1111';
        }
        if (module.libraries.length > 11) {
          return '#FF2222';
        }
        if (module.libraries.length > 9) {
          return '#FF3333';
        }
        if (module.libraries.length > 7) {
          return '#FF4444';
        }
        if (module.libraries.length > 5) {
          return '#FF5555';
        }
        if (module.libraries.length > 1) {
          return 'orange';
        }
        return 'green';
    }
})
.filter('elapsed', function () {
    return function (date) {
        if (!date) return;
        var time = new Date();
        time.setTime(date);
        var timeNow = new Date().getTime(),
            difference = timeNow - time,
            seconds = Math.floor(difference / 1000),
            minutes = Math.floor(seconds / 60),
            hours = Math.floor(minutes / 60),
            days = Math.floor(hours / 24),
            weeks = Math.floor(days / 7),
            months = Math.floor(weeks / 4),
            years = Math.floor(days / 365);
        if (years > 1) {
            return years + " years ago";
        } else if (years == 1) {
            return "a year ago";
        } else if (months > 1) {
            return months + " months ago";
        } else if (months == 1) {
            return "a month ago";
        } else if (weeks > 1) {
            return weeks + " weeks ago";
        } else if (weeks == 1) {
            return "a week ago";
        } else if (days > 1) {
            return days + " days ago";
        } else if (days == 1) {
            return "yesterday"
        } else if (hours > 1) {
            return hours + " hours ago";
        } else if (hours == 1) {
            return "an hour ago";
        } else if (minutes > 1) {
            return minutes + " minutes ago";
        } else if (minutes == 1) {
            return "a minute ago";
        } else if (seconds < 0) {
            return "in the future!"
        } else {
            return "a few seconds ago";
        }
    }
}).filter('capitalize', function () {
    return function (input) {
        return (!!input) ? input.replace(/([^\W_]+[^\s-]*) */g, function (txt) {
            return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
        }) : '';
    }
}).filter('toEnglish', function(){
    var toEnglishLookup = {};

    toEnglishLookup['someKey'] = 'My Beautiful Key';
    return function (input) {
        if (!input) {
            return '';
        }
        if (toEnglishLookup[input]) {
            return toEnglishLookup[input];
        }
        return input;
    }
});