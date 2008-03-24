<%@    taglib prefix="set"   uri="/v/org/infogrid/jee/taglib/mesh/objectset/objectset.tld"
 %><%@ taglib prefix="mesh"  uri="/v/org/infogrid/jee/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="candy" uri="/v/org/infogrid/jee/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"     uri="/v/org/infogrid/jee/taglib/util/util.tld"
 %><%@ taglib prefix="v"     uri="/v/org/infogrid/jee/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"
 %>
<v:viewletAlternatives />
<u:refresh>Reload page</u:refresh>
<v:viewlet>
 <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-create', {} )" title="Create a MeshObject"><img src="${CONTEXT}/s/icons/add.png" alt="Create"/></a></div>
 <h1>All MeshObjects in the MeshBase</h1>
 <table class="set">
  <thead>
   <tr>
    <th>Identifier</th>
    <th>Types and Attributes</th>
    <th>Neighbors</th>
    <th>Audit</th>
   </tr>
  </thead>
  <tbody>
   <c:forEach items="${Viewlet.cursorIterator}" var="current" varStatus="currentStatus">
    <u:rotatingTr varStatus="currentStatus" htmlClasses="bright,dark" firstRowHtmlClass="first" lastRowHtmlClass="last">
     <td>
      <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-delete', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="current" stringRepresentation="Plain" />' } )" title="Delete this MeshObject"><img src="${CONTEXT}/s/icons/trash.png" alt="Delete"/></a></div>
      <mesh:meshObjectLink meshObjectName="current"><mesh:meshObjectId meshObjectName="current" maxLength="30"/></mesh:meshObjectLink>
     </td>
     <td>
      <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-bless', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="current" stringRepresentation="Plain" />' } )" title="Bless this MeshObject"><img src="${CONTEXT}/s/icons/add.png" alt="Add type"/></a></div>
      <ul class="types">
       <mesh:blessedByIterate meshObjectName="current" blessedByLoopVar="blessedBy">
        <li>
         <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-unbless', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="current" stringRepresentation="Plain" />', 'mesh.subjecttype' : '<mesh:meshTypeId meshTypeName="blessedBy" stringRepresentation="Plain" />' } )" title="Unbless this MeshObject"><img src="${CONTEXT}/s/icons/trash.png" alt="Delete"/></a></div>
         <mesh:type meshTypeName="blessedBy"/>
         <ul class="properties">
          <mesh:propertyIterate meshObjectName="current" meshTypeName="blessedBy" propertyTypeLoopVar="propertyType" propertyValueLoopVar="propertyValue">
          <li><mesh:type          meshTypeName="propertyType" />:&nbsp;<mesh:propertyValue propertyValueName="propertyValue" /></li>
          </mesh:propertyIterate>
         </ul>
        </li>
       </mesh:blessedByIterate>
      </ul>
     </td>
     <td>
      <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-relate', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="current" stringRepresentation="Plain" />' } )" title="Relate to a new neighbor"><img src="${CONTEXT}/s/icons/add.png" alt="Relate to neighbor"/></a></div>
      <ul class="neighbors">
       <mesh:neighborIterate meshObjectName="current" neighborLoopVar="neighbor">
        <li>
         <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-blessRole', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="current" stringRepresentation="Plain" />', 'mesh.object' : '<mesh:meshObjectId meshObjectName="neighbor" stringRepresentation="Plain" />' } )" title="Add a role"><img src="${CONTEXT}/s/icons/add.png" alt="Add role"/></a></div>
         <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-unrelate', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="current" stringRepresentation="Plain" />', 'mesh.object' : '<mesh:meshObjectId meshObjectName="neighbor" stringRepresentation="Plain" />' } )" title="Remove a neighbor"><img src="${CONTEXT}/s/icons/trash.png" alt="Delete neighbor"/></a></div>
         <p><mesh:meshObjectLink meshObjectName="neighbor"><mesh:meshObjectId meshObjectName="neighbor" stringRepresentation="Html" maxLength="30" /></mesh:meshObjectLink></p>
         <ul class="neighborRoleTypes">
          <mesh:roleIterate startMeshObjectName="current" destinationMeshObjectName="neighbor" roleTypeLoopVar="roleType">
           <li>
            <div class="slide-in-button"><a href="javascript:overlay_show( 'org-infogrid-jee-shell-http-HttpShellVerb-unblessRole', { 'mesh.subject' : '<mesh:meshObjectId meshObjectName="current" stringRepresentation="Plain" />', 'mesh.object' : '<mesh:meshObjectId meshObjectName="neighbor" stringRepresentation="Plain" />', 'mesh.roletype' : '<mesh:meshTypeId meshTypeName="roleType" stringRepresentation="Plain" />' } )" title="Remove a role"><img src="${CONTEXT}/s/icons/trash.png" alt="Delete role"/></a></div>
            <p><mesh:type meshTypeName="roleType" filter="false"/></p>
           </li>
          </mesh:roleIterate>
         </ul>
        </li>
       </mesh:neighborIterate>       
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
       <tr>
        <td class="label">Last&nbsp;read:</td><td><mesh:timeRead meshObjectName="current" /></td>
       </tr>
      </table>
     </td>
    </u:rotatingTr>
   </c:forEach>
  </tbody>
 </table>
 <%@ include file="/v/org/infogrid/jee/shell/http/HttpShellVerb.jsp" %>
</v:viewlet>
