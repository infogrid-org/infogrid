<%@    page contentType="text/html"
 %><%@ taglib prefix="set"   uri="/org/infogrid/web/taglib/mesh/set/set.tld"
 %><%@ taglib prefix="mesh"  uri="/org/infogrid/web/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="candy" uri="/org/infogrid/web/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"     uri="/org/infogrid/web/taglib/util/util.tld"
 %><%@ taglib prefix="v"     uri="/org/infogrid/web/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="tmpl"  uri="/org/infogrid/web/taglib/templates/templates.tld"
 %><%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"
 %><%@ page import="org.infogrid.web.viewlet.WebViewlet"
 %><%@ page import="org.infogrid.web.viewlet.meshbase.AllMeshObjectsViewlet"
 %><%@ page import="org.infogrid.meshbase.MeshBase"
 %><%@ page import="org.infogrid.mesh.MeshObject"
 %>
<tmpl:stylesheet href="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/AllMeshObjectsViewlet.css"/>
<v:viewletAlternatives />
<v:viewlet>
 <div class="slide-in-button">
  <u:callJspo name="create" action="${Viewlet.postUrl}" linkTitle="Create a MeshObject" submitLabel="Create">
   <span class="org-infogrid-web-viewlet-meshbase-AllMeshObjectsViewlet-create-button"><span>Create</span></span>
  </u:callJspo>
 </div>
<%
    AllMeshObjectsViewlet v = (AllMeshObjectsViewlet) pageContext.getRequest().getAttribute( WebViewlet.VIEWLET_ATTRIBUTE_NAME );

    if( v.isFiltered() ) {
%>
 <h1>MeshObjects in the MeshBase (filtered)</h1>
<%
    } else {
        MeshBase mb = v.getSubject().getMeshBase();
%>
 <h1>MeshObjects in the MeshBase (<%= mb.getSize() %> total)</h1>
<%
    }
