<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Profile"/>
<t:page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/profile.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <dl>
                <dt>Name: </dt>
                <dd><c:out value="${idToken.name}"/></dd>
                <dt>Email: </dt>
                <dd><c:out value="${idToken.email}"/></dd>
                <dt>Groups: </dt>
                <dd>
                    <ul>
                        <c:forEach items="${roleList}" var="role">
                            <li><c:out value="${role}"/></li>
                        </c:forEach>
                    </ul>
                </dd>
            </dl>
        </section>
    </jsp:body>
</t:page>