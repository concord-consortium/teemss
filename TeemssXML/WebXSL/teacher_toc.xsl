<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>
<xsl:output method="html" indent="yes"/>

<xsl:template match="/project">
Found project
<redirect:write file="html/teacher_notes.html">
<html>
<head><title>Teacher Notes</title></head>
<body>
<xsl:apply-templates select="unit"/>
</body>
</html>
</redirect:write>
</xsl:template>

<xsl:template match="unit">
<h2><xsl:value-of select="title"/></h2>
<xsl:apply-templates select="investigation/teacher-notes"/>
</xsl:template>

<xsl:template match="teacher-notes">
<h3><xsl:value-of select="../title"/> Teacher Notes</h3>
<ul>
<li><a href="{../../@name}/{../@name}_teacher_intro.html">Introduction</a></li>
<li><a href="{ancestor::unit/@name}/{ancestor::investigation/@name}_teacher_discuss.html">
Discussion Guide</a></li>
<li><a href="{ancestor::unit/@name}/{ancestor::investigation/@name}_teacher_background.html">
Additional Teacher Background</a></li>
<li><a href="{ancestor::unit/@name}/{ancestor::investigation/@name}_teacher_timeline.html">
Suggested Timeline</a></li>
</ul>
</xsl:template>

</xsl:stylesheet>


