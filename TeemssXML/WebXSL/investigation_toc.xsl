<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">
<xsl:template match="/project/unit/investigation">
<redirect:write file="html/{../@name}_{@name}.html">

<html>
<head><title>Teemss website</title></head>
<body>
INVESTIGATION CONTENTS:<br/>
<xsl:value-of select="title"/>
<ul>
<li><a href="{../@name}_{@name}_intro.html">Introduction</a></li>
<li><a href="{../@name}_{@name}_think.html">Thinking About the Question</a></li>
<li><a href="{../@name}_{@name}_materials.html">Materials</a></li>
<li><a href="{../@name}_{@name}_safety.html">Safety</a></li>
<xsl:for-each select="trial">
<li><a href="{../../@name}_{../@name}_trial_{position()}.html">Trial <xsl:number value="position()"
format="I"/>: <xsl:value-of select="@title"/></a></li>
</xsl:for-each>
<li><a href="{../@name}_{@name}_hints.html">Technical Hints</a></li>
<li><a href="{../@name}_{@name}_analysis.html">Analysis</a></li>
<li><a href="{../@name}_{@name}_further.html">Further Investigations</a></li>
</ul>
</body>
</html>
</redirect:write>
</xsl:template>

</xsl:stylesheet>
