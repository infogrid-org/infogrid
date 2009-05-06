<%@    page contentType="text/html"
 %><%@ taglib prefix="mesh"  uri="/v/org/infogrid/jee/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="candy" uri="/v/org/infogrid/jee/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"     uri="/v/org/infogrid/jee/taglib/util/util.tld"
 %><%@ taglib prefix="v"     uri="/v/org/infogrid/jee/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="tmpl"  uri="/v/org/infogrid/jee/taglib/templates/templates.tld"
 %><%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"
 %>
<tmpl:stylesheet href="${CONTEXT}/v/org/infogrid/jee/viewlet/wikiobject/WikiObjectEditViewlet.css"/>
<tmpl:stylesheet href="${CONTEXT}/v/org/infogrid/jee/taglib/viewlet/ChangeViewletStateTag.css"/>
<tmpl:stylesheet href="${CONTEXT}/v/org/infogrid/jee/taglib/mesh/MeshObjectTag.css"/>
<tmpl:script src="${CONTEXT}/v/org/infogrid/jee/taglib/mesh/MeshObjectTag.js"/>
<v:viewletAlternatives />
<v:changeViewletState viewletStates="edit" display="compact"/>
<v:viewlet formId="viewlet">
 <v:ifState viewletState="edit">
  <div class="viewlet-state"><p>Edit mode (not saved yet)</p></div>
 </v:ifState>
 <h1>Wiki Editor Viewlet for: <mesh:meshObjectId meshObjectName="Subject" stringRepresentation="Plain" filter="true" maxLength="30"/></h1>
 <v:ifState viewletState="edit">
  <div class="current-content">
   <mesh:property meshObjectName="Subject" propertyType="org.infogrid.model.Wiki#WikiObject/Content" stringRepresentation="Edit"/>
  </div>
  <table class="dialog-buttons">
   <u:safeFormHiddenInput/>
   <input id="shell.submit" type="hidden" name="shell.submit" value="" />
   <tr>
    <td><button type="button" name="ViewletStateTransition" value="do-cancel"  onclick="document.getElementById( 'shell.submit' ).value='cancel'; document.getElementById('viewlet').submit()">Discard</button></td>
    <td><button type="button" name="ViewletStateTransition" value="do-commit"  onclick="document.getElementById( 'shell.submit' ).value='commit'; document.getElementById('viewlet').submit()">Save</button></td>
   </tr>
  </table>
 </v:ifState>
 <v:ifState viewletState="view">
  <div class="content">
   <mesh:property meshObjectName="Subject" propertyType="org.infogrid.model.Wiki#WikiObject/Content" stringRepresentation="Plain"/>
  </div>
 </v:ifState>
</v:viewlet>
