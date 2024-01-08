<%@tag description="The Setup Page Template Tag" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="title" %>
<%@attribute name="stylesheets" fragment="true" %>
<%@attribute name="scripts" fragment="true" %>
<t:page title="${title}" category="Setup">
    <jsp:attribute name="stylesheets">
        <jsp:invoke fragment="stylesheets"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <jsp:invoke fragment="scripts"/>
    </jsp:attribute>
    <jsp:attribute name="secondaryNavigation">
                        <ul>
                            <li${fn:startsWith(currentPath, '/setup/user') ? ' class="current-secondary"' : ''}>
                                <a href="${pageContext.request.contextPath}/setup/users">Users (Cache)</a>
                            </li>
                            <li${fn:startsWith(currentPath, '/setup/group') ? ' class="current-secondary"' : ''}>
                                <a href="${pageContext.request.contextPath}/setup/groups">Groups (Cache)</a>
                            </li>
                        </ul>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody/>
    </jsp:body>
</t:page>
