<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt" xmlns:fo="http://www.w3.org/1999/XSL/Format" 		xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    	extension-element-prefixes="redirect cc-ext"
    	xmlns:cc-ext="ext1">

<xsl:output method="xml" indent="yes" /> 

<xsl:template match="/project/unit/investigation">
<redirect:write file="pdf/{../@name}_{@name}.fo">

<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <fo:layout-master-set>
  <!-- fo:layout-master-set defines in its children the page layout: 
       the pagination and layout specifications
      - page-masters: have the role of describing the intended subdivisions 
                       of a page and the geometry of these subdivisions 
                      In this case there is only a simple-page-master which defines the 
                      layout for all pages of the text
  -->
    <!-- layout information -->
    <fo:simple-page-master master-name="simple"
                page-height="abs(-30cm + .3cm)"
                  page-width="(10cm * 2) + 1cm"
                  margin-top="round(.5) * 1cm" 
                  margin-bottom="round(.5) * 1cm"
                  margin-left="2.5 * 1cm" 
                  margin-right="5.5cm - 3cm">
      <fo:region-body margin-top="2cm" margin-bottom="2cm"/>
      <fo:region-before extent="2.5cm"/>
      <fo:region-after extent="1.5cm" column-count="2"/>
    </fo:simple-page-master>
  </fo:layout-master-set>
  <!-- end: defines page layout -->

 <!-- start page-sequence
       here comes the text (contained in flow objects)
       the page-sequence can contain different fo:flows 
       the attribute value of master-name refers to the page layout
       which is to be used to layout the text contained in this
       page-sequence-->
  <fo:page-sequence master-name="simple" initial-page-number="1" language="en" country = "us">

      <!-- start fo:flow
           each flow is targeted 
           at one (and only one) of the following:
           xsl-region-body (usually: normal text)
           xsl-region-before (usually: header)
           xsl-region-after  (usually: footer)
           xsl-region-start  (usually: left margin) 
           xsl-region-end    (usually: right margin)
           ['usually' applies here to languages with left-right and top-down 
            writing direction like English]
        -->
    <fo:static-content flow-name="xsl-region-before">
           <fo:block text-align="center" font-size="10pt" 
            font-family="serif" 
            line-height="1em + 4pt" >
		TEEMSS:<xsl:value-of select="title"/>
	   </fo:block>
    </fo:static-content>
    <fo:static-content flow-name="xsl-region-after">
	<fo:block text-align="center">
	<fo:inline text-align="start">
       Copyright &#169; 2001 The Concord Consortium, All rights reserved.
</fo:inline>
<fo:inline text-align="end" >
  p. <fo:page-number/>
       </fo:inline></fo:block>
    </fo:static-content>

    <fo:flow flow-name="xsl-region-body">

     <!-- each paragraph is encapsulated in a block element
           the attributes of the block define
           font-family and size, line-heigth etc. -->

       <!-- this defines a title -->
<!--
      <fo:block font-size="18pt" 
            font-family="sans-serif" 
            line-height="24pt"
            space-after.optimum="15pt"
            background-color="white"
            color="black"
            text-align="center"
            padding-top="3pt">
	
        <xsl:value-of select="title"/>
      </fo:block>
-->

<fo:block break-after="page">
<fo:block font-size="18pt" font-weight="bold"
     space-before="6pt" space-after="6pt"
     text-align="center">  
<fo:block>
<xsl:value-of select="ancestor::unit/title"/>
</fo:block>
<fo:block>
<xsl:value-of select="title"/>
</fo:block>
</fo:block>
<fo:block  space-after="2em" margin-left="2em">
<xsl:apply-templates select="question"/>
</fo:block>
<fo:list-block>
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Introduction
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Thinking About the Question
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Materials
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Safety
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
<xsl:for-each select="trial">
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Trial <xsl:number value="position()"
format="I"/>: <xsl:value-of select="@title"/>
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
</xsl:for-each>
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Technical Hints
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Analysis
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
<fo:list-item space-after="0.5em">
 <fo:list-item-label start-indent="1em">
	<fo:block>
		&#x2022;
	</fo:block>
 </fo:list-item-label>
 <fo:list-item-body  start-indent="2em">
	<fo:block>
Further Investigations
	</fo:block>
  </fo:list-item-body>
</fo:list-item>			
</fo:list-block>
</fo:block>


