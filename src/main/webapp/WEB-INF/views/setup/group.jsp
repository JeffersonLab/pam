<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="${group.name}"/>
<t:setup-page title="${title}">
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
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/group.js"></script>
    </jsp:attribute>        
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li><a href="groups">Groups</a></li>
                <li><c:out value="${title}"/></li>
            </ul>
        </div>        
        <section>
            <c:choose>
                <c:when test="${group eq null}">
                    <div>No Group Found With ID <c:out value="${param.groupId}"/></div>
                </c:when>
                <c:otherwise>
                    <form id="filter-form" method="get" action="group">
                        <input type="hidden" id="groupId" name="groupId" value="${param.groupId}"/>
                        <input type="hidden" id="offset-input" name="offset" value="0"/>
                    </form>
                    <div id="action-button-panel">
                        <button type="button" id="delete-group-button">Delete Group</button>
                    </div>
                    <ul class="key-value-list">                          
                        <li>
                            <div class="li-key">
                                <span>Group ID:</span>
                            </div>
                            <div class="li-value">
                                <span id="group-id"><c:out value="${group.workgroupId}"/></span>
                            </div>
                        </li>     
                        <li>
                            <div class="li-key">
                                <span>Description:</span>
                            </div>
                            <div class="li-value">
                                <c:out value="${group.description}"/>
                            </div>
                        </li> 
                    </ul>
                    <c:if test="${group.name eq 'Program Deputies'}">
                        <div class="error-message">WARNING: Program Deputies are synced from the <a href="http://opweb.acc.jlab.org/CSUEApps/bta03/pd_assignments.php">PD Assignments</a> app and should be managed there.</div>
                    </c:if>
                    <h3>Staff List</h3>
                    <div class="message-box"><c:out value="${selectionMessage}"/></div>
                    <s:editable-row-table-controls excludeEdit="${true}">
                        <button type="button" class="no-selection-row-action" id="open-batch-edit-button">Batch Edit</button>
                    </s:editable-row-table-controls>
                    <c:if test="${fn:length(staffList) > 0}">
                        <table id="staff-table" class="data-table stripped-table uniselect-table editable-row-table">
                            <thead>
                                <tr>
                                    <th>Lastname</th>
                                    <th>Firstname</th>
                                    <th>Username</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${staffList}" var="staff">
                                    <tr data-staff-id="${staff.staffId}">
                                        <td><c:out value="${staff.lastname}"/></td>
                                        <td><c:out value="${staff.firstname}"/></td>
                                        <td><c:out value="${staff.username}"/></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <button id="previous-button" type="button" data-offset="${paginator.previousOffset}" value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous</button>                        
                        <button id="next-button" type="button" data-offset="${paginator.nextOffset}" value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next</button>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </section>
        <s:editable-row-table-dialog>
            <form id="row-form">
                <ul class="key-value-list">                          
                    <li>
                        <div class="li-key">
                            <label for="add-username">Username</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="add-username" name="username"/>
                        </div>
                    </li>                                               
                </ul>
                <div class="dialog-button-panel">
                    <button type="button" id="add-user-button" class="dialog-submit">Save</button>
                    <button type="button" class="dialog-close-button">Cancel</button>
                </div>
            </form>
        </s:editable-row-table-dialog>
        <div id="batch-edit-dialog" class="dialog" title="Edit Group's Users">
            <form>
                <label for="batch-input">Comma/Whitespace Separated List of Usernames</label>
                <textarea id="batch-input"></textarea>
                <div class="dialog-button-panel">
                    <button type="button" id="batch-edit-button" class="dialog-submit">Save</button>
                    <button type="button" class="dialog-close-button">Cancel</button>
                </div>
            </form>
        </div>            
    </jsp:body>         
</t:setup-page>
