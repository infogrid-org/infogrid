<%@    page contentType="text/json"
 %><%@ taglib prefix="mesh"      uri="/v/org/infogrid/jee/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="objectset" uri="/v/org/infogrid/jee/taglib/mesh/set/objectset.tld"
 %><%@ taglib prefix="candy"     uri="/v/org/infogrid/jee/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"         uri="/v/org/infogrid/jee/taglib/util/util.tld"
 %><%@ taglib prefix="v"         uri="/v/org/infogrid/jee/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="tmpl"      uri="/v/org/infogrid/jee/taglib/viewlet/templates/templates.tld"
 %><%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core"
 %>
[
<objectset:iterate meshObjectSetName="Viewlet.Objects" meshObjectLoopVar="current">
  {
    "Identifier" : <mesh:meshObjectId meshObjectName="current" stringRepresentation="Java"/>,
    "Types"      : [
  <mesh:blessedByIterate meshObjectName="current" blessedByLoopVar="blessedBy">
      <mesh:meshTypeId meshTypeName="blessedBy" stringRepresentation="Java" />,
  </mesh:blessedByIterate>
                   ]
  <mesh:blessedByIterate meshObjectName="current" blessedByLoopVar="blessedBy">
    <mesh:propertyIterate meshObjectName="current" meshTypeName="blessedBy" propertyTypeLoopVar="propertyType" propertyValueLoopVar="propertyValue" skipNullProperty="false">
       , <mesh:meshTypeId meshTypeName="propertyType" stringRepresentation="Java" />
       : <mesh:propertyValue propertyValueName="propertyValue" stringRepresentation="Java" ignore="true" nullString="null"/>
    </mesh:propertyIterate>
  </mesh:blessedByIterate>
  }<u:ifIterationHasNext>,</u:ifIterationHasNext>
</objectset:iterate>
]