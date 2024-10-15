$(document).ready(function() {

    /* validator */

    $("*[data-valid-type]").on("keyup", function(event) {
        $(event.target).removeClass("invalid");
    });

    $.validator = {

        type: {
            text: function(value) {
                var regex = new RegExp("^(\\s*\\S+\\s*)+$", "i");
                return regex.test(value);
            },
            number: function(value) {
                var regex = new RegExp("^(\\s*\\d+([\\.,]\\d+)?\\s*)+$", "i");
                return regex.test(value);
            }/*,
            word: function(value) {
                var regex = new RegExp("^(\\s*[A-Za-zА-ЯЁа-яё]+\\s*)+$", "i");
                return regex.test(value);
            },
            date: function(value) {
                var regex = new RegExp("^\\s*(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)\\s*$", "i");
                if (!regex.test(value)) return false;
                var valueDate = value.replace(regex, "$3-$2-$1");
                var date = Date.parse(valueDate);
                return !isNaN(date);
            }*/
        },

        validate: function(doc) {
            var valid = true;
            var test = function(i, elem) {
                var validEmpty = $(elem).attr("data-valid-empty");
                var value = $(elem).val();
                if (!value) {
                    if (validEmpty) {
                        if (!(validEmpty.toLowerCase() === "true")) {
                            $(elem).addClass("invalid");
                            valid = false;
                        }
                        return;
                    }
                }
                var validType = $(elem).attr("data-valid-type");
                if (validType && !$.validator.type[validType](value)) {
                    $(elem).addClass("invalid");
                    valid = false;
                }
            }
            $(doc).each(test);
            $(doc).find("*[data-valid-type]").each(test);
            return valid;
        },

        valid: function(doc) {
            var reset = function(i, elem) {
                var validType = $(elem).attr("data-valid-type");
                if (validType) {
                    $(elem).removeClass("invalid");
                }
            }
            $(doc).each(reset);
            $(doc).find("*[data-valid-type]").each(reset);
        }
    }

});

