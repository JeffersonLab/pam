package org.jlab.pam.presentation.controller;

import org.jlab.pam.business.session.WorkgroupFacade;
import org.jlab.pam.persistence.entity.Workgroup;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ryans
 */
@WebServlet(name = "RoleHistories", urlPatterns = {"/role-histories"})
public class RoleHistories extends HttpServlet {

    @EJB
    WorkgroupFacade workgroupFacade;

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

        String cn = request.getParameter("cn");

        List<Date> dateList = workgroupFacade.findGroupMembershipHistories(cn);

        Workgroup group;

        if(cn != null) {
            group = workgroupFacade.findByName(cn);
        } else {
            group = new Workgroup();
        }

        request.setAttribute("group", group);
        request.setAttribute("dateList", dateList);

        request.getRequestDispatcher("/WEB-INF/views/role-histories.jsp").forward(request, response);
    }
}
