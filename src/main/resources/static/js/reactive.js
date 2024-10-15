$(document).ready(function() {

    $.reactive = {
        data: null,

        init: function() {
            $("#toExperiment").on("click", function() {
                $(".reactive-selected-info").html("");
                $(".reactive-selected").addClass("d-none");
            });
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
            for (var i = 0; i < this.data.length; i++) {
                reactive = this.data[i];
                html += "<tr data-reactive-id='" + reactive.id + "' class='item' onclick='javascript:$.reactive.select(this.getAttribute(\"data-reactive-id\"));'>" +
                            "<th class='table-light'>" + reactive.id + "</th>" +
                            "<td>" + reactive.name + "</td>" +
                            "<td>" + (reactive.unit ? reactive.unit : "") + "</td>" +
                        "</tr>";
            }
            $(".reactive-tbody").html(html);
            $(".reactive").removeClass("d-none");
        },

        select: function(id) {
            $.ajax({
                method: "GET",
                url: "/api/reactive/" + id,
                async: false
            }).done(function(reactive) {
                $(".reactive-selected-info").html(reactive.name);
                $(".reactive-selected").removeClass("d-none");
            }).fail(function(result) {

            });
        }
    }

    $.reactive.init();
    $.reactive.load();

    $("#createReactive").on("show.bs.modal", () => {
        $("#createReactive form :input").val('');
        $.validator.valid($("#createReactive form"));
    });
    $("#createReactive").on("shown.bs.modal", () => {
        $("#createReactive form #name").trigger('focus');
    });
    $("#createReactive #save").on("click", () => {
        var inputs = $("#createReactive form input");
        var value = {};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input.value) {
                value[input.id] = input.value;
            }
        }
        if ($.validator.validate($("#createReactive form"))) {
            $.ajax({
                method: "POST",
                url: "/api/reactive/",
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(reactive) {
                $.reactive.select(reactive.id);
                $.reactive.load();
                bootstrap.Modal.getInstance("#createReactive").hide();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

});

