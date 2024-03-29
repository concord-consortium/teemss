<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<xsl:apply-templates select="project/unit/investigation"/>
</xsl:template>

<xsl:template match="/project/unit/investigation">
<xsl:apply-templates select="trial"/>
</xsl:template>

<xsl:template match="trial">

<xsl:text>outputing: </xsl:text>
<xsl:value-of select="@title"/>
<xsl:text>
</xsl:text>
<redirect:write file="html/{../../@name}/{../@name}_trial_{position()}.html">

<html>
<head><title>TEEMSS: <xsl:value-of select="../title"/> 
Trial <xsl:number value="position()" format="I"/></title>
</head>
<body>
<h2><xsl:value-of select="../title"/> 
Trial <xsl:number value="position()" format="I"/></h2>
<h3><xsl:value-of select="@title"/></h3>
<xsl:apply-templates/>
<xsl:call-template name="navigation">
<xsl:with-param name="back-link"><xsl:choose>
<xsl:when test="position()=1">_safety</xsl:when>
<xsl:otherwise>_trial_<xsl:number value="position()-1"/></xsl:otherwise>
</xsl:choose></xsl:with-param>
<xsl:with-param name="next-link"><xsl:choose>
<xsl:when test="position()=last()">_hints</xsl:when>
<xsl:otherwise>_trial_<xsl:number value="position()+1"/></xsl:otherwise>
</xsl:choose></xsl:with-param>
</xsl:call-template>
</body>
</html>
</redirect:write>
</xsl:template>

</xsl:stylesheet>
