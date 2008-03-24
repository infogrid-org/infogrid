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
 <h1>Wiki Editor Viewlet for: <mesh:meshObjectId meshObjectName="Subject" stringRepresentation="Html" maxLength="30"/></h1>
 <form action="" method="post">

  <c:if test="${mode eq 'edit'}">
   <div class="mode"><p>Edit mode (not saved yet)</p></div>
   <textarea class="current-content" name="current-content">${Viewlet.currentContent}</textarea>
   <table class="dialog-buttons">
    <tr>
     <td><button type="submit" name="action" value="cancel">Cancel edits</button></td>
     <td><button type="submit" name="action" value="preview">Preview</button></td>
     <td><button type="submit" name="action" value="publish">Publish</button></td>
    </tr>
   </table>
  </c:if>
  <c:if test="${mode eq 'preview'}">
   <div class="mode"><p>Preview mode (not saved yet)</p></div>
   <div class="content">${Viewlet.currentContent}</div>
   <textarea class="current-content" name="current-content">${Viewlet.currentContent}</textarea>
   <table class="dialog-buttons">
    <tr>
     <td><button type="submit" name="action" value="cancel">Cancel edits</button></td>
     <td><button type="submit" name="action" value="publish">Publish</button></td>
     <td><button type="submit" name="action" value="edit">Edit</button></td>
    </tr>
   </table>
  </c:if>
  <c:if test="${mode eq 'view'}">
   <div class="mode"><p>View mode</p></div>
   <div class="content">
    ${Viewlet.currentContent}
   </div>
   <table class="dialog-buttons">
    <tr>
     <td><button type="submit" name="action" value="edit">Edit</button></td>
    </tr>
   </table>
  </c:if>
  
 </form>  
</v:viewlet>
