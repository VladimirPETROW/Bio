$(document).ready(function() {

    $.organism = {
        data: null,

        init: function() {
            $("#toExperiment").on("click", function() {
                $(".organism-selected-info").html("");
                $(".organism-selected").addClass("d-none");
            });
        },

        load: function() {
            $.ajax({
                method: "GET",
                url: "/api/organism/"
            }).done(function(result) {
                $.organism.data = result;
                $.organism.refresh();
            }).fail(function(result) {
                var html = result.responseText;
                $(".info").html(html);
            });
        },

        refresh: function() {
            var html = "";
            for (var i = 0; i < this.data.length; i++) {
                organism = this.data[i];
                html += "<tr data-organism-id='" + organism.id + "' class='item' onclick='javascript:$.organism.select(this.getAttribute(\"data-organism-id\"));'>" +
                            "<th class='table-light'>" + organism.id + "</th>" +
                            "<td>" + organism.name + "</td>" +
                            "<td>" + organism.doubling + "</td>" +
                        "</tr>";
            }
            $(".organism-tbody").html(html);
            $(".organism").removeClass("d-none");
        },

        select: function(id) {
            $.ajax({
                method: "GET",
                url: "/api/organism/" + id,
                async: false
            }).done(function(organism) {
                $(".organism-selected-info").html(organism.name);
                $(".organism-selected").removeClass("d-none");
            }).fail(function(result) {

            });
        }
    }

    $.organism.init();
    $.organism.load();

    $("#createOrganism").on("show.bs.modal", () => {
        $("#createOrganism form :input").val('');
        $.validator.valid($("#createOrganism form"));
    });
    $("#createOrganism").on("shown.bs.modal", () => {
        $("#createOrganism form #name").trigger('focus');
    });
    $("#createOrganism #save").on("click", () => {
        var inputs = $("#createOrganism form input");
        var value = {};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            value[input.id] = input.value;
        }
        if ($.validator.validate($("#createOrganism form"))) {
            $.ajax({
                method: "POST",
                url: "/api/organism/",
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(organism) {
                $.organism.select(organism.id);
                $.organism.load();
                bootstrap.Modal.getInstance("#createOrganism").hide();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

});

