<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<xsl:apply-templates select="project/unit/investigation/teacher-notes/teacher-discuss"/>
</xsl:template>

<xsl:template match="/project/unit/investigation/teacher-notes/teacher-discuss">
Writing: <xsl:value-of select="ancestor::unit/@name"/>_<xsl:value-of select="ancestor::investigation/@name"/>
<redirect:write file="html/{ancestor::unit/@name}/{ancestor::investigation/@name}_teacher_discuss.html">

<html>
<head><title>Teacher Notes</title></head>
<body>
<h2>Discussion Guide</h2>
The Discussion Guide is divided into three sections. Select from the following:
<ul>
<li>Using this Guide</li>
<li>Overview</li>
<li>Setting the Stage</li>
<li>Wrap Up</li>
</ul>
<h3>Using this Guide</h3>
This guide is designed to help you convert the investigations your students experience into solid learning. The "Overview" section mentions some of the learning issues raised by this content. These issues might come up in conversations with students anytime. The "Setting the Stage" section provides ideas for a discussion you might hold before beginning the investigations. This discussion is important to motivate and alert students to observations that might answer their questions. The "Wrap Up" section can be used after the investigations to help student reflect on what they have done. Taking time to reflect while the investigations are fresh in students' minds has been shown to substantially increase learning.
<xsl:apply-templates/>
</body>
</html>
</redirect:write>
</xsl:template>

<xsl:template match="teacher-overview">
<h3>Overview</h3>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="teacher-stage">
<h3>Setting the Stage</h3>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="teacher-wrap">
<h3>Wrap Up</h3>
<xsl:apply-templates/>
</xsl:template>

<xsl:template match="teacher-questions">
<ul>
<xsl:apply-templates select="question"/>
</ul>
</xsl:template>

<xsl:template match="question">
<li>
"<xsl:apply-templates/>"
<xsl:apply-templates select="following-sibling::*[position()=1 and self::answer]"/>
</li>
</xsl:template>

<xsl:template match="answer">
[<xsl:apply-templates/>]
</xsl:template> 

</xsl:stylesheet>
