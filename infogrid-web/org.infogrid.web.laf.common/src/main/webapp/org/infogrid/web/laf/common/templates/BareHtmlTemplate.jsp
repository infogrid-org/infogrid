<%@    page contentType="text/html"
 %><%@ page pageEncoding="UTF-8"
 %><%@ taglib prefix="set"   uri="/org/infogrid/web/taglib/mesh/set/set.tld"
 %><%@ taglib prefix="mesh"  uri="/org/infogrid/web/taglib/mesh/mesh.tld"
 %><%@ taglib prefix="candy" uri="/org/infogrid/web/taglib/candy/candy.tld"
 %><%@ taglib prefix="u"     uri="/org/infogrid/web/taglib/util/util.tld"
 %><%@ taglib prefix="v"     uri="/org/infogrid/web/taglib/viewlet/viewlet.tld"
 %><%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core"
 %><%@ taglib prefix="tmpl"  uri="/org/infogrid/web/taglib/templates/templates.tld"
 %><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
 <head>
  <tmpl:inline sectionName="html-title"/>
  <link rel="stylesheet" href="${CONTEXT}/s/org/infogrid/web/laf/common/assets/master.css" type="text/css" />
  <link rel="stylesheet" href="${CONTEXT}/s/org/infogrid/web/laf/common/assets/layout.css" type="text/css" />
  <link rel="stylesheet" href="${CONTEXT}/s/org/infogrid/web/laf/common/assets/color.css"  type="text/css" />
  <link rel="stylesheet" href="${CONTEXT}/s/org/infogrid/web/taglib/mesh/RefreshTag.css" type="text/css" />
  <link rel="stylesheet" href="${CONTEXT}/s/org/infogrid/web/taglib/candy/OverlayTag.css" type="text/css" />
  <script src="${CONTEXT}/s/org/infogrid/web/taglib/candy/OverlayTag.js" type="text/javascript"></script>
  <tmpl:inline sectionName="html-head"/>
 </head>
 <body>
  <div id="canvas-middle">
   <div class="canvas-main">
    <tmpl:ifErrors>
     <div class="errors">
      <h2>Errors:</h2>
      <tmpl:inlineErrors stringRepresentation="Html"/>
     </div>
    </tmpl:ifErrors>
    <tmpl:inline sectionName="default"/>
   </div>
  </div>
 </body>
</html>
