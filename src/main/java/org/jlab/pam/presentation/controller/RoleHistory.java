package org.jlab.pam.presentation.controller;

import org.jlab.pam.business.session.WorkgroupFacade;
import org.jlab.pam.persistence.entity.Staff;
import org.jlab.pam.persistence.entity.Workgroup;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ryans
 */
@WebServlet(name = "RoleHistory", urlPatterns = {"/role-history"})
public class RoleHistory extends HttpServlet {

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
        Date versionDate = null;
        try {
            versionDate = convertISO8601DateTimeDefault(request, "date");
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ServletException("Unable to parse version date");
        }

        List<Staff> staffList = workgroupFacade.findGroupMembershipHistory(cn, versionDate);

        Workgroup group = null;
        
        if(cn != null) {
            group = workgroupFacade.findByName(cn);
        }


        request.setAttribute("versionDate", versionDate);
        request.setAttribute("group", group);
        request.setAttribute("staffList", staffList);

        request.getRequestDispatcher("/WEB-INF/views/role-history.jsp").forward(request, response);
    }

    public static Date convertISO8601DateTimeDefault(HttpServletRequest request, String name) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        Date value = null;
        String valueStr = request.getParameter(name);
        if (valueStr != null && !valueStr.isEmpty()) {
            LocalDateTime ldt = (LocalDateTime)formatter.parse(valueStr, LocalDateTime::from);
            ZonedDateTime zdt = ldt.atZone(ZoneId.of("America/New_York"));
            value = Date.from(zdt.toInstant());
        }

        return value;
    }
}