%>

 <div class="nav">
  <div class="left">
   <c:if test="${Viewlet.navigationStartMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationStartMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_start_blue.png" alt="Go to start" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationStartMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_start.png" alt="Go to start (disabled)" />
   </c:if>
  </div>

  <div class="left">
   <c:if test="${Viewlet.navigationBackMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationBackMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_rewind_blue.png" alt="Previous" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationBackMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_rewind.png" alt="Previous (disabled)" />
   </c:if>
  </div>

  <div class="right">
   <c:if test="${Viewlet.navigationEndMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationEndMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_end_blue.png" alt="Go to last" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationEndMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_end.png" alt="Go to last (disabled)" />
   </c:if>
  </div>

  <div class="right">
   <c:if test="${Viewlet.navigationForwardMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationForwardMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_fastforward_blue.png" alt="Next" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationForwardMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_fastforward.png" alt="Next (disabled)" />
   </c:if>
  </div>
  <div class="middle">
   <u:safeForm action="${Viewlet.postUrl}" method="GET">
    <input type="hidden" name="lid-format" value="viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet" />
    <div class="middle-item">
     Regex for Identifier: <input type="text" autocorrect="off" autocapitalization="off" name="identifier-regex" value="${Viewlet.idRegex}"/>
    </div>
    <div class="middle-item">
     Show MeshTypes:
     <select name="show-types">
      ${Viewlet.showTypesHtml}
     </select>
    </div>
    <div class="middle-item">
     <input  type="submit" name="lid-submit" value="Filter" />
    </div>
   </u:safeForm>
  </div>
 </div>
 <table class="set">
  <thead>
   <tr>
    <th>Identifier</th>
    <th>Neighbors</th>
    <th>Types and Attributes</th>
    <th>Audit</th>
   </tr>
  </thead>
  <tbody>
   <c:forEach items="${Viewlet.cursorIterator}" var="current" varStatus="currentStatus">
    <u:rotatingTr statusVar="currentStatus" htmlClasses="bright,dark" firstRowHtmlClass="first" lastRowHtmlClass="last">
     <td>
      <div class="slide-in-button">
       <mesh:meshObjectLink meshObjectName="current" addArguments="lid-format=viewlet:org.infogrid.web.viewlet.propertysheet.PropertySheetViewlet"><img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/pencil.png" alt="Edit"/></mesh:meshObjectLink>
       <u:callJspo name="delete" action="${Viewlet.postUrl}" linkTitle="Delete this MeshObject" submitLabel="Delete">
        <u:callJspoParam name="toDelete" value="${current}"/>
        <span class="org-infogrid-web-viewlet-meshbase-AllMeshObjectsViewlet-delete-button"><span>Delete</span></span>
       </u:callJspo>
      </div>
      <mesh:meshObjectLink meshObjectName="current"><mesh:meshObjectId meshObjectName="current" maxLength="30"/></mesh:meshObjectLink>
     </td>
     <td>
      <mesh:neighborIterate meshObjectName="current" neighborLoopVar="neighbor">
       <mesh:meshObjectLink meshObjectName="neighbor"><mesh:meshObjectId meshObjectName="neighbor" maxLength="30"/></mesh:meshObjectLink><br />
      </mesh:neighborIterate>
     </td>
     <td>
      <ul class="types">
       <mesh:blessedByIterate meshObjectName="current" blessedByLoopVar="blessedBy">
        <li>
         <mesh:type meshTypeName="blessedBy"/>
         <ul class="properties">
          <mesh:propertyIterate meshObjectName="current" meshTypeName="blessedBy" propertyTypeLoopVar="propertyType" propertyValueLoopVar="propertyValue">
           <li><mesh:type meshTypeName="propertyType" />:&nbsp;<mesh:property meshObjectName="current" propertyTypeName="propertyType" /></li>
          </mesh:propertyIterate>
         </ul>
        </li>
       </mesh:blessedByIterate>
      </ul>
     </td>
     <td>
      <table class="audit">
       <tr>
        <td class="label">Created:</td><td><mesh:timeCreated meshObjectName="current" /></td>
       </tr>
       <tr>
        <td class="label">Updated:</td><td><mesh:timeUpdated meshObjectName="current" /></td>
       </tr>
      </table>
     </td>
    </u:rotatingTr>
   </c:forEach>
  </tbody>
 </table>
 <div class="nav">
  <div class="left">
   <c:if test="${Viewlet.navigationStartMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationStartMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_start_blue.png" alt="Go to start" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationStartMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_start.png" alt="Go to start (disabled)" />
   </c:if>
  </div>

  <div class="left">
   <c:if test="${Viewlet.navigationBackMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationBackMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_rewind_blue.png" alt="Previous" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationBackMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_rewind.png" alt="Previous (disabled)" />
   </c:if>
  </div>

  <div class="right">
   <c:if test="${Viewlet.navigationEndMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationEndMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_end_blue.png" alt="Go to last" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationEndMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_end.png" alt="Go to last (disabled)" />
   </c:if>
  </div>

  <div class="right">
   <c:if test="${Viewlet.navigationForwardMeshObject != null}">
    <v:navigateToPage meshObject="${Viewlet.navigationForwardMeshObject}" addArguments="lid-format=viewlet:org.infogrid.jee.viewlet.meshbase.AllMeshObjectsViewlet&page-length=${Viewlet.pageLength}&id-regex=${Viewlet.idRegex}&show-types=${Viewlet.showTypes}">
     <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_fastforward_blue.png" alt="Next" />
    </v:navigateToPage>
   </c:if>
   <c:if test="${Viewlet.navigationForwardMeshObject == null}">
    <img src="${CONTEXT}/v/org/infogrid/web/viewlet/meshbase/control_fastforward.png" alt="Next (disabled)" />
   </c:if>
  </div>
 </div>
</v:viewlet>
