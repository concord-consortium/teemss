<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<xsl:apply-templates select="project/tech-hint-list/tech-hint"/>
</xsl:template>

<xsl:template match="tech-hint">
<redirect:write file="html/tech_hint_{@name}.html">
<h3><xsl:value-of select="title"/></h3>
<h4><xsl:value-of select="heading"/>:</h4>
<xsl:apply-templates select="tech-hint-body"/>
</redirect:write>
</xsl:template>

</xsl:stylesheet>
