<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<xsl:apply-templates select="project/unit/investigation/teacher-notes/teacher-timeline"/>
</xsl:template>

<xsl:template match="/project/unit/investigation/teacher-notes/teacher-timeline">
Writing: <xsl:value-of select="ancestor::unit/@name"/>_<xsl:value-of select="ancestor::investigation/@name"/>
<redirect:write file="html/{ancestor::unit/@name}_{ancestor::investigation/@name}_teacher_timeline.html">
<html>
<head><title>Teacher Notes</title></head>
<body>
<h2>Suggested Timeline</h2>
The amount of time you spend on introductory discussions, data collection, and analysis, will determine your overall timeline. The following represents a possible timeline.
<ul>
<xsl:apply-templates/>
</ul>
Additional days can be used for Further Investigations.
</body>
</html>
</redirect:write>
</xsl:template>

<xsl:template match="time-period[@type='setup']">
<li><xsl:call-template name="num-periods-text">
<xsl:with-param name="number"><xsl:value-of select="@periods"/>
</xsl:with-param></xsl:call-template>
 - "Setting Up" discussion</li>
</xsl:template>

<xsl:template match="time-period[@type='wrap']">
<li><xsl:call-template name="num-periods-text">
<xsl:with-param name="number"><xsl:value-of select="@periods"/>
</xsl:with-param></xsl:call-template>
 - Analysis and "Wrap Up" discussion</li>
</xsl:template>

<xsl:template match="time-period[@type='trial']">
<li><xsl:call-template name="num-periods-text">
<xsl:with-param name="number"><xsl:value-of select="@periods"/>
</xsl:with-param></xsl:call-template>
 - Trial <xsl:number value="@number" format="I"/>: 
<xsl:variable name="trialNumber"><xsl:value-of select="@number"/></xsl:variable>
<xsl:value-of select="ancestor::investigation/trial[position()=$trialNumber]/@title"/>
 </li>
</xsl:template>

<xsl:template name="num-periods-text">
<xsl:param name="number">1</xsl:param>
<xsl:choose>
<xsl:when test="$number=0.5">
One half class period
</xsl:when>
<xsl:when test="$number=1">
One class period
</xsl:when>
<xsl:when test="$number=1.5">
One and half class periods
</xsl:when>
</xsl:choose>
</xsl:template>

</xsl:stylesheet>
