package org.jlab.pam.presentation.controller;

import org.jlab.pam.business.session.LDAPFacade;
import org.jlab.pam.persistence.entity.Staff;
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
@WebServlet(name = "Role", urlPatterns = {"/role"})
public class Role extends HttpServlet {

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

        String cn = request.getParameter("cn");

        List<Staff> staffList = null;
        try {
            staffList = ldapFacade.findStaffByGroupCn(cn);
        } catch (NamingException e) {
            e.printStackTrace();
            throw new ServletException("Unable to query directory server");
        }

        Workgroup group = null;
        
        if(cn != null) {
            try {
                group = ldapFacade.findGroupByCn(cn);
            } catch(NamingException e) {
                e.printStackTrace();
                throw new ServletException("Unable to query directory server");
            }
        }


        request.setAttribute("group", group);
        request.setAttribute("staffList", staffList);

        request.getRequestDispatcher("/WEB-INF/views/role.jsp").forward(request, response);
    }
}
