<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:lxslt="http://xml.apache.org/xslt" xmlns:fo="http://www.w3.org/1999/XSL/Format" 		xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    	extension-element-prefixes="redirect cc-ext"
    	xmlns:cc-ext="ext1">

<xsl:output method="xml" indent="yes" /> 

<xsl:template match="/project/unit/investigation">
<redirect:write file="pdf/{../@name}_{@name}_TN.fo">

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
		TEEMSS:<xsl:value-of select="title"/> Teacher Notes
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
      <fo:block font-size="18pt" 
            font-family="sans-serif" 
            line-height="24pt"
            space-after.optimum="15pt"
            background-color="white"
            color="black"
            text-align="center"
            padding-top="3pt">
	
        <xsl:value-of select="title"/>: Teacher Notes

      </fo:block>


	<xsl:apply-templates select="teacher-notes"/>

    </fo:flow> <!-- closes the flow element-->
  </fo:page-sequence> <!-- closes the page-sequence -->
</fo:root>

</redirect:write>

</xsl:template>

<xsl:template match="title"/>

<xsl:template match="teacher-notes">

	<xsl:apply-templates select="teacher-intro"/>
	<xsl:apply-templates select="teacher-discuss"/>
	<xsl:apply-templates select="teacher-background"/>
	<xsl:apply-templates select="teacher-timeline"/>

</xsl:template>


<xsl:template match="teacher-intro">
    	<xsl:apply-templates select="summary"/>
    	<xsl:apply-templates select="skills"/>
</xsl:template>

<xsl:template match="summary">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold"
		space-before="6pt" space-after="6pt">
	Introduction
	</fo:block>
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


<xsl:template match="skills">
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
In addition, students will gain experience with inquiry skills, including:
	</fo:block>
	<fo:list-block
		space-before="0.25em" space-after="0.25em" >
		<xsl:apply-templates/>
	</fo:list-block>
	<fo:block break-after="page"/>
</xsl:template>

<xsl:key name="skill-def" match="skill" use="@name"/>

<xsl:template match="skill-ref">
		<fo:list-item space-after="0.5em">
			<fo:list-item-label start-indent="1em">
				<fo:block>
				&#x2022;
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body  start-indent="2em">
				<fo:block>
				<xsl:value-of select="key('skill-def', @ref)"/>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>			
</xsl:template>


<xsl:template match="teacher-discuss">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold"
		space-before="6pt" space-after="6pt">
	Discussion Guide
	</fo:block>
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="normal"
		space-before="6pt" space-after="6pt">
	Using this Guide
	</fo:block>
	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
This guide is designed to help you convert the investigations your students experience into solid learning. The "Overview" section mentions some of the learning issues raised by this content. These issues might come up in conversations with students anytime. The "Setting the Stage" section provides ideas for a discussion you might hold before beginning the investigations. This discussion is important to motivate and alert students to observations that might answer their questions. The "Wrap Up" section can be used after the investigations to help student reflect on what they have done. Taking time to reflect while the investigations are fresh in students' minds has been shown to substantially increase learning.
    	</fo:block>
    	<xsl:apply-templates select="teacher-overview"/>
    	<xsl:apply-templates select="teacher-stage"/>
    	<xsl:apply-templates select="teacher-wrap"/>
</xsl:template>

<xsl:template match="teacher-overview">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="normal"
		space-before="6pt" space-after="6pt">
	Overview
	</fo:block>
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

<xsl:template match="teacher-stage">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="normal"
		space-before="6pt" space-after="6pt">
	Setting the Stage
	</fo:block>
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

<xsl:template match="teacher-wrap">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="normal"
		space-before="6pt" space-after="6pt">
	Wrap Up
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

<xsl:template match="teacher-questions">
<xsl:apply-templates select="question"/>
</xsl:template>

<xsl:template match="question">
<xsl:apply-templates select="following-sibling::*[position()=1 and self::answer]"/>
</xsl:template>

<xsl:template match="answer">
[<xsl:apply-templates/>]
</xsl:template> 

<xsl:template match="teacher-background">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold"
		space-before="6pt" space-after="6pt">
	Additional Teacher Background
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

<xsl:template match="teacher-timeline">
	<fo:block font-size="14pt" font-family="san-serif"
		font-weight="bold"
		space-before="6pt" space-after="6pt">
	Suggested Timeline
	</fo:block>
 	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
 The amount of time you spend on introductory discussions, data collection, and analysis, will determine your overall timeline. The following represents a possible timeline.
   	</fo:block>
   	<xsl:apply-templates/>
 	<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
Additional days can be used for Further Investigations.
   	</fo:block>
</xsl:template>

<xsl:template match="time-period[@type='setup']">
		<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">

	<xsl:call-template name="num-periods-text">
	<xsl:with-param name="number"><xsl:value-of select="@periods"/>
	</xsl:with-param></xsl:call-template>
	 - "Setting Up" discussion
   	</fo:block>
</xsl:template>
	
<xsl:template match="time-period[@type='wrap']">
		<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
	<xsl:call-template name="num-periods-text">
	<xsl:with-param name="number"><xsl:value-of select="@periods"/>
	</xsl:with-param></xsl:call-template>
	 - Analysis and "Wrap Up" discussion
   	</fo:block>
</xsl:template>

<xsl:template match="time-period[@type='trial']">
		<fo:block
        	text-indent="1em"
        	font-family="sans-serif" font-size="12pt"
        	space-before.minimum="2pt"
        	space-before.maximum="6pt"
        	space-before.optimum="4pt"
        	space-after.minimum="2pt"
        	space-after.maximum="6pt"
        	space-after.optimum="4pt">
	<xsl:call-template name="num-periods-text">
	<xsl:with-param name="number"><xsl:value-of select="@periods"/>
	</xsl:with-param></xsl:call-template>
 	- Trial <xsl:number value="@number" format="I"/>: 
	<xsl:variable name="trialNumber"><xsl:value-of select="@number"/></xsl:variable>
<xsl:value-of select="ancestor::investigation/trial[position()=$trialNumber]/@title"/>
   	</fo:block>
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
			src="file:../images/Technical_Hints/screenshots/{@name}/RAW_{@name}.tif"/>
			</fo:block>
		</xsl:when>
		<xsl:otherwise>
			<fo:block text-align="center" border="solid 0.5pt black" margin-left="2in"
				padding-start="0.2in" margin-right="2in">
			<fo:external-graphic height="2.658in" width="3in"	
			src="file:../images/Technical_Hints/pictures/{@name}/RAW_{@name}.tif"/>
			</fo:block>
		</xsl:otherwise> 
	</xsl:choose> 
      </xsl:template>


<xsl:template match="ext-image">
	<fo:block text-align="center" border="solid 0.5pt black" margin-left="2in"
		padding-start="0.2in" margin-right="2in">

	<fo:external-graphic  height="2.658in" width="3in"
		src="file:../images/{ancestor::unit/@name}/{ancestor::investigation/@name}/{@name}/RAW_{@name}.tif"/>
	</fo:block>
      </xsl:template>



</xsl:stylesheet>
