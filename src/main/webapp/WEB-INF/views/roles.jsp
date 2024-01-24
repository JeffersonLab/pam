<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Accelerator Operations Roles"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">    
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/groups.js"></script>
    </jsp:attribute>        
    <jsp:body>
        <section>
            <h2 id="page-header-title"><c:out value="${title}"/></h2>

                <ul>
                    <c:forEach items="${groupList}" var="group">
                        <li>
                            <a href="role?cn=${group.name}"><c:out value="${group.description}"/></a>
                        </li>
                    </c:forEach>
                </ul>
        </section>
    </jsp:body>         
</t:page>
