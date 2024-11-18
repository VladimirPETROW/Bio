
$(document).ready(function() {

    $.program = {
        data: null,

        load: function() {
            $.ajax({
                method: "GET",
                url: "/api/program/",
                async: false
            }).done(function(program) {
                $.program.data = program;
                $.program.refresh();
            }).fail(function(result) {

            });
        },

        refresh: function() {
            $(".version").text($.program.data.version);
            if ($.program.data.portable) {
                $(".portable").removeClass("invisible");
            }
        }
    };

    $.program.load();

    $("#stopProgram_stop").on("click", () => {
        bootstrap.Modal.getInstance("#stopProgram_view").hide();
        var html = "<div class='h-100 d-flex align-items-center justify-content-center text-secondary bg-light'>Закройте это окно.</div>";
        $("body").html(html);
        document.title = "Закройте это окно";
        $.ajax({
            method: "POST",
            url: "/api/program/stop",
            //async: false
        }).done(function(result) {
            //bootstrap.Modal.getInstance("#stopProgram_view").hide();
        }).fail(function(result) {
            /*
            var response = result.responseJSON;
            alert(response.message);
            */
            //$(".info").html(html);
        });
    });

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
            },
            datetime: function(value) {
                var regex = new RegExp("^\\s*(\\d?\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)\\s*([01]?\\d|2[0-3]):([0-5]\\d)\\s*$", "i");
                var match = regex.exec(value);
                //if (!regex.test(value)) return false;
                if (!match) return false;
                if (match[1].length < 2) {
                    match[1] = "0" + match[1];
                }
                if (match[4].length < 2) {
                    match[4] = "0" + match[4];
                }
                //var valueDate = value.replace(regex, "$3-$2-$1T$4:$5");
                var valueDate = match[3] + "-" + match[2] + "-" + match[1] + "T" + match[4] + ":" + match[5];
                var date = Date.parse(valueDate);
                return !isNaN(date);
            }
            /*,
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
            }
            */
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

