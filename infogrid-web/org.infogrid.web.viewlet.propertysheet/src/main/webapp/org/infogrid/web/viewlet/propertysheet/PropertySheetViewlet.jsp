<%@    page contentType="text/html"
 %><%@ taglib prefix="mesh"  uri="/org/infogrid/web/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="candy" uri="/org/infogrid/web/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"     uri="/org/infogrid/web/taglib/util/util.tld"
 %><%@ taglib prefix="v"     uri="/org/infogrid/web/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="tmpl"  uri="/org/infogrid/web/taglib/templates/templates.tld"
 %><%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"
 %>
<tmpl:stylesheet href="${CONTEXT}/v/org/infogrid/web/viewlet/propertysheet/PropertySheetViewlet.css"/>
<tmpl:stylesheet href="${CONTEXT}/s/org/infogrid/web/taglib/viewlet/ChangeViewletStateTag.css"/>
<tmpl:stylesheet href="${CONTEXT}/s/org/infogrid/web/taglib/mesh/PropertyTag.css"/>
<tmpl:script src="${CONTEXT}/s/org/infogrid/web/taglib/mesh/PropertyTag.js"/>

<v:viewletAlternatives />
<v:changeViewletState viewletStates="edit" display="compact"/>
<v:viewlet formId="viewlet">
 <table class="audit"> <!-- IE is unable to render float:right correctly, so here is a table for you -->
  <tr>
   <td class="title">
    <v:notIfState viewletState="edit">
     <div class="slide-in-button">
      <u:callJspo name="delete" action="${Viewlet.postUrl}" linkTitle="Delete this MeshObject" submitLabel="Delete">
       <u:callJspoParam name="toDelete" value="${Subject}"/>
       <span class="org-infogrid-web-viewlet-propertysheet-PropertySheetViewlet-delete-button"><span>Delete</span></span>
      </u:callJspo>
     </div>
    </v:notIfState>
    <h1>Property Sheet for: <mesh:meshObjectId meshObjectName="Subject" stringRepresentation="Html" maxLength="30"/></h1>
   </td>
   <td class="audit">
 <%@ include file="PropertySheetViewlet/audit.jspi" %>
   </td>
  </tr>
 </table>

<%@ include file="PropertySheetViewlet/attributes.jspi" %>
<%@ include file="PropertySheetViewlet/neighbors.jspi" %>

 <v:ifState viewletState="edit">
  <div class="dialog-buttons">
   <u:safeFormHiddenInput/>
   <input id="shell.submit" type="hidden" name="shell.submit" value="" />
   <table class="dialog-buttons">
    <tr>
     <td><button type="button" name="ViewletStateTransition" value="do-cancel" class="cancel" onclick="document.getElementById( 'shell.submit' ).value='cancel'; document.getElementById('viewlet').submit()">Discard</button></td>
     <td><button type="button" name="ViewletStateTransition" value="do-commit" class="commit" onclick="document.getElementById( 'shell.submit' ).value='commit'; document.getElementById('viewlet').submit()">Save</button></td>
    </tr>
   </table>
  </div>
 </v:ifState>
</v:viewlet>
