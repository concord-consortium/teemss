<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

<xsl:import href="teemss-header.xsl"/>

<xsl:output method="html" indent="yes"/>

<xsl:template match="project">
<xsl:text>outputing: </xsl:text>
<xsl:value-of select="@name"/>
<redirect:write file="html/{@name}.html">

<html>
<head><title><xsl:value-of select="title"/>: INVESTIGATIONS</title>
<xsl:call-template name="teemss-header-script"/>
</head>
<body BACKGROUND="images/grid.gif" VLINK="#000000">
<xsl:call-template name="teemss-header"/>
<table CELLPADDING="0" CELLSPACING="2" BORDER="0" WIDTH="550">

<tr>

<td ALIGN="LEFT" VALIGN="TOP">
<menu>
<h3>Investigations:</h3>

<font FACE="VERDANA, ARIAL, HELVETICA" SIZE="2">

<b><font COLOR="#006600">If this is your first time here, be sure to <a HREF="help.htm">visit the help section</a>!</font></b>
<p>
<b>To start your TEEMSS Investigations choose one of the following: </b>
</p>
<ul TYPE="DISC">
<xsl:for-each select="unit">
<p><li><font FACE="VERDANA, ARIAL, HELVETICA" SIZE="2"><a href="{@name}.html"><xsl:value-of select="title"/> Instructional Unit</a></font></li></p>
</xsl:for-each>

</ul>

<p/>

</font></menu>
</td></tr>

<tr><td>
<hr NOSHADE="" SIZE="1"/>
<font SIZE="2">
Copyright &#169; 2001 <a HREF="http://www.concord.org" TARGET="NEW">The Concord Consortium</a>, All
rights reserved.<br/>
</font></td>
</tr>

</table>

</body>
</html>

</redirect:write>
</xsl:template>

</xsl:stylesheet>
