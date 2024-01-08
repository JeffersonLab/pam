package org.jlab.pam.presentation.controller.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jlab.smoothness.presentation.util.ParamConverter;
import org.jlab.pam.business.session.WorkgroupFacade;

/**
 *
 * @author ryans
 */
@WebServlet(name = "BatchEditGroupMembership", urlPatterns = {"/ajax/batch-edit-membership"})
public class BatchEditGroupMembership extends HttpServlet {

    private static final Logger logger = Logger.getLogger(
            BatchEditGroupMembership.class.getName());    
    
    @EJB
    WorkgroupFacade groupFacade;
    
    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorReason = null;

        try {
            BigInteger groupId = ParamConverter.convertBigInteger(
                    request, "groupId");
            String usernames = request.getParameter("usernames");
            
            groupFacade.batchEditMembership(groupId, usernames);
        } catch(EJBAccessException e) {
            logger.log(Level.WARNING, "Not authorized", e);
            errorReason = "Not authorized";             
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to edit group membership", e);
            errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        
        response.setContentType("text/xml");

        PrintWriter pw = response.getWriter();

        String xml;

        if (errorReason == null) {
            xml = "<response><span class=\"status\">Success</span></response>";
        } else {
            xml = "<response><span class=\"status\">Error</span><span "
                    + "class=\"reason\">" + errorReason + "</span></response>";
        }

        pw.write(xml);

        pw.flush();

        boolean error = pw.checkError();

        if (error) {
            logger.log(Level.SEVERE, "PrintWriter Error");
        }        
    }
}
