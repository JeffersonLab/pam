<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Users (Cache)"/>
<t:setup-page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
    </jsp:attribute>        
    <jsp:body>
        <section>
            <s:filter-flyout-widget ribbon="false">
            <form id="filter-form" class="filter-form" method="get" action="users">
                <fieldset>
                    <legend>Filter</legend>
                    <ul class="key-value-list"> 
                        <li>
                            <div class="li-key">
                                <label for="firstname">Firstname</label>
                            </div>
                            <div class="li-value">
                                <input id="firstname" name="firstname" value="${fn:escapeXml(param.firstname)}"/>
                                (use % as wildcard)
                            </div>
                        </li>                          
                        <li>
                            <div class="li-key">
                                <label for="lastname">Lastname</label>
                            </div>
                            <div class="li-value">
                                <input id="lastname" name="lastname" value="${fn:escapeXml(param.lastname)}"/>
                                (use % as wildcard)
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="username">Username</label>
                            </div>
                            <div class="li-value">
                                <input id="username" name="username" value="${fn:escapeXml(param.username)}"/>
                                (use % as wildcard)
                            </div>
                        </li>                          
                    </ul>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                </fieldset>
                <input id="filter-form-submit-button" type="submit" value="Apply"/>
            </form>     
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>            
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <table class="data-table stripped-table">
                <thead>
                    <tr>
                        <th>Select</th>
                        <th>Staff ID</th>
                        <th>Lastname</th>
                        <th>Firstname</th>
                        <th>Username</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${staffList}" var="staff">
                        <tr>
                            <td>
                                <form method="get" action="user">
                                    <input type="hidden" name="staffId" value="${staff.staffId}"/>
                                    <button class="single-char-button" type="submit">&rarr;</button>
                                </form>
                            </td>
                            <td><c:out value="${staff.staffId}"/></td>
                            <td><c:out value="${staff.lastname}"/></td>
                            <td><c:out value="${staff.firstname}"/></td>
                            <td><c:out value="${staff.username}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <button id="previous-button" type="button" data-offset="${paginator.previousOffset}" value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous</button>                        
            <button id="next-button" type="button" data-offset="${paginator.nextOffset}" value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next</button>
        </section>
    </jsp:body>         
</t:setup-page>
