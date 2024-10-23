$(document).ready(function() {

    $.solution = {

        init: function() {
        },

        create: function() {

        },

        setVisible: function(visible) {
            if (visible) {
                $(".solution-area").removeClass("d-none");
            }
            else {
                $(".solution-area").addClass("d-none");
            }
        }
    }

    $("*[data-tab-target]").on("click", (e) => {
        var elem = e.currentTarget;
        var tabTarget = $(elem).attr("data-tab-target");
        var tabContr = $(elem).attr("data-tab-contr");
        $(tabTarget).removeClass("d-none");
        $(tabContr).addClass("d-none");
    });

});

