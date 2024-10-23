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
            for (var i = 0; i < this.data.length; i++) {
                feed = this.data[i];
                html += "<tr data-feed-id='" + feed.id + "' class='item' onclick='javascript:$.feed.select(this.getAttribute(\"data-feed-id\"));'>" +
                            "<th class='table-light'>" + feed.id + "</th>" +
                            "<td>" + feed.name + "</td>" +
                        "</tr>";
            }
            $(".feed-tbody").html(html);
            $(".feed").removeClass("d-none");
        },

        select: function(id) {
            $.ajax({
                method: "GET",
                url: "/api/feed/" + id,
                async: false
            }).done(function(reactive) {
                $(".feed-selected-info").html(feed.name);
                $(".feed-selected").removeClass("d-none");
            }).fail(function(result) {

            });
        }
    }

    $.feed.init();
    $.feed.load();

});

