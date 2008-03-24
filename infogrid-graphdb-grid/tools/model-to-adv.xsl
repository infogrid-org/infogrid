<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" indent="yes"/>
<xsl:template match="model">
<modelmodule>
  <xsl:apply-templates select="subjectarea"/>
</modelmodule>
</xsl:template>
<xsl:template match="subjectarea">
  <name>
<xsl:value-of select='name'/>
  </name>
  <version>
<xsl:value-of select='version'/>
  </version>
  <username>
<xsl:value-of select='username'/>
  </username>
  <provides>
    <jar>
<xsl:value-of select='name'/><xsl:text>.jar</xsl:text>
<!-- <xsl:value-of select='name'/>.V<xsl:value-of select='version'/><xsl:text>.jar</xsl:text> -->
    </jar>
  </provides>
  <dependencies>
    <requires name="org.infogrid.kernel"/>
<xsl:apply-templates select="*/subjectareareference"/>
  </dependencies>
</xsl:template>
<xsl:template match="subjectareareference">
  <requires>
    <xsl:attribute name="name">
<xsl:value-of select='name'/>
    </xsl:attribute>
    <xsl:attribute name="version">
<xsl:value-of select='minversion'/>
    </xsl:attribute>
  </requires>
</xsl:template>
</xsl:stylesheet>