<xsl:apply-templates select="question"/>
<xsl:apply-templates select="intro"/>
<xsl:apply-templates select="think"/>
<xsl:apply-templates select="materials"/>
<xsl:apply-templates select="safety"/>
<xsl:apply-templates select="trial"/>
<xsl:apply-templates select="hints"/>
<xsl:apply-templates select="analysis"/>
<xsl:apply-templates select="further"/>


<!--   <xsl:apply-templates/> -->

    </fo:flow> <!-- closes the flow element-->
  </fo:page-sequence> <!-- closes the page-sequence -->
</fo:root>

</redirect:write>

</xsl:template>

<xsl:template match="title"/>


<xsl:template match="question">
	<fo:block font-size="14pt" font-family="san-serif"
		space-before="6pt" space-after="6pt">
	Discovery Question
	</fo:block>
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
		font-style="italic"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
    	<xsl:apply-templates/>
    </fo:block>
</xsl:template>

<xsl:template match="intro">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold"
		space-before="6pt" space-after="6pt">
	Introduction
	</fo:block>
	<xsl:apply-templates select="question"/>
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt" break-after="page">
    	<xsl:apply-templates/>
    	</fo:block>
</xsl:template>

<xsl:template match="think">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt">
	Thinking About The Question
	</fo:block>
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
		font-style="italic"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
    	<xsl:value-of select="../question"/>
    	</fo:block>

	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt"  break-after="page">
    	<xsl:apply-templates/>
    	</fo:block>
</xsl:template>

<xsl:template match="materials">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt">
	Materials
	</fo:block>
	<fo:list-block
		space-before="0.25em" space-after="0.25em" >
		<xsl:apply-templates/>
	</fo:list-block>
	<fo:block break-after="page"/>
</xsl:template>

<xsl:template match="safety">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt">
	Safety
	</fo:block>
	<fo:list-block
		space-before="0.25em" space-after="0.25em" >
		<xsl:apply-templates/>
	</fo:list-block>
	<fo:block break-after="page"/>
</xsl:template>

<xsl:template match="analysis">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt">
	Analysis
	</fo:block>
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt"  break-after="page">
    	<xsl:apply-templates/>
    	</fo:block>
</xsl:template>

<xsl:template match="further">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt">
	Further Investigations
	</fo:block>
	<fo:list-block
		space-before="0.25em" space-after="0.25em">
		<xsl:apply-templates/>
	</fo:list-block>
</xsl:template>

<xsl:template match="trial">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt">	
		Trial <xsl:number value="position()" format="I"/>: <xsl:value-of select="@title"/>
	</fo:block>
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt"  break-after="page">
    	<xsl:apply-templates/>
    	</fo:block>

</xsl:template>


<xsl:template match="item">
		<fo:list-item space-after="0.5em">
			<fo:list-item-label start-indent="1em">
				<fo:block>
				&#x2022;
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body  start-indent="2em">
				<fo:block>
				<xsl:apply-templates/>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>			
</xsl:template>

<xsl:template match="steps">
	<fo:list-block
		space-before="0.25em" space-after="0.25em"
		provisional-distance-between-starts="10mm"
		provisional-label-separation="5mm">
		<xsl:for-each select="*">
			<xsl:variable name="type_label">
  			<xsl:call-template name="level_label" /> 
  			</xsl:variable>
			<fo:list-item space-after="0.5em">
				<fo:list-item-label>
					<fo:block>
					<xsl:number format="{$type_label}" count="*"/>.
					</fo:block>
				</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<fo:block>
				<xsl:apply-templates select="."/>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:for-each>			
	</fo:list-block>
</xsl:template>


<xsl:template match="instructions">
	<fo:list-block
		space-before="0.25em" space-after="0.25em"
		provisional-distance-between-starts="10mm"
		provisional-label-separation="5mm">
		<xsl:apply-templates/>
	</fo:list-block>
</xsl:template>

<xsl:template match="instruction">
 	<xsl:variable name="type_label">
  		<xsl:call-template name="level_label" /> 
  	</xsl:variable>
		<fo:list-item space-after="0.5em">
			<fo:list-item-label>
				<fo:block>
					<xsl:value-of select="$type_label"/>.
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<fo:block font-size="14pt" font-family="san-serif"
						font-weight="bold" color="black"
						space-before="6pt" space-after="6pt">	
					<xsl:value-of select="@title"/>
				</fo:block>
				<fo:block>
				<xsl:apply-templates/>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>			
</xsl:template>

