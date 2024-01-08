var jlab = jlab || {};
jlab.staff = jlab.staff || {};
jlab.staff.validateAddGroupForm = function() {
    if ($("#add-name").val() === '') {
        alert('Please provide a name');
        return false;
    }

    if ($("#add-description").val() === '') {
        alert('Please provide a description');
        return false;
    }

    return true;
};
jlab.staff.addGroup = function() {
    if (!jlab.staff.validateAddGroupForm()) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $ajaxButton = $("#add-group-button");

    $ajaxButton.html("<span class=\"button-indicator\"></span>");
    $ajaxButton.attr("disabled", "disabled");

    var name = $("#add-name").val(),
            description = $("#add-description").val(),
            doReload = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/add-group",
        type: "POST",
        data: {
            name: name,
            description: description
        },
        dataType: "html"
    });

    request.done(function(data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to add group: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.error(function(xhr, textStatus) {
        window.console && console.log('Unable to group: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to add group; server did not handle request');
    });

    request.always(function() {
        jlab.requestEnd();
        if (doReload) {
            document.location.reload(true);
        } else {
            $ajaxButton.html("Save");
            $ajaxButton.removeAttr("disabled");
        }
    });
};
$(document).on("click", "#next-button, #previous-button", function() {
    $("#offset-input").val($(this).attr("data-offset"));
    $("#filter-form").submit();
});
$(document).on("click", "#open-add-group-button", function() {
    $("#add-group-dialog form")[0].reset();
    $("#add-group-dialog").dialog("open");
});
$(document).on("click", "#add-group-button", function() {
    jlab.staff.addGroup();
});
$(function() {
    $("#add-group-dialog").dialog({
        autoOpen: false,
        width: 740,
        minWidth: 740,
        height: 480,
        minHeight: 480,
        modal: true
    });
});