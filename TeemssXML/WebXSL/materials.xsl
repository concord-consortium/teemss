<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:template match="investigation">
<xsl:apply-templates select="materials"/>
</xsl:template>

<xsl:template match="materials">
<redirect:write file="html/{../../@name}_{../@name}_materials.html">
<html>
<head><title>TEEMSS: <xsl:value-of select="../title"/> 
Materials</title>
</head>
<body>
<h3><xsl:value-of select="../title"/> Materials</h3>
<xsl:apply-templates select="ext_image"/>
<xsl:call-template name="items"/>
<xsl:call-template name="navigation">
<xsl:with-param name="back-link">_think</xsl:with-param>
<xsl:with-param name="next-link">_safety</xsl:with-param>
</xsl:call-template>
</body></html>
</redirect:write>
</xsl:template>


</xsl:stylesheet>
