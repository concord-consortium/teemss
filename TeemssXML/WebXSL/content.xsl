<?xml version="1.0"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    version="1.0"   
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:cc-ext="ext1"
    extension-element-prefixes="cc-ext">
<lxslt:component prefix="cc-ext" elements="copy" functions="getImageDir">
	<lxslt:script lang="javascript">
	function copy(xslPC, elem)
	{	
		try{
		source = new java.io.FileInputStream(
			elem.getAttribute("source", xslPC.getContextNode(),xslPC.getTransformer()));
		target = new java.io.FileOutputStream(
			elem.getAttribute("target", xslPC.getContextNode(),xslPC.getTransformer()));

		while (source.available() &gt; 0) {
			data = source.read();
			target.write(data);
		}
		source.close();
		target.close();
		} catch ( e ) {
			java.lang.System.err.println(e);
		}
		return null;
    }

	function getImageDir(imagesDir, imageName)
	{
		var children = (new java.io.File("images/" + imagesDir)).listFiles();
		if(children == null) {
			java.lang.System.err.println("error: no files in: " + imagesDir);
			return "InvalidImageName";
		}
		var i;
		for (i=0; i &lt; children.length; i++){
			if(children[i].getName().startsWith(imageName)){
				break;
			}
		}
		if(i &lt; children.length){
			return imagesDir + "/" + children[i].getName();
		} else {
			java.lang.System.err.println("error: can't find: " + imageName + " in:");
			java.lang.System.err.println("\t" + imagesDir);
			return "InvalidImageName";
		}
	}	
    </lxslt:script>
</lxslt:component>

<xsl:template match="p">
<p>
<xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="image">
<cc-ext:copy source="{@dir}/{@name}" target="html/images/{@name}"/>
<p><img src="string(images/{@name}.gif)" width="158" height="140"/></p>
</xsl:template>

<xsl:template match="ext_image">
<cc-ext:copy source="images/{cc-ext:getImageDir(concat(string(ancestor::unit/@name),'/',string(ancestor::investigation/@name)),string(@name))}/WEB_{@name}.{@type}" target="html/images/auto_{@name}.{@type}"/>
<p>
<xsl:choose>
	<xsl:when test="@type='gif'">
<img src="images/auto_{@name}.{@type}" width="158" height="140" border="1"/>
	</xsl:when>
	<xsl:when test="@type='jpg'">
<img src="images/auto_{@name}.{@type}"/>
	</xsl:when>	
</xsl:choose>
</p>
</xsl:template>

<xsl:template match="ext_image_sequence">
	<xsl:for-each select="ext_image">
		<xsl:apply-templates select="."/>
		<xsl:if test="position()!=last()"><img src="images/arrow.gif"/></xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="items">
<ul>
<xsl:apply-templates select="item"/>
</ul>
</xsl:template>

<xsl:template match="item">
<li><p>
<xsl:apply-templates/>
</p></li>
</xsl:template>

<xsl:template match="steps">
<ol TYPE="a" START="1">
<xsl:apply-templates select="step"/>
</ol>
</xsl:template>

<xsl:template match="instruction/title"/>

<xsl:template match="step">
<li><p>
<xsl:apply-templates/>
</p></li>
</xsl:template>

<xsl:template name="navigation">
<xsl:param name="back-link"/>
<xsl:param name="next-link"/>
<hr/>
<table>
<tr>
<td><a href="{../../@name}_{../@name}{$back-link}.html">back</a></td>
<td><a href="{../../@name}_{../@name}.html">investigation contents</a></td>
<td><a href="{../../@name}_{../@name}{$next-link}.html">next</a></td>
</tr></table>
</xsl:template>

</xsl:stylesheet>
