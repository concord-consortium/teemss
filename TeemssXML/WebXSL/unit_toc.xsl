<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">
<xsl:template match="/project/unit">
<redirect:write file="html/{@name}.html">
<html>
<head><title>Teemss website</title></head>
<body>
<xsl:value-of select="title"/>
<hr/>
<xsl:apply-templates select="investigation"/>
</body>
</html>
</redirect:write>
</xsl:template>

<xsl:template match="investigation">
<dl>
<dt><font FACE="VERDANA, ARIAL, HELVETICA" SIZE="2"><b></b><xsl:value-of select="title"/></font></dt>
<dd><strong><xsl:value-of select="summary"/></strong>
<ul type="disc">
<xsl:for-each select="trial">
<li>Trial <xsl:number value="position()"
format="I"/>: <xsl:value-of select="@title"/></li>
</xsl:for-each>
</ul></dd>
<dd><a href="{../@name}_{@name}.html">Go to Investigation Contents</a></dd>
</dl>
<hr/>
</xsl:template>
</xsl:stylesheet>
