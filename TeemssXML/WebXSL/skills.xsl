<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<xsl:apply-templates select="project/skill-list"/></xsl:template>

<xsl:template match="/project/skill-list">
<redirect:write file="skills.html">

<html>
<head><title>Skills</title></head>
<body>
<h2>Skills</h2>
<xsl:apply-templates select="skill">
<xsl:sort select="@name"/>
</xsl:apply-templates>
</body>
</html>
</redirect:write>
</xsl:template>

<xsl:template match="skill">
<b><xsl:value-of select="@name"/></b><br/>
<xsl:apply-templates/><br/><br/>
</xsl:template>

</xsl:stylesheet>
