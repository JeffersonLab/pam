package org.jlab.pam.presentation.controller.directory;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.pam.business.session.StaffFacade;
import org.jlab.pam.business.session.WorkgroupFacade;
import org.jlab.pam.persistence.entity.Staff;
import org.jlab.pam.persistence.entity.Workgroup;
import org.jlab.pam.presentation.util.FilterSelectionMessage;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Group", urlPatterns = {"/setup/group"})
public class Group extends HttpServlet {

    @EJB
    StaffFacade staffFacade;
    @EJB
    WorkgroupFacade groupFacade;
    
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BigInteger groupId = ParamConverter.convertBigInteger(request, "groupId");

        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int max = 10;

        List<Staff> staffList = staffFacade.filterList(null, null, null, null, groupId, offset, max);
        long totalRecords = staffFacade.countList(null, null, null, null, groupId, offset, max);

        Paginator paginator = new Paginator(totalRecords, offset, max);

        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage;

        if (paginator.getTotalRecords() == 0) {
            selectionMessage = "Found 0 Staff";
        } else {
            selectionMessage = "Showing Staff " + formatter.format(paginator.getStartNumber())
                    + " - " + formatter.format(paginator.getEndNumber())
                    + " of " + formatter.format(paginator.getTotalRecords());
        }

        Workgroup group = null;
        
        if(groupId != null) {
            group = groupFacade.find(groupId);
        }
        
        String filters = FilterSelectionMessage.getMessage(null, null, null, null, group, null);

        /*if (filters.length() > 0) {
            selectionMessage = selectionMessage + " with " + filters;
        }*/

        request.setAttribute("group", group);
        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("staffList", staffList);
        request.setAttribute("paginator", paginator);

        request.getRequestDispatcher("/WEB-INF/views/setup/group.jsp").forward(request, response);
    }
}
