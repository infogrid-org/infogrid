<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect" extension-element-prefixes="redirect">
 <xsl:output method="html" indent="yes"/>

 <xsl:template match="/">
  <html>
   <head>
    <title>Module Overview for Module <xsl:apply-templates select="model/subjectarea/name"/></title>
   </head>
   <body>
    <xsl:choose>
     <xsl:when test='model/subjectarea/userdescription'>
      <p>
       <xsl:apply-templates select="model/subjectarea/userdescription"/>
      </p>
     </xsl:when>
    </xsl:choose>
   </body>
  </html>
 </xsl:template>

</xsl:stylesheet>
