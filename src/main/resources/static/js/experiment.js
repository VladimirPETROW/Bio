$(document).ready(function() {

    $.experiment = {
        data: null,

        init: function() {
            /*
            $("#toExperiment").on("click", function() {
                $(".reactive-selected-info").html("");
                $(".reactive-selected").addClass("d-none");
            });
            */
        },

        load: function() {
            $.ajax({
                method: "GET",
                url: "/api/experiment/"
            }).done(function(result) {
                $.experiment.data = result;
                $.experiment.refresh();
            }).fail(function(result) {
                var html = result.responseText;
                $(".info").html(html);
            });
        },

        refresh: function() {
            var html = "";
            if (this.data.length > 0) {
                for (var i = 0; i < this.data.length; i++) {
                    experiment = this.data[i];
                    html += "<tr data-experiment-id='" + experiment.id + "' class='item' onclick='javascript:$.experiment.select(this.getAttribute(\"data-experiment-id\"));'>" +
                                //"<th class='table-light'>" + experiment.id + "</th>" +
                                "<td>" + experiment.feed.solution.name + "</td>" +
                                "<td><nobr>" + (experiment.fermentBegin ? experiment.fermentBegin : "") + "</nobr></td>" +
                                "<td><nobr>" + (experiment.fermentEnd ? experiment.fermentEnd : "") + "</nobr></td>" +
                                "<td>" + (experiment.speed ? experiment.speed : "") + "</td>" +
                                "<td>" + (experiment.temperature ? experiment.temperature : "") + "</td>" +
                                "<td>" + (experiment.ph ? experiment.ph : "") + "</td>" +
                                "<td>" + (experiment.koe ? experiment.koe : "") + "</td>" +
                                "<td>" + (experiment.comment ? experiment.comment : "") + "</td>" +
                            "</tr>";
                }
                $(".experiment-table").removeClass("d-none");
                $(".experiment-empty").addClass("d-none");
            }
            else {
                $(".experiment-empty").removeClass("d-none");
                $(".experiment-table").addClass("d-none");
            }
            $(".experiment-tbody").html(html);
            $(".experiment").removeClass("d-none");
        },

        select: function(id) {
            $.ajax({
                method: "GET",
                url: "/api/experiment/" + id,
                async: false
            }).done(function(result) {
                $.experimentSelected.data = result;
                $.experimentSelected.refresh();
            }).fail(function(result) {

            });
        }
    }

    $.experimentSelected = {
        data: null,

        refresh: function() {
            $(".experiment-selected-info").html(this.data.feed.solution.name);
            $(".experiment-selected").removeClass("d-none");
        }
    }

    $.experiment.init();
    $.experiment.load();

    $("#createExperiment_view").on("show.bs.modal", () => {
        $("#createExperiment_view form :input").val('');
        $("#createExperiment_feed").text($.feedSelected.data.solution.name);
        $.validator.valid($("#createExperiment_view form"));
    });
    $("#createExperiment_view").on("shown.bs.modal", () => {
        $("#createExperiment_fermentBegin").trigger('focus');
    });
    $("#createExperiment_create").on("click", () => {
        var inputs = $("#createExperiment_view form *[name]");
        var value = {"feed": $.feedSelected.data.id};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input.value) {
                var names = input.name.split('.');
                var nl = names.length - 1;
                var val = value;
                for (var n = 0; n < nl; n++) {
                    var name = names[n];
                    val[name] = {};
                    val = val[name];
                }
                val[names[nl]] = input.value.trim();
            }
        }
        if ($.validator.validate($("#createExperiment_view form"))) {
            $.ajax({
                method: "POST",
                url: "/api/experiment/",
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(result) {
                bootstrap.Modal.getInstance("#createExperiment_view").hide();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

    $("#selectFeed_view").on("hide.bs.offcanvas", () => {
        $(".experiment").addClass("d-none");
        $.experiment.load();
    });

    $("#selectFeed_feed_toExperiment").on("click", () => {
        var modal = bootstrap.Modal.getInstance("#createExperiment_view");
        if (!modal) {
            modal = new bootstrap.Modal("#createExperiment_view");
        }
        modal.show();
    });

    $("#openExperiment").on("click", () => {
        window.location = "/experiment/" + $.experimentSelected.data.id;
    });

});

