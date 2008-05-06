<%@    taglib prefix="set"         uri="/v/org/infogrid/jee/taglib/mesh/set/set.tld"
 %><%@ taglib prefix="mesh"        uri="/v/org/infogrid/jee/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="netmesh"     uri="/v/org/infogrid/jee/taglib/mesh/net/netmesh.tld"
 %><%@ taglib prefix="netmeshbase" uri="/v/org/infogrid/jee/taglib/meshbase/net/netmeshbase.tld"
 %><%@ taglib prefix="candy"       uri="/v/org/infogrid/jee/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"           uri="/v/org/infogrid/jee/taglib/util/util.tld"
 %><%@ taglib prefix="v"           uri="/v/org/infogrid/jee/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="tmpl"  uri="/v/org/infogrid/jee/taglib/viewlet/templates/templates.tld"
 %><%@ taglib prefix="c"           uri="http://java.sun.com/jsp/jstl/core"
 %>
<tmpl:stylesheet href="${CONTEXT}/v/org/infogrid/jee/viewlet/propertysheet/net/NetPropertySheetViewlet.css"/>
<v:viewletAlternatives />
<u:refresh>Reload page</u:refresh>
<v:viewlet>

 <%@ include file="/v/org/infogrid/jee/viewlet/propertysheet/PropertySheetViewlet/audit.jsp" %>

 <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-delete', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="Subject" stringRepresentation="Plain" />' } )" title="Delete this MeshObject"><img src="${CONTEXT}/s/icons/trash.png" alt="Delete"/></a></div>
 <h1>Net Property Sheet for: <mesh:meshObjectId meshObjectName="Subject" stringRepresentation="Html" maxLength="30"/></h1>
 
 <%@ include file="/v/org/infogrid/jee/viewlet/propertysheet/PropertySheetViewlet/attributes.jsp" %>
 <%@ include file="/v/org/infogrid/jee/viewlet/propertysheet/PropertySheetViewlet/neighbors.jsp" %>
 <%@ include file="/v/org/infogrid/jee/viewlet/propertysheet/net/NetPropertySheetViewlet/replica-info.jsp" %>

 <%@ include file="/v/org/infogrid/jee/shell/http/HttpShellVerb.jsp" %>
</v:viewlet>
