$(document).ready(function() {

    $.experimentItem = {
        data: null,

        load: function() {
            $.ajax({
                method: "GET",
                url: "/api/experiment/" + experimentId,
                async: false
            }).done(function(result) {
                $.experimentItem.data = result;
                $.experimentItem.refresh();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        },

        refresh: function() {
            var experiment = this.data;
            $("#experimentItem_feed").text(experiment.feed.solution.name);
            $("#experimentItem_fermentBegin").val(experiment.fermentBegin);
            $("#experimentItem_fermentEnd").val(experiment.fermentEnd);
            $("#experimentItem_speed").val(experiment.speed);
            $("#experimentItem_temperature").val(experiment.temperature);
            $("#experimentItem_ph").val(experiment.ph);
            $("#experimentItem_koe").val(experiment.koe);
            $("#experimentItem_comment").val(experiment.comment);
            $("#experimentItem_view").removeClass("d-none");
        }
    }

    $.experimentItem.load();

    /*
    $("*[data-input-target]").on("click", (event) => {
        var elem = event.target;
        $(elem).addClass("d-none");
        var input = $(elem).attr("data-input-target");
        $(input).removeClass("d-none");
        $(input).focus();
    });
    */

    $(window).on("beforeunload", (event) => {
        //$(document.activeElement).trigger("blur");
        if (!$(".experiment-edited").hasClass("d-none")) {
            event.preventDefault();
        }
    });
    $("#experimentItem_view *[name]").on("focus", (event) => {
        //alert(event);
        //console.log(event);
        var elem = event.target;
        $(elem).addClass("edited");
        $(elem).removeClass("invalid");
        $(".experiment-edited").removeClass("d-none");
    });
    $("#experimentItem_save").on("click", () => {
        var inputs = $("#experimentItem_view *[name]");
        var value = {"feed": $.experimentItem.data.feed.id};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input.value) {
                value[input.name] = input.value.trim();
            }
        }
        if ($.validator.validate($("#experimentItem_view"))) {
            $.ajax({
                method: "PUT",
                url: "/api/experiment/" + $.experimentItem.data.id,
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(result) {
                $(".experiment-edited").addClass("d-none");
                $.experimentItem.load();
                $("#experimentItem_view *[name]").removeClass("edited");
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

    $("#revertEdit_revert").on("click", () => {
        bootstrap.Modal.getInstance("#revertEdit_view").hide();
        $(".experiment-edited").addClass("d-none");
        $.experimentItem.load();
        $("#experimentItem_view *[name]").removeClass("edited");
        $("#experimentItem_view *[name]").removeClass("invalid");
    });

});

