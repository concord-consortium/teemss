<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
<html>
<head><title>Teemss website</title></head>
<body>
<ul TYPE="DISC">
<xsl:apply-templates mode="project_toc"/>
</ul>
<xsl:apply-templates mode="unit_toc"/>
</body>
</html>
</xsl:template>

<xsl:template match="unit" mode="project_toc">
<p><li><a href="#{@name}"><xsl:value-of select="title"/> Instructional Unit</a></li></p>
</xsl:template>

<xsl:template  match="unit" mode="unit_toc">
<hr/>
<a name="{@name}">
<xsl:value-of select="title"/></a>
<xsl:apply-templates select="investigation" mode="unit_toc"/>
<hr/>
<xsl:apply-templates select="investigation" mode="investigation_toc"/>
</xsl:template>

<xsl:template match="investigation" mode="unit_toc">
<dl>
<dt><font FACE="VERDANA, ARIAL, HELVETICA" SIZE="2"><b></b><xsl:value-of select="title"/></font></dt>
<dd><strong><xsl:value-of select="summary"/></strong>
<ul type="disc">
<xsl:for-each select="trial">
<li>Trial <xsl:number value="position()"
format="I"/>: <xsl:value-of select="title"/></li>
</xsl:for-each>
</ul></dd>
<dd><a href="#{../@name}_{@name}">Go to Investigation Contents</a></dd>
</dl>
</xsl:template>

<xsl:template match="investigation" mode="investigation_toc">
<a name="{../@name}_{@name}"/>
INVESTIGATION CONTENTS:<br/>
<xsl:value-of select="title"/>
<ul>
<li><a href="#{../@name}_{@name}_intro">Introduction</a></li>
<li>Thinking About the Question</li>
<li>Materials</li>
<li>Saftey</li>
<xsl:for-each select="trial">
<li>Trial <xsl:number value="position()"
format="I"/>: <xsl:value-of select="title"/></li>
</xsl:for-each>
<li>Technical Hints</li>
<li>Analysis</li>
<li>Further Investigations</li>
</ul>
<hr/>
<xsl:apply-templates select="intro" mode="investigation_content"/>
<hr/>
</xsl:template>

<xsl:template match="intro" mode="investigation_content">
<a name="{../../@name}_{../@name}_intro"/>
<xsl:value-of select="../title"/> Introduction<br/>
Discovery Question:<br/>
<xsl:value-of select="../question"/><br/>
<xsl:apply-templates/>
</xsl:template>

</xsl:stylesheet>

