package org.jlab.pam.presentation.util;

import java.util.ArrayList;
import java.util.List;
import org.jlab.pam.persistence.entity.Staff;
import org.jlab.pam.persistence.entity.Workgroup;

/**
 *
 * @author ryans
 */
public final class FilterSelectionMessage {

    private FilterSelectionMessage() {
        // Private constructor
    }

    public static String getMessage(String name, String username, String firstname, String lastname, Workgroup group, Staff staff) {

        List<String> filters = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            filters.add("Name \"" + name + "\"");
        }        
        
        if (username != null && !username.isEmpty()) {
            filters.add("Username \"" + username + "\"");
        }        

        if (firstname != null && !firstname.isEmpty()) {
            filters.add("Firstname \"" + firstname + "\"");
        }
        
        if (lastname != null && !lastname.isEmpty()) {
            filters.add("Lastname \"" + lastname + "\"");
        }

        if(group != null) {
            filters.add("Group \"" + group.getName() + "\"");
        }
        
        if(staff != null) {
            filters.add("Staff \"" + Functions.formatStaff(staff) + "\"");
        }        
        
        String message = "";

        if (!filters.isEmpty()) {
            for (String filter : filters) {
                message += " " + filter + " and";
            }

            // Remove trailing " and"
            message = message.substring(0, message.length() - 4);
        }

        return message;
    }
}
