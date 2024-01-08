var jlab = jlab || {};
jlab.staff = jlab.staff || {};

jlab.staff.validateSetPasswordForm = function() {
    if ($("#local-password").val() === '') {
        alert('Please select a password');
        return false;
    }

    return true;
};
jlab.staff.setPassword = function() {
    if (!jlab.staff.validateSetPasswordForm()) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $ajaxButton = $("#add-user-button");

    $ajaxButton.html("<span class=\"button-indicator\"></span>");
    $ajaxButton.attr("disabled", "disabled");

    var staffId = $("#staff-id").val(),
            password = $("#local-password").val(),
            doReload = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/set-local-password",
        type: "POST",
        data: {
            staffId: staffId,
            password: password
        },
        dataType: "html"
    });

    request.done(function(data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to set local password: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.error(function(xhr, textStatus) {
        window.console && console.log('Unable to set local password: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to set local password; server did not handle request');
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
$(document).on("click", "#open-local-pass-dialog-button", function() {
    $("#local-pass-dialog form")[0].reset();
    $("#local-pass-dialog").dialog("open");
});
$(document).on("click", "#set-password-button", function() {
    jlab.staff.setPassword();
});
$(function() {
    $("#local-pass-dialog").dialog({
        autoOpen: false,
        width: 640,
        minWidth: 640,
        height: 380,
        minHeight: 380,
        modal: true
    });
});