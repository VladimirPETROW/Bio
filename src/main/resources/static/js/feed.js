$(document).ready(function() {

    $.feed = {
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
                url: "/api/feed/"
            }).done(function(result) {
                $.feed.data = result;
                $.feed.refresh();
            }).fail(function(result) {
                var html = result.responseText;
                $(".info").html(html);
            });
        },

        refresh: function() {
            var html = "";
            if (this.data.length > 0) {
                for (var i = 0; i < this.data.length; i++) {
                    feed = this.data[i];
                    html += "<tr data-feed-id='" + feed.id + "' class='item' onclick='javascript:$.feed.select(this.getAttribute(\"data-feed-id\"));'>" +
                                "<th class='table-light'>" + feed.id + "</th>" +
                                "<td>" + feed.solution.name + "</td>" +
                            "</tr>";
                }
                $(".feed-table").removeClass("d-none");
                $(".feed-empty").addClass("d-none");
            }
            else {
                $(".feed-empty").removeClass("d-none");
                $(".feed-table").addClass("d-none");
            }
            $(".feed-tbody").html(html);
            $(".feed").removeClass("d-none");
        },

        select: function(id) {
            $.ajax({
                method: "GET",
                url: "/api/feed/" + id,
                async: false
            }).done(function(result) {
                $.feedSelected.data = result;
                $.feedSelected.refresh();
            }).fail(function(result) {

            });
        }
    }

    $.feedSelected = {
        data: null,

        refresh: function() {
            $(".feed-selected-info").html(this.data.solution.name);
            $(".feed-selected").removeClass("d-none");
        }
    }

    $.feed.init();
    $.feed.load();

    $("#createFeed_view").on("show.bs.modal", () => {
        $("#createFeed_view form :input").val('');
        $.validator.valid($("#createFeed_view form"));
    });
    $("#createFeed_view").on("shown.bs.modal", () => {
        $("#createFeed_name").trigger('focus');
    });
    $("#createFeed_create").on("click", () => {
        var inputs = $("#createFeed_view form input");
        var value = {};
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
        if ($.validator.validate($("#createFeed_view form"))) {
            $.ajax({
                method: "POST",
                url: "/api/feed/",
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(feed) {
                //$.feed.select(feed.id);
                $.feed.load();
                bootstrap.Modal.getInstance("#createFeed_view").hide();
                window.location = "/feed/" + feed.id;
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

    $("#createCopyFeed_view").on("show.bs.modal", () => {
        $("#createCopyFeed_view form :input").val('');
        $("#createCopyFeed_base").text($.feedSelected.data.solution.name);
        $.validator.valid($("#createCopyFeed_view form"));
    });
    $("#createCopyFeed_view").on("shown.bs.modal", () => {
        $("#createCopyFeed_name").trigger('focus');
    });
    $("#createCopyFeed_create").on("click", () => {
        var inputs = $("#createCopyFeed_view form input");
        var value = {"base": $.feedSelected.data.id};
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
        if ($.validator.validate($("#createCopyFeed_view form"))) {
            $.ajax({
                method: "POST",
                url: "/api/feed/",
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(feed) {
                //$.feed.select(feed.id);
                $.feed.load();
                bootstrap.Modal.getInstance("#createCopyFeed_view").hide();
                window.location = "/feed/" + feed.id;
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

    $("#editFeed").on("click", () => {
        window.location = "/feed/" + $.feedSelected.data.id;
    });

});

