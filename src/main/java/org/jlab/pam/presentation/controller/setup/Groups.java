package org.jlab.pam.presentation.controller.setup;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jlab.smoothness.presentation.util.Paginator;
import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.pam.business.session.WorkgroupFacade;
import org.jlab.pam.persistence.entity.Workgroup;
import org.jlab.pam.presentation.util.FilterSelectionMessage;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Groups", urlPatterns = {"/setup/groups"})
public class Groups extends HttpServlet {

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

        String name = request.getParameter("name");

        int offset = ParamUtil.convertAndValidateNonNegativeInt(request, "offset", 0);
        int max = 10;

        List<Workgroup> groupList = groupFacade.filterList(name, null, null, offset, max);
        long totalRecords = groupFacade.countList(name, null, null, offset, max);

        Paginator paginator = new Paginator(totalRecords, offset, max);

        DecimalFormat formatter = new DecimalFormat("###,###");

        String selectionMessage;

        if (paginator.getTotalRecords() == 0) {
            selectionMessage = "Found 0 Groups";
        } else {
            selectionMessage = "Showing Group " + formatter.format(paginator.getStartNumber())
                    + " - " + formatter.format(paginator.getEndNumber())
                    + " of " + formatter.format(paginator.getTotalRecords());
        }

        String filters = FilterSelectionMessage.getMessage(name, null, null, null, null, null);

        if (filters.length() > 0) {
            selectionMessage = selectionMessage + " with " + filters;
        }

        request.setAttribute("selectionMessage", selectionMessage);
        request.setAttribute("groupList", groupList);
        request.setAttribute("paginator", paginator);

        request.getRequestDispatcher("/WEB-INF/views/setup/groups.jsp").forward(request, response);
    }
}
