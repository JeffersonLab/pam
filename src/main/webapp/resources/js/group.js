var jlab = jlab || {};
jlab.staff = jlab.staff || {};

jlab.editableRowTable.entity = 'User';
jlab.editableRowTable.dialog.width = 400;
jlab.editableRowTable.dialog.height = 300;


jlab.staff.validateAddUserForm = function () {
    if ($("#add-username").val() === '') {
        alert('Please select a username');
        return false;
    }

    return true;
};
jlab.staff.addUser = function () {
    if (!jlab.staff.validateAddUserForm()) {
        return;
    }

    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $ajaxButton = $("#table-row-save-button");

    $ajaxButton.width($ajaxButton.width());
    $ajaxButton.height($ajaxButton.height());

    $ajaxButton.html("<span class=\"button-indicator\"></span>");
    $ajaxButton.attr("disabled", "disabled");

    var groupId = $("#group-id").text(),
            username = $("#add-username").val(),
            doReload = false;

    window.console && console.log("username: " + username);

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/add-user-to-group",
        type: "POST",
        data: {
            groupId: groupId,
            username: username
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to add user: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to user: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to add user; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (doReload) {
            document.location.reload(true);
        } else {
            $ajaxButton.html("Save");
            $ajaxButton.removeAttr("disabled");
        }
    });
};
jlab.staff.removeUser = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    var $selectedRow = $("#staff-table tbody tr.selected-row");

    if ($selectedRow.length < 1) {
        return;
    }

    jlab.requestStart();

    var $ajaxButton = $("#remove-row-button");

    $ajaxButton.width($ajaxButton.width());
    $ajaxButton.height($ajaxButton.height());
    $ajaxButton.html("<span class=\"button-indicator\"></span>");
    $ajaxButton.attr("disabled", "disabled");

    var groupId = $("#group-id").text(),
            staffId = $selectedRow.attr("data-staff-id"),
            doReload = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/remove-user-from-group",
        type: "POST",
        data: {
            groupId: groupId,
            staffId: staffId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to add user: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to add user: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to add user; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (doReload) {
            document.location.reload(true);
        } else {
            $ajaxButton.html("Remove Selected");
            $ajaxButton.removeAttr("disabled");
        }
    });
};
jlab.staff.deleteGroup = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $ajaxButton = $("#delete-group-button");

    $ajaxButton.html("<span class=\"button-indicator\"></span>");
    $ajaxButton.attr("disabled", "disabled");

    var groupId = $("#group-id").text(),
            doReload = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/delete-group",
        type: "POST",
        data: {
            groupId: groupId
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to delete group: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to delete group: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to delete group; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (doReload) {
            document.location.href = jlab.contextPath + '/groups';
        } else {
            $ajaxButton.html("Delete Group");
            $ajaxButton.removeAttr("disabled");
        }
    });
};
jlab.staff.fetchGroupUsers = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $ajaxButton = $("#open-batch-edit-button");

    $ajaxButton.width($ajaxButton.width());
    $ajaxButton.height($ajaxButton.height());

    $ajaxButton.html("<span class=\"button-indicator\"></span>");
    $ajaxButton.attr("disabled", "disabled");

    var groupId = $("#group-id").text();

    var request = jQuery.ajax({
        url: jlab.contextPath + "/data/users",
        type: "GET",
        data: {
            'group-id': groupId
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.length > 0) {
            var $input = $("#batch-input");
            $input.empty();
            $(json).each(function () {
                $input.append(this.username + ' ');
            });
        }
        $("#batch-edit-dialog").dialog("open");
    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to fetch users: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to fetch users; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        $ajaxButton.html("Batch Edit");
        $ajaxButton.removeAttr("disabled");
    });
};
jlab.staff.batchEdit = function () {
    if (jlab.isRequest()) {
        window.console && console.log("Ajax already in progress");
        return;
    }

    jlab.requestStart();

    var $ajaxButton = $("#batch-edit-button");

    $ajaxButton.width($ajaxButton.width());
    $ajaxButton.height($ajaxButton.height());

    $ajaxButton.html("<span class=\"button-indicator\"></span>");
    $ajaxButton.attr("disabled", "disabled");

    var groupId = $("#group-id").text(),
            usernames = $("#batch-input").val(),
            doReload = false;

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/batch-edit-membership",
        type: "POST",
        data: {
            groupId: groupId,
            usernames: usernames
        },
        dataType: "html"
    });

    request.done(function (data) {
        if ($(".status", data).html() !== "Success") {
            alert('Unable to edit group membership: ' + $(".reason", data).html());
        } else {
            doReload = true;
        }

    });

    request.error(function (xhr, textStatus) {
        window.console && console.log('Unable to edit group membership: Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to edit group membership; server did not handle request');
    });

    request.always(function () {
        jlab.requestEnd();
        if (doReload) {
            document.location.reload(true);
        } else {
            $ajaxButton.html("Save");
            $ajaxButton.removeAttr("disabled");
        }
    });
};
$(document).on("click", "#open-add-user-button", function () {
    $("#add-user-dialog form")[0].reset();
    $("#add-user-dialog").dialog("open");
});
$(document).on("click", "#open-batch-edit-button", function () {
    jlab.staff.fetchGroupUsers();
});
$(document).on("click", "#batch-edit-button", function () {
    jlab.staff.batchEdit();
});
$(document).on("table-row-add", function () {
    jlab.staff.addUser();
});
$(document).on("click", "#remove-row-button", function () {
    if (confirm('Are you sure you want to remove the selected user?')) {
        jlab.staff.removeUser();
    }
});
$(document).on("click", "#delete-group-button", function () {
    if (confirm('Are you sure you want to delete this group?')) {
        jlab.staff.deleteGroup();
    }
});
$(function () {
    $("#batch-edit-dialog").dialog({
        autoOpen: false,
        width: 640,
        height: 480,
        modal: true,
        resizable: false
    });

    $("#add-username").autocomplete({
        minLength: 2,
        source: function (request, response) {
            $.ajax({
                data: {
                    term: request.term
                },
                url: '/hco/ajax/search-user',
                success: function (data) {
                    response($.map(data.records, function (item) {
                        return {
                            id: item.id,
                            label: item.label,
                            value: item.value
                        };
                    }));
                }
            });
        },
        select: function (event, ui) {
            $("#add-username").attr("data-staff-id", ui.item.id);
        }
    });
});