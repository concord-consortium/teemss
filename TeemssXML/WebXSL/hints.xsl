<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:template match="investigation">
<xsl:apply-templates select="hints"/>
</xsl:template>

<xsl:template match="hints">
<redirect:write file="html/{../../@name}/{../@name}_hints.html">
<html>
<head><title>TEEMSS: <xsl:value-of select="../title"/> 
Technical Hints</title>
</head>
<body>
<h3><xsl:value-of select="../title"/> Technical Hints</h3>
<xsl:apply-templates select="hint"/>
<xsl:call-template name="navigation">
<xsl:with-param name="back-link">_trial_<xsl:number value="count(preceding-sibling::trial)"/></xsl:with-param>
<xsl:with-param name="next-link">_analysis</xsl:with-param>
</xsl:call-template>
</body></html>
</redirect:write>
</xsl:template>

<xsl:template match="hint">
<h3><xsl:value-of select="title"/></h3>
<h4><xsl:value-of select="heading"/></h4>
<xsl:apply-templates select="body"/>
</xsl:template>

</xsl:stylesheet>
