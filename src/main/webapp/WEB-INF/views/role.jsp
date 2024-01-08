<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="${group.description}"/>
<t:page title="${title}">
    <jsp:attribute name="stylesheets">     
        <style type="text/css">
            #batch-input {
                display: block;
                width: 580px;
                height: 250px;
                resize: none;
            }
            #delete-group-button {
                float: right;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li><a href="roles">Roles</a></li>
                <li><c:out value="${title}"/></li>
            </ul>
        </div>        
        <section>
            <c:choose>
                <c:when test="${group eq null}">
                    <div>No Role Found With ID <c:out value="${param.cn}"/></div>
                </c:when>
                <c:otherwise>
                    <div style="float: right;"><a href="role-histories?cn=${param.cn}">History</a></div>
                    <div><b>UNIX Group Name: </b> <c:out value="${group.name}"/></div>
                    <h3>Staff List</h3>
                    <c:if test="${fn:length(staffList) > 0}">
                        <table id="staff-table" class="data-table stripped-table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Email</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${staffList}" var="staff">
                                    <tr data-staff-id="${staff.staffId}">
                                        <td><c:out value="${staff.firstname}"/> <c:out value="${staff.lastname}"/></td>
                                        <td><c:out value="${staff.username}"/>@jlab.org</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </section>
    </jsp:body>         
</t:page>
