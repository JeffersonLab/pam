package org.jlab.pam.presentation.controller;

import org.wildfly.security.auth.server.SecurityDomain;
import org.wildfly.security.auth.server.SecurityIdentity;
import org.wildfly.security.authz.Attributes;

import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Profile", urlPatterns = {"/profile"})
public class Profile extends HttpServlet {
    
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

        SecurityDomain domain = SecurityDomain.getCurrent();
        SecurityIdentity identity = domain.getCurrentSecurityIdentity();
        org.wildfly.security.authz.Roles roles = identity.getRoles();

        List<String> roleList = StreamSupport
                .stream(roles.spliterator(), false)
                .collect(Collectors.toList());

        roleList.sort(String.CASE_INSENSITIVE_ORDER);

        Attributes attributes = identity.getAttributes();

        for(Attributes.Entry e: attributes.entries()) {
            System.out.print(e.getKey());
            System.out.print("=");
            System.out.print(e.getClass().getSimpleName());
            System.out.print(";");
            System.out.println(e);
        }


        request.setAttribute("roleList", roleList);

        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
}