<xsl:template name="level_label">
	<xsl:variable name="depth">
  		<xsl:value-of select="count(ancestor::steps | ancestor::instructions | ancestor::querys)" /> 
  	</xsl:variable>
	<xsl:choose>
  		<xsl:when test="$depth ='1'">1</xsl:when> 
  		<xsl:when test="$depth ='2'">a</xsl:when> 
  	</xsl:choose>
</xsl:template>

<xsl:template match="query-response">
 	<xsl:choose>
  		<xsl:when test="@layout='paragraph'">
			<fo:block
        			text-indent="1em"
        			font-family="sans-serif" font-size="12pt"
        			space-before.minimum="2pt"
        			space-before.maximum="6pt"
        			space-before.optimum="4pt"
        			space-after.minimum="2pt"
        			space-after.maximum="6pt"
        			space-after.optimum="4pt">
    			<xsl:apply-templates/>
    			</fo:block>
		</xsl:when> 
  		<xsl:when test="@layout='list'">
	<xsl:apply-templates select="query-description"/>
	<fo:list-block
		space-before="0.25em" space-after="0.25em"
		provisional-distance-between-starts="10mm"
		provisional-label-separation="5mm">
		<xsl:for-each select="querys/query">
			<xsl:variable name="type_label">
  			<xsl:call-template name="level_label" /> 
  			</xsl:variable>
			<fo:list-item space-after="0.5em">
				<fo:list-item-label>
					<fo:block>
					<xsl:number format="{$type_label}" count="*"/>.
					</fo:block>
				</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<fo:block>
				<xsl:apply-templates select="."/>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:for-each>			
	</fo:list-block>
	<xsl:apply-templates select="query-static-link"/>
			
		</xsl:when> 
  	</xsl:choose>
</xsl:template>

<xsl:template match="hints">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt">
	Technical Hints
	</fo:block>
	<fo:block>
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>


<xsl:key name="tech-hint-def" match="tech-hint" use="@name"/>


<xsl:template match="tech-hint-ref">
	<xsl:apply-templates select="key('tech-hint-def', @ref)"/>
</xsl:template>

<xsl:template match="tech-hint">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold" color="black"
		space-before="6pt" space-after="6pt" text-align="center">
	<xsl:value-of select="title"/>
	</fo:block>
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt" break-after="page">
    	<xsl:apply-templates/>
    	</fo:block>
</xsl:template>

<xsl:template match="tech-hint-body">
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
    	<xsl:apply-templates/>
    	</fo:block>
</xsl:template>


<xsl:template match="p">
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
    	<xsl:apply-templates/>
    	</fo:block>
</xsl:template>


<lxslt:component prefix="cc-ext" functions="getImageDir">
<!-- notice this function looks for the image in the pdf/images directory
	because this function will be executed by xalan -->

	<lxslt:script lang="javascript">

	function getImageDir(imagesDir1, imageName)
	{
		var children = (new java.io.File("pdf/images/" + imagesDir1)).listFiles();
		var i;
		for (i=0; i &lt; children.length; i++){
			if(children[i].getName().startsWith(imageName)){
				break;
			}
		}
		if(i &lt; children.length){
			return imagesDir1 + "/" + children[i].getName();
		} else {
			return "InvalidImageName";
		}
	}	
    </lxslt:script>
</lxslt:component>


<xsl:template match="shared-image">
	<xsl:choose>
  		<xsl:when test="@screenshot='true'">
			<fo:block text-align="center" border="solid 0.5pt black" margin-left="2in"
				padding-start="0.2in" margin-right="2in">
			<fo:external-graphic  width="3in"	
			src="file:/images/Technical_Hints/screenshots/{@name}/RAW_{@name}.tif"/>
			</fo:block>
		</xsl:when>
		<xsl:otherwise>
			<fo:block text-align="center" border="solid 0.5pt black" margin-left="2in"
				padding-start="0.2in" margin-right="2in">
			<fo:external-graphic height="2.658in" width="3in"	
			src="file:/images/Technical_Hints/pictures/{@name}/RAW_{@name}.tif"/>
			</fo:block>
		</xsl:otherwise> 
	</xsl:choose> 
      </xsl:template>


<xsl:template match="ext-image">
	<fo:block text-align="center" border="solid 0.5pt black" margin-left="2in"
		padding-start="0.2in" margin-right="2in">

	<fo:external-graphic  height="2.658in" width="3in"
		src="file:images/{ancestor::unit/@name}/{ancestor::investigation/@name}/{@name}/RAW_{@name}.tif"/>
	</fo:block>
      </xsl:template>


</xsl:stylesheet>
