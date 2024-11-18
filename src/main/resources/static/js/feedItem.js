$(document).ready(function() {

    $.feedItem = {
        data: null,

        load: function() {
            $.ajax({
                method: "GET",
                url: "/api/feed/" + feedId,
                async: false
            }).done(function(result) {
                $.feedItem.data = result;
                $.feedItem.refresh();
                $.solution.data = result.solution;
                $.solution.refresh();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        },

        refresh: function() {
            var html = "<span>" + this.data.solution.name + "</span>";
            $(".feed-caption").html(html);
        }
    }

    $.solution = {
        data: null,

        reload: function() {
            $.ajax({
                method: "GET",
                url: "/api/solution/" + $.solution.data.id,
                async: false
            }).done(function(result) {
                $.solution.data = result;
                $.solution.refresh();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        },

        refresh: function() {
            var html = "";
            var reactives = this.data.reactives;
            if (reactives) {
                for (var i = 0; i < reactives.length; i++) {
                    solutionReactive = reactives[i];
                    html += "<tr data-reactive-id='" + solutionReactive.reactive.id + "' class='item' onclick='javascript:$.solution.select(this.getAttribute(\"data-reactive-id\"));'>" +
                                "<td>" + solutionReactive.reactive.name + "</td>" +
                                "<td>" + solutionReactive.count + "</td>" +
                                "<td>" + solutionReactive.unit + "</td>" +
                            "</tr>";
                }
                $(".solution-table").removeClass("d-none");
                $(".solution-empty").addClass("d-none");
            }
            else {
                $(".solution-empty").removeClass("d-none");
                $(".solution-table").addClass("d-none");
            }
            $(".solution-tbody").html(html);
            $(".solution").removeClass("d-none");
        },

        select: function(id) {
            $.ajax({
                method: "GET",
                url: "/api/solutionReactive/" + $.solution.data.id + "/" + id,
                async: false
            }).done(function(result) {
                $.solutionReactive.data = result;
                $.solutionReactive.edit();
            }).fail(function(result) {

            });
        }
    }

    $.solutionReactive = {
        data: null,

        edit: function() {
            var modal = new bootstrap.Modal("#inSolutionReactive_view");
            modal.show();
        }
    }

    $("*[data-tab-target]").on("click", (e) => {
        var elem = e.currentTarget;
        var tabTarget = $(elem).attr("data-tab-target");
        var tabContr = $(elem).attr("data-tab-contr");
        $(tabTarget).removeClass("d-none");
        $(tabContr).addClass("d-none");
    });

    $.feedItem.load();

    $("#selectReactiveMaterial_view").on("hide.bs.offcanvas", () => {
        $(".solution").addClass("d-none");
        $.solution.reload();
    });

    $("#selectReactiveMaterial_reactive_toSolution").on("click", () => {
        $.ajax({
            method: "GET",
            url: "/api/solutionReactive/" + $.solution.data.id + "/" + $.reactiveSelected.data.id,
            async: false
        }).done(function(result) {
            $.solutionReactive.data = result;
            var modal = bootstrap.Modal.getInstance("#toinSolutionReactive_view");
            if (!modal) {
                modal = new bootstrap.Modal("#toinSolutionReactive_view");
            }
            modal.show();
        }).fail(function(result) {
            if (result.status == 404) {
                var modal = bootstrap.Modal.getInstance("#toSolutionReactive_view");
                if (!modal) {
                    modal = new bootstrap.Modal("#toSolutionReactive_view");
                }
                modal.show();
            }
            else {

            }
        });
    });

    $("#toSolutionReactive_view").on("show.bs.modal", () => {
        $("#toSolutionReactive_view form :input").val('');
        $("#toSolutionReactive_name").text($.reactiveSelected.data.name);
        $("#toSolutionReactive_unit").val($.reactiveSelected.data.unit);
        $.validator.valid($("#toSolutionReactive_view form"));
    });
    $("#toSolutionReactive_view").on("shown.bs.modal", () => {
        $("#toSolutionReactive_count").trigger('focus');
    });
    $("#toSolutionReactive_add").on("click", () => {
        var inputs = $("#toSolutionReactive_view form input");
        var value = {"reactive": {"id": $.reactiveSelected.data.id}};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input.value) {
                value[input.name] = input.value.trim();
            }
        }
        if ($.validator.validate($("#toSolutionReactive_view form"))) {
            $.ajax({
                method: "POST",
                url: "/api/solutionReactive/" + $.solution.data.id,
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(result) {
                bootstrap.Modal.getInstance("#toSolutionReactive_view").hide();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

    $("#toinSolutionReactive_view").on("show.bs.modal", () => {
        $("#toinSolutionReactive_view form :input").val('');
        $("#toinSolutionReactive_name").text($.solutionReactive.data.reactive.name);
        $("#toinSolutionReactive_count").val($.solutionReactive.data.count);
        $("#toinSolutionReactive_unit").val($.solutionReactive.data.unit);
        $.validator.valid($("#toinSolutionReactive_view form"));
    });
    $("#toinSolutionReactive_view").on("shown.bs.modal", () => {
        $("#toinSolutionReactive_count").trigger('focus');
    });
    $("#toinSolutionReactive_save").on("click", () => {
        var inputs = $("#toinSolutionReactive_view form input");
        var value = {};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input.value) {
                value[input.name] = input.value.trim();
            }
        }
        if ($.validator.validate($("#toinSolutionReactive_view form"))) {
            $.ajax({
                method: "PUT",
                url: "/api/solutionReactive/" + $.solution.data.id + "/" + $.solutionReactive.data.reactive.id,
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(result) {
                $(".solution").addClass("d-none");
                $.solution.reload();
                bootstrap.Modal.getInstance("#toinSolutionReactive_view").hide();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

    $("#inSolutionReactive_view").on("show.bs.modal", () => {
        $("#inSolutionReactive_view form :input").val('');
        $("#inSolutionReactive_name").text($.solutionReactive.data.reactive.name);
        $("#inSolutionReactive_count").val($.solutionReactive.data.count);
        $("#inSolutionReactive_unit").val($.solutionReactive.data.unit);
        $.validator.valid($("#inSolutionReactive_view form"));
    });
    $("#inSolutionReactive_view").on("shown.bs.modal", () => {
        $("#inSolutionReactive_count").trigger('focus');
    });
    $("#inSolutionReactive_save").on("click", () => {
        var inputs = $("#inSolutionReactive_view form input");
        var value = {};
        for (var i = 0; i < inputs.length; i++) {
            var input = inputs[i];
            if (input.value) {
                value[input.name] = input.value.trim();
            }
        }
        if ($.validator.validate($("#inSolutionReactive_view form"))) {
            $.ajax({
                method: "PUT",
                url: "/api/solutionReactive/" + $.solution.data.id + "/" + $.solutionReactive.data.reactive.id,
                data: JSON.stringify(value),
                contentType: "application/json",
                async: false
            }).done(function(result) {
                $(".solution").addClass("d-none");
                $.solution.reload();
                bootstrap.Modal.getInstance("#inSolutionReactive_view").hide();
            }).fail(function(result) {
                var response = result.responseJSON;
                alert(response.message);
                //$(".info").html(html);
            });
        }
    });

    $("#deleteSolutionReactive_view").on("show.bs.modal", (event) => {
        $("#deleteSolutionReactive_name").text($.solutionReactive.data.reactive.name);
    });
    $("#deleteSolutionReactive_delete").on("click", () => {
        $.ajax({
            method: "DELETE",
            url: "/api/solutionReactive/" + $.solution.data.id + "/" + $.solutionReactive.data.reactive.id,
            async: false
        }).done(function(result) {
            $(".solution").addClass("d-none");
            $.solution.reload();
            bootstrap.Modal.getInstance("#deleteSolutionReactive_view").hide();
        }).fail(function(result) {
            var response = result.responseJSON;
            alert(response.message);
            //$(".info").html(html);
        });
    });

});

