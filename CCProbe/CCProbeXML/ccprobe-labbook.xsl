<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:output method="xml" indent="yes"
  doctype-public="-//Concord.ORG//DTD LabBook Description//EN" 
  doctype-system="../../XML2LabBook/labbook.dtd"/>

<xsl:strip-space elements="*"/>

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="labbook">
  <LABBOOK>
    <xsl:copy-of select="document('ccprobe.xml')"/>
  </LABBOOK>
</xsl:template>

</xsl:stylesheet>
