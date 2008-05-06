<%@    taglib prefix="set"         uri="/v/org/infogrid/jee/taglib/mesh/set/set.tld"
 %><%@ taglib prefix="mesh"        uri="/v/org/infogrid/jee/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="netmesh"     uri="/v/org/infogrid/jee/taglib/mesh/net/netmesh.tld"
 %><%@ taglib prefix="meshbase"    uri="/v/org/infogrid/jee/taglib/meshbase/meshbase.tld"
 %><%@ taglib prefix="netmeshbase" uri="/v/org/infogrid/jee/taglib/meshbase/net/netmeshbase.tld"
 %><%@ taglib prefix="candy"       uri="/v/org/infogrid/jee/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"           uri="/v/org/infogrid/jee/taglib/util/util.tld"
 %><%@ taglib prefix="v"           uri="/v/org/infogrid/jee/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="c"           uri="http://java.sun.com/jsp/jstl/core"
 %>
<u:refresh>Reload page</u:refresh>
<v:viewlet>
 <h1><netmeshbase:proxyId proxyName="Subject"/></h1>
 <table>
  <thead>
   <tr>
    <th>Attribute</th>
    <th>Value</th>
   </tr>
  </thead>
  <tbody>
   <tr>
    <td>Local NetMeshBase</td>
    <td><meshbase:meshBaseId meshBaseName="Subject.NetMeshBase"/></td>
   </tr>
   <tr>
    <td>Partner NetMeshBase</td>
    <td><u:toExternalForm objectName="Subject.PartnerMeshBaseIdentifier" filter="true"/></td>
   </tr>
   <tr>
    <td>Coherence Specification</td>
    <td>${Subject.coherenceSpecification}</td>
   </tr>
   <tr>
    <td>Time created</td>
    <td><u:timeStamp objectName="Subject.timeCreated"/></td>
   </tr>
   <tr>
    <td>Time updated</td>
    <td><u:timeStamp objectName="Subject.timeUpdated"/></td>
   </tr>
   <tr>
    <td>Time read</td>
    <td><u:timeStamp objectName="Subject.timeRead"/></td>
   </tr>
   <tr>
    <td>Time expires</td>
    <td><u:timeStamp objectName="Subject.timeExpires"/></td>
   </tr>
   <tr>
    <td>Token last sent</td>
    <td><c:out value="${Subject.messageEndpoint.lastSentToken}"/></td>
   </tr>
   <tr>
    <td>Token last received</td>
    <td><c:out value="${Subject.messageEndpoint.lastReceivedToken}"/></td>
   </tr>
  </tbody>
 </table>     
</v:viewlet>
