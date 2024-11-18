$(document).ready(function() {

    $.reactive = {
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
                url: "/api/reactive/"
            }).done(function(result) {
                $.reactive.data = result;
                $.reactive.refresh();
            }).fail(function(result) {
                var html = result.responseText;
                $(".info").html(html);
            });
        },

        refresh: function() {
            var html = "";
            if (this.data.length > 0) {
                for (var i = 0; i < this.data.length; i++) {
                    reactive = this.data[i];
                    html += "<tr data-reactive-id='" + reactive.id + "' class='item' onclick='javascript:$.reactive.select(this.getAttribute(\"data-reactive-id\"));'>" +
                                "<th class='table-light'>" + reactive.id + "</th>" +
                                "<td>" + reactive.name + "</td>" +
                                "<td>" + (reactive.kind ? reactive.kind : "") + "</td>" +
                                "<td>" + (reactive.unit ? reactive.unit : "") + "</td>" +
                            "</tr>";
                }
                $(".reactive-table").removeClass("d-none");
                $(".reactive-empty").addClass("d-none");
            }
            else {
                $(".reactive-empty").removeClass("d-none");
                $(".reactive-table").addClass("d-none");
            }
            $(".reactive-tbody").html(html);
            $(".reactive").removeClass("d-none");
        },

        select: function(id) {
            $.ajax({
                method: "GET",
                url: "/api/reactive/" + id,
                async: false
            }).done(function(result) {
                $.reactiveSelected.data = result;
                $.reactiveSelected.refresh();
            }).fail(function(result) {

            });
        }
    }

    $.reactiveSelected = {
        data: null,

        refresh: function() {
            $(".reactive-selected-info").html(this.data.name);
            $(".reactive-selected").removeClass("d-none");
        }

    }

    $.reactive.init();
    $.reactive.load();

    $("#createReactive_view").on("show.bs.modal", () => {
        $("#createReactive_view form :input").val('');
        $.validator.valid($("#createReactive_view form"));
    });
    $("#createReactive_view").on("shown.bs.modal", () => {
        $("#createReactive_name").trigger('focus');
    });
    $("#createReactive_create").on("click", () => {
        var inputs = $("#createReactive_view form input");
        var value = {};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input.value) {
                value[input.name] = input.value.trim();
            }
        }
        if ($.validator.validate($("#createReactive_view form"))) {
            $.ajax({
                method: "POST",
                url: "/api/reactive/",
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(reactive) {
                $.reactive.select(reactive.id);
                $.reactive.load();
                bootstrap.Modal.getInstance("#createReactive_view").hide();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

});

