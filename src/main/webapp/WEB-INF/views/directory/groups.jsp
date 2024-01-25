<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Groups"/>
<t:directory-page title="${title}">
    <jsp:attribute name="stylesheets">    
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/groups.js"></script>
    </jsp:attribute>        
    <jsp:body>
        <section>
            <s:filter-flyout-widget ribbon="false">
                <form id="filter-form" class="filter-form" method="get" action="groups">
                    <fieldset>
                        <legend>Filter</legend>
                        <ul class="key-value-list">                       
                            <li>
                                <div class="li-key">
                                    <label for="name">Name</label>
                                </div>
                                <div class="li-value">
                                    <input id="name" name="name" value="${fn:escapeXml(param.name)}"/>
                                    (use % as wildcard)
                                </div>
                            </li>                        
                        </ul>
                    </fieldset>
                    <input type="hidden" id="offset-input" name="offset" value="0"/>
                    <input id="filter-form-submit-button" type="submit" value="Apply"/>                                
                </form>   
            </s:filter-flyout-widget>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>                                
            <div id="action-button-panel">
                <button id="open-add-group-button" type="button">Add New Group</button>
            </div>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <table class="data-table stripped-table">
                <thead>
                    <tr>
                        <th>Select</th>
                        <th>Name</th>
                        <th>Description</th>  
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${groupList}" var="group">
                        <tr>
                            <td>
                                <form method="get" action="group">
                                    <input type="hidden" name="groupId" value="${group.workgroupId}"/>
                                    <button class="single-char-button" type="submit">&rarr;</button>
                                </form>
                            </td>
                            <td><c:out value="${group.name}"/></td>
                            <td><c:out value="${group.description}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <button id="previous-button" type="button" data-offset="${paginator.previousOffset}" value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous</button>                        
            <button id="next-button" type="button" data-offset="${paginator.nextOffset}" value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next</button>            
        </section>
        <div id="add-group-dialog" class="dialog" title="Add Group">
            <form>
                <ul class="key-value-list">                          
                    <li>
                        <div class="li-key">
                            <label for="add-name">Name</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="add-name" name="name"/>
                            <span>(max 8 char, lowercase w/no spaces)</span>
                        </div>
                    </li>   
                    <li>
                        <div class="li-key">
                            <label for="add-description">Description</label>
                        </div>
                        <div class="li-value">
                            <input type="text" id="add-description" name="description" maxlength="8"/>
                        </div>
                    </li>                      
                </ul>
                <div class="dialog-button-panel">
                    <button type="button" id="add-group-button" class="dialog-submit">Save</button>
                    <button type="button" class="dialog-close-button">Cancel</button>
                </div>
            </form>
        </div>
    </jsp:body>         
</t:directory-page>
