<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<xsl:apply-templates select="project/unit/investigation/teacher-notes/teacher-intro"/>
</xsl:template>

<xsl:template match="project/unit/investigation/teacher-notes/teacher-intro">
Found intro
<redirect:write file="html/{ancestor::unit/@name}/{ancestor::investigation/@name}_teacher_intro.html">

<html>
<head><title>Teacher Notes</title></head>
<body>
<h2>Introduction</h2>
<xsl:value-of select="summary"/>
<xsl:apply-templates select="skills"/>
</body>
</html>
</redirect:write>
</xsl:template>

<xsl:template match="skills">
<p>
In addition, students will gain experience with inquiry skills, including:
<ul>
<xsl:apply-templates/>
</ul>
</p>
</xsl:template>

<xsl:key name="skill-def" match="skill" use="@name"/>

<xsl:template match="skill-ref">
<li>
<xsl:value-of select="key('skill-def', @ref)"/>
</li>
</xsl:template>

</xsl:stylesheet>
