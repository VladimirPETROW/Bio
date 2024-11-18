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
            $("#feed").text(experiment.feed.solution.name);
            $("#fermentBegin").text(experiment.fermentBegin);
            $("#fermentEnd").text(experiment.fermentEnd);
            $("#speed").text(experiment.speed);
            $("#temperature").text(experiment.temperature);
            $("#ph").text(experiment.ph);
            $("#koe").text(experiment.koe);
            $("#comment").text(experiment.comment);
            $(".experimentItem").removeClass("d-none");
        }
    }

    $.experimentItem.load();

});

