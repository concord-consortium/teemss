<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:template match="investigation">
<xsl:apply-templates select="safety"/>
</xsl:template>

<xsl:template match="safety">
<redirect:write file="html/{../../@name}_{../@name}_safety.html">
<html>
<head><title>TEEMSS: <xsl:value-of select="../title"/> 
Safety</title>
</head>
<body>
<h3><xsl:value-of select="../title"/> Safety</h3>
<xsl:call-template name="items"/>
<xsl:call-template name="navigation">
<xsl:with-param name="back-link">_materials</xsl:with-param>
<xsl:with-param name="next-link">_trial_1</xsl:with-param>
</xsl:call-template>
</body></html>
</redirect:write>
</xsl:template>

</xsl:stylesheet>
