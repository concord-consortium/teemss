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

<xsl:template match="ext-image">
<p>
<xsl:choose>
	<xsl:when test="@type='gif'">
<img src="../../images/{ancestor::unit/@name}/{ancestor::investigation/@name}/{@name}/WEB_{@name}.{@type}"
width="158" height="140" border="1"/>
	</xsl:when>
	<xsl:when test="@type='jpg'">
<img src="../../images/{ancestor::unit/@name}/{ancestor::investigation/@name}/{@name}/WEB_{@name}.{@type}"/>
	</xsl:when>	
</xsl:choose>
</p>
</xsl:template>

<xsl:template match="shared-image">
<p>
<xsl:choose><xsl:when test="ancestor::investigation">
<xsl:choose><xsl:when test="@screenshot = 'true'">
<img src="../../images/Technical_Hints/screenshot/{@name}/WEB_{@name}.gif"  border="1"/>
</xsl:when><xsl:otherwise>
<img src="../../images/Technical_Hints/pictures/{@name}/WEB_{@name}.gif"  border="1"/>
</xsl:otherwise></xsl:choose>
</xsl:when><xsl:otherwise>
<xsl:choose><xsl:when test="@screenshot = 'true'">
<img src="../images/Technical_Hints/screenshot/{@name}/WEB_{@name}.gif"  border="1"/>
</xsl:when><xsl:otherwise>
<img src="../images/Technical_Hints/pictures/{@name}/WEB_{@name}.gif"  border="1"/>
</xsl:otherwise></xsl:choose>
</xsl:otherwise></xsl:choose>
</p>
</xsl:template>

<xsl:template match="ext-image-sequence">
	<xsl:for-each select="ext-image | shared-image">
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

<xsl:template name="level_label">
<xsl:variable name="depth">
<xsl:value-of select="count(ancestor::steps | ancestor::instructions | ancestor::querys)"/>
</xsl:variable>
<xsl:choose>
<xsl:when test="$depth ='0'">1</xsl:when>
<xsl:when test="$depth ='1'">a</xsl:when>
<xsl:when test="$depth ='2'">i</xsl:when>
</xsl:choose>
</xsl:template>

<xsl:template name="outline-parent">
<xsl:variable name="type_label">
<xsl:call-template name="level_label"/>
</xsl:variable>
<ol TYPE="{$type_label}" START="1">
<xsl:for-each select="step | query-response | query">
<li>
<xsl:apply-templates select="."/>
</li>
</xsl:for-each>
</ol>
</xsl:template>

<xsl:template match="instruction/title"/>

<xsl:template match="steps">
<xsl:call-template name="outline-parent"/>
</xsl:template>

<xsl:template match="step">
<p>
<xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="instructions">
<xsl:variable name="instructions_num">
<xsl:number value="position()"/>
</xsl:variable>
<ul>
<xsl:for-each select="instruction">
<li><a href="#instruction_{$instructions_num}_{position()}">
<xsl:value-of select="@title"/></a></li>
</xsl:for-each>
</ul>
<hr/>
<ol TYPE="1" START="1">
<xsl:for-each select="instruction">
<li><a name="instruction_{$instructions_num}_{position()}">
<b><xsl:value-of select="@title"/></b></a>
<xsl:apply-templates/>
</li>
</xsl:for-each>
</ol>
</xsl:template>

<xsl:template match="query-response">
<xsl:variable name="section_id">
<xsl:value-of select="generate-id(ancestor::*[../../investigation])"/>
</xsl:variable>
<h2>number in investigation( 
<xsl:value-of select="generate-id(ancestor::*[../../investigation])"/>,
<xsl:value-of select="$section_id"/>):
<xsl:number level="any" 
count="query-response[generate-id(ancestor::*[../../investigation]) = $section_id]"/>
</h2>

<p>
<xsl:apply-templates/>
</p>
</xsl:template>

<xsl:template match="querys">
<xsl:choose>
<xsl:when test="ancestor::query-response/@layout = 'paragraph'">
<xsl:apply-templates/>
</xsl:when>
<xsl:otherwise>
<xsl:call-template name="outline-parent"/>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="equation">
<ul>
<xsl:apply-templates/>
</ul>
</xsl:template>

<xsl:template match="table">
<table border="1">
<xsl:apply-templates select="th"/>
<xsl:copy-of select="tr"/>
</table>
</xsl:template>

<xsl:template match="th">
<tr>
<xsl:for-each select="td">
<td><b><xsl:apply-templates/></b></td>
</xsl:for-each>
</tr>
</xsl:template>

<xsl:template match="ul">
<xsl:copy-of select="."/>
</xsl:template>

<xsl:template name="navigation">
<xsl:param name="back-link"/>
<xsl:param name="next-link"/>
<hr/>
<table>
<tr>
<td><a href="{../@name}{$back-link}.html">back</a></td>
<td><a href="{../@name}.html">investigation contents</a></td>
<td><a href="{../@name}{$next-link}.html">next</a></td>
</tr></table>
</xsl:template>




</xsl:stylesheet>
