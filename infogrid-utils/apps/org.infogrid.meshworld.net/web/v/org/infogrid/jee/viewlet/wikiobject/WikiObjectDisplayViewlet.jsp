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
 <div class="content">
  ${Viewlet.content}
 </div>
</v:viewlet>