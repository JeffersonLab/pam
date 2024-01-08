package org.jlab.pam.presentation.controller;

import org.jlab.smoothness.presentation.util.ParamUtil;
import org.jlab.pam.business.session.LDAPFacade;
import org.jlab.pam.persistence.entity.Workgroup;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Roles", urlPatterns = {"/roles"})
public class Roles extends HttpServlet {

    @EJB
    LDAPFacade ldapFacade;

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

        List<Workgroup> groupList = null;
        try {
            groupList = ldapFacade.findOpsRoles();
        } catch (NamingException e) {
            e.printStackTrace();
            throw new ServletException("Unable to connect to directory server");
        }

        request.setAttribute("groupList", groupList);

        request.getRequestDispatcher("/WEB-INF/views/roles.jsp").forward(request, response);
    }
}
