<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:template match="investigation">
<xsl:apply-templates select="analysis"/>
</xsl:template>

<xsl:template match="analysis">
<redirect:write file="html/{../../@name}/{../@name}_analysis.html">
<html>
<head><title>TEEMSS: <xsl:value-of select="../title"/> 
Analysis</title>
</head>
<body>
<h3><xsl:value-of select="../title"/> Analysis</h3>
<p>
<xsl:if test="child::data">
Open the data from Trials 
<xsl:for-each select="data">
<xsl:number value="@trial" format="I"/>
<xsl:choose>
<xsl:when test="position() = last()">. </xsl:when>
<xsl:when test="position() = (last()- 1)"> and </xsl:when>
<xsl:otherwise>,  </xsl:otherwise>
</xsl:choose>
</xsl:for-each>
<br/>
</xsl:if>
Please answer the following questions in Notes on your handheld computer:</p>
<xsl:apply-templates select="steps"/>
<xsl:call-template name="navigation">
<xsl:with-param name="back-link">_hints</xsl:with-param>
<xsl:with-param name="next-link">_further</xsl:with-param>
</xsl:call-template>
</body></html>
</redirect:write>
</xsl:template>

</xsl:stylesheet>
