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
  <tmpl:ifNotEmpty sectionName="html-app-header">
   <div id="canvas-top">
    <div id="canvas-app-row">
     <div class="canvas-main">
      <tmpl:inline sectionName="html-app-header"/>
     </div>
    </div>
   </div>
  </tmpl:ifNotEmpty>
  <div id="canvas-middle">
   <div class="canvas-main">
    <noscript>
     <div class="errors">
      <h2>Errors:</h2>
      <p>This site requires Javascript. Please enable Javascript before attempting to proceed.</p>
     </div>
    </noscript>
    <tmpl:ifErrors>
     <div class="errors">
      <h2>Errors:</h2>
      <tmpl:inlineErrors stringRepresentation="Html"/>
     </div>
    </tmpl:ifErrors>
    <mesh:refresh>Reload page</mesh:refresh>
    <tmpl:inline sectionName="default"/>
   </div>
  </div>
  <tmpl:ifNotEmpty sectionName="html-app-footer">
   <div id="canvas-bottom">
    <div class="canvas-main footnote">
     <tmpl:inline sectionName="html-app-footer"/>
    </div>
   </div>
  </tmpl:ifNotEmpty>
 </body>
</html>
