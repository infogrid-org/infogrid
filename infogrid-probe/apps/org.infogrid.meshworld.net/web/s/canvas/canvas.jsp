<?xml version="1.0"?>
<%@
   page contentType="application/xhtml+xml"%><%@
   page pageEncoding="UTF-8"%><%@
   taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core'%><%@
   taglib prefix="u"uri="/v/org/infogrid/jee/taglib/util/util.tld"%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
 <head>
  <title>The NetMesh World</title>
  <link rel="stylesheet" href="${CONTEXT}/s/app/app.css" type="text/css" />
  <script src="${CONTEXT}/s/app/scripts.js" type="text/javascript"></script>
  <script type="text/javascript">
      includeAll( '${CONTEXT}' );
  </script>
 </head>
 <body>
<%@ include file="canvas-top.jsp" %>
  <div id="canvas-middle">
   <div class="canvas-main">
@ERRORS@
    <u:namedServletInclude servletName="ViewletDispatcher" />
   </div>
  </div>
<%@ include file="canvas-bottom.jsp" %>
 </body>
</html>
