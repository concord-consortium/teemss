<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="content.xsl"/>
<xsl:output method="html" indent="yes"/>

<xsl:template match="/">
<xsl:apply-templates select="project/unit/investigation/teacher-notes/teacher-background"/>
</xsl:template>

<xsl:template match="/project/unit/investigation/teacher-notes/teacher-background">
Writing: <xsl:value-of select="ancestor::unit/@name"/>_<xsl:value-of select="ancestor::investigation/@name"/>
<redirect:write file="html/{ancestor::unit/@name}_{ancestor::investigation/@name}_teacher_background.html">
<html>
<head><title>Teacher Notes</title></head>
<body>
<h2>Additional Teacher Background</h2>
<xsl:apply-templates/>
</body>
</html>
</redirect:write>
</xsl:template>


</xsl:stylesheet>
