<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<%@taglib prefix="jlab" uri="http://jlab.org/staff/functions"%>
<c:set var="title" value="${jlab:formatStaff(staff)}"/>
<t:directory-page title="${title}">
    <jsp:attribute name="stylesheets">        
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/user.js"></script>
    </jsp:attribute>        
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li><a href="users">Users</a></li>
                <li><c:out value="${title}"/></li>
            </ul>
        </div>        
        <section>
            <div id="action-button-panel">
                <button id="open-local-pass-dialog-button" type="button">Local Password</button>
            </div>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <span>Staff ID:</span>
                    </div>
                    <div class="li-value">
                        <c:out value="${staff.staffId}"/>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <span>Lastname:</span>
                    </div>
                    <div class="li-value">
                        <c:out value="${staff.lastname}"/>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <span>Firstname:</span>
                    </div>
                    <div class="li-value">
                        <c:out value="${staff.firstname}"/>
                    </div>
                </li>   
                <li>
                    <div class="li-key">
                        <span>Username:</span>
                    </div>
                    <div class="li-value">
                        <c:out value="${staff.username}"/>
                    </div>
                </li>
            </ul>
            <h3>Group List</h3>
            <form id="filter-form" class="filter-form" method="get" action="user">
                <input type="hidden" id="staff-id-input" name="staffId" value="${param.staffId}"/>
                <input type="hidden" id="offset-input" name="offset" value="0"/>
            </form>         
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <c:if test="${fn:length(groupList) > 0}">
                <table class="data-table stripped-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${groupList}" var="group">
                            <tr>
                                <td><a href="group?groupId=${group.workgroupId}"><c:out value="${group.name}"/></a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <button id="previous-button" type="button" data-offset="${paginator.previousOffset}" value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous</button>                        
                <button id="next-button" type="button" data-offset="${paginator.nextOffset}" value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next</button>                     
            </c:if>
        </section>
        <div id="local-pass-dialog" class="dialog" title="Set Local Password">
            <p>Note: The Local Password is only used with the Local Login Module.</p>
            <form>
                <ul class="key-value-list">                          
                    <li>
                        <div class="li-key">
                            <label for="local-password">Password</label>
                        </div>
                        <div class="li-value">
                            <input type="password" id="local-password" name="password"/>
                        </div>
                    </li>                                               
                </ul>
                <div class="dialog-button-panel">
                    <input type="hidden" id="staff-id" name="staffId" value="${staff.staffId}"/>
                    <button type="button" id="set-password-button" class="dialog-submit">Save</button>
                    <button type="button" class="dialog-close-button">Cancel</button>
                </div>
            </form>
        </div>
    </jsp:body>         
</t:directory-page>
