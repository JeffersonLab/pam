<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%> 
<c:set var="title" value="Role Histories"/>
<t:page title="${title}">  
    <jsp:attribute name="stylesheets">    
    </jsp:attribute>
    <jsp:attribute name="scripts"> 
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/groups.js"></script>
    </jsp:attribute>        
    <jsp:body>
        <div class="breadbox">
            <ul class="breadcrumb">
                <li><a href="roles">Roles</a></li>
                <li><a href="role?cn=${param.cn}">${group.description}</a></li>
                <li>History</li>
            </ul>
        </div>
        <section>
            <h3>Version List</h3>
                <ul>
                    <c:forEach items="${dateList}" var="record">
                        <li>
                            <a href="role-history?cn=${param.cn}&date=${record}"><fmt:formatDate pattern="yyyy-MMM-dd" value="${record}"/></a>
                        </li>
                    </c:forEach>
                </ul>
        </section>
    </jsp:body>         
</t:page>
